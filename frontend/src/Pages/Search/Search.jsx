import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';
import { API } from '../../constants';
import { Link } from 'react-router-dom';
import Sidebar from '../../Components/Sidebar/Sidebar'; // Import Sidebar component
import { Typography, Card, Empty } from 'antd';
import './Search.css';

const { Title, Paragraph } = Typography;

const Search = ({ sidebar }) => {
  const [videos, setVideos] = useState([]);
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(true);

  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const key = searchParams.get('key');

  useEffect(() => {
    fetchSearchResults();
  }, [key]);

  const truncateString = (str) =>  {
    const maxLength = 20;
    if (str.length <= maxLength) {
        return str;
    }
    return str.slice(0, maxLength - 3) + '...';
}

  const fetchSearchResults = async () => {
    setLoading(true); // Set loading state when fetching data

    try {
      if (key) {
        const encodedKey = encodeURIComponent(key);
        const response = await axios.get(API + `/api/home/search/results?key=${encodedKey}`);
        const data = response.data;

        if (data.length > 0) {
          setVideos(data);
          setMessage('');
        } else {
          setMessage('No search results found.');
          setVideos([]);
        }
      } else {
        setMessage('No search query specified.');
        setVideos([]);
      }
    } catch (error) {
      setMessage('An error occurred while fetching the search results.');
      setVideos([]);
    } finally {
      setLoading(false); // Always set loading state to false after fetching
    }
  };

  return (
    <>
      <Sidebar sidebar={sidebar} />
      <div className={`search-page ${sidebar ? '' : 'large-container'}`}>
        <Title level={2}>Search Results for "{key || ''}"</Title>
        {loading ? (
          <div className="loader"></div>
        ) : message ? (
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
                      <h5 className="card-title">{truncateString(video.videoTitle)}</h5>
                    </Link>
                    <h3 className="card-text">{video.videoChannelName}</h3>
                    <div className="card-details">
                      <span className="views">{video.videoViews} views</span>
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
    </>
  );
};

export default Search;
