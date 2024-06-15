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
import { API } from '../../constants';

const Navbar = ({ setSidebar }) => {
  const [showDropdown, setShowDropdown] = useState(false);
  const [user, setUser] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);

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

  useEffect(() => {
    if (searchQuery) {
      const fetchSearchResults = async () => {
        const response = await fetch(API + `/api/home/search?key=${searchQuery}`);
        const data = await response.json();
        setSearchResults(data);
      };
      fetchSearchResults();
    } else {
      setSearchResults([]);
    }
  }, [searchQuery]);

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

  const handleAdminPage = () => {
    navigate('/admin-page');
  };

  const handleInformation = () => {
    navigate('/account-info');
  };

  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
  };

  const handleResultClick = (key) => {
    navigate(`/search?key=${key}`);
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
          <input type='text' placeholder='Search' value={searchQuery} onChange={handleSearchChange} />
          <img src={search_icon} alt='Search' />
        </div>
        {searchResults.length > 0 && (
          <div className='search-results'>
            {searchResults.map(video => (
              <div key={video.id} onClick={() => handleResultClick(video.name)}>
                <h1>{video.name}</h1>
              </div>
            ))}
          </div>
        )}
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
                  <button onClick={handleInformation}>Account Information</button>
                  <button onClick={handleManageChannel}>Manage Channel</button>
                  <button onClick={handleLogout}>Log Out</button>
                  {(user.role === 1 || user.role === 2) && <button onClick={handleManageAccount}>Manage Account</button>}
                  {(user.role === 1 || user.role === 2) && <button onClick={handleAdminPage}>Censor</button>}
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
