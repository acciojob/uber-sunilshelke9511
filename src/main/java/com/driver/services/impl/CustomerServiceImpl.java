package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		customerRepository2.delete(customerRepository2.findById(customerId).get());

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		List<Driver> listOfDrivers = driverRepository2.findAll();
		int min = Integer.MAX_VALUE;
		Driver driver = null;
		for(Driver d : listOfDrivers){
			if(d.getDriverId()<min && d.getCab().getAvailable()){
				min = d.getDriverId();
				driver = d;
			}
		}

		if(driver==null){
			throw new Exception("No cab available!");
		}

		TripBooking tripBooking = new TripBooking();
		tripBooking.setCustomer(customerRepository2.findById(customerId).get());
		tripBooking.setDriver(driver);
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setStatus(TripStatus.CONFIRMED);
		driver.getCab().setAvailable(false);


		Customer customer = customerRepository2.findById(customerId).get();
		customer.getTripBookingList().add(tripBooking);

		driver.getTripBookingList().add(tripBooking);

		customerRepository2.save(customer);
		driverRepository2.save(driver);

		//no need to save as tripBooking is the child and we already saved its parent(customer/driver)
		tripBookingRepository2.save(tripBooking);      //we can comment out this line


		return tripBooking;


	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking trip = tripBookingRepository2.findById(tripId).get();
		trip.setStatus(TripStatus.CANCELED);
		trip.getDriver().getCab().setAvailable(true);
		trip.setBill(0);

		tripBookingRepository2.save(trip);

	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking trip = tripBookingRepository2.findById(tripId).get();
		trip.setStatus(TripStatus.COMPLETED);
		trip.getDriver().getCab().setAvailable(true);

		int dist = trip.getDistanceInKm();
		int rate = trip.getDriver().getCab().getPerKmRate();
		int bill = dist * rate;

		trip.setBill(bill);

		tripBookingRepository2.save(trip);

	}
}