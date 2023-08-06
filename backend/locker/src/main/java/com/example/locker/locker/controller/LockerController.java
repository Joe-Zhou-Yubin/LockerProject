package com.example.locker.locker.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.example.locker.locker.entity.Locker;
import com.example.locker.locker.service.LockerAuth;

@RestController
public class LockerController {
	
	@Autowired
	LockerAuth lockerAuth;
	
	private final Firestore firestore = FirestoreClient.getFirestore();
	
	private static final String COLLECTION_NAME = "lockers";
	
	@PostMapping("/createlocker")
	public ResponseEntity<Locker> createLocker(@RequestBody Locker locker) {
        Locker createdLocker = lockerAuth.createLocker(locker);
        return new ResponseEntity<>(createdLocker, HttpStatus.CREATED);
    }
	
	@PostMapping("/unlocklocker")
	public ResponseEntity<?> loginLocker (@RequestBody Map<String, String> credentials){
		String lockerId = credentials.get("lockerId");
		String password = credentials.get("lockerPassword");
		
		boolean unlockSuccessful = lockerAuth.loginLocker(lockerId, password);
		
		Map<String, Object> response = new HashMap<>();
        response.put("unlockSuccessful", unlockSuccessful);

        if (unlockSuccessful) {
            response.put("message", "Unlock successful.");
            // You can include any additional data you want to send to the client upon successful login
        } else {
            response.put("message", "Unlock failed. Please check your password.");
        }

        return ResponseEntity.ok(response);
	}
	
	@PostMapping("/unlockpassword")
	public ResponseEntity<?> unlockLockerByPassword(@RequestBody Map<String, String> request) {
	    String lockerPassword = request.get("lockerPassword");

	    boolean unlocked = lockerAuth.unlockLockerByPassword(lockerPassword);
	    if (unlocked) {
	        Map<String, Object> response = new HashMap<>();
	        response.put("message", "Locker unlocked successfully.");
	        return ResponseEntity.ok(response);
	    } else {
	        Map<String, Object> response = new HashMap<>();
	        response.put("message", "Failed to unlock locker. Please provide lockerId as well.");
	        return ResponseEntity.badRequest().body(response);
	    }
	}
	
	@GetMapping("/getalllockers")
    public ResponseEntity<List<Locker>> getAllLockers() {
        List<Locker> lockers = lockerAuth.getAllLockers();
        return ResponseEntity.ok(lockers);
    }

	
	@GetMapping("/getlocker/{lockerId}")
    public ResponseEntity<?> getLocker(@PathVariable String lockerId) {
        Locker locker = lockerAuth.getLocker(lockerId);
        if (locker != null) {
            if (!locker.isLocked()) {
                return ResponseEntity.ok(locker);
            } else {
                return ResponseEntity.ok("Locker is locked and cannot be accessed.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
	
	@PostMapping("/locklocker/{lockerid}")
    public ResponseEntity<?> lockLocker(@PathVariable("lockerid") String lockerId) {
        boolean locked = lockerAuth.lockLocker(lockerId);
        if (locked) {
            return ResponseEntity.ok("Locker locked successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
	
	@PostMapping("/lockalllocker")
	public ResponseEntity<Map<String, Object>> lockAllLockers() {
	    lockerAuth.lockAllLockers();

	    Map<String, Object> response = new HashMap<>();
	    response.put("message", "All lockers have been locked.");
	    return ResponseEntity.ok(response);
	}

	
	@GetMapping("/islockerlocked/{lockerId}")
	public boolean isLockerLocked(@PathVariable String lockerId) {
	    boolean isLocked = lockerAuth.isLockerLocked(lockerId);
	    
	    return isLocked;
	}

	
	@PostMapping("/unlockalllocker")
	public ResponseEntity<Map<String, Object>> unlockAllLockers() {
	    lockerAuth.unlockAllLockers();

	    Map<String, Object> response = new HashMap<>();
	    response.put("message", "All lockers have been unlocked.");
	    return ResponseEntity.ok(response);
	}


	
	@PostMapping("/clearlocker/{lockerId}")
	public ResponseEntity<?> clearLocker(@PathVariable String lockerId) {
	    lockerAuth.clearLocker(lockerId);
	    return ResponseEntity.ok("Locker cleared successfully.");
	}
	
	@PostMapping("/updatelocker/{lockerId}")
	public ResponseEntity<Locker> updateLocker(
	        @PathVariable String lockerId,
	        @RequestBody Locker updatedLocker
	) {
	    Locker existingLocker = lockerAuth.findLockerbylockerID(lockerId);

	    if (existingLocker != null) {
	        // Set the updated values from the request body
	        existingLocker.setLockerInfo(updatedLocker.getLockerInfo());
	        existingLocker.setMediaType(updatedLocker.getMediaType());
	        existingLocker.setMediaURL(updatedLocker.getMediaURL());

	        // Update the locker in the Firestore
	        lockerAuth.updateLocker(existingLocker);

	        return ResponseEntity.ok(existingLocker);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}


	



	

	
}
