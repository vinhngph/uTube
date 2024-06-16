import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import Sidebar from '../../Components/Sidebar/Sidebar'; // Import the Sidebar component
import './ManageChannel.css';
import { API } from '../../constants';

const ManageChannel = ({ sidebar }) => {
  const [userId, setUserId] = useState(null);
  const [videos, setVideos] = useState([]);
  const [error, setError] = useState(null);

  const getCookie = (name) => {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
    return null;
  };

  const getUserIdFromCookie = () => {
    const userCookie = getCookie('user');
    if (userCookie) {
      try {
        const userData = JSON.parse(userCookie);
        return userData.userId;
      } catch (error) {
        console.error('Failed to parse user cookie', error);
        return null;
      }
    }
    return null;
  };

  useEffect(() => {
    const userIdFromCookie = getUserIdFromCookie();
    if (userIdFromCookie) {
      setUserId(userIdFromCookie);
    } else {
      setError('User not logged in or invalid user data.');
    }
  }, []);

  useEffect(() => {
    if (userId) {
      axios.get(API + '/api/user/video/management', {
        params: { user_id: userId }
      })
        .then(response => {
          setVideos(response.data);
          setError('');
        })
        .catch(error => {
          if (error.response) {
            if (error.response.status === 400) {
              setError('Missing parameter: user_id.');
            } else if (error.response.status === 404) {
              setError('User ID not found.');
            } else {
              setError('Error fetching videos. Please try again later.');
            }
          } else {
            setError('Error fetching videos. Please try again later.');
          }
          console.error('Error fetching videos:', error);
        });
    }
  }, [userId]);

  return (
    <>
      <Sidebar sidebar={sidebar} />
      <div className={`manage-channel-container ${sidebar ? '' : 'large-container'}`}>
        <div className="manage-channel">
          <h1>Manage Channel</h1>
          {error ? (
            <p className="error-message">{error}</p>
          ) : videos.length > 0 ? (
            <div className="video-list">
              {videos.map(video => (
                <div key={video.videoId} className="video-item">
                  <Link to={`/watch/${video.videoId}`}>
                    <img src={video.videoThumbnail} alt={video.videoTitle} className="video-thumbnail" />
                  </Link>
                  <div className="video-details">
                    <h2 className="video-title">{video.videoTitle}</h2>
                    <p className="video-description">{video.videoDescription}</p>
                    <p className="video-date">{new Date(video.videoDate).toLocaleDateString()}</p>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <p>No videos found.</p>
          )}
        </div>
      </div>
    </>
  );
};

export default ManageChannel;
