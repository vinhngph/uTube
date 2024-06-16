import React, { useEffect, useState } from 'react';
import './Feed.css';
import { useNavigate } from 'react-router-dom';
import { API } from '../../constants';

const Feed = () => {
  const [videos, setVideos] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    fetch(API + '/api/home/videos')
      .then(response => response.json())
      .then(data => {
        const videos = data;
        setVideos(videos);
      })
      .catch(error => console.error('Error fetching videos:', error));
  }, []);

  const handleCardClick = (videoId) => {
    navigate(`/watch/${videoId}`);
  };

  const truncateString = (str) =>  {
    const maxLength = 50;
    if (str.length <= maxLength) {
        return str;
    }
    return str.slice(0, maxLength - 3) + '...';
}

  return (
    <div className="feed">
      {videos.map(video => (
        <div key={video.videoId} className="card" onClick={() => handleCardClick(video.videoId)}>
          <img src={video.videoThumbnail} alt={video.videoTitle} className="thumbnail" />
          <div className="video-info">
            <h2 className="video-title">{truncateString(video.videoTitle)}</h2>
            <p className="video-channel">{video.videoChannelName}</p>
            <p className="video-views">{video.videoViews} views</p>
            <p className="video-date">{new Date(video.videoDate).toLocaleDateString()}</p>
          </div>
        </div>
      ))}
    </div>
  );
};

export default Feed;
