import React, { useState, useEffect } from 'react';
import { Typography, Button, Form, message, Input, Progress } from 'antd';
import Dropzone from 'react-dropzone';
import { PlusOutlined } from '@ant-design/icons';
import './UploadVideo.css';

const { Title } = Typography;
const { TextArea } = Input;

const UploadVideo = () => {
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
    setVideoFile(acceptedFiles[0]);
    setVideoUrl(URL.createObjectURL(acceptedFiles[0]));
  };

  const onThumbnailDrop = (acceptedFiles) => {
    setThumbnailFile(acceptedFiles[0]);
    setThumbnailUrl(URL.createObjectURL(acceptedFiles[0]));
  };

  const onSubmit = () => {
    if (!title || !description || !videoFile || !thumbnailFile || !userId) {
      return message.error('Please fill all the fields and upload the required files');
    }

    const formData = new FormData();
    formData.append('videoFile', videoFile);
    formData.append('thumbnailFile', thumbnailFile);
    formData.append('title', title);
    formData.append('description', description);
    formData.append('userId', userId);

    const xhr = new XMLHttpRequest();
    xhr.open('POST', 'http://175.41.183.124:4000/api/upload', true);

    xhr.upload.onprogress = (event) => {
      if (event.lengthComputable) {
        const percentComplete = Math.round((event.loaded / event.total) * 100);
        setUploadProgress(percentComplete);
      }
    };

    xhr.onload = () => {
        console.log(xhr.responseText);
      if (xhr.status === 200) {
        const response = JSON.parse(xhr.responseText);
        if (response.success) {
          message.success('Video uploaded successfully');
          setUploadProgress(100);
        } else {
          message.error('Failed to upload video');
          setUploadProgress(0);
        }
      } else {
        message.error('Failed to upload video');
        setUploadProgress(0);
      }
    };

    xhr.onerror = () => {
      message.error('Failed to upload video');
      setUploadProgress(0);
    };

    xhr.send(formData);
  };

  return (
    <div className="upload-video-container">
      <div className="upload-video-header">
        <Title level={2}>Upload Video</Title>
      </div>

      <Form>
        <div className="dropzone-container">
          <div className="dropzone-section">
            <label>Video File</label>
            <Dropzone onDrop={onVideoDrop} multiple={false} maxSize={800000000}>
              {({ getRootProps, getInputProps }) => (
                <div className="dropzone" {...getRootProps()}>
                  <input {...getInputProps()} />
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

        <Button type="primary" size="large" onClick={onSubmit}>
          Submit
        </Button>
      </Form>
    </div>
  );
};

export default UploadVideo;
