import React from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function LockerAdmin() {
  const navigate = useNavigate();

  const handleUnlockAllClick = async () => {
    try {
      // Call the /unlockalllocker endpoint to unlock all lockers
      await axios.post('http://localhost:8080/unlockalllocker');
      // Handle any success or UI updates if needed

      // Navigate back to the main page
      navigate('/');
    } catch (error) {
      console.error('Error unlocking all lockers:', error);
    }
  };

  const handleLockAllClick = async () => {
    try {
      // Call the /lockalllocker endpoint to lock all lockers
      await axios.post('http://localhost:8080/lockalllocker');
      // Handle any success or UI updates if needed

      // Navigate back to the main page
      navigate('/');
    } catch (error) {
      console.error('Error locking all lockers:', error);
    }
  };

  return (
    <div className="container">
      <h1 className="text-center">Locker Admin</h1>
      <div className="d-flex justify-content-center align-items-center">
        <button className="btn btn-primary mx-2" onClick={handleUnlockAllClick}>
          Unlock All
        </button>
        <button className="btn btn-primary mx-2" onClick={handleLockAllClick}>
          Lock All
        </button>
      </div>
    </div>
  );
}

export default LockerAdmin;
