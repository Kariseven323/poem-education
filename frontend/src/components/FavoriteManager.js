// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "a77743be-39b5-4c83-8b62-c393aedede9e"
//   Timestamp: "2025-08-08T18:20:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "React Hooks模式，组件化设计"
//   Quality_Check: "响应式设计，错误处理完善，用户体验优化。"
// }}
// {{START_MODIFICATIONS}}
import React, { useState, useEffect } from 'react';
import {
  Card,
  List,
  Button,
  Modal,
  Form,
  Input,
  Popconfirm,
  message,
  Spin,
  Empty,
  Typography,
  Space,
  Tooltip,
  Badge
} from 'antd';
import {
  FolderOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  HeartOutlined,
  SettingOutlined
} from '@ant-design/icons';
import { favoriteAPI } from '../utils/api';
import './FavoriteManager.css';

const { Title, Text } = Typography;

/**
 * 收藏夹管理组件
 * 提供收藏夹的创建、重命名、删除、统计等功能
 */
const FavoriteManager = ({ onFolderSelect, showStats = true }) => {
  // 状态管理
  const [folders, setFolders] = useState([]);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(false);
  const [createModalVisible, setCreateModalVisible] = useState(false);
  const [renameModalVisible, setRenameModalVisible] = useState(false);
  const [selectedFolder, setSelectedFolder] = useState(null);
  const [createForm] = Form.useForm();
  const [renameForm] = Form.useForm();

  // 加载收藏夹列表
  const loadFolders = async () => {
    setLoading(true);
    try {
      const response = await favoriteAPI.getFolders();
      setFolders(response.data || []);
    } catch (error) {
      console.error('加载收藏夹失败:', error);
      message.error('加载收藏夹失败，请重试');
    } finally {
      setLoading(false);
    }
  };

  // 加载统计信息
  const loadStats = async () => {
    if (!showStats) return;

    try {
      const response = await favoriteAPI.getFolderStats();
      setStats(response.data);
    } catch (error) {
      console.error('加载统计信息失败:', error);
    }
  };

  // 组件初始化
  useEffect(() => {
    loadFolders();
    loadStats();
  }, [showStats]);

  // 创建收藏夹
  const handleCreateFolder = async (values) => {
    try {
      await favoriteAPI.createFolder(values.folderName);

      message.success('收藏夹创建成功');
      setCreateModalVisible(false);
      createForm.resetFields();
      loadFolders();
      loadStats();
    } catch (error) {
      console.error('创建收藏夹失败:', error);
      message.error('创建收藏夹失败，请重试');
    }
  };

  // 重命名收藏夹
  const handleRenameFolder = async (values) => {
    try {
      await favoriteAPI.renameFolder(selectedFolder.folderName, values.newName);

      message.success('收藏夹重命名成功');
      setRenameModalVisible(false);
      setSelectedFolder(null);
      renameForm.resetFields();
      loadFolders();
    } catch (error) {
      console.error('重命名收藏夹失败:', error);
      message.error('重命名收藏夹失败，请重试');
    }
  };

  // 删除收藏夹
  const handleDeleteFolder = async (folderName) => {
    try {
      await favoriteAPI.deleteFolder(folderName);

      message.success('收藏夹删除成功');
      loadFolders();
      loadStats();
    } catch (error) {
      console.error('删除收藏夹失败:', error);
      message.error('删除收藏夹失败，请重试');
    }
  };

  // 打开重命名对话框
  const openRenameModal = (folder) => {
    setSelectedFolder(folder);
    renameForm.setFieldsValue({ newName: folder.folderName });
    setRenameModalVisible(true);
  };

  // 渲染收藏夹项
  const renderFolderItem = (folder) => {
    const isDefault = folder.isDefault;
    
    return (
      <List.Item
        className="folder-item"
        actions={[
          <Tooltip title="重命名">
            <Button
              type="text"
              icon={<EditOutlined />}
              onClick={() => openRenameModal(folder)}
              disabled={loading}
            />
          </Tooltip>,
          !isDefault && (
            <Popconfirm
              title="确定要删除这个收藏夹吗？"
              description="删除后收藏夹内的所有收藏将被清空，此操作不可恢复。"
              onConfirm={() => handleDeleteFolder(folder.folderName)}
              okText="确定"
              cancelText="取消"
              disabled={loading}
            >
              <Tooltip title="删除收藏夹">
                <Button
                  type="text"
                  danger
                  icon={<DeleteOutlined />}
                  disabled={loading}
                />
              </Tooltip>
            </Popconfirm>
          )
        ].filter(Boolean)}
        onClick={() => onFolderSelect && onFolderSelect(folder)}
        style={{ cursor: onFolderSelect ? 'pointer' : 'default' }}
      >
        <List.Item.Meta
          avatar={
            <Badge count={folder.itemCount} showZero>
              <FolderOutlined 
                style={{ 
                  fontSize: '24px', 
                  color: isDefault ? '#1890ff' : '#52c41a' 
                }} 
              />
            </Badge>
          }
          title={
            <Space>
              <Text strong>{folder.folderName}</Text>
              {isDefault && <Text type="secondary">(默认)</Text>}
            </Space>
          }
          description={`${folder.itemCount} 个收藏`}
        />
      </List.Item>
    );
  };

  return (
    <div className="favorite-manager">
      {/* 统计信息卡片 */}
      {showStats && stats && (
        <Card className="stats-card" size="small">
          <div className="stats-content">
            <div className="stat-item">
              <HeartOutlined style={{ color: '#ff4d4f' }} />
              <div>
                <div className="stat-number">{stats.totalItems}</div>
                <div className="stat-label">总收藏</div>
              </div>
            </div>
            <div className="stat-item">
              <FolderOutlined style={{ color: '#1890ff' }} />
              <div>
                <div className="stat-number">{stats.totalFolders}</div>
                <div className="stat-label">收藏夹</div>
              </div>
            </div>
          </div>
        </Card>
      )}

      {/* 收藏夹列表 */}
      <Card
        title={
          <Space>
            <SettingOutlined />
            <span>收藏夹管理</span>
          </Space>
        }
        extra={
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => setCreateModalVisible(true)}
            disabled={loading}
          >
            新建收藏夹
          </Button>
        }
        className="folders-card"
      >
        <Spin spinning={loading}>
          {folders.length > 0 ? (
            <List
              dataSource={folders}
              renderItem={renderFolderItem}
              className="folders-list"
            />
          ) : (
            <Empty
              description="暂无收藏夹"
              image={Empty.PRESENTED_IMAGE_SIMPLE}
            />
          )}
        </Spin>
      </Card>

      {/* 创建收藏夹对话框 */}
      <Modal
        title="创建新收藏夹"
        open={createModalVisible}
        onCancel={() => {
          setCreateModalVisible(false);
          createForm.resetFields();
        }}
        footer={null}
        destroyOnClose
      >
        <Form
          form={createForm}
          layout="vertical"
          onFinish={handleCreateFolder}
        >
          <Form.Item
            name="folderName"
            label="收藏夹名称"
            rules={[
              { required: true, message: '请输入收藏夹名称' },
              { max: 50, message: '收藏夹名称不能超过50个字符' },
              { 
                validator: (_, value) => {
                  if (value && folders.some(f => f.folderName === value)) {
                    return Promise.reject(new Error('收藏夹名称已存在'));
                  }
                  return Promise.resolve();
                }
              }
            ]}
          >
            <Input placeholder="请输入收藏夹名称" maxLength={50} />
          </Form.Item>
          <Form.Item className="modal-buttons">
            <Space>
              <Button onClick={() => setCreateModalVisible(false)}>
                取消
              </Button>
              <Button type="primary" htmlType="submit">
                创建
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>

      {/* 重命名收藏夹对话框 */}
      <Modal
        title="重命名收藏夹"
        open={renameModalVisible}
        onCancel={() => {
          setRenameModalVisible(false);
          setSelectedFolder(null);
          renameForm.resetFields();
        }}
        footer={null}
        destroyOnClose
      >
        <Form
          form={renameForm}
          layout="vertical"
          onFinish={handleRenameFolder}
        >
          <Form.Item
            name="newName"
            label="新名称"
            rules={[
              { required: true, message: '请输入新的收藏夹名称' },
              { max: 50, message: '收藏夹名称不能超过50个字符' },
              { 
                validator: (_, value) => {
                  if (value && value !== selectedFolder?.folderName && 
                      folders.some(f => f.folderName === value)) {
                    return Promise.reject(new Error('收藏夹名称已存在'));
                  }
                  return Promise.resolve();
                }
              }
            ]}
          >
            <Input placeholder="请输入新的收藏夹名称" maxLength={50} />
          </Form.Item>
          <Form.Item className="modal-buttons">
            <Space>
              <Button onClick={() => setRenameModalVisible(false)}>
                取消
              </Button>
              <Button type="primary" htmlType="submit">
                确定
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default FavoriteManager;
// {{END_MODIFICATIONS}}
