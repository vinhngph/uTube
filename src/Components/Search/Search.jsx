import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { API } from '../../constants';
import { Link } from 'react-router-dom';
import { Typography, Row, Col, Card, Empty } from 'antd';
import './Search.css';

const { Title, Paragraph } = Typography;

const Search = () => {
  const [videos, setVideos] = useState([]);
  const [message, setMessage] = useState('');

  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const key = searchParams.get('key');

  useEffect(() => {
    fetchSearchResults();
  }, [key]);

  const fetchSearchResults = async () => {
    try {
      if (key) {
        const response = await fetch(API + `/api/home/search/results?key=${key}`);
        const data = await response.json();
        if (data.length > 0) {
          setVideos(data);
          setMessage('');
        } else {
          setMessage('No search results found.');
          setVideos([]);
        }
      }
    } catch (error) {
      setMessage('An error occurred while fetching the search results.');
      setVideos([]);
    }
  };

  return (
    <div className='search-page'>
      <Title level={2}>Search Results for "{key || ''}"</Title>
      {message ? (
        <Paragraph>{message}</Paragraph>
      ) : (
        <div className='video-list'>
          {videos.length > 0 ? (
            videos.map((video) => (
              <Card key={video.videoId} className="video-card" hoverable>
                <Link to={`/watch/${video.videoId}`} className="thumbnail-container">
                  <img alt={video.videoTitle} src={video.videoThumbnail} className="card-img-top" />
                </Link>
                <div className="card-body">
                  <Link to={`/watch/${video.videoId}`}>
                    <h5 className="card-title">{video.videoTitle}</h5>
                  </Link>
                  <p className="card-text">{video.videoDescription}</p>
                  <div className="card-details">
                    <span className="views">{video.views} views</span>
                  </div>
                </div>
              </Card>
            ))
          ) : (
            <Empty description="No search results found." />
          )}
        </div>
      )}
    </div>
  );
};

export default Search;
