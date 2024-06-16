import React, { useState, useEffect, useRef } from 'react';
import './Navbar.css';
import { MenuOutlined, SearchOutlined, UploadOutlined, BellOutlined, MoreOutlined, UserOutlined } from '@ant-design/icons';
import { Input, Avatar, Dropdown, Menu } from 'antd';
import logo from '../../assets/logo.png';
import { Link, useNavigate } from 'react-router-dom';
import { API } from '../../constants';

const Navbar = ({ setSidebar }) => {
  const [showSearchResults, setShowSearchResults] = useState(false);
  const [user, setUser] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const searchResultsRef = useRef(null);
  const navigate = useNavigate();

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
    setShowSearchResults(false);
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearchIconClick();
    }
  };

  const handleSearchIconClick = () => {
    if (searchQuery.trim() !== '') {
      navigate(`/search?key=${encodeURIComponent(searchQuery.trim())}`);
      setShowSearchResults(false);
    }
  };

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (searchResultsRef.current && !searchResultsRef.current.contains(e.target)) {
        setShowSearchResults(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const userMenu = (
    <Menu>
      {user ? (
        <>
          {(user.role !== 1) && <Menu.Item onClick={handleInformation}>Account Information</Menu.Item>}
          <Menu.Item onClick={handleManageChannel}>Manage Channel</Menu.Item>
          <Menu.Item onClick={handleLogout}>Log Out</Menu.Item>
          {(user.role === 1 || user.role === 2) && <Menu.Item onClick={handleManageAccount}>Manage Account</Menu.Item>}
          {(user.role === 1 || user.role === 2) && <Menu.Item onClick={handleAdminPage}>Censor</Menu.Item>}
        </>
      ) : (
        <>
          <Menu.Item>
            <Link to='/login'>Sign In</Link>
          </Menu.Item>
          <Menu.Item>
            <Link to='/register'>Register</Link>
          </Menu.Item>
        </>
      )}
    </Menu>
  );

  return (
    <nav className='flex-div'>
      <div className='nav-left'>
        <MenuOutlined className='menu-icon' onClick={() => setSidebar(prev => !prev)} />
        <Link to="/" onClick={handleHome}>
          <img className='logo' src={logo} alt='Logo' />
        </Link>
      </div>

      <div className='nav-middle'>
        <div className='search-box'>
          <Input
            placeholder='Search'
            value={searchQuery}
            onChange={handleSearchChange}
            onKeyPress={handleKeyPress}
            prefix={<SearchOutlined onClick={handleSearchIconClick} style={{ cursor: 'pointer' }} />}
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
        <UploadOutlined className='nav-icon' onClick={handleUploadClick} />
        <MoreOutlined className='nav-icon' />
        <BellOutlined className='nav-icon' />
        <Dropdown overlay={userMenu} trigger={['click']} placement="bottomRight">
        <Avatar icon={<UserOutlined />} className='user-icon' />
        </Dropdown>
      </div>
    </nav>
  );
};

export default Navbar;
