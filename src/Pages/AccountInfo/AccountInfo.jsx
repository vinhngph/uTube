import React, { useEffect, useState } from 'react';
import { Form, Input, Typography, Spin, Alert, Button, DatePicker, message } from 'antd';
import axios from 'axios';
import { API } from '../../constants';
import './AccountInfo.css';
import Sidebar from '../../Components/Sidebar/Sidebar';

const { Title } = Typography;

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

const AccountInfo = ({sidebar}) => {
  const [accountDetails, setAccountDetails] = useState(null);
  const [error, setError] = useState(null);
  const [isEditing, setIsEditing] = useState(false);

  useEffect(() => {
    const fetchAccountDetails = async () => {
      const userId = getUserIdFromCookie();

      if (!userId) {
        setError('User ID not found in cookies');
        return;
      }

      try {
        const usersResponse = await fetch(API + '/api/accounts/users');
        const users = await usersResponse.json();
        let user = users.find((u) => u.userId === userId);

        if (!user) {
          const staffsResponse = await fetch(API + '/api/accounts/admins');
          const staffs = await staffsResponse.json();
          user = staffs.find((s) => s.userId === userId);
        }

        if (!user) {
          setError('User not found');
          return;
        }

        const detailsResponse = await fetch(API + `/api/accounts/details?user_id=${userId}`);
        if (detailsResponse.status === 404) {
          setError('User ID is wrong or something went wrong');
          return;
        }

        const details = await detailsResponse.json();
        const date = new Date(details.user_dob);

        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');

        const user_dob = `${year}-${month}-${day}`;
        const user_id = details.user_id;
        const user_fullname = details.user_fullname;

        setAccountDetails({ user_dob: user_dob, user_id: user_id, user_fullname: user_fullname, email: user.email, username: user.username });
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

  const handleSave = async () => {
    try {
      const userId = getUserIdFromCookie();
      if (!userId) {
        setError('User ID not found in cookies');
        return;
      }

      const { user_fullname, user_dob } = accountDetails;
      console.log(user_dob)

      await axios.put(API + `/api/accounts/details?user_id=${userId}&full_name=${user_fullname}&dob=${user_dob}`);

      setIsEditing(false); // Exit edit mode after successful save
      setError(null); // Clear any existing errors
      message.success('Account details updated successfully'); // Show success message
    } catch (error) {
      setError('Failed to save account details');
    }
  };

  if (error) {
    return <Alert message="Error" description={error} type="error" showIcon />;
  }

  if (!accountDetails) {
    return <Spin size="large" />;
  }

  return (
    <>
<Sidebar sidebar={sidebar} />
<div className={`account-info-container ${sidebar ? '' : 'large-container'}`}>
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
            <Input type='date' value={accountDetails.user_dob} onChange={handleChangeDOB} />
          ) : (
            <Input type='date' value={accountDetails.user_dob} readOnly />
          )}
        </Form.Item>
        <Form.Item>
          <Button type="primary" onClick={isEditing ? handleSave : handleToggleEdit}>
            {isEditing ? 'Save' : 'Edit'}
          </Button>
        </Form.Item>
      </Form>
    </div>
    
    </>
  );
};

export default AccountInfo;