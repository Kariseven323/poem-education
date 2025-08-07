import React, { useState } from 'react';
import { Form, Input, Button, Card, message, Space, Typography } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { authAPI } from '../utils/api';

const { Title, Link } = Typography;

const Login = ({ onLogin }) => {
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();

  const handleSubmit = async (values) => {
    setLoading(true);
    try {
      const response = await authAPI.login(values);
      if (response.code === 200) {
        onLogin(response.data.user, response.data.accessToken);
      } else {
        message.error(response.message || '登录失败');
      }
    } catch (error) {
      console.error('Login error:', error);
      message.error(error.response?.data?.message || '登录失败，请检查网络连接');
    } finally {
      setLoading(false);
    }
  };

  // 快速登录测试账号
  const quickLogin = (username, password) => {
    form.setFieldsValue({ username, password });
    handleSubmit({ username, password });
  };

  return (
    <div style={{ 
      display: 'flex', 
      justifyContent: 'center', 
      alignItems: 'center', 
      minHeight: '60vh',
      padding: '20px'
    }}>
      <Card style={{ width: 400, boxShadow: '0 4px 12px rgba(0,0,0,0.1)' }}>
        <div style={{ textAlign: 'center', marginBottom: 24 }}>
          <Title level={2}>用户登录</Title>
          <p style={{ color: '#666' }}>登录诗词交流鉴赏平台</p>
        </div>

        <Form
          form={form}
          name="login"
          onFinish={handleSubmit}
          autoComplete="off"
          size="large"
        >
          <Form.Item
            name="username"
            rules={[
              { required: true, message: '请输入用户名!' },
              { min: 3, message: '用户名至少3个字符' }
            ]}
          >
            <Input 
              prefix={<UserOutlined />} 
              placeholder="用户名" 
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[
              { required: true, message: '请输入密码!' },
              { min: 6, message: '密码至少6个字符' }
            ]}
          >
            <Input.Password 
              prefix={<LockOutlined />} 
              placeholder="密码" 
            />
          </Form.Item>

          <Form.Item>
            <Button 
              type="primary" 
              htmlType="submit" 
              loading={loading}
              style={{ width: '100%' }}
            >
              登录
            </Button>
          </Form.Item>
        </Form>

        <div style={{ textAlign: 'center', marginTop: 16 }}>
          <Space direction="vertical" size="small">
            <div>
              还没有账号？ 
              <Link href="/register"> 立即注册</Link>
            </div>
            
            <div style={{ marginTop: 16, padding: '12px', background: '#f5f5f5', borderRadius: '4px' }}>
              <div style={{ marginBottom: 8, fontWeight: 'bold', color: '#666' }}>
                快速测试登录：
              </div>
              <Space wrap>
                <Button 
                  size="small" 
                  onClick={() => quickLogin('testuser', 'password123')}
                >
                  测试账号1
                </Button>
                <Button 
                  size="small" 
                  onClick={() => quickLogin('admin', 'admin123')}
                >
                  管理员
                </Button>
                <Button 
                  size="small" 
                  onClick={() => quickLogin('poet', 'poet123')}
                >
                  诗人账号
                </Button>
              </Space>
              <div style={{ fontSize: '12px', color: '#999', marginTop: 4 }}>
                * 如果账号不存在，请先注册或使用API测试页面创建
              </div>
            </div>
          </Space>
        </div>
      </Card>
    </div>
  );
};

export default Login;
