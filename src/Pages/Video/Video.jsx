import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import './Video.css';
import { API } from '../../constants';

const Video = () => {
  const { videoId } = useParams();
  const [videoUrl, setVideoUrl] = useState('');
  const [videoInfo, setVideoInfo] = useState({ title: '', description: '', date: '', owner: '' });
  const [interaction, setInteraction] = useState({ views: 0, likes: 0, dislikes: 0 });
  const [error, setError] = useState('');

  useEffect(() => {
    // Fetch video source URL
    axios.get(API + `/api/video`, { params: { id: videoId } })
      .then(response => {
        setVideoUrl(API + `/api/video?id=${videoId}`);
        setError('');
      })
      .catch(error => {
        setError('Error fetching video. Please try again later.');
        console.error('Error fetching video:', error);
      });

    // Fetch video information and owner details
    axios.get(API + `/api/video/information`, { params: { id: videoId } })
      .then(response => {
        if (response.status === 200) {
          let timeFromAPI = response.data.videoDate;
          let time = new Date(timeFromAPI).toLocaleString();

          // Fetch video owner details
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

    // Fetch video interactions
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
  }, [videoId]);

  return (
    <div className="video-container">
      {error && <p className="error">{error}</p>}
      {!error && (
        <>
          <div className="video-layout">
            <div className="video-column">
              <div className="video-wrapper">
                <video src={videoUrl} controls className="video-player" />
              </div>
            </div>
            <div className="info-column">
              <h1 className="video-title">{videoInfo.title}</h1>
              <div className="interaction-buttons">
                <button className="like-button">Like {interaction.likes}</button>
                <button className="dislike-button">Dislike {interaction.dislikes}</button>
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
