import React, { useState } from 'react';
import './Login.css';
import { FaUser, FaLock } from "react-icons/fa";
import { useLocation } from 'react-router-dom';
import { Link } from 'react-router-dom';

const Login = () => {
  const location = useLocation();
  const isLoginPage = location.pathname === '/login';

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const formData = new URLSearchParams();
      formData.append('username', username);
      formData.append('password', password);

      const response = await fetch('http://18.139.19.28:4000/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: formData,
        credentials: 'include', // Include cookies in the request
      });

      // Handle response
      if (response.ok) {
        // Redirect or perform actions upon successful login
        window.location.href = '/'; // Example redirect
      } else {
        // Handle failed login (e.g., display error message)
        console.error('Login failed');
      }
    } catch (error) {
      console.error('Error:', error);
    }
  };

  return (
    <>
      {isLoginPage && (
        <div className={`container ${isLoginPage ? 'login-background' : ''}`}>
          <div className='wrapper'>
            <form onSubmit={handleSubmit}>
              <h1>Login</h1>
              <div className='input-box'>
                <input
                  type='text'
                  placeholder='Username'
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                />
                <FaUser className='icon' />
              </div>
              <div className='input-box'>
                <input
                  type='password'
                  placeholder='Password'
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
                <FaLock className='icon' />
              </div>
              <div className='remember-forgot'>
                <label>
                  <input type='checkbox' /> Remember me
                </label>
                <a href='#'>Forgot password?</a>
              </div>
              <button type='submit'>Login</button>
              <div className='register-link'>
                <p>
                  Don't have an account? <Link to='/register'>Register</Link>
                </p>
              </div>
            </form>
          </div>
        </div>
      )}
    </>
  );
};

export default Login;
