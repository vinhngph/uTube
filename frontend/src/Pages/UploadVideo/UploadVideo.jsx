import React, { useState, useEffect } from 'react';
import { Typography, Button, Form, message, Input, Progress } from 'antd';
import Dropzone from 'react-dropzone';
import { PlusOutlined } from '@ant-design/icons';
import './UploadVideo.css';
import { API } from '../../constants';
import Sidebar from '../../Components/Sidebar/Sidebar';
import axios from 'axios';

const { Title } = Typography;
const { TextArea } = Input;

const CHUNK_SIZE = 50 * 1024 * 1024; // 50MB

const UploadVideo = ({ sidebar }) => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [videoFile, setVideoFile] = useState(null);
  const [thumbnailFile, setThumbnailFile] = useState(null);
  const [userId, setUserId] = useState(null);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [videoUrl, setVideoUrl] = useState('');
  const [thumbnailUrl, setThumbnailUrl] = useState('');

  useEffect(() => {
    const cookies = document.cookie.split(';').reduce((acc, cookie) => {
      const [name, value] = cookie.split('=').map(c => c.trim());
      acc[name] = decodeURIComponent(value);
      return acc;
    }, {});
    if (cookies.user) {
      const user = JSON.parse(cookies.user);
      setUserId(user.userId);
    }
  }, []);

  const handleChangeTitle = (event) => {
    setTitle(event.currentTarget.value);
  };

  const handleChangeDescription = (event) => {
    setDescription(event.currentTarget.value);
  };

  const onVideoDrop = (acceptedFiles) => {
    const file = acceptedFiles[0];
    if (file && file.type.startsWith('video/')) {
      setVideoFile(file);
      setVideoUrl(URL.createObjectURL(file));
    } else {
      message.error('Please upload a valid video file');
    }
  };

  const onThumbnailDrop = (acceptedFiles) => {
    const file = acceptedFiles[0];
    if (file && file.type.startsWith('image/')) {
      setThumbnailFile(file);
      setThumbnailUrl(URL.createObjectURL(file));
    } else {
      message.error('Please upload a valid image file');
    }
  };

  const uploadChunk = async (chunk, videoId, extension, index, totalChunks, type) => {
    const formData = new FormData();
    formData.append('chunk', chunk);
    formData.append('videoId', videoId);
    formData.append('ext', extension)
    formData.append('index', index);
    formData.append('totalChunks', totalChunks);
    formData.append('type', type);

    try {
      await axios.put(API + '/api/video/upload', formData)
        .then(response => {
          if (response.status === 201) {
            if (type === "video") {
              message.success('Video uploaded successfully');
            } else {
              message.success('Thumbnail uploaded successfully');
            }
          }
        })
    } catch (error) {
      console.error('Error uploading chunk:', error);
    }
  };

  const handleUpload = async () => {
    if (!videoFile) return;

    let videoId = '';
    const videoIdForm = new FormData();
    videoIdForm.append('title', title);
    videoIdForm.append('description', description);
    videoIdForm.append('userId', userId);

    await fetch(API + '/api/video/upload', {
      method: 'POST',
      body: videoIdForm
    })
      .then(response => response.json())
      .then(data => {
        videoId = data.videoId;
      })

    const totalVideoChunks = Math.ceil(videoFile.size / CHUNK_SIZE);
    const totalThumbnailChunks = Math.ceil(thumbnailFile.size / CHUNK_SIZE);

    for (let i = 0; i < totalVideoChunks; i++) {
      const start = i * CHUNK_SIZE;
      const end = Math.min(start + CHUNK_SIZE, videoFile.size);
      const chunk = videoFile.slice(start, end);
      const name = videoFile.name;
      const extension = name.substring(name.lastIndexOf('.') + 1);

      await uploadChunk(chunk, videoId, extension, i, totalVideoChunks, "video");
      setUploadProgress(Math.round(((i + 1) / totalVideoChunks) * 100));
    }

    for (let i = 0; i < totalThumbnailChunks; i++) {
      const start = i * CHUNK_SIZE;
      const end = Math.min(start + CHUNK_SIZE, thumbnailFile.size);
      const chunk = thumbnailFile.slice(start, end);
      const name = thumbnailFile.name;
      const extension = name.substring(name.lastIndexOf('.') + 1);

      await uploadChunk(chunk, videoId, extension, i, totalThumbnailChunks, "thumbnail");
    }
  };

  const onSubmit = () => {
    if (!title || !description || !videoFile || !thumbnailFile || !userId) {
      return message.error('Please fill all the fields and upload the required files');
    }

    handleUpload();
  };

  return (
    <>
      <Sidebar sidebar={sidebar} />
      <div className={`upload-video-container ${sidebar ? '' : 'large-container'}`}>
        <div className="upload-video-header">
          <Title level={2}>Upload Video </Title>
        </div>

        <Form>
          <div className="dropzone-container">
            <div className="dropzone-section">
              <label>Video File (Allow video type)</label>
              <Dropzone onDrop={onVideoDrop} multiple={false}>
                {({ getRootProps, getInputProps }) => (
                  <div className="dropzone" {...getRootProps()}>
                    <input {...getInputProps()} accept="video/*" />
                    {videoFile ? (
                      <video width="100%" height="240px" controls>
                        <source src={videoUrl} type="video/mp4" />
                      </video>
                    ) : (
                      <PlusOutlined style={{ fontSize: '3rem' }} />
                    )}
                  </div>
                )}
              </Dropzone>
            </div>
            <div className="dropzone-section">
              <label>Thumbnail File</label>
              <Dropzone onDrop={onThumbnailDrop} multiple={false} maxSize={800000000}>
                {({ getRootProps, getInputProps }) => (
                  <div className="dropzone" {...getRootProps()}>
                    <input {...getInputProps()} />
                    {thumbnailFile ? (
                      <img src={thumbnailUrl} alt="Thumbnail" width="100%" height="240px" />
                    ) : (
                      <PlusOutlined style={{ fontSize: '3rem' }} />
                    )}
                  </div>
                )}
              </Dropzone>
            </div>
          </div>

          {uploadProgress > 0 && <Progress percent={uploadProgress} />}

          <label>Title</label>
          <Input onChange={handleChangeTitle} value={title} />
          <label>Description</label>
          <TextArea onChange={handleChangeDescription} value={description} />

          <Button type="primary" size="large" onClick={onSubmit} className='submit-button'>
            Submit
          </Button>
        </Form>
      </div>
    </>
  );
};

export default UploadVideo;
