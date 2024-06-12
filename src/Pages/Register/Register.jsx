import React, { useState } from 'react';
import './Register.css';
import { useLocation, useNavigate } from 'react-router-dom';
import { API } from '../../constants';

const Register = () => {
  const location = useLocation();
  const navigate = useNavigate();  // Initialize useNavigate hook
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

  const nextStep = () => {
    setStep(step + 1);
  };

  const prevStep = () => {
    setStep(step - 1);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  const checkUser = async () => {
    try {
      const response = await fetch(API + '/api/isUser', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          email: formData.email,
          username: formData.username
        })
      });
      if (!response.ok) {
        throw new Error('User already exists');
      }
      nextStep();
    } catch (error) {
      setError('User with this email or username already exists');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (step === 1) {
      await checkUser();
    } else {
      try {
        const formDataToSend = new URLSearchParams();
        formDataToSend.append('email', formData.email);
        formDataToSend.append('username', formData.username);
        formDataToSend.append('password', formData.password);
        formDataToSend.append('fullName', formData.fullName);
        formDataToSend.append('dob', formData.dob);
        const response = await fetch(API + '/register', {
          method: 'POST',
          body: formDataToSend,
          credentials: 'include'
        });
        if (!response.ok) {
          throw new Error('Registration failed');
        }
        const data = await response.json();
        // Handle successful registration response
        console.log('Registration Successful', data);
        // Navigate to login page
        navigate('/login');
      } catch (error) {
        console.error('Registration Error:', error.message);
        // Handle registration error
      }
    }
  };

  return (
    <>
      {isRegisterPage && (
        <div className={`container ${isRegisterPage ? 'login-background' : ''}`}>
          <div className="wrapper">
            <form onSubmit={handleSubmit}>
              {step === 1 && (
                <div id="step1">
                  <h2>Step 1: Account Information</h2>
                  <div className="input-box">
                    <label htmlFor="email">Email:</label>
                    <input
                      type="email"
                      id="email"
                      name="email"
                      value={formData.email}
                      onChange={handleChange}
                      required
                    />
                  </div>

                  <div className="input-box">
                    <label htmlFor="username">Username:</label>
                    <input
                      type="text"
                      id="username"
                      name="username"
                      value={formData.username}
                      onChange={handleChange}
                      required
                    />
                  </div>

                  <div className="input-box">
                    <label htmlFor="password">Password:</label>
                    <input
                      type="password"
                      id="password"
                      name="password"
                      value={formData.password}
                      onChange={handleChange}
                      required
                    />
                  </div>

                  <button type="submit">Next</button>
                  {error && <p>{error}</p>}
                </div>
              )}

              {step === 2 && (
                <div id="step2">
                  <h2>Step 2: Personal Information</h2>
                  <div className="input-box">
                    <label htmlFor="fullname">Full Name:</label>
                    <input
                      type="text"
                      id="fullname"
                      name="fullName"
                      value={formData.fullName}
                      onChange={handleChange}
                      required
                    />
                  </div>

                  <div className="input-box">
                    <label htmlFor="dob">Date of Birth:</label>
                    <input
                      type="date"
                      id="dob"
                      name="dob"
                      value={formData.dob}
                      onChange={handleChange}
                      required
                    />
                  </div>

                  <button type="button" onClick={prevStep}>Back</button>
                  <button type="submit">Complete</button>
                </div>
              )}
            </form>
          </div>
        </div>
      )}
    </>
  );
};

export default Register;
