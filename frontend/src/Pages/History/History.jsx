import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { API } from '../../constants';
import { Link } from 'react-router-dom';
import { Table, Space, Spin, message } from 'antd'; // Import Table and other necessary components from Ant Design
import Sidebar from '../../Components/Sidebar/Sidebar'; // Import Sidebar component
import './History.css';


const History = ({ sidebar }) => {
  const [watchedVideos, setWatchedVideos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchHistory();
  }, []);

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

      await axios.delete(API + `/api/user/history?video_id=${videoId}&user_id=${userId}`);
      fetchHistory();
    } catch (error) {
      console.error('Failed to delete video:', error);
    }
  };

  const formatTime = (timeInSeconds) => {
    const minutes = Math.floor(timeInSeconds / 60);
    const seconds = timeInSeconds % 60;
    return `${minutes}:${seconds.toFixed(0).padStart(2, '0')}`;
  };

  const columns = [
    {
      title: 'Thumbnail',
      dataIndex: 'videoThumbnail',
      key: 'thumbnail',
      render: (thumbnail, record) => (
        <Link to={`/watch/${record.videoId}`} className="thumbnail-container">
          <img src={thumbnail} alt={record.videoTitle} className="card-img-top" />
        </Link>
      ),
    },
    {
      title: 'Title',
      dataIndex: 'videoTitle',
      key: 'title',
      render: (title) => <span>{title}</span>,
    },
    {
      title: 'Description',
      dataIndex: 'videoDescription',
      key: 'description',
      render: (description) => <span>{description}</span>,
    },
    {
      title: 'Watched',
      dataIndex: 'trackTime',
      key: 'trackTime',
      render: (trackTime) => <span>{formatTime(trackTime)}</span>,
    },
    {
      title: 'Action',
      key: 'action',
      render: (text, record) => (
        <Space size="middle">
          <button onClick={() => handleDelete(record.videoId)} className="delete-button">
            Remove
          </button>
        </Space>
      ),
    },
  ];

  if (loading) return <Spin size="large" />; // Use Ant Design's Spin component for loading indicator
  if (error) return <div>Error: {error}</div>;

  return (

    <>
      <Sidebar sidebar={sidebar} />
      <div className={`history-container ${sidebar ? '' : 'large-container'}`}>
        <h1 className="history-title">Watch History</h1>
        <Table
          dataSource={watchedVideos}
          columns={columns}
          rowKey="videoId"
          pagination={{ pageSize: 5 }} // Example pagination settings
        />
      </div>
    </>
  );
};

export default History;
