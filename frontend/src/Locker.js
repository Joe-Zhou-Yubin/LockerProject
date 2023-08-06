import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';

function Locker() {
  const [lockers, setLockers] = useState([]);
  const [errorMessage, setErrorMessage] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchLocker = async () => {
      try {
        // Fetch data from the backend server on port 8080
        const response = await axios.get('http://localhost:8080/getalllockers');
        const fetchedLockers = response.data;

        // Sort lockers based on increasing lockerId
        fetchedLockers.sort((a, b) => parseInt(a.lockerId) - parseInt(b.lockerId));

        // Update state with the fetched and sorted lockers
        setLockers(fetchedLockers);
      } catch (error) {
        console.error('Error fetching lockers: ', error);
      }
    };

    fetchLocker();
  }, []);

  const handleButtonClick = (lockerId) => {
    // Navigate to the /lockerdisplay/:lockerId route
    navigate(`/lockerdisplay/${lockerId}`);
  };

  const handleUnlockClick = async () => {
    // Get the values from the input fields
    const lockerId = document.getElementById('lockerId').value;
    const lockerPassword = document.getElementById('lockerPassword').value;
  
    try {
      if (lockerId && lockerPassword) {
        // Call /unlocklocker if both fields are filled
        await axios.post('http://localhost:8080/unlocklocker', {
          lockerId,
          lockerPassword,
        });
      } else if (lockerPassword) {
        // Call /unlockpassword if only the password field is filled
        await axios.post('http://localhost:8080/unlockpassword', {
          lockerPassword,
        });
      }
  
      // Reset the input fields and clear the error message
      document.getElementById('lockerId').value = '';
      document.getElementById('lockerPassword').value = '';
      setErrorMessage('');
  
      // Reload the page
      window.location.reload();
    } catch (error) {
      console.error('Error unlocking locker:', error);
      // Set the error message to display the error to the user
      setErrorMessage('Error unlocking locker. Please check your Locker ID and Password.');
    }
  };

  return (
    <div className="container">
      <div className="d-flex justify-content-between align-items-center">
        <h1 className="text-center">All Lockers</h1>
        <div>
          <Link to="/lockercreate" className="btn btn-primary">Create Locker</Link>
          <Link to="/lockeradmin" className="btn btn-primary mx-2">Admin</Link>
        </div>
      </div>
      <div className="my-4">
        <label htmlFor="lockerId" className="form-label">Your Locker ID</label>
        <input
          type="text"
          className="form-control"
          id="lockerId"
          placeholder="Enter your Locker ID"
        />
      </div>
      <div className="my-4">
        <label htmlFor="lockerPassword" className="form-label">Your Password</label>
        <input
          type="password"
          className="form-control"
          id="lockerPassword"
          placeholder="Enter your Password"
        />
      </div>
      <div className="my-2 text-danger">{errorMessage}</div>
      <button className="btn btn-primary " onClick={handleUnlockClick}>
        Unlock
      </button>
      <div className="d-flex justify-content-center align-items-center">
        <div className="row">
          {lockers.map((locker, index) => (
            <div key={locker.id} className="col-sm-1 m-3">
              <button
                className={`btn btn-lg btn-block ${locker.locked ? 'btn-danger' : 'btn-success'}`}
                onClick={() => handleButtonClick(locker.lockerId)}
              >
                {locker.lockerId}
              </button>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default Locker;
