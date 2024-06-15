import React from 'react'
import { useNavigate } from 'react-router-dom'
import { Layout, Menu } from 'antd'
import { HomeOutlined, HistoryOutlined, UserOutlined } from '@ant-design/icons'
import './Sidebar.css'

const { Sider } = Layout;

const Sidebar = ({ sidebar }) => {
  const navigate = useNavigate();

  return (
    <Sider
      collapsible
      collapsed={!sidebar}
      className="sidebar"
    >
      <Menu theme="light" mode="inline" defaultSelectedKeys={[]}>
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
}

export default Sidebar
