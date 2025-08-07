import React, { useState } from 'react';
import { Card, Button, Input, Form, message, Tabs, Space, Typography, Divider } from 'antd';
import { PlayCircleOutlined, ClearOutlined } from '@ant-design/icons';
import { authAPI, userAPI, guwenAPI, writerAPI, sentenceAPI, commentAPI } from '../utils/api';

const { TextArea } = Input;
const { Title, Text } = Typography;
const { TabPane } = Tabs;

const APITest = () => {
  const [responses, setResponses] = useState({});
  const [loading, setLoading] = useState({});

  const executeAPI = async (apiName, apiFunction, params = {}) => {
    setLoading(prev => ({ ...prev, [apiName]: true }));
    try {
      const response = await apiFunction(params);
      setResponses(prev => ({
        ...prev,
        [apiName]: {
          success: true,
          data: response,
          timestamp: new Date().toLocaleString()
        }
      }));
      message.success(`${apiName} 执行成功`);
    } catch (error) {
      console.error(`${apiName} error:`, error);
      setResponses(prev => ({
        ...prev,
        [apiName]: {
          success: false,
          error: error.response?.data || error.message,
          timestamp: new Date().toLocaleString()
        }
      }));
      message.error(`${apiName} 执行失败`);
    } finally {
      setLoading(prev => ({ ...prev, [apiName]: false }));
    }
  };

  const clearResponse = (apiName) => {
    setResponses(prev => {
      const newResponses = { ...prev };
      delete newResponses[apiName];
      return newResponses;
    });
  };

  const renderResponse = (apiName) => {
    const response = responses[apiName];
    if (!response) return null;

    return (
      <div className={`response-container ${response.success ? 'success-response' : 'error-response'}`}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
          <Text strong>{response.success ? '✅ 成功' : '❌ 失败'}</Text>
          <Space>
            <Text type="secondary" style={{ fontSize: '12px' }}>{response.timestamp}</Text>
            <Button size="small" icon={<ClearOutlined />} onClick={() => clearResponse(apiName)} />
          </Space>
        </div>
        <pre style={{ 
          background: response.success ? '#f6ffed' : '#fff2f0',
          padding: '8px',
          borderRadius: '4px',
          fontSize: '12px',
          overflow: 'auto',
          maxHeight: '200px'
        }}>
          {JSON.stringify(response.success ? response.data : response.error, null, 2)}
        </pre>
      </div>
    );
  };

  const APITestCard = ({ title, children }) => (
    <Card title={title} className="api-test-card" size="small">
      {children}
    </Card>
  );

  return (
    <div>
      <Title level={2}>API 接口测试</Title>
      <Text type="secondary">测试后端API接口的连通性和功能</Text>
      
      <Tabs defaultActiveKey="auth" style={{ marginTop: 16 }}>
        <TabPane tab="认证模块" key="auth">
          <Space direction="vertical" style={{ width: '100%' }}>
            <APITestCard title="用户注册 POST /api/v1/auth/register">
              <Form
                layout="inline"
                onFinish={(values) => executeAPI('register', authAPI.register, values)}
              >
                <Form.Item name="username" rules={[{ required: true }]}>
                  <Input placeholder="用户名" />
                </Form.Item>
                <Form.Item name="email" rules={[{ required: true }]}>
                  <Input placeholder="邮箱" />
                </Form.Item>
                <Form.Item name="nickname" rules={[{ required: true }]}>
                  <Input placeholder="昵称" />
                </Form.Item>
                <Form.Item name="password" rules={[{ required: true }]}>
                  <Input.Password placeholder="密码" />
                </Form.Item>
                <Form.Item>
                  <Button 
                    type="primary" 
                    htmlType="submit" 
                    icon={<PlayCircleOutlined />}
                    loading={loading.register}
                  >
                    测试注册
                  </Button>
                </Form.Item>
              </Form>
              {renderResponse('register')}
            </APITestCard>

            <APITestCard title="用户登录 POST /api/v1/auth/login">
              <Form
                layout="inline"
                onFinish={(values) => executeAPI('login', authAPI.login, values)}
              >
                <Form.Item name="username" rules={[{ required: true }]}>
                  <Input placeholder="用户名" />
                </Form.Item>
                <Form.Item name="password" rules={[{ required: true }]}>
                  <Input.Password placeholder="密码" />
                </Form.Item>
                <Form.Item>
                  <Button 
                    type="primary" 
                    htmlType="submit" 
                    icon={<PlayCircleOutlined />}
                    loading={loading.login}
                  >
                    测试登录
                  </Button>
                </Form.Item>
              </Form>
              {renderResponse('login')}
            </APITestCard>

            <APITestCard title="检查用户名 GET /api/v1/auth/check-username">
              <Form
                layout="inline"
                onFinish={(values) => executeAPI('checkUsername', authAPI.checkUsername, values.username)}
              >
                <Form.Item name="username" rules={[{ required: true }]}>
                  <Input placeholder="要检查的用户名" />
                </Form.Item>
                <Form.Item>
                  <Button 
                    type="primary" 
                    htmlType="submit" 
                    icon={<PlayCircleOutlined />}
                    loading={loading.checkUsername}
                  >
                    检查用户名
                  </Button>
                </Form.Item>
              </Form>
              {renderResponse('checkUsername')}
            </APITestCard>
          </Space>
        </TabPane>

        <TabPane tab="用户模块" key="user">
          <Space direction="vertical" style={{ width: '100%' }}>
            <APITestCard title="获取当前用户信息 GET /api/v1/users/profile">
              <Button 
                type="primary" 
                icon={<PlayCircleOutlined />}
                loading={loading.getProfile}
                onClick={() => executeAPI('getProfile', userAPI.getProfile)}
              >
                获取用户信息
              </Button>
              {renderResponse('getProfile')}
            </APITestCard>

            <APITestCard title="根据ID获取用户信息 GET /api/v1/users/{userId}">
              <Form
                layout="inline"
                onFinish={(values) => executeAPI('getUserById', userAPI.getUserById, values.userId)}
              >
                <Form.Item name="userId" rules={[{ required: true }]}>
                  <Input placeholder="用户ID" />
                </Form.Item>
                <Form.Item>
                  <Button 
                    type="primary" 
                    htmlType="submit" 
                    icon={<PlayCircleOutlined />}
                    loading={loading.getUserById}
                  >
                    获取用户信息
                  </Button>
                </Form.Item>
              </Form>
              {renderResponse('getUserById')}
            </APITestCard>
          </Space>
        </TabPane>

        <TabPane tab="古文模块" key="guwen">
          <Space direction="vertical" style={{ width: '100%' }}>
            <APITestCard title="获取古文列表 GET /api/v1/guwen">
              <Form
                layout="inline"
                onFinish={(values) => executeAPI('getGuwenList', guwenAPI.getList, values)}
              >
                <Form.Item name="page">
                  <Input placeholder="页码 (默认1)" />
                </Form.Item>
                <Form.Item name="size">
                  <Input placeholder="每页数量 (默认20)" />
                </Form.Item>
                <Form.Item name="dynasty">
                  <Input placeholder="朝代" />
                </Form.Item>
                <Form.Item name="writer">
                  <Input placeholder="作者" />
                </Form.Item>
                <Form.Item>
                  <Button 
                    type="primary" 
                    htmlType="submit" 
                    icon={<PlayCircleOutlined />}
                    loading={loading.getGuwenList}
                  >
                    获取古文列表
                  </Button>
                </Form.Item>
              </Form>
              {renderResponse('getGuwenList')}
            </APITestCard>

            <APITestCard title="获取古文详情 GET /api/v1/guwen/{id}">
              <Form
                layout="inline"
                onFinish={(values) => executeAPI('getGuwenById', guwenAPI.getById, values.id)}
              >
                <Form.Item name="id" rules={[{ required: true }]}>
                  <Input placeholder="古文ID (MongoDB ObjectId)" />
                </Form.Item>
                <Form.Item>
                  <Button 
                    type="primary" 
                    htmlType="submit" 
                    icon={<PlayCircleOutlined />}
                    loading={loading.getGuwenById}
                  >
                    获取古文详情
                  </Button>
                </Form.Item>
              </Form>
              {renderResponse('getGuwenById')}
            </APITestCard>

            <APITestCard title="搜索古文 POST /api/v1/guwen/search">
              <Form
                layout="vertical"
                onFinish={(values) => executeAPI('searchGuwen', guwenAPI.search, values)}
              >
                <Form.Item name="keyword">
                  <Input placeholder="关键词" />
                </Form.Item>
                <Form.Item name="writer">
                  <Input placeholder="作者" />
                </Form.Item>
                <Form.Item name="dynasty">
                  <Input placeholder="朝代" />
                </Form.Item>
                <Form.Item>
                  <Button 
                    type="primary" 
                    htmlType="submit" 
                    icon={<PlayCircleOutlined />}
                    loading={loading.searchGuwen}
                  >
                    搜索古文
                  </Button>
                </Form.Item>
              </Form>
              {renderResponse('searchGuwen')}
            </APITestCard>
          </Space>
        </TabPane>

        <TabPane tab="作者模块" key="writer">
          <Space direction="vertical" style={{ width: '100%' }}>
            <APITestCard title="获取作者详情 GET /api/v1/writers/{id}">
              <Form
                layout="inline"
                onFinish={(values) => executeAPI('getWriterById', writerAPI.getById, values.id)}
              >
                <Form.Item name="id" rules={[{ required: true }]}>
                  <Input placeholder="作者ID (MongoDB ObjectId)" />
                </Form.Item>
                <Form.Item>
                  <Button 
                    type="primary" 
                    htmlType="submit" 
                    icon={<PlayCircleOutlined />}
                    loading={loading.getWriterById}
                  >
                    获取作者详情
                  </Button>
                </Form.Item>
              </Form>
              {renderResponse('getWriterById')}
            </APITestCard>
          </Space>
        </TabPane>

        <TabPane tab="名句模块" key="sentence">
          <Space direction="vertical" style={{ width: '100%' }}>
            <APITestCard title="获取名句列表 GET /api/v1/sentences">
              <Form
                layout="inline"
                onFinish={(values) => executeAPI('getSentenceList', sentenceAPI.getList, values)}
              >
                <Form.Item name="page">
                  <Input placeholder="页码 (默认1)" />
                </Form.Item>
                <Form.Item name="size">
                  <Input placeholder="每页数量 (默认20)" />
                </Form.Item>
                <Form.Item>
                  <Button
                    type="primary"
                    htmlType="submit"
                    icon={<PlayCircleOutlined />}
                    loading={loading.getSentenceList}
                  >
                    获取名句列表
                  </Button>
                </Form.Item>
              </Form>
              {renderResponse('getSentenceList')}
            </APITestCard>
          </Space>
        </TabPane>

        <TabPane tab="快速测试" key="quick">
          <Space direction="vertical" style={{ width: '100%' }}>
            <APITestCard title="快速API连通性测试">
              <Space wrap>
                <Button
                  onClick={() => executeAPI('quickTest1', () => guwenAPI.getList({ page: 1, size: 5 }))}
                  loading={loading.quickTest1}
                >
                  测试古文列表
                </Button>
                <Button
                  onClick={() => executeAPI('quickTest2', () => sentenceAPI.getList({ page: 1, size: 5 }))}
                  loading={loading.quickTest2}
                >
                  测试名句列表
                </Button>
                <Button
                  onClick={() => executeAPI('quickTest3', () => authAPI.checkUsername('testuser123'))}
                  loading={loading.quickTest3}
                >
                  测试用户名检查
                </Button>
              </Space>
              {renderResponse('quickTest1')}
              {renderResponse('quickTest2')}
              {renderResponse('quickTest3')}
            </APITestCard>
          </Space>
        </TabPane>
      </Tabs>
    </div>
  );
};

export default APITest;
