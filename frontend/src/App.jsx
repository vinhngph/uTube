import React, { useState } from 'react'
import Navbar from './Components/Navbar/Navbar'
import { useLocation } from 'react-router-dom';
import { Route, Routes } from 'react-router-dom' 
import Home from './Pages/Home/Home'
import Video from './Pages/Video/Video'
import Login from './Pages/Login/Login'
import Register from './Pages/Register/Register'
import UploadVideo from './Pages/UploadVideo/UploadVideo'
import ManageAccount from './Pages/ManageAccount/ManageAccount';
import ManageChannel from './Pages/ManangeChannel/ManageChannel';
import Search from './Pages/Search/Search';
import History from './Pages/History/History';
import AdminPage from './Pages/AdminPage/AdminPage';
import AccountInfo from './Pages/AccountInfo/AccountInfo';
import ForgotPassword from './Pages/ForgotPassword/ForgotPassword';

const App = () => {
  const location = useLocation();
  const [sidebar, setSidebar] = useState(true); 

  // Check if current route is '/login'
  const isLoginPage = location.pathname === '/login';
  const isRegisterPage = location.pathname === '/register';
  const isForgotPasswordPage = location.pathname === '/forgot-password';
  return (
    <div>
      {/* Render Navbar only if not on the login or register page */}
      {!isLoginPage && !isRegisterPage && !isForgotPasswordPage &&<Navbar setSidebar={setSidebar} />}
      <Routes>
        <Route path='/login' element={<Login />} />
        <Route path='/register' element={<Register />} />

        <Route path='/forgot-password' element={<ForgotPassword/>}/>
        <Route path='/' element={<Home sidebar={sidebar} />} />
        <Route path='/upload' element={<UploadVideo sidebar={sidebar} />} />
        <Route path='/account-info' element={<AccountInfo sidebar={sidebar} />} />
        <Route path='/admin-page' element={< AdminPage sidebar={sidebar}/>} />
        <Route path='/manage-account' element={<ManageAccount sidebar={sidebar} />} />
        <Route path='/manage-channel' element={<ManageChannel sidebar={sidebar} />} />
        <Route path="/watch/:videoId" element={<Video sidebar={sidebar} />} />
        <Route path="/search" element={<Search sidebar={sidebar} />} />
        <Route path='/feed/history' element={< History sidebar={sidebar}/>} />
      </Routes>
    </div>
  )
}

export default App