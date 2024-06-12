import React, { useState } from 'react'
import Navbar from './Components/Navbar/Navbar'
import { useLocation } from 'react-router-dom';
import { Route, Routes } from 'react-router-dom' 
import Home from './Pages/Home/home'
import Video from './Pages/Video/Video'
import Login from './Pages/Login/Login'
import Register from './Pages/Register/Register'
import UploadVideo from './Pages/UploadVideo/UploadVideo'
import ManageAccount from './Pages/ManageAccount/ManageAccount';
import ManageChannel from './Pages/ManangeChannel/ManageChannel';

const App = () => {
  const location = useLocation();
  const [sidebar, setSidebar] = useState(true); 

  // Check if current route is '/login'
  const isLoginPage = location.pathname === '/login';
  const isRegisterPage = location.pathname === '/register';
  return (
    <div>
      {/* Render Navbar only if not on the login or register page */}
      {!isLoginPage && !isRegisterPage && <Navbar setSidebar={setSidebar} />}
      <Routes>
        <Route path='/login' element={<Login />} />
        <Route path='/register' element={<Register />} />
        <Route path='/' element={<Home sidebar={sidebar} />} />
        <Route path='/upload' element={<UploadVideo />} />
        <Route path='/manage-account' element={<ManageAccount />} />
        <Route path='/manage-channel' element={<ManageChannel />} />
        <Route path="/watch/:videoId" element={<Video />} />
      </Routes>
    </div>
  )
}

export default App