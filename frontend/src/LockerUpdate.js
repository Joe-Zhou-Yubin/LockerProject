import React, { useState } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';
import { ref, uploadBytes, getDownloadURL } from 'firebase/storage'; // Import storage functions
import { v4 as uuidv4 } from 'uuid';
import { storage } from './firebase'; // Import Firebase storage object


function LockerUpdate() {
    const { lockerId } = useParams();
  const [lockerInfo, setLockerInfo] = useState('');
  const [mediaType, setMediaType] = useState('image'); // Default value is 'image'
  const [mediaFile, setMediaFile] = useState(null); 
   const navigate = useNavigate();

   const handleSaveClick = async (e) => {
    e.preventDefault();
    let mediaURL = null;

    if (mediaFile) {
      const uniqueFilename = `${uuidv4()}_${mediaFile.name}`;
      const storageRef = ref(storage, uniqueFilename);
      const metadata = {
        contentType: mediaType === 'image' ? 'image/jpeg' : 'video/mp4',
      };

      try {
        await uploadBytes(storageRef, mediaFile, metadata);
        mediaURL = await getDownloadURL(storageRef);
      } catch (error) {
        console.error('Error uploading media:', error);
        return;
      }
    }

    try {
      // Create a JSON object with the updated locker data
      const updatedLockerData = {
        lockerInfo: lockerInfo,
        mediaType: mediaType,
      };

      // Only append the mediaURL field if it's not null (i.e., a new media file is uploaded)
      if (mediaURL !== null) {
        updatedLockerData.mediaURL = mediaURL;
      }

      // Call the /updatelocker/{lockerId} endpoint with the updated locker data
      await axios.post(`http://localhost:8080/updatelocker/${lockerId}`, updatedLockerData);

      // Navigate back to the LockerDisplay component with the updated lockerId
      navigate(`/lockerdisplay/${lockerId}`);
    } catch (error) {
      console.error('Error updating locker:', error);
    }
  };
  
  const handleCancelClick = () => {
    // Navigate back to the LockerDisplay component
    navigate(`/lockerdisplay/${lockerId}`);
  };

  

  return (
    <div className="container mt-4">
      <h2>Edit Locker Information</h2>
      <div className="mb-3">
        <label htmlFor="lockerInfo" className="form-label">
          Locker Info
        </label>
        <input
          type="text"
          className="form-control"
          id="lockerInfo"
          name="lockerInfo"
          value={lockerInfo}
          onChange={(e) => setLockerInfo(e.target.value)}
        />
      </div>
      <div className="mb-3">
        <label htmlFor="mediaType" className="form-label">
          Media Type
        </label>
        <select
          className="form-control"
          id="mediaType"
          name="mediaType"
          value={mediaType}
          onChange={(e) => setMediaType(e.target.value)}
        >
          <option value="image">Image</option>
          <option value="video">Video</option>
        </select>
      </div>
      <div className="mb-3">
        <label htmlFor="mediaURL" className="form-label">
          Media Upload
        </label>
        <input
  type="file"
  className="form-control"
  id="mediaURL"
  name="mediaURL"
  accept="image/*, video/*" // Specify the file type restriction (images and videos)
  onChange={(e) => setMediaFile(e.target.files[0])}
/>
      </div>
      {/* Add the save and cancel buttons */}
      <button className="btn btn-primary" onClick={handleSaveClick}>
        Save
      </button>
      <button className="btn btn-secondary ml-2" onClick={handleCancelClick}>
        Cancel
      </button>
    </div>
  );
}

export default LockerUpdate;
