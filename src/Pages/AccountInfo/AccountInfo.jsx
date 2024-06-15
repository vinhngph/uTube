import React, { useEffect, useState } from 'react';
import { Form, Input, Typography, Spin, Alert, Button } from 'antd';
import { API } from '../../constants';
import './AccountInfo.css';

const { Title } = Typography;

// Function to get a cookie value by name
const getCookie = (name) => {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(';').shift();
  return null;
};

// Function to get the user ID from the user cookie
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

const AccountInfo = () => {
  const [accountDetails, setAccountDetails] = useState(null);
  const [error, setError] = useState(null);
  const [isEditing, setIsEditing] = useState(false); // State to manage edit mode

  useEffect(() => {
    const fetchAccountDetails = async () => {
      const userId = getUserIdFromCookie();

      if (!userId) {
        setError('User ID not found in cookies');
        return;
      }

      let user = null;
      let userRole = null;
      let email = null;

      try {
        // Fetch all users and staff to find the current user's details
        const usersResponse = await fetch(API + '/api/accounts/users');
        const users = await usersResponse.json();
        user = users.find((u) => u.userId === userId);

        if (!user) {
          const staffsResponse = await fetch(API + '/api/accounts/staffs');
          const staffs = await staffsResponse.json();
          user = staffs.find((s) => s.userId === userId);
        }

        if (user) {
          userRole = user.role;
          email = user.email;
        } else {
          setError('User not found');
          return;
        }

        // Fetch account details using the userId
        const detailsResponse = await fetch(API + `/api/accounts/details?user_id=${userId}`);
        if (detailsResponse.status === 404) {
          setError('User ID is wrong or something went wrong');
          return;
        }
        const details = await detailsResponse.json();
        setAccountDetails({ ...details, email, username: user.username });
      } catch (error) {
        setError('Failed to fetch account details');
      }
    };

    fetchAccountDetails();
  }, []);

  const handleToggleEdit = () => {
    setIsEditing(!isEditing);
  };

  const handleChangeFullName = (e) => {
    setAccountDetails({ ...accountDetails, user_fullname: e.target.value });
  };

  const handleChangeDOB = (e) => {
    setAccountDetails({ ...accountDetails, user_dob: e.target.value });
  };

  if (error) {
    return <Alert message="Error" description={error} type="error" showIcon />;
  }

  if (!accountDetails) {
    return <Spin size="large" />;
  }

  return (
    <div className="account-info-container">
      <Title level={2} className="account-info-title">Account Details</Title>
      <Form layout="vertical" className="account-info-form">
        <Form.Item label="User ID">
          <Input value={accountDetails.user_id} readOnly />
        </Form.Item>
        <Form.Item label="Email">
          <Input value={accountDetails.email} readOnly />
        </Form.Item>
        <Form.Item label="Username">
          <Input value={accountDetails.username} readOnly />
        </Form.Item>
        <Form.Item label="Full Name">
          {isEditing ? (
            <Input value={accountDetails.user_fullname} onChange={handleChangeFullName} />
          ) : (
            <Input value={accountDetails.user_fullname} readOnly />
          )}
        </Form.Item>
        <Form.Item label="Date of Birth">
          {isEditing ? (
            <Input value={accountDetails.user_dob} onChange={handleChangeDOB} />
          ) : (
            <Input value={accountDetails.user_dob} readOnly />
          )}
        </Form.Item>
        <Form.Item>
          <Button type="primary" onClick={handleToggleEdit}>
            {isEditing ? 'Save' : 'Edit'}
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
};

export default AccountInfo;
