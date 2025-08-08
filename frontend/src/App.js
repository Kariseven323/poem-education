import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Menu, Button, Avatar, Dropdown, message } from 'antd';
import {
  HomeOutlined,
  BookOutlined,
  UserOutlined,
  EditOutlined,
  MessageOutlined,
  LoginOutlined,
  LogoutOutlined,
  SettingOutlined,
} from '@ant-design/icons';
import Login from './components/Login';
import Register from './components/Register';
import Home from './components/Home';
import PoemList from './components/PoemList';
import PoemDetail from './components/PoemDetail';
import WriterList from './components/WriterList';
import WriterDetail from './components/WriterDetail';
import SentenceList from './components/SentenceList';
import UserProfile from './components/UserProfile';
import APITest from './components/APITest';
import { userAPI } from './utils/api';

const { Header, Content, Sider } = Layout;

// 内部组件，用于使用React Router hooks
function AppContent() {
  const navigate = useNavigate();
  const location = useLocation();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [collapsed, setCollapsed] = useState(false);

  useEffect(() => {
    // 检查本地存储的用户信息
    const token = localStorage.getItem('token');
    const userData = localStorage.getItem('user');
    
    if (token && userData) {
      try {
        setUser(JSON.parse(userData));
        // 验证token有效性
        userAPI.getProfile()
          .then(response => {
            if (response.code === 200) {
              setUser(response.data);
              localStorage.setItem('user', JSON.stringify(response.data));
            }
          })
          .catch(() => {
            // token无效，清除本地存储
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            setUser(null);
          });
      } catch (error) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
      }
    }
    setLoading(false);
  }, []);

  const handleLogin = (userData, token) => {
    setUser(userData);
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(userData));
    message.success('登录成功！');
  };

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    message.success('已退出登录');
  };

  const menuItems = [
    {
      key: '/',
      icon: <HomeOutlined />,
      label: '首页',
    },
    {
      key: '/poems',
      icon: <BookOutlined />,
      label: '诗词鉴赏',
    },
    {
      key: '/writers',
      icon: <UserOutlined />,
      label: '文人墨客',
    },
    {
      key: '/sentences',
      icon: <EditOutlined />,
      label: '名句摘录',
    },
    {
      key: '/api-test',
      icon: <SettingOutlined />,
      label: 'API测试',
    },

  ];

  const userMenuItems = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: '个人资料',
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      onClick: handleLogout,
    },
  ];

  if (loading) {
    return <div>加载中...</div>;
  }

  return (
      <Layout style={{ minHeight: '100vh' }}>
        <Sider collapsible collapsed={collapsed} onCollapse={setCollapsed}>
          <div className="logo">
            {collapsed ? '诗' : '诗词鉴赏'}
          </div>
          <Menu
            theme="dark"
            selectedKeys={[location.pathname]}
            mode="inline"
            items={menuItems}
            onClick={({ key }) => {
              navigate(key);
            }}
          />
        </Sider>
        
        <Layout className="site-layout">
          <Header style={{ padding: '0 16px', background: '#fff', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h2 style={{ margin: 0 }}>诗词交流鉴赏平台</h2>
            
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
              {user ? (
                <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
                  <div style={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
                    <Avatar icon={<UserOutlined />} />
                    <span style={{ marginLeft: 8 }}>{user.nickname || user.username}</span>
                  </div>
                </Dropdown>
              ) : (
                <div>
                  <Button type="link" icon={<LoginOutlined />} onClick={() => navigate('/login')}>
                    登录
                  </Button>
                  <Button type="primary" onClick={() => navigate('/register')}>
                    注册
                  </Button>
                </div>
              )}
            </div>
          </Header>
          
          <Content style={{ margin: '16px' }}>
            <div className="site-layout-content">
              <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/login" element={user ? <Navigate to="/" /> : <Login onLogin={handleLogin} />} />
                <Route path="/register" element={user ? <Navigate to="/" /> : <Register onRegister={handleLogin} />} />
                <Route path="/poems" element={<PoemList />} />
                <Route path="/poems/:id" element={<PoemDetail />} />
                <Route path="/writers" element={<WriterList />} />
                <Route path="/writers/:id" element={<WriterDetail />} />
                <Route path="/sentences" element={<SentenceList />} />
                <Route path="/profile" element={user ? <UserProfile user={user} /> : <Navigate to="/login" />} />
                <Route path="/api-test" element={<APITest />} />

              </Routes>
            </div>
          </Content>
        </Layout>
      </Layout>
  );
}

// 主App组件，包装Router
function App() {
  return (
    <Router>
      <AppContent />
    </Router>
  );
}

export default App;
