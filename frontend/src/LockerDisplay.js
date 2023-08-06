import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams, useNavigate, Link } from 'react-router-dom';

function LockerDisplay() {
  const { lockerId } = useParams();
  const [lockerData, setLockerData] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const checkLockerStatus = async () => {
      try {
        // Call the /islockerlocked/{lockerId} endpoint to check if the locker is locked
        const response = await axios.get(`http://localhost:8080/islockerlocked/${lockerId}`);
        const isLocked = response.data;

        // If the locker is locked, navigate back to the main page
        if (isLocked) {
          navigate('/');
        } else {
          // If the locker is not locked, fetch the locker data
          const lockerResponse = await axios.get(`http://localhost:8080/getlocker/${lockerId}`);
          setLockerData(lockerResponse.data);
        }
      } catch (error) {
        console.error('Error checking locker status:', error);
      }
    };

    checkLockerStatus();
  }, [lockerId, navigate]);

  const handleLockClick = async () => {
    try {
      // Call the /locklocker/{lockerId} endpoint to lock the specific locker
      await axios.post(`http://localhost:8080/locklocker/${lockerId}`);
      // Update lockerData to reflect the change (assuming the API returns updated data)
      // You can also simply set the 'locked' property of lockerData to true manually
      setLockerData((prevData) => ({ ...prevData, locked: true }));

      // Navigate back to the previous page (/)
      navigate('/');
    } catch (error) {
      console.error('Error locking the locker:', error);
    }
  };

  const handleClearClick = async () => {
    // Show an alert to confirm clearing the locker
    const confirmation = window.confirm('Are you sure you want to clear the locker?');
    if (confirmation) {
      try {
        // Call the /clearlocker/{lockerId} endpoint to clear the locker
        await axios.post(`http://localhost:8080/clearlocker/${lockerId}`);
        // Update lockerData to reflect the change (assuming the API returns updated data)
        // You can also simply set the 'locked' property of lockerData to false manually
        setLockerData((prevData) => ({ ...prevData, locked: false }));
        
        // Reload the page to fetch the updated locker data again
        window.location.reload();
      } catch (error) {
        console.error('Error clearing the locker:', error);
      }
    }
  };
  const handleBackClick = () => {
    // Navigate back to the main page
    navigate('/');
  };

  

  

  // Render loading state while fetching data
  if (!lockerData) {
    return <div>Loading...</div>;
  }

  // Render locker information once data is fetched
  return (
    <div className="container mt-4">
        <button className="btn btn-secondary mr-2" onClick={handleBackClick}>
          Back
        </button>
      <h2>Locker Information</h2>
      <div className="card">
        <div className="card-body">
          <p className="card-text">Locker Info: {lockerData.lockerInfo}</p>
          <p className="card-text">Media Type: {lockerData.mediaType}</p>
          {lockerData.mediaType === 'image' && (
            <img
              src={lockerData.mediaURL}
              alt="Locker Media"
              className="img-fluid"
              style={{ maxWidth: '100%' }}
            />
          )}
          {lockerData.mediaType === 'video' && (
            <video controls className="w-100">
              <source src={lockerData.mediaURL} type="video/mp4" />
              Your browser does not support the video tag.
            </video>
          )}
          <p className="card-text">Locked: {lockerData.locked ? 'Yes' : 'No'}</p>
          <button className="btn btn-danger" onClick={handleLockClick}>
            Lock
          </button>
          <Link to={`/lockerupdate/${lockerId}`} className="btn btn-primary ml-2">
            Update
          </Link>
          <button className="btn btn-warning ml-2" onClick={handleClearClick}>
            Clear
          </button>
        </div>
      </div>
    </div>
  );
}

export default LockerDisplay;
