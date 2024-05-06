import React from 'react';
import './Login.css';
import { FaUser } from "react-icons/fa";
import { FaLock } from "react-icons/fa";
import { useLocation } from 'react-router-dom';
import { Link } from 'react-router-dom';
const Login = () => {
  const location = useLocation();
  const isLoginPage = location.pathname === '/login';

  return (
    <>
      {isLoginPage && (
        <div className={`container ${isLoginPage ? 'login-background' : ''}`}>
          <div className='wrapper'>
            <form action='/ '>
              <h1>Login</h1>
              <div className='input-box'>
                <input
                  type='text'
                  placeholder='Username'
                  required
                  //value={username}
                  //onChange={(e) => setUsername(e.target.value)}
                />
                <FaUser className='icon' />
              </div>
              <div className='input-box'>
                <input
                  type='password'
                  placeholder='Password'
                  required
                  //value={password}
                  //onChange={(e) => setPassword(e.target.value)}
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
