// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "a10516a2-0a17-45db-bdb6-8c12d7447829"
//   Timestamp: "2025-08-08T18:30:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "批量操作优化，用户体验提升"
//   Quality_Check: "批量收藏操作高效，进度反馈清晰。"
// }}
// {{START_MODIFICATIONS}}
import React, { useState } from 'react';
import {
  Button,
  Modal,
  message,
  Progress,
  Space,
  Typography,
  List,
  Tag
} from 'antd';
import {
  StarOutlined,
  FolderOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined
} from '@ant-design/icons';
import { favoriteAPI } from '../utils/api';
import FolderSelector from './FolderSelector';

const { Text } = Typography;

/**
 * 批量收藏按钮组件
 * 支持批量收藏多个项目到指定收藏夹
 */
const BatchFavoriteButton = ({ 
  selectedItems = [], 
  onBatchComplete,
  disabled = false,
  size = "middle"
}) => {
  // 状态管理
  const [modalVisible, setModalVisible] = useState(false);
  const [selectedFolder, setSelectedFolder] = useState('默认收藏夹');
  const [processing, setProcessing] = useState(false);
  const [progress, setProgress] = useState(0);
  const [results, setResults] = useState([]);

  // 开始批量收藏
  const handleBatchFavorite = async () => {
    if (!selectedItems.length) {
      message.warning('请先选择要收藏的项目');
      return;
    }

    if (!selectedFolder) {
      message.warning('请选择收藏夹');
      return;
    }

    setProcessing(true);
    setProgress(0);
    setResults([]);

    const totalItems = selectedItems.length;
    const batchResults = [];

    try {
      for (let i = 0; i < totalItems; i++) {
        const item = selectedItems[i];
        
        try {
          const response = await favoriteAPI.addFavorite({
            targetId: item.id,
            targetType: item.type || 'guwen',
            folderName: selectedFolder,
            notes: `批量收藏 - ${item.title || item.id}`
          });

          if (response.code === 200) {
            batchResults.push({
              ...item,
              status: 'success',
              message: '收藏成功'
            });
          } else {
            batchResults.push({
              ...item,
              status: 'error',
              message: response.message || '收藏失败'
            });
          }
        } catch (error) {
          console.error('批量收藏项目失败:', item.id, error);
          batchResults.push({
            ...item,
            status: 'error',
            message: '网络错误'
          });
        }

        // 更新进度
        const currentProgress = Math.round(((i + 1) / totalItems) * 100);
        setProgress(currentProgress);
        setResults([...batchResults]);

        // 添加小延迟避免请求过于频繁
        if (i < totalItems - 1) {
          await new Promise(resolve => setTimeout(resolve, 100));
        }
      }

      // 统计结果
      const successCount = batchResults.filter(r => r.status === 'success').length;
      const errorCount = batchResults.filter(r => r.status === 'error').length;

      if (successCount > 0) {
        message.success(`成功收藏 ${successCount} 个项目到 ${selectedFolder}`);
      }
      if (errorCount > 0) {
        message.error(`${errorCount} 个项目收藏失败`);
      }

      // 回调通知父组件
      if (onBatchComplete) {
        onBatchComplete(batchResults, selectedFolder);
      }

    } catch (error) {
      console.error('批量收藏操作失败:', error);
      message.error('批量收藏操作失败，请稍后重试');
    } finally {
      setProcessing(false);
    }
  };

  // 关闭对话框
  const handleCancel = () => {
    if (processing) {
      Modal.confirm({
        title: '确认取消',
        content: '批量收藏正在进行中，确定要取消吗？',
        onOk: () => {
          setModalVisible(false);
          setProcessing(false);
          setProgress(0);
          setResults([]);
        }
      });
    } else {
      setModalVisible(false);
      setProgress(0);
      setResults([]);
    }
  };

  // 渲染结果列表
  const renderResults = () => {
    if (!results.length) return null;

    return (
      <div style={{ marginTop: 16 }}>
        <Text strong>处理结果：</Text>
        <List
          size="small"
          dataSource={results}
          renderItem={(item) => (
            <List.Item>
              <Space>
                {item.status === 'success' ? (
                  <CheckCircleOutlined style={{ color: '#52c41a' }} />
                ) : (
                  <ExclamationCircleOutlined style={{ color: '#ff4d4f' }} />
                )}
                <span>{item.title || item.id}</span>
                <Tag color={item.status === 'success' ? 'green' : 'red'}>
                  {item.message}
                </Tag>
              </Space>
            </List.Item>
          )}
          style={{ maxHeight: 200, overflow: 'auto' }}
        />
      </div>
    );
  };

  return (
    <>
      <Button
        icon={<StarOutlined />}
        onClick={() => setModalVisible(true)}
        disabled={disabled || !selectedItems.length}
        size={size}
      >
        批量收藏 ({selectedItems.length})
      </Button>

      <Modal
        title="批量收藏"
        open={modalVisible}
        onCancel={handleCancel}
        footer={[
          <Button key="cancel" onClick={handleCancel} disabled={processing}>
            取消
          </Button>,
          <Button
            key="submit"
            type="primary"
            onClick={handleBatchFavorite}
            loading={processing}
            disabled={!selectedItems.length || !selectedFolder}
          >
            {processing ? '收藏中...' : '开始收藏'}
          </Button>
        ]}
        width={600}
        destroyOnClose={!processing}
        maskClosable={!processing}
      >
        <div>
          <div style={{ marginBottom: 16 }}>
            <Text>
              将选中的 <Text strong>{selectedItems.length}</Text> 个项目收藏到：
            </Text>
          </div>

          <div style={{ marginBottom: 16 }}>
            <FolderSelector
              value={selectedFolder}
              onChange={setSelectedFolder}
              placeholder="选择收藏夹"
              disabled={processing}
              style={{ width: '100%' }}
            />
          </div>

          {processing && (
            <div style={{ marginBottom: 16 }}>
              <Text>收藏进度：</Text>
              <Progress 
                percent={progress} 
                status={progress === 100 ? 'success' : 'active'}
                strokeColor={{
                  '0%': '#108ee9',
                  '100%': '#87d068',
                }}
              />
            </div>
          )}

          {renderResults()}

          {selectedItems.length > 0 && !processing && (
            <div style={{ marginTop: 16 }}>
              <Text type="secondary">预览选中项目：</Text>
              <List
                size="small"
                dataSource={selectedItems.slice(0, 5)}
                renderItem={(item) => (
                  <List.Item>
                    <Text ellipsis>{item.title || item.id}</Text>
                  </List.Item>
                )}
                style={{ maxHeight: 120, overflow: 'auto' }}
              />
              {selectedItems.length > 5 && (
                <Text type="secondary">
                  还有 {selectedItems.length - 5} 个项目...
                </Text>
              )}
            </div>
          )}
        </div>
      </Modal>
    </>
  );
};

export default BatchFavoriteButton;
// {{END_MODIFICATIONS}}
