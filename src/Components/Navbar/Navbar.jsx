import React, { useState, useEffect, useRef } from 'react';
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
  const [showUserDropdown, setShowUserDropdown] = useState(false);
  const [showSearchResults, setShowSearchResults] = useState(false);
  const [user, setUser] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const userDropdownRef = useRef(null);
  const searchResultsRef = useRef(null);

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
        try {
          const response = await fetch(API + `/api/home/search?key=${searchQuery}`);
          const data = await response.json();
          setSearchResults(data);
          setShowSearchResults(true);
        } catch (error) {
          console.error('Error fetching search results:', error);
          setSearchResults([]);
          setShowSearchResults(false);
        }
      };
      fetchSearchResults();
    } else {
      setSearchResults([]);
      setShowSearchResults(false);
    }
  }, [searchQuery]);

  const toggleUserDropdown = () => {
    setShowUserDropdown(prev => !prev);
  };

  const closeUserDropdown = () => {
    if (userDropdownRef.current && !userDropdownRef.current.contains(e.target)) {
      setShowUserDropdown(false);
    }
  };

  const closeSearchResults = () => {
    setShowSearchResults(false);
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
    closeUserDropdown();
    closeSearchResults();
  };

  const handleLogout = () => {
    sessionStorage.clear();
    clearCookies();
    navigate('/login');
    closeUserDropdown();
    closeSearchResults();
  };

  const handleUploadClick = () => {
    navigate('/upload');
    closeUserDropdown();
    closeSearchResults();
  };

  const handleManageAccount = () => {
    navigate('/manage-account');
    closeUserDropdown();
    closeSearchResults();
  };

  const handleManageChannel = () => {
    navigate('/manage-channel');
    closeUserDropdown();
    closeSearchResults();
  };

  const handleAdminPage = () => {
    navigate('/admin-page');
    closeUserDropdown();
    closeSearchResults();
  };

  const handleInformation = () => {
    navigate('/account-info');
    closeUserDropdown();
    closeSearchResults();
  };

  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
  };

  const handleResultClick = (key) => {
    navigate(`/search?key=${key}`);
    setShowSearchResults(false); // Close search results dropdown after clicking a result
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearchIconClick();
    }
  };

  const handleSearchIconClick = () => {
    if (searchQuery.trim() !== '') {
      navigate(`/search?key=${encodeURIComponent(searchQuery.trim())}`);
      setShowSearchResults(false); // Close search results dropdown on search icon click
    }
  };

  // Close dropdowns when clicking outside
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (userDropdownRef.current && !userDropdownRef.current.contains(e.target)) {
        setShowUserDropdown(false);
      }
      if (searchResultsRef.current && !searchResultsRef.current.contains(e.target)) {
        setShowSearchResults(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

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
          <input 
            type='text' 
            placeholder='Search' 
            value={searchQuery} 
            onChange={handleSearchChange} 
            onKeyPress={handleKeyPress} // Added to handle Enter key press
          />
          <img
            src={search_icon}
            alt='Search'
            onClick={handleSearchIconClick}
            style={{ cursor: 'pointer' }}
          />
          {showSearchResults && (
            <div ref={searchResultsRef} className='search-results'>
              {searchResults.map(video => (
                <div key={video.id} onClick={() => handleResultClick(video.name)}>
                  <h1>{video.name}</h1>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      <div className='nav-right'>
        <img src={upload_icon} alt='Upload' onClick={handleUploadClick} />
        <img src={more_icon} alt='More' />
        <img src={notification_icon} alt='Notifications' />
        <div className='user-dropdown' ref={userDropdownRef} onClick={toggleUserDropdown}>
          <img className='user-icon' src={profile_icon} alt='Profile' />
          {showUserDropdown && (
            <div className='dropdown-content'>
              {user ? (
                <>
                  {(user.role !== 1) && <button onClick={handleInformation}>Account Information</button>}
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
