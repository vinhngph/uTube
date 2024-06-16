import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { API } from '../../constants';
import { Link } from 'react-router-dom';
import Sidebar from '../../Components/Sidebar/Sidebar'; // Import Sidebar component
import './History.css';

const getUserIdFromCookie = () => {
  const userCookie = document.cookie.split('; ').find(row => row.startsWith('user='));
  if (userCookie) {
    try {
      const userData = JSON.parse(userCookie.split('=')[1]);
      return userData.userId;
    } catch (error) {
      console.error('Failed to parse user cookie', error);
      return null;
    }
  }
  return null;
};

const History = ({ sidebar }) => {
  const [watchedVideos, setWatchedVideos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchHistory();
  }, []);

  const fetchHistory = async () => {
    const userId = getUserIdFromCookie();
    if (!userId) {
      setError('User ID not found in cookie');
      setLoading(false);
      return;
    }

    try {
      const response = await axios.get(API + `/api/user/history?user_id=${userId}`);
      setWatchedVideos(response.data || []);
      setLoading(false);
    } catch (error) {
      if (error.response) {
        if (error.response.status === 400) {
          setError('Missing parameter');
        } else if (error.response.status === 404) {
          setError('User ID not found');
        } else {
          setError('Failed to fetch data');
        }
      } else {
        setError('Network error');
      }
      setLoading(false);
    }
  };

  const handleDelete = async (videoId) => {
    try {
      const userId = getUserIdFromCookie();
      if (!userId) {
        setError('User ID not found in cookie');
        return;
      }

      await axios.delete(API + `/api/user/history/${videoId}?user_id=${userId}`);
      fetchHistory();
    } catch (error) {
      console.error('Failed to delete video:', error);
    }
  };

  if (loading) return <div className="loader"></div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <>
      <Sidebar sidebar={sidebar} />
      <div className={`history-container ${sidebar ? '' : 'large-container'}`}>
        <h1 className="history-title">Watch History</h1>
        <div className="video-list">
          {watchedVideos.map((video) => (
            <div key={video.videoId} className="video-card">
              <Link to={`/watch/${video.videoId}`} className="thumbnail-container">
                <img src={video.videoThumbnail} alt={video.videoTitle} className="card-img-top" />
              </Link>
              <div className="card-body">
                <h5 className="card-title">{video.videoTitle}</h5>
                <p className="card-text">{video.videoDescription}</p>
                <div className="card-details">
                  <span className="views">{video.views} views</span>
                  <span className="timestamp">{video.timestamp}</span>
                </div>
                <button onClick={() => handleDelete(video.videoId)} className="delete-button">
                  Remove from history
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </>
  );
};

export default History;
