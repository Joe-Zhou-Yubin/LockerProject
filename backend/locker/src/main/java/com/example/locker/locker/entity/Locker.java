package com.example.locker.locker.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Locker {
	
	@Id
	private String id;
	
	private String lockerId;
	private String lockerPassword;
	private boolean isLocked;
	private String lockerInfo;
	private String mediaType;
	private String mediaURL;
	
	public Locker() {
		this.isLocked = true;
	}

	public Locker(String id, String lockerId, String lockerPassword, boolean isLocked, String mediaType,String mediaURL, String lockerInfo) {
		super();
		this.id = id;
		this.lockerId = lockerId; // generate unique uuid
		this.lockerPassword = lockerPassword;
		this.isLocked = isLocked;
		this.lockerInfo = lockerInfo;
		this.mediaType = mediaType;
		this.mediaURL = mediaURL;
		
		
	}
	
	private String generateLockerId() {
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString().replaceAll("-", "").substring(0,8);
		return randomUUIDString;
	}
	
	

	public String getLockerInfo() {
		return lockerInfo;
	}

	public void setLockerInfo(String lockerInfo) {
		this.lockerInfo = lockerInfo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLockerId() {
		return lockerId;
	}

	public void setLockerId(String lockerId) {
		this.lockerId = lockerId;
	}

	public String getLockerPassword() {
		return lockerPassword;
	}

	public void setLockerPassword(String lockerPassword) {
		this.lockerPassword = lockerPassword;
	}

	public boolean isLocked() {
		return isLocked;
	}

	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public String getMediaURL() {
		return mediaURL;
	}

	public void setMediaURL(String mediaURL) {
		this.mediaURL = mediaURL;
	}

	@Override
	public String toString() {
		return "Locker [id=" + id + ", lockerId=" + lockerId + ", lockerPassword=" + lockerPassword + ", isLocked="
				+ isLocked + ", lockerInfo=" + lockerInfo + ", mediaType=" + mediaType + ", mediaURL=" + mediaURL + "]";
	}

	
	
}
