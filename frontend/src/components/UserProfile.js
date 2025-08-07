import React, { useState } from 'react';
import { Card, Form, Input, Button, Avatar, Typography, Space, message, Divider } from 'antd';
import { UserOutlined, EditOutlined, SaveOutlined } from '@ant-design/icons';
import { userAPI } from '../utils/api';

const { Title, Text } = Typography;
const { TextArea } = Input;

const UserProfile = ({ user }) => {
  const [editing, setEditing] = useState(false);
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();

  const handleEdit = () => {
    setEditing(true);
    form.setFieldsValue({
      nickname: user.nickname,
      bio: user.bio || '',
      avatar: user.avatar || ''
    });
  };

  const handleCancel = () => {
    setEditing(false);
    form.resetFields();
  };

  const handleSave = async (values) => {
    setLoading(true);
    try {
      const response = await userAPI.updateProfile(values);
      if (response.code === 200) {
        message.success('个人信息更新成功！');
        setEditing(false);
        // 更新本地用户信息
        const updatedUser = { ...user, ...values };
        localStorage.setItem('user', JSON.stringify(updatedUser));
        window.location.reload(); // 简单刷新页面更新用户信息
      } else {
        message.error(response.message || '更新失败');
      }
    } catch (error) {
      console.error('Update profile error:', error);
      message.error(error.response?.data?.message || '更新失败，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <Card>
        <div style={{ display: 'flex', alignItems: 'flex-start', gap: '24px' }}>
          {/* 头像区域 */}
          <div style={{ textAlign: 'center' }}>
            <Avatar 
              size={120} 
              src={user.avatar}
              icon={<UserOutlined />}
              style={{ backgroundColor: '#1890ff' }}
            >
              {user.nickname?.charAt(0) || user.username?.charAt(0)}
            </Avatar>
            <div style={{ marginTop: 12 }}>
              <Text type="secondary" style={{ fontSize: '12px' }}>
                用户ID: {user.id}
              </Text>
            </div>
          </div>

          {/* 用户信息区域 */}
          <div style={{ flex: 1 }}>
            {!editing ? (
              // 显示模式
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
                  <Title level={3} style={{ margin: 0 }}>
                    {user.nickname || user.username}
                  </Title>
                  <Button 
                    type="primary" 
                    icon={<EditOutlined />}
                    onClick={handleEdit}
                  >
                    编辑资料
                  </Button>
                </div>

                <Space direction="vertical" size="middle" style={{ width: '100%' }}>
                  <div>
                    <Text strong>用户名：</Text>
                    <Text>{user.username}</Text>
                  </div>
                  
                  <div>
                    <Text strong>邮箱：</Text>
                    <Text>{user.email}</Text>
                  </div>
                  
                  <div>
                    <Text strong>昵称：</Text>
                    <Text>{user.nickname || '未设置'}</Text>
                  </div>
                  
                  <div>
                    <Text strong>个人简介：</Text>
                    <div style={{ marginTop: 4 }}>
                      <Text>{user.bio || '这个人很懒，什么都没有留下...'}</Text>
                    </div>
                  </div>
                  
                  <div>
                    <Text strong>注册时间：</Text>
                    <Text>{user.createdAt ? new Date(user.createdAt).toLocaleString() : '未知'}</Text>
                  </div>
                  
                  <div>
                    <Text strong>账号状态：</Text>
                    <Text style={{ color: user.status === 1 ? '#52c41a' : '#f5222d' }}>
                      {user.status === 1 ? '正常' : '禁用'}
                    </Text>
                  </div>
                </Space>
              </div>
            ) : (
              // 编辑模式
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
                  <Title level={3} style={{ margin: 0 }}>
                    编辑个人资料
                  </Title>
                </div>

                <Form
                  form={form}
                  layout="vertical"
                  onFinish={handleSave}
                >
                  <Form.Item
                    label="头像URL"
                    name="avatar"
                    rules={[
                      { type: 'url', message: '请输入有效的URL地址' }
                    ]}
                  >
                    <Input placeholder="请输入头像图片URL" />
                  </Form.Item>

                  <Form.Item
                    label="昵称"
                    name="nickname"
                    rules={[
                      { required: true, message: '请输入昵称' },
                      { min: 2, max: 20, message: '昵称长度为2-20个字符' }
                    ]}
                  >
                    <Input placeholder="请输入昵称" />
                  </Form.Item>

                  <Form.Item
                    label="个人简介"
                    name="bio"
                    rules={[
                      { max: 200, message: '个人简介不能超过200个字符' }
                    ]}
                  >
                    <TextArea 
                      rows={4} 
                      placeholder="介绍一下自己吧..." 
                      showCount 
                      maxLength={200}
                    />
                  </Form.Item>

                  <Form.Item>
                    <Space>
                      <Button 
                        type="primary" 
                        htmlType="submit" 
                        loading={loading}
                        icon={<SaveOutlined />}
                      >
                        保存
                      </Button>
                      <Button onClick={handleCancel}>
                        取消
                      </Button>
                    </Space>
                  </Form.Item>
                </Form>
              </div>
            )}
          </div>
        </div>
      </Card>

      {/* 其他功能区域 */}
      <Card title="我的活动" style={{ marginTop: 16 }}>
        <div style={{ 
          textAlign: 'center', 
          color: '#999', 
          padding: '40px',
          background: '#fafafa',
          borderRadius: '8px'
        }}>
          <UserOutlined style={{ fontSize: '48px', marginBottom: 16 }} />
          <div>活动记录功能开发中...</div>
        </div>
      </Card>

      <Card title="我的收藏" style={{ marginTop: 16 }}>
        <div style={{ 
          textAlign: 'center', 
          color: '#999', 
          padding: '40px',
          background: '#fafafa',
          borderRadius: '8px'
        }}>
          <UserOutlined style={{ fontSize: '48px', marginBottom: 16 }} />
          <div>收藏功能开发中...</div>
        </div>
      </Card>
    </div>
  );
};

export default UserProfile;
