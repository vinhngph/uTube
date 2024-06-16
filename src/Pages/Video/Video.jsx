import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import { useParams, useLocation } from 'react-router-dom';
import './Video.css';
import { API } from '../../constants';
import { message } from 'antd';

const Video = () => {
  const { videoId } = useParams();
  const [videoUrl, setVideoUrl] = useState('');
  const [videoInfo, setVideoInfo] = useState({ title: '', description: '', date: '', owner: '' });
  const [interaction, setInteraction] = useState({ views: 0, likes: 0, dislikes: 0 });
  const [error, setError] = useState('');
  const videoRef = useRef(null);
  const location = useLocation();

  useEffect(() => {
    fetchVideoInfoAndOwner();
    handleView();

    const video = document.querySelector('video');
    postHistory(video.currentTime);

    return () => {
      postHistory(video.currentTime);
    }

  }, [videoId, location]);

  const postHistory = (trackTime) => {
    const userId = getUserIdFromCookie();

    if (!userId) {
      return;
    }

    axios.post(API + '/api/user/history', null, { params: { user_id: userId, video_id: videoId, track_time: trackTime } })
      .then((response) => {
        if (response.status === 200) {
          if (response.data.trackTime > 0) {
            videoRef.current.currentTime = response.data.trackTime;
            videoRef.current.play();
          }
        }
      })
      .catch(error => {
        console.error('Error updating user history:', error);
      });
  }

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

  const fetchVideoInfoAndOwner = () => {
    axios.get(API + `/api/video/information`, { params: { id: videoId } })
      .then(response => {
        if (response.status === 200) {
          let timeFromAPI = response.data.videoDate;
          let time = new Date(timeFromAPI).toLocaleString();

          axios.get(API + `/api/accounts/details`, { params: { user_id: response.data.videoOwner } })
            .then(ownerResponse => {
              setVideoInfo({
                title: response.data.videoTitle,
                description: response.data.videoDescription,
                date: time,
                owner: ownerResponse.data.user_fullname,
              });
            })
            .catch(error => {
              setError('Error fetching video owner information.');
              console.error('Error fetching video owner information:', error);
            });
        } else {
          setError('Video is not available.');
        }
      })
      .catch(error => {
        if (error.response && error.response.status === 404) {
          setError('Video not found.');
        } else if (error.response && error.response.status === 500) {
          setError('Server error. Please try again later.');
        }
        console.error('Error fetching video information:', error);
      });
  };

  const fetchInteractions = () => {
    axios.get(API + `/api/video/interaction`, { params: { id: videoId } })
      .then(response => {
        setInteraction({
          views: response.data.view,
          likes: response.data.like,
          dislikes: response.data.dislike,
        });
      })
      .catch(error => {
        console.error('Error fetching video interactions:', error);
      });
  };

  const handleView = () => {
    axios.put(API + `/api/video/view?id=${videoId}`)
      .then(() => {
        fetchInteractions();
      })
      .catch(error => {
        console.error('Error viewing video:', error);
      });
  };

  const handleLike = () => {
    const userId = getUserIdFromCookie();
    axios.put(API + `/api/video/like`, null, { params: { user_id: userId, video_id: videoId } })
      .then(() => {
        fetchInteractions();
      })
      .catch(error => {
        console.error('Error liking video:', error);
      });
  };

  const handleDislike = () => {
    const userId = getUserIdFromCookie();
    axios.put(API + `/api/video/dislike`, null, { params: { user_id: userId, video_id: videoId } })
      .then(() => {
        fetchInteractions();
      })
      .catch(error => {
        console.error('Error disliking video:', error);
      });
  };

  return (
    <div className="video-container">
      {error && <p className="error">{error}</p>}
      {!error && (
        <>
          <div className="video-layout">
            <div className="video-column">
              <div className="video-wrapper">
                <video ref={videoRef}  controls className="video-player" autoPlay>
                <source src={API + `/api/video?id=${videoId}`} type="video/webm"/>
                </video>
              </div>
            </div>
            <div className="info-column">
              <h1 className="video-title">{videoInfo.title}</h1>
              <div className="interaction-buttons">
                <button className="like-button" onClick={handleLike}>
                  Like {interaction.likes}
                </button>
                <button className="dislike-button" onClick={handleDislike}>
                  Dislike {interaction.dislikes}
                </button>
              </div>
              <p className="video-date">Posted on: {videoInfo.date}</p>
              <p className="video-owner">Channel: {videoInfo.owner}</p>
              <p className="video-description">{videoInfo.description}</p>
            </div>
          </div>
          <div className="video-views">
            Views: {interaction.views}
          </div>
        </>
      )}
    </div>
  );
};

export default Video;