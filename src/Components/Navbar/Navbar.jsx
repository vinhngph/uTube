import React, { useState, useEffect } from 'react';
import './Navbar.css';
import menu_icon from '../../assets/menu.png';
import logo from '../../assets/logo.png';
import search_icon from '../../assets/search.png';
import upload_icon from '../../assets/upload.png';
import more_icon from '../../assets/more.png';
import notification_icon from '../../assets/notification.png';
import profile_icon from '../../assets/jack.png';
import { Link, useNavigate } from 'react-router-dom';

const Navbar = ({ setSidebar }) => {
  const [showDropdown, setShowDropdown] = useState(false);
  const [user, setUser] = useState(null);

  useEffect(() => {
    const cookies = document.cookie.split(';').reduce((acc, cookie) => {
      const [name, value] = cookie.split('=').map(c => c.trim());
      acc[name] = decodeURIComponent(value);
      return acc;
    }, {});
    if (cookies.user) {
      setUser(JSON.parse(cookies.user));
    }
  }, []);

  const toggleDropdown = () => {
    setShowDropdown(prev => !prev);
  };

  const navigate = useNavigate();

  const clearCookies = () => {
    const cookies = document.cookie.split(';');
    for (let i = 0; i < cookies.length; i++) {
      const cookie = cookies[i];
      const eqPos = cookie.indexOf('=');
      const name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
      document.cookie = name.trim() + '=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/';
    }
  };

  const handleHome = () => {
    navigate('/');
  };


  const handleLogout = () => {
    sessionStorage.clear();
    clearCookies();
    navigate('/login');
  };

  const handleUploadClick = () => {
    navigate('/upload');
  };

  const handleManageAccount = () => {
    navigate('/manage-account');
  };

  const handleManageChannel = () => {
    navigate('/manage-channel');
  };

  return (
    <nav className='flex-div'>
      <div className='nav-left'>
        <img className='menu-icon' onClick={() => setSidebar(prev => !prev)} src={menu_icon} alt='Menu' />
        <Link to="/" onClick={handleHome}>
          <img className='logo' src={logo} alt='Logo' />
        </Link>
      </div>

      <div className='nav-middle'>
        <div className='search-box'>
          <input type='text' placeholder='Search' />
          <img src={search_icon} alt='Search' />
        </div>
      </div>

      <div className='nav-right'>
        <img src={upload_icon} alt='Upload' onClick={handleUploadClick} />
        <img src={more_icon} alt='More' />
        <img src={notification_icon} alt='Notifications' />
        <div className='user-dropdown' onClick={toggleDropdown}>
          <img className='user-icon' src={profile_icon} alt='Profile' />
          {showDropdown && (
            <div className='dropdown-content'>
              {user ? (
                <>
                  <p>Account Info</p>
                  <button onClick={handleManageChannel}>Manage Channel</button>
                  <button onClick={handleLogout}>Log Out</button>
                  {(user.role === 1 || user.role === 2) && <button onClick={handleManageAccount}>Manage Account</button>}
                </>
              ) : (
                <>
                  <Link to='/login'>Sign In</Link>
                  <Link to='/register'>Register</Link>
                </>
              )}
            </div>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
