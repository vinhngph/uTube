import React, { useState } from 'react';
import './Register.css';
import { useLocation, useNavigate } from 'react-router-dom';
import { API } from '../../constants';
import { Form, Input, Button, DatePicker, message } from 'antd';
import moment from 'moment';

const Register = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const isRegisterPage = location.pathname === '/register';
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState({
    email: '',
    username: '',
    password: '',
    fullName: '',
    dob: ''
  });
  const [error, setError] = useState('');
  const [emailStatus, setEmailStatus] = useState(''); // 'exists', 'available', ''
  const [usernameStatus, setUsernameStatus] = useState(''); // 'exists', 'available', ''

  const handleEmailChange = async (e) => {
    const { value } = e.target;
    setFormData({
      ...formData,
      email: value
    });
    if (value) {
      await checkAvailability('email', value);
    } else {
      setEmailStatus('');
    }
  };

  const handleUsernameChange = async (e) => {
    const { value } = e.target;
    setFormData({
      ...formData,
      username: value
    });
    if (value) {
      await checkAvailability('username', value);
    } else {
      setUsernameStatus('');
    }
  };

  const checkAvailability = async (type, value) => {
    const url = `${API}/api/accounts/isUser/${type}?${type}=${value}`;
    try {
      const response = await fetch(url, { method: 'GET' });
      if (response.status === 409) {
        if (type === 'email') {
          setEmailStatus('exists');
        } else if (type === 'username') {
          setUsernameStatus('exists');
        }
      } else if (response.status === 200) {
        if (type === 'email') {
          setEmailStatus('available');
        } else if (type === 'username') {
          setUsernameStatus('available');
        }
      }
    } catch (error) {
      console.error('Error checking availability:', error);
    }
  };

  const nextStep = () => {
    setStep(step + 1);
  };

  const prevStep = () => {
    setStep(step - 1);
  };

  const handleSubmit = async () => {
    if (step === 1) {
      if (emailStatus === 'available' && usernameStatus === 'available') {
        nextStep();
      } else {
        setError('Please fix the errors before proceeding');
      }
    } else {
      try {
        const formDataToSend = new FormData();
        formDataToSend.append('email', formData.email);
        formDataToSend.append('username', formData.username);
        formDataToSend.append('password', formData.password);
        formDataToSend.append('fullName', formData.fullName);
        formDataToSend.append('dob', moment(formData.dob).format('YYYY-MM-DD'));

        const response = await fetch(`${API}/register`, {
          method: 'POST',
          body: formDataToSend,
          credentials: 'include'
        });

        if (!response.ok) {
          throw new Error('Registration failed');
        }

        const data = await response.json();
        console.log('Registration Successful', data);
        message.success('Registration successful');
        navigate('/login');
      } catch (error) {
        console.error('Registration Error:', error.message);
        setError('Registration failed. Please try again.');
      }
    }
  };

  return (
    <>
      {isRegisterPage && (
        <div className={`container youtube-theme ${isRegisterPage ? 'login-background' : ''}`}>
          <div className="wrapper">
            <Form onFinish={handleSubmit}>
              {step === 1 && (
                <div id="step1">
                  <h2>Account Information</h2>
                  <Form.Item
                    label="Email:"
                    validateStatus={emailStatus === 'exists' ? 'error' : emailStatus === 'available' ? 'success' : ''}
                    help={emailStatus === 'exists' ? 'Email already taken' : ''}
                  >
                    <Input
                      type="email"
                      id="email"
                      name="email"
                      value={formData.email}
                      onChange={handleEmailChange}
                      required
                      className={`${
                        !formData.email || emailStatus === 'exists' ? 'input-black' : 'input-white'
                      } ${emailStatus === 'exists' ? 'ant-input-error' : emailStatus === 'available' ? 'ant-input-success' : ''}`}
                      suffix={
                        emailStatus === 'exists' ? (
                          <span className="input-suffix">{'Email already taken'}</span>
                        ) : null
                      }
                    />
                  </Form.Item>

                  <Form.Item
                    label="Username:"
                    validateStatus={usernameStatus === 'exists' ? 'error' : usernameStatus === 'available' ? 'success' : ''}
                    help={usernameStatus === 'exists' ? 'Username already taken' : ''}
                  >
                    <Input
                      type="text"
                      id="username"
                      name="username"
                      value={formData.username}
                      onChange={handleUsernameChange}
                      required
                      className={`${
                        !formData.username || usernameStatus === 'exists' ? 'input-black' : 'input-white'
                      } ${usernameStatus === 'exists' ? 'ant-input-error' : usernameStatus === 'available' ? 'ant-input-success' : ''}`}
                      suffix={
                        usernameStatus === 'exists' ? (
                          <span className="input-suffix">{'Username already taken'}</span>
                        ) : null
                      }
                    />
                  </Form.Item>

                  <Form.Item label="Password:">
                    <Input.Password
                      id="password"
                      name="password"
                      value={formData.password}
                      onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                      required
                    />
                  </Form.Item>

                  <Form.Item>
                    <Button type="primary" htmlType="submit">
                      Next
                    </Button>
                    {error && <p className="error">{error}</p>}
                  </Form.Item>
                </div>
              )}

              {step === 2 && (
                <div id="step2">
                  <h2>Personal Information</h2>
                  <Form.Item label="Full Name:">
                    <Input
                      type="text"
                      id="fullname"
                      name="fullName"
                      value={formData.fullName}
                      onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                      required
                    />
                  </Form.Item>

                  <Form.Item label="Date of Birth:">
                    <DatePicker
                      id="dob"
                      name="dob"
                      value={formData.dob ? moment(formData.dob, 'YYYY-MM-DD') : null}
                      onChange={(date, dateString) => setFormData({ ...formData, dob: dateString })}
                      required
                    />
                  </Form.Item>

                  <Form.Item className="custom-button-item">
  <Button type="default" onClick={prevStep} className="custom-back-button">
    Back
  </Button>
</Form.Item>
<Form.Item className="custom-button-item">
  <Button type="primary" htmlType="submit" className="custom-complete-button">
    Complete
  </Button>
</Form.Item>


                </div>
              )}
            </Form>
          </div>
        </div>
      )}
    </>
  );
};

export default Register;
