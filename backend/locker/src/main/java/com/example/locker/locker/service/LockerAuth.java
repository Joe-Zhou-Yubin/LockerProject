package com.example.locker.locker.service;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.locker.locker.entity.Locker;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.storage.Acl.User;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class LockerAuth {

	private final Firestore firestore = FirestoreClient.getFirestore();
	
	private static final String COLLECTION_NAME = "lockers";
	
	
	public Locker createLocker(Locker locker) {
		// Validate lockerPassword and lockerInfo
        if (locker.getLockerPassword() == null || locker.getLockerPassword().isEmpty()) {
            throw new IllegalArgumentException("Locker password cannot be null or empty.");
        }
        if (locker.getLockerInfo() == null || locker.getLockerInfo().isEmpty()) {
            throw new IllegalArgumentException("Locker info cannot be null or empty.");
        }
        
        CollectionReference collectionReference = firestore.collection(COLLECTION_NAME);

        // Find the highest lockerId in the existing lockers
        String highestLockerId = findHighestLockerId(collectionReference);

        // Increment the highestLockerId and set it as the new lockerId for the created locker
        int newLockerId = Integer.parseInt(highestLockerId) + 1;
        locker.setLockerId(String.valueOf(newLockerId));

        // Write the Locker object to Firestore
        DocumentReference documentReference = collectionReference.document();
        locker.setId(documentReference.getId());
        documentReference.set(locker);

        return locker;
    }
	
	private boolean isNumeric(String str) {
        return str != null && str.matches("-?\\d+");
    }

	private String findHighestLockerId(CollectionReference collectionReference) {
	    int highestLockerId = 0; // Assuming lockerIds are positive integers

	    try {
	        ApiFuture<QuerySnapshot> future = collectionReference.get();
	        QuerySnapshot snapshot = future.get();
	        List<QueryDocumentSnapshot> documents = snapshot.getDocuments();

	        for (QueryDocumentSnapshot document : documents) {
	            Locker locker = document.toObject(Locker.class);
	            if (locker != null) {
	                String lockerId = locker.getLockerId();
	                if (lockerId != null && isNumeric(lockerId)) {
	                    int numericLockerId = Integer.parseInt(lockerId);
	                    if (numericLockerId > highestLockerId) {
	                        highestLockerId = numericLockerId;
	                    }
	                }
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return String.valueOf(highestLockerId);
	}

	
	public boolean loginLocker (String lockerId, String password) {
		Locker locker = findLockerbylockerID(lockerId);
		if (locker != null && locker.getLockerPassword().equals(password)) {
			locker.setLocked(false);
            firestore.collection(COLLECTION_NAME).document(locker.getId()).set(locker);
            return true;
		}
		return false;
	}
	
	public Locker findLockerbylockerID(String lockerId) {
		CollectionReference collectionReference = firestore.collection(COLLECTION_NAME);
        Query query = collectionReference.whereEqualTo("lockerId", lockerId).limit(1);
        
        try {
            ApiFuture<QuerySnapshot> future = query.get();
            QuerySnapshot snapshot = future.get();
            if (!snapshot.isEmpty()) {
                DocumentSnapshot document = snapshot.getDocuments().get(0);
                Locker locker = document.toObject(Locker.class);
                if (locker != null) {
                    locker.setId(document.getId()); // Map Firestore document ID to id field
                    return locker;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}
	
	public Locker getLocker(String lockerId) {
        CollectionReference collectionReference = firestore.collection(COLLECTION_NAME);
        Query query = collectionReference.whereEqualTo("lockerId", lockerId).limit(1);

        try {
            ApiFuture<QuerySnapshot> future = query.get();
            QuerySnapshot snapshot = future.get();
            if (!snapshot.isEmpty()) {
                DocumentSnapshot document = snapshot.getDocuments().get(0);
                Locker locker = document.toObject(Locker.class);
                if (locker != null) {
                    locker.setId(document.getId()); // Map Firestore document ID to id field
                    return locker;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	public List<Locker> getAllLockers() {
        List<Locker> lockers = new ArrayList<>();

        CollectionReference collectionReference = firestore.collection(COLLECTION_NAME);

        try {
            ApiFuture<QuerySnapshot> future = collectionReference.get();
            QuerySnapshot snapshot = future.get();
            List<QueryDocumentSnapshot> documents = snapshot.getDocuments();

            for (QueryDocumentSnapshot document : documents) {
                Locker locker = document.toObject(Locker.class);
                locker.setId(document.getId()); // Map Firestore document ID to id field
                lockers.add(locker);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lockers;
    }
	
	public boolean lockLocker(String lockerId) {
        Locker locker = findLockerbylockerID(lockerId);
        if (locker != null) {
            locker.setLocked(true);
            firestore.collection(COLLECTION_NAME).document(locker.getId()).set(locker);
            return true;
        }
        return false;
    }
	
	public boolean unlockLockerByPassword(String lockerPassword) {
	    CollectionReference collectionReference = firestore.collection(COLLECTION_NAME);
	    Query query = collectionReference.whereEqualTo("lockerPassword", lockerPassword);

	    try {
	        ApiFuture<QuerySnapshot> future = query.get();
	        QuerySnapshot snapshot = future.get();
	        List<QueryDocumentSnapshot> documents = snapshot.getDocuments();

	        if (documents.size() == 1) {
	            Locker locker = documents.get(0).toObject(Locker.class);
	            locker.setLocked(false);
	            firestore.collection(COLLECTION_NAME).document(locker.getId()).set(locker);
	            return true;
	        } else if (documents.size() > 1) {
	            System.out.println("Please input lockerId as well to unlock.");
	            return false;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return false;
	}
	
	public boolean isLockerLocked(String lockerId) {
	    Locker locker = findLockerbylockerID(lockerId);
	    if (locker != null) {
	        boolean isLocked = locker.isLocked();
	        return isLocked;
	    }
	    return false;
	}

	
	public void lockAllLockers() {
	    CollectionReference collectionReference = firestore.collection(COLLECTION_NAME);

	    try {
	        ApiFuture<QuerySnapshot> future = collectionReference.get();
	        QuerySnapshot snapshot = future.get();
	        List<QueryDocumentSnapshot> documents = snapshot.getDocuments();

	        WriteBatch batch = firestore.batch();

	        for (QueryDocumentSnapshot document : documents) {
	            Locker locker = document.toObject(Locker.class);
	            if (locker != null && !locker.isLocked()) {
	                locker.setLocked(true);
	                DocumentReference documentReference = firestore.collection(COLLECTION_NAME).document(locker.getId());
	                batch.set(documentReference, locker);
	            }
	        }

	        // Commit the batch operation
	        ApiFuture<List<WriteResult>> batchFuture = batch.commit();
	        batchFuture.get();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void clearLocker(String lockerId) {
	    Locker locker = findLockerbylockerID(lockerId);
	    if (locker != null) {
	        locker.setLockerInfo("");
	        locker.setMediaType("");
	        locker.setMediaURL("");
	        firestore.collection(COLLECTION_NAME).document(locker.getId()).set(locker);
	    }
	}

	
	public void unlockAllLockers() {
	    CollectionReference collectionReference = firestore.collection(COLLECTION_NAME);

	    try {
	        ApiFuture<QuerySnapshot> future = collectionReference.get();
	        QuerySnapshot snapshot = future.get();
	        List<QueryDocumentSnapshot> documents = snapshot.getDocuments();

	        WriteBatch batch = firestore.batch();

	        for (QueryDocumentSnapshot document : documents) {
	            Locker locker = document.toObject(Locker.class);
	            if (locker != null && locker.isLocked()) {
	                locker.setLocked(false);
	                DocumentReference documentReference = firestore.collection(COLLECTION_NAME).document(locker.getId());
	                batch.set(documentReference, locker);
	            }
	        }

	        // Commit the batch operation
	        ApiFuture<List<WriteResult>> batchFuture = batch.commit();
	        batchFuture.get();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public Locker updateLocker(Locker locker) {
        // Validate lockerId and lockerInfo
        if (locker.getLockerId() == null || locker.getLockerId().isEmpty()) {
            throw new IllegalArgumentException("Locker ID cannot be null or empty.");
        }
        if (locker.getLockerInfo() == null) {
            locker.setLockerInfo(""); // Set the lockerInfo to an empty string if it's null
        }

        CollectionReference collectionReference = firestore.collection(COLLECTION_NAME);

        try {
            ApiFuture<QuerySnapshot> future = collectionReference.whereEqualTo("lockerId", locker.getLockerId()).get();
            QuerySnapshot snapshot = future.get();
            if (!snapshot.isEmpty()) {
                DocumentSnapshot document = snapshot.getDocuments().get(0);
                Locker existingLocker = document.toObject(Locker.class);
                if (existingLocker != null) {
                    existingLocker.setLockerInfo(locker.getLockerInfo());
                    existingLocker.setMediaType(locker.getMediaType());
                    existingLocker.setMediaURL(locker.getMediaURL());

                    document.getReference().set(existingLocker).get();
                    return existingLocker;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




	
	

	

}
