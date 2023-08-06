import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { v4 as uuidv4 } from 'uuid';
import axios from 'axios';
import { ref, uploadBytes, getDownloadURL } from 'firebase/storage'; // Import storage functions
import { storage } from './firebase'; // Import Firebase storage object

function LockerCreate() {
  const [lockerPassword, setLockerPassword] = useState('');
  const [lockerInfo, setLockerInfo] = useState('');
  const [mediaType, setMediaType] = useState('image');
  const [mediaFile, setMediaFile] = useState(null);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    let mediaURL = ''; // Initialize mediaURL

    // If mediaFile exists, proceed with media upload
    if (mediaFile) {
      const uniqueFilename = `${uuidv4()}_${mediaFile.name}`;
      const storageRef = ref(storage, uniqueFilename);
      const metadata = {
        contentType: mediaType === 'image' ? 'image/jpeg' : 'video/mp4',
      };

      try {
        await uploadBytes(storageRef, mediaFile, metadata);
        mediaURL = await getDownloadURL(storageRef); // Get the download URL of the uploaded media file
      } catch (error) {
        console.error('Error uploading media:', error);
        return;
      }
    }

    try {
      // Send the form data along with the mediaURL to the backend server on port 8080
      await axios.post('http://localhost:8080/createlocker', {
        lockerPassword,
        lockerInfo,
        mediaType,
        mediaURL,
      });

      // Reset the form fields
      setLockerPassword('');
      setLockerInfo('');
      setMediaType('image');
      setMediaFile(null);

      // Redirect to the Locker component or any other desired route
      navigate('/');
    } catch (error) {
      console.error('Error creating locker:', error);
    }
  };

  return (
    <div className="container">
      <h1 className="text-center">Create Locker</h1>
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label htmlFor="lockerPassword" className="form-label">Locker Password</label>
          <input
            type="text"
            className="form-control"
            id="lockerPassword"
            value={lockerPassword}
            onChange={(e) => setLockerPassword(e.target.value)}
          />
        </div>
        <div className="mb-3">
          <label htmlFor="lockerInfo" className="form-label">Locker Info</label>
          <textarea
            className="form-control"
            id="lockerInfo"
            value={lockerInfo}
            onChange={(e) => setLockerInfo(e.target.value)}
          />
        </div>
        <div className="mb-3">
          <label htmlFor="mediaType" className="form-label">Media Type</label>
          <select
            className="form-control"
            id="mediaType"
            value={mediaType}
            onChange={(e) => setMediaType(e.target.value)}
          >
            <option value="image">Image</option>
            <option value="video">Video</option>
          </select>
        </div>
        <div className="mb-3">
          <label htmlFor="mediaFile" className="form-label">Media File</label>
          <input
            type="file"
            className="form-control"
            id="mediaFile"
            onChange={(e) => setMediaFile(e.target.files[0])}
          />
        </div>
        <button type="submit" className="btn btn-primary">Create Locker</button>
      </form>
    </div>
  );
}

export default LockerCreate;
