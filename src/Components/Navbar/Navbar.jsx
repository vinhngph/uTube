import React, { useState } from 'react';
import './Navbar.css';
import menu_icon from '../../assets/menu.png';
import logo from '../../assets/logo.png';
import search_icon from '../../assets/search.png';
import upload_icon from '../../assets/upload.png';
import more_icon from '../../assets/more.png';
import notification_icon from '../../assets/notification.png';
import profile_icon from '../../assets/jack.png';
import { Link } from 'react-router-dom';

const Navbar = ({ setSidebar }) => {
  const [showDropdown, setShowDropdown] = useState(false);

  const toggleDropdown = () => {
    setShowDropdown((prev) => !prev);
  };

  return (
    <nav className='flex-div'>
      <div className='nav-left flex-div'>
        <img className='menu-icon' onClick={() => setSidebar((prev) => !prev)} src={menu_icon} alt='' />
        <img className='logo' src={logo} alt='' />
      </div>

      <div className='nav-middle flex-div'>
        <div className='search-box flex-div'>
          <input type='text' placeholder='Search' />
          <img src={search_icon} alt='' />
        </div>
      </div>

      <div className='nav-right flex-div'>
        <img src={upload_icon} alt='' />
        <img src={more_icon} alt='' />
        <img src={notification_icon} alt='' />
        <div className='user-dropdown' onClick={toggleDropdown}>
          <img className='user-icon' src={profile_icon} alt='' />
          {showDropdown && (
            <div className='dropdown-content'>
              <Link to='/login'>Login</Link>
              <a href='#'>Sign Out</a>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
