import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { API } from '../../constants';
import { ToastContainer, toast } from 'react-toastify';
import Modal from 'react-modal';
import 'react-toastify/dist/ReactToastify.css';
import './AdminPage.css'; // Import the CSS file
import Sidebar from '../../Components/Sidebar/Sidebar'; // Import Sidebar component

Modal.setAppElement('#root'); // Specify the root element for accessibility

const AdminPage = ({sidebar}) => {
  const [videos, setVideos] = useState([]);
  const [error, setError] = useState('');
  const [selectedVideo, setSelectedVideo] = useState(null);

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
    const userId = getUserIdFromCookie();
    axios.get(API + `/api/admin/review?user_id=${userId}`)
      .then(response => {
        setVideos(response.data || []);
      })
      .catch(error => {
        console.error('Error fetching videos:', error);
        setError('Error fetching videos');
      });
  }, []);

  const handleAccept = (videoId) => {
    const userId = getUserIdFromCookie();
    axios.put(API + `/api/admin/review?user_id=${userId}&video_id=${videoId}`)
      .then(response => {
        if (response.status === 200) {
          setVideos(prevVideos => prevVideos.filter(video => video.videoId !== videoId));
          toast.success('Video accepted successfully');
        } else if (response.status === 401) {
          setError('Invalid role to modify');
          toast.error('Invalid role to modify');
        } else if (response.status === 404) {
          setError('Cannot find this video');
          toast.error('Cannot find this video');
        }
      })
      .catch(error => {
        console.error('Error accepting video:', error);
        setError('Error accepting video');
        toast.error('Error accepting video');
      });
  };

  const handleDelete = (videoId) => {
    const userId = getUserIdFromCookie();
    axios.delete(API + `/api/admin/review?user_id=${userId}&video_id=${videoId}`)
      .then(response => {
        if (response.status === 200) {
          setVideos(prevVideos => prevVideos.filter(video => video.videoId !== videoId));
          toast.success('Video deleted successfully');
        } else if (response.status === 401) {
          setError('Invalid role to delete');
          toast.error('Invalid role to delete');
        } else if (response.status === 404) {
          setError('Cannot find this video');
          toast.error('Cannot find this video');
        }
      })
      .catch(error => {
        console.error('Error deleting video:', error);
        setError('Error deleting video');
        toast.error('Error deleting video');
      });
  };

  const openModal = (video) => {
    setSelectedVideo(video);
  };

  const closeModal = () => {
    setSelectedVideo(null);
  };

  return (
    <>
<Sidebar sidebar={sidebar} />
<div className={`admin-page ${sidebar ? '' : 'large-container'}`}>
      <ToastContainer />
      <h1>Admin Page</h1>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <ul className="video-list">
        {videos.map(video => (
          <li key={video.videoId} className="video-item">
            <div onClick={() => openModal(video)}>
              <img src={API + `/api/video/thumbnail?id=${video.videoId}`} alt={video.videoTitle} />
            </div>
            <div className="video-details">
              <h2 className="video-title">{video.videoTitle}</h2>
              <p className="video-description">{video.videoDescription}</p>
              <p className="video-meta">{new Date(video.videoDate).toLocaleString()}</p>
              <p className="video-owner">Owner: {video.videoChannelName}</p>
              <p className="video-meta">Status: {video.videoStatus ? 'Accepted' : 'Pending'}</p>
              <div className="button-group">
                <button className="button-accept" onClick={() => handleAccept(video.videoId)}>Accept</button>
                <button className="button-delete" onClick={() => handleDelete(video.videoId)}>Delete</button>
              </div>
            </div>
          </li>
        ))}
      </ul>

      {selectedVideo && (
        <Modal
          isOpen={!!selectedVideo}
          onRequestClose={closeModal}
          className="react-modal-content"
          overlayClassName="react-modal-overlay"
        >
          <h2>{selectedVideo.videoTitle}</h2>
          <p>{selectedVideo.videoDescription}</p>
          <p>{new Date(selectedVideo.videoDate).toLocaleString()}</p>
          <p>Owner: {selectedVideo.videoOwner}</p>
          <p>Status: {selectedVideo.videoStatus ? 'Accepted' : 'Pending'}</p>
          <video width="100%" controls>
            <source src={API + `/api/video?id=${selectedVideo.videoId}`} type="video/webm" />
            Your browser does not support the video tag.
          </video>
          <div className="button-group">
            <button className="button-accept" onClick={() => { handleAccept(selectedVideo.videoId); closeModal(); }}>Accept</button>
            <button className="button-delete" onClick={() => { handleDelete(selectedVideo.videoId); closeModal(); }}>Delete</button>
          </div>
        </Modal>
      )}
    </div>
    </>
    
  );
};

export default AdminPage;
