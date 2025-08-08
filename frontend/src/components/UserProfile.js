import React, { useState, useEffect } from 'react';
import {
  Card,
  Form,
  Input,
  Button,
  Avatar,
  Typography,
  Space,
  message,
  Divider,
  List,
  Tag,
  Empty,
  Spin,
  Row,
  Col,
  Pagination,
  Tabs,
  Badge
} from 'antd';
import {
  UserOutlined,
  EditOutlined,
  SaveOutlined,
  BookOutlined,
  EyeOutlined,
  CalendarOutlined,
  TagOutlined,
  ThunderboltOutlined,
  HeartOutlined,
  FolderOutlined,
  SettingOutlined
} from '@ant-design/icons';
import { userAPI, creationAPI, favoriteAPI } from '../utils/api';
import { useNavigate } from 'react-router-dom';
import moment from 'moment';
import FavoriteManager from './FavoriteManager';

const { Title, Text } = Typography;
const { TextArea } = Input;

const UserProfile = ({ user }) => {
  const navigate = useNavigate();
  const [editing, setEditing] = useState(false);
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();

  // Tab相关状态
  const [activeTab, setActiveTab] = useState('profile');

  // 用户创作相关状态
  const [myCreations, setMyCreations] = useState([]);
  const [creationsLoading, setCreationsLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize] = useState(6);
  const [total, setTotal] = useState(0);

  // 收藏相关状态
  const [favoriteStats, setFavoriteStats] = useState(null);
  const [myFavorites, setMyFavorites] = useState([]);
  const [favoritesLoading, setFavoritesLoading] = useState(false);
  const [favoritePage, setFavoritePage] = useState(1);
  const [favoriteTotal, setFavoriteTotal] = useState(0);
  const [selectedFolder, setSelectedFolder] = useState(null);

  // 加载用户创作列表
  const loadMyCreations = async (page = 1) => {
    setCreationsLoading(true);
    try {
      const response = await creationAPI.getMyList({
        page: page,
        pageSize: pageSize
      });
      if (response.code === 200) {
        setMyCreations(response.data.list || []);
        setTotal(response.data.total || 0);
        setCurrentPage(page);
      } else {
        message.error(response.message || '获取创作列表失败');
      }
    } catch (error) {
      console.error('Failed to load my creations:', error);
      message.error('获取创作列表失败，请稍后重试');
    } finally {
      setCreationsLoading(false);
    }
  };

  // 加载收藏统计信息
  const loadFavoriteStats = async () => {
    try {
      const response = await favoriteAPI.getFolderStats();
      if (response.code === 200) {
        setFavoriteStats(response.data);
      }
    } catch (error) {
      console.error('Failed to load favorite stats:', error);
    }
  };

  // 加载用户收藏列表
  const loadMyFavorites = async (page = 1, folderName = null) => {
    setFavoritesLoading(true);
    try {
      const params = {
        page: page,
        size: pageSize
      };
      if (folderName) {
        params.folderName = folderName;
      }

      const response = await favoriteAPI.getFavorites(params);
      if (response.code === 200) {
        setMyFavorites(response.data.list || []);
        setFavoriteTotal(response.data.total || 0);
        setFavoritePage(page);
      } else {
        message.error(response.message || '获取收藏列表失败');
      }
    } catch (error) {
      console.error('Failed to load my favorites:', error);
      message.error('获取收藏列表失败，请稍后重试');
    } finally {
      setFavoritesLoading(false);
    }
  };

  // 处理收藏夹选择
  const handleFolderSelect = (folder) => {
    setSelectedFolder(folder);
    loadMyFavorites(1, folder.folderName);
  };

  // 组件挂载时加载数据
  useEffect(() => {
    loadMyCreations();
    loadFavoriteStats();
  }, []);

  // Tab切换时加载对应数据
  useEffect(() => {
    if (activeTab === 'favorites' && myFavorites.length === 0) {
      loadMyFavorites();
    }
  }, [activeTab]);

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

  // 渲染个人信息Tab内容
  const renderProfileTab = () => (
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
  );

  // 渲染我的创作Tab内容
  const renderCreationsTab = () => (
    <Card
      title={
        <Space>
          <BookOutlined />
          我的创作
          <Tag color="blue">{total}</Tag>
        </Space>
      }
      extra={
        <Button
          type="primary"
          icon={<EditOutlined />}
          onClick={() => navigate('/creations')}
        >
          创作新诗词
        </Button>
      }
    >
      <Spin spinning={creationsLoading}>
        {myCreations.length > 0 ? (
          <>
            <List
              grid={{
                gutter: 16,
                xs: 1,
                sm: 2,
                md: 2,
                lg: 3,
                xl: 3,
                xxl: 3,
              }}
              dataSource={myCreations}
              renderItem={(creation) => (
                <List.Item>
                  <Card
                    size="small"
                    hoverable
                    onClick={() => navigate(`/creations/${creation.id}`)}
                    style={{ height: '100%' }}
                    bodyStyle={{ padding: '16px' }}
                  >
                    <div style={{ marginBottom: 12 }}>
                      <Typography.Title
                        level={5}
                        style={{
                          margin: 0,
                          fontSize: '16px',
                          overflow: 'hidden',
                          textOverflow: 'ellipsis',
                          whiteSpace: 'nowrap'
                        }}
                      >
                        {creation.title}
                      </Typography.Title>
                    </div>

                    <div style={{ marginBottom: 12 }}>
                      <Typography.Paragraph
                        style={{
                          margin: 0,
                          fontSize: '14px',
                          color: '#666',
                          height: '60px',
                          overflow: 'hidden',
                          display: '-webkit-box',
                          WebkitLineClamp: 3,
                          WebkitBoxOrient: 'vertical'
                        }}
                      >
                        {creation.content}
                      </Typography.Paragraph>
                    </div>

                    <div style={{ marginBottom: 8 }}>
                      <Space wrap size="small">
                        {creation.style && (
                          <Tag color="blue" size="small">
                            <TagOutlined style={{ fontSize: '10px' }} />
                            {creation.style}
                          </Tag>
                        )}
                        {creation.aiScore && creation.aiScore.totalScore && (
                          <Tag color="orange" size="small">
                            <ThunderboltOutlined style={{ fontSize: '10px' }} />
                            {creation.aiScore.totalScore}分
                          </Tag>
                        )}
                        {creation.isPublic && (
                          <Tag color="green" size="small">
                            <EyeOutlined style={{ fontSize: '10px' }} />
                            公开
                          </Tag>
                        )}
                      </Space>
                    </div>

                    <div style={{
                      fontSize: '12px',
                      color: '#999',
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center'
                    }}>
                      <span>
                        <CalendarOutlined style={{ marginRight: 4 }} />
                        {moment(creation.createdAt).format('MM-DD')}
                      </span>
                      <span>点击查看详情</span>
                    </div>
                  </Card>
                </List.Item>
              )}
            />

            {total > pageSize && (
              <div style={{ textAlign: 'center', marginTop: 24 }}>
                <Pagination
                  current={currentPage}
                  total={total}
                  pageSize={pageSize}
                  onChange={(page) => loadMyCreations(page)}
                  showSizeChanger={false}
                  showQuickJumper
                  showTotal={(total, range) =>
                    `第 ${range[0]}-${range[1]} 条，共 ${total} 条创作`
                  }
                />
              </div>
            )}
          </>
        ) : (
          <Empty
            image={Empty.PRESENTED_IMAGE_SIMPLE}
            description={
              <div>
                <div style={{ marginBottom: 8 }}>还没有创作任何诗词</div>
                <Button
                  type="primary"
                  icon={<EditOutlined />}
                  onClick={() => navigate('/creations')}
                >
                  开始创作
                </Button>
              </div>
            }
          />
        )}
      </Spin>
    </Card>
  );

  // 渲染我的收藏Tab内容
  const renderFavoritesTab = () => (
    <div>
      {/* 收藏统计信息 */}
      {favoriteStats && (
        <Card size="small" style={{ marginBottom: 16 }}>
          <Row gutter={16}>
            <Col span={8}>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#1890ff' }}>
                  {favoriteStats.totalItems}
                </div>
                <div style={{ color: '#666' }}>总收藏</div>
              </div>
            </Col>
            <Col span={8}>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#52c41a' }}>
                  {favoriteStats.totalFolders}
                </div>
                <div style={{ color: '#666' }}>收藏夹</div>
              </div>
            </Col>
            <Col span={8}>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#fa8c16' }}>
                  {selectedFolder ? favoriteStats.folderCounts[selectedFolder.folderName] || 0 : favoriteStats.totalItems}
                </div>
                <div style={{ color: '#666' }}>
                  {selectedFolder ? `${selectedFolder.folderName}` : '全部收藏'}
                </div>
              </div>
            </Col>
          </Row>
        </Card>
      )}

      {/* 收藏内容列表 */}
      <Card
        title={
          <Space>
            <HeartOutlined />
            我的收藏
            {selectedFolder && (
              <>
                <span>-</span>
                <Tag color="blue">{selectedFolder.folderName}</Tag>
              </>
            )}
          </Space>
        }
        extra={
          selectedFolder && (
            <Button
              size="small"
              onClick={() => {
                setSelectedFolder(null);
                loadMyFavorites(1);
              }}
            >
              查看全部
            </Button>
          )
        }
      >
        <Spin spinning={favoritesLoading}>
          {myFavorites.length > 0 ? (
            <>
              <List
                dataSource={myFavorites}
                renderItem={(favorite) => (
                  <List.Item
                    actions={[
                      <Button
                        type="text"
                        size="small"
                        onClick={() => {
                          // TODO: 实现收藏项详情查看
                          message.info('查看收藏详情功能开发中...');
                        }}
                      >
                        查看
                      </Button>
                    ]}
                  >
                    <List.Item.Meta
                      avatar={
                        <Avatar
                          icon={
                            favorite.targetType === 'guwen' ? <BookOutlined /> :
                            favorite.targetType === 'sentence' ? <TagOutlined /> :
                            <UserOutlined />
                          }
                          style={{
                            backgroundColor:
                              favorite.targetType === 'guwen' ? '#1890ff' :
                              favorite.targetType === 'sentence' ? '#52c41a' :
                              '#fa8c16'
                          }}
                        />
                      }
                      title={
                        <Space>
                          <span>{favorite.targetId}</span>
                          <Tag size="small">{favorite.targetType}</Tag>
                          <Tag color="blue" size="small">{favorite.folderName}</Tag>
                        </Space>
                      }
                      description={
                        <div>
                          {favorite.notes && (
                            <div style={{ marginBottom: 4 }}>{favorite.notes}</div>
                          )}
                          <Text type="secondary" style={{ fontSize: '12px' }}>
                            收藏于 {moment(favorite.createdAt).format('YYYY-MM-DD HH:mm')}
                          </Text>
                        </div>
                      }
                    />
                  </List.Item>
                )}
              />

              {favoriteTotal > pageSize && (
                <div style={{ textAlign: 'center', marginTop: 24 }}>
                  <Pagination
                    current={favoritePage}
                    total={favoriteTotal}
                    pageSize={pageSize}
                    onChange={(page) => loadMyFavorites(page, selectedFolder?.folderName)}
                    showSizeChanger={false}
                    showQuickJumper
                    showTotal={(total, range) =>
                      `第 ${range[0]}-${range[1]} 条，共 ${total} 条收藏`
                    }
                  />
                </div>
              )}
            </>
          ) : (
            <Empty
              image={Empty.PRESENTED_IMAGE_SIMPLE}
              description={
                <div>
                  <div style={{ marginBottom: 8 }}>
                    {selectedFolder ? `${selectedFolder.folderName}中暂无收藏` : '还没有任何收藏'}
                  </div>
                  <Button
                    type="primary"
                    icon={<HeartOutlined />}
                    onClick={() => navigate('/poems')}
                  >
                    去发现好内容
                  </Button>
                </div>
              }
            />
          )}
        </Spin>
      </Card>
    </div>
  );

  // 渲染收藏夹管理Tab内容
  const renderFolderManagementTab = () => (
    <FavoriteManager
      onFolderSelect={handleFolderSelect}
      showStats={true}
    />
  );

  return (
    <div>
      <Tabs
        activeKey={activeTab}
        onChange={setActiveTab}
        items={[
          {
            key: 'profile',
            label: (
              <Space>
                <UserOutlined />
                个人信息
              </Space>
            ),
            children: renderProfileTab(),
          },
          {
            key: 'creations',
            label: (
              <Space>
                <BookOutlined />
                我的创作
                <Badge count={total} showZero={false} />
              </Space>
            ),
            children: renderCreationsTab(),
          },
          {
            key: 'favorites',
            label: (
              <Space>
                <HeartOutlined />
                我的收藏
                <Badge count={favoriteStats?.totalItems || 0} showZero={false} />
              </Space>
            ),
            children: renderFavoritesTab(),
          },
          {
            key: 'folder-management',
            label: (
              <Space>
                <SettingOutlined />
                收藏夹管理
                <Badge count={favoriteStats?.totalFolders || 0} showZero={false} />
              </Space>
            ),
            children: renderFolderManagementTab(),
          },
        ]}
      />
    </div>
  );
};

export default UserProfile;
