// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "a77743be-39b5-4c83-8b62-c393aedede9e"
//   Timestamp: "2025-08-08T18:20:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "React Hooks模式，组件复用"
//   Quality_Check: "用户体验优化，快速操作支持。"
// }}
// {{START_MODIFICATIONS}}
import React, { useState, useEffect } from 'react';
import {
  Select,
  Button,
  Modal,
  Form,
  Input,
  message,
  Space,
  Divider,
  Typography
} from 'antd';
import {
  FolderOutlined,
  PlusOutlined,
  HeartOutlined
} from '@ant-design/icons';
import { favoriteAPI } from '../utils/api';

const { Option } = Select;
const { Text } = Typography;

/**
 * 收藏夹选择器组件
 * 用于收藏时选择目标收藏夹，支持快速创建新收藏夹
 */
const FolderSelector = ({ 
  value, 
  onChange, 
  placeholder = "选择收藏夹",
  allowCreate = true,
  size = "middle",
  style = {},
  disabled = false
}) => {
  // 状态管理
  const [folders, setFolders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [createModalVisible, setCreateModalVisible] = useState(false);
  const [createForm] = Form.useForm();

  // 加载收藏夹列表
  const loadFolders = async () => {
    setLoading(true);
    try {
      const response = await favoriteAPI.getFolders();
      setFolders(response.data || []);
    } catch (error) {
      console.error('加载收藏夹失败:', error);
      message.error('加载收藏夹失败');
    } finally {
      setLoading(false);
    }
  };

  // 组件初始化
  useEffect(() => {
    loadFolders();
  }, []);

  // 创建新收藏夹
  const handleCreateFolder = async (values) => {
    try {
      await favoriteAPI.createFolder(values.folderName);

      message.success('收藏夹创建成功');
      setCreateModalVisible(false);
      createForm.resetFields();

      // 重新加载收藏夹列表
      await loadFolders();

      // 自动选择新创建的收藏夹
      if (onChange) {
        onChange(values.folderName);
      }
    } catch (error) {
      console.error('创建收藏夹失败:', error);
      message.error('创建收藏夹失败，请重试');
    }
  };

  // 打开创建对话框
  const openCreateModal = () => {
    setCreateModalVisible(true);
  };

  // 渲染收藏夹选项
  const renderFolderOption = (folder) => {
    return (
      <Option key={folder.folderName} value={folder.folderName}>
        <Space>
          <FolderOutlined 
            style={{ 
              color: folder.isDefault ? '#1890ff' : '#52c41a' 
            }} 
          />
          <span>{folder.folderName}</span>
          <Text type="secondary">({folder.itemCount})</Text>
          {folder.isDefault && <Text type="secondary">(默认)</Text>}
        </Space>
      </Option>
    );
  };

  return (
    <>
      <Select
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        loading={loading}
        disabled={disabled}
        size={size}
        style={{ minWidth: 200, ...style }}
        dropdownRender={(menu) => (
          <>
            {menu}
            {allowCreate && (
              <>
                <Divider style={{ margin: '8px 0' }} />
                <Space style={{ padding: '0 8px 4px' }}>
                  <Button
                    type="text"
                    icon={<PlusOutlined />}
                    onClick={openCreateModal}
                    size="small"
                    style={{ width: '100%', textAlign: 'left' }}
                  >
                    创建新收藏夹
                  </Button>
                </Space>
              </>
            )}
          </>
        )}
        suffixIcon={<HeartOutlined />}
      >
        {folders.map(renderFolderOption)}
      </Select>

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
        width={400}
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
            <Input 
              placeholder="请输入收藏夹名称" 
              maxLength={50}
              prefix={<FolderOutlined />}
            />
          </Form.Item>
          <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
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
    </>
  );
};

export default FolderSelector;
// {{END_MODIFICATIONS}}
