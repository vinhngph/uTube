import React, { useState } from 'react';
import './ForgotPassword.css';
import axios from 'axios';
import { API } from '../../constants';
import { useNavigate } from 'react-router-dom';
import { Form, Input, Button, Typography, message } from 'antd';

const { Title } = Typography;

const ForgotPassword = () => {
  const [email, setEmail] = useState('');
  const [stage, setStage] = useState(1); // 1 for email input, 2 for OTP input
  const [otp, setOtp] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const navigate = useNavigate();

  const handleEmailSubmit = async () => {
    try {
      const response = await axios.post(API + `/api/accounts/forgot-password?email=${email}`);

      if (response.status === 200) {
        setStage(2); // Move to OTP input stage
        message.success('OTP sent to your email.');
      }
      
    } catch (error) {
      if (error.response) {
        if (error.response.status === 404) {
          message.error('Email not found');
        } else if (error.response.status === 500) {
          message.error('Server error. Please try again later.');
        } else {
          message.error('Failed to send OTP. Please check your email and try again.');
        }
      } else {
        message.error('Failed to send OTP. Please check your email and try again.');
      }
    }
  };

  const handleOTPSubmit = async () => {
    try {
      const response = await axios.put(API + `/api/accounts/forgot-password?email=${email}&otp=${otp}&new_password=${newPassword}`);

      if (response.status === 200) {
        // Password reset successful
        message.success('Password reset successful. Redirecting to login...');
        navigate('/login'); // Redirect to login page
      }
      
    } catch (error) {
      if (error.response && error.response.status === 400) {
        message.error('Invalid OTP. Please try again.');
      } else {
        message.error('Invalid OTP or email not found. Please try again.');
      }
    }
  };

  return (
    <div className="forgot-password-container">
      {stage === 1 ? (
        <div className="forgot-password-form">
          <Title level={2}>Forgot Password</Title>
          <Form onFinish={handleEmailSubmit}>
            <Form.Item
              label="Email"
              name="email"
              rules={[{ required: true, message: 'Please input your email!' }]}
            >
              <Input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit" block>
                Send OTP
              </Button>
            </Form.Item>
          </Form>
        </div>
      ) : (
        <div className="forgot-password-form">
          <Title level={2}>Enter OTP</Title>
          <Form onFinish={handleOTPSubmit}>
            <Form.Item
              label="OTP"
              name="otp"
              rules={[{ required: true, message: 'Please input the OTP!' }]}
            >
              <Input value={otp} onChange={(e) => setOtp(e.target.value)} />
            </Form.Item>
            <Form.Item
              label="New Password"
              name="newPassword"
              rules={[{ required: true, message: 'Please input your new password!' }]}
            >
              <Input.Password value={newPassword} onChange={(e) => setNewPassword(e.target.value)} />
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit" block>
                Submit OTP
              </Button>
            </Form.Item>
          </Form>
        </div>
      )}
    </div>
  );
};

export default ForgotPassword;
