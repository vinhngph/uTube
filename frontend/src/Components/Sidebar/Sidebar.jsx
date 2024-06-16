import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Layout, Menu } from 'antd';
import { HomeOutlined, HistoryOutlined, UserOutlined } from '@ant-design/icons';
import './Sidebar.css';

const { Sider } = Layout;

const Sidebar = ({ sidebar }) => {
  const navigate = useNavigate();
  const location = useLocation();

  // Determine the selected key based on the current path
  const getSelectedKey = () => {
    switch (location.pathname) {
      case '/':
        return '1';
      case '/feed/history':
        return '2';
      case '/manage-channel':
        return '3';
      default:
        return '1';
    }
  };

  return (
    <Sider collapsible collapsed={!sidebar} className="sidebar">
      <Menu theme="light" mode="inline" selectedKeys={[getSelectedKey()]}>
        <Menu.Item key="1" icon={<HomeOutlined />} onClick={() => navigate('/')}>
          Home
        </Menu.Item>
        <Menu.Item key="2" icon={<HistoryOutlined />} onClick={() => navigate('/feed/history')}>
          History
        </Menu.Item>
        <Menu.Item key="3" icon={<UserOutlined />} onClick={() => navigate('/manage-channel')}>
          You
        </Menu.Item>
      </Menu>
    </Sider>
  );
};

export default Sidebar;
