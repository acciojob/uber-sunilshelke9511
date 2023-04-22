package com.driver.controllers;

import com.driver.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/driver")
public class DriverController {
	@Autowired
	DriverService driverService;
	@PostMapping("/register")
	public ResponseEntity<Void> registerDriver(@RequestParam String mobile, @RequestParam String password){
		driverService.register(mobile, password);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/delete")
	public void deleteDriver(@RequestParam Integer driverId){
		driverService.removeDriver(driverId);
	}

	@PutMapping("/status")
	public void updateStatus(@RequestParam Integer driverId){
		driverService.updateStatus(driverId);
	}
}