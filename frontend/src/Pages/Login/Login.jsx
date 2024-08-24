import React, { useState } from 'react';
import './Login.css';
import { FaUser, FaLock } from 'react-icons/fa';
import { useLocation, useNavigate } from 'react-router-dom';
import { Link } from 'react-router-dom';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { API } from '../../constants';

const Login = () => {
  const location = useLocation();
  const isLoginPage = location.pathname === '/login';
  const navigate = useNavigate();

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const setCookie = (name, value, days) => {
    const d = new Date();
    d.setTime(d.getTime() + (days * 24 * 60 * 60 * 1000));
    const expires = "expires=" + d.toUTCString();
    document.cookie = `${name}=${value};${expires};path=/`;
  };

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

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const formData = new URLSearchParams();
      formData.append('username', username);
      formData.append('password', password);

      const response = await fetch(API + '/api/login', {
        method: 'POST',
        body: formData,
        credentials: 'include', // Ensures cookies are included in the request
      });

      if (response.ok) {
        const data = await response.json();
        toast.success('Login successful');
        console.log(getUserIdFromCookie());
        sessionStorage.setItem('username', username);

        // Set cookie with the specific value from server response
        const cookieValue = JSON.stringify({
          userId: data.userId,
          username: data.username,
          email: data.email,
          password: data.password,
          role: data.role,
        });
        setCookie('user', cookieValue, 7); // Cookie expires in 7 days

        setTimeout(() => {
          navigate('/');
        }, 1000);
      } else {
        throw new Error('Invalid credentials');
      }
    } catch (error) {
      toast.error('Login failed: ' + error.message);
    }
  };

  return (
    <>
      <ToastContainer />
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
                <Link to='/forgot-password'>Forgot password?</Link>
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
