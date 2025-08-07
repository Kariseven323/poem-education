import React, { useState } from 'react';
import { Form, Input, Button, Card, message, Typography } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined, EditOutlined } from '@ant-design/icons';
import { authAPI } from '../utils/api';

const { Title, Link } = Typography;

const Register = ({ onRegister }) => {
  const [loading, setLoading] = useState(false);
  const [checkingUsername, setCheckingUsername] = useState(false);
  const [form] = Form.useForm();

  const handleSubmit = async (values) => {
    setLoading(true);
    try {
      const response = await authAPI.register(values);
      if (response.code === 200) {
        message.success('注册成功！');
        // 注册成功后自动登录
        const loginResponse = await authAPI.login({
          username: values.username,
          password: values.password
        });
        if (loginResponse.code === 200) {
          onRegister(loginResponse.data.user, loginResponse.data.accessToken);
        }
      } else {
        message.error(response.message || '注册失败');
      }
    } catch (error) {
      console.error('Register error:', error);
      message.error(error.response?.data?.message || '注册失败，请检查网络连接');
    } finally {
      setLoading(false);
    }
  };

  const checkUsername = async (username) => {
    if (!username || username.length < 3) return;
    
    setCheckingUsername(true);
    try {
      const response = await authAPI.checkUsername(username);
      if (response.code === 200) {
        if (!response.data) {
          return Promise.reject(new Error('用户名已存在'));
        }
      }
    } catch (error) {
      return Promise.reject(new Error('用户名已存在'));
    } finally {
      setCheckingUsername(false);
    }
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
          <Title level={2}>用户注册</Title>
          <p style={{ color: '#666' }}>加入诗词交流鉴赏平台</p>
        </div>

        <Form
          form={form}
          name="register"
          onFinish={handleSubmit}
          autoComplete="off"
          size="large"
        >
          <Form.Item
            name="username"
            rules={[
              { required: true, message: '请输入用户名!' },
              { min: 3, max: 20, message: '用户名长度为3-20个字符' },
              { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线' },
              { validator: (_, value) => checkUsername(value) }
            ]}
            hasFeedback
            validateTrigger="onBlur"
          >
            <Input 
              prefix={<UserOutlined />} 
              placeholder="用户名（3-20个字符）" 
              loading={checkingUsername}
            />
          </Form.Item>

          <Form.Item
            name="email"
            rules={[
              { required: true, message: '请输入邮箱!' },
              { type: 'email', message: '请输入有效的邮箱地址!' }
            ]}
          >
            <Input 
              prefix={<MailOutlined />} 
              placeholder="邮箱地址" 
            />
          </Form.Item>

          <Form.Item
            name="nickname"
            rules={[
              { required: true, message: '请输入昵称!' },
              { min: 2, max: 20, message: '昵称长度为2-20个字符' }
            ]}
          >
            <Input 
              prefix={<EditOutlined />} 
              placeholder="昵称（2-20个字符）" 
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[
              { required: true, message: '请输入密码!' },
              { min: 6, max: 20, message: '密码长度为6-20个字符' }
            ]}
            hasFeedback
          >
            <Input.Password 
              prefix={<LockOutlined />} 
              placeholder="密码（6-20个字符）" 
            />
          </Form.Item>

          <Form.Item
            name="confirmPassword"
            dependencies={['password']}
            rules={[
              { required: true, message: '请确认密码!' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('两次输入的密码不一致!'));
                },
              }),
            ]}
            hasFeedback
          >
            <Input.Password 
              prefix={<LockOutlined />} 
              placeholder="确认密码" 
            />
          </Form.Item>

          <Form.Item>
            <Button 
              type="primary" 
              htmlType="submit" 
              loading={loading}
              style={{ width: '100%' }}
            >
              注册
            </Button>
          </Form.Item>
        </Form>

        <div style={{ textAlign: 'center', marginTop: 16 }}>
          已有账号？ 
          <Link href="/login"> 立即登录</Link>
        </div>
      </Card>
    </div>
  );
};

export default Register;
