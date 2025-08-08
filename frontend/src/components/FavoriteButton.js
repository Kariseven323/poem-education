// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "a10516a2-0a17-45db-bdb6-8c12d7447829"
//   Timestamp: "2025-08-08T18:30:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "组件复用，用户体验优化"
//   Quality_Check: "收藏操作简单直观，移动端适配良好。"
// }}
// {{START_MODIFICATIONS}}
import React, { useState, useEffect } from 'react';
import {
  Button,
  Dropdown,
  message,
  Tooltip,
  Space
} from 'antd';
import {
  StarOutlined,
  StarFilled,
  DownOutlined,
  FolderOutlined,
  PlusOutlined
} from '@ant-design/icons';
import { favoriteAPI } from '../utils/api';
import FolderSelector from './FolderSelector';

/**
 * 通用收藏按钮组件
 * 支持快速收藏和收藏夹选择功能
 */
const FavoriteButton = ({ 
  targetId, 
  targetType, 
  size = "middle",
  type = "default",
  showText = true,
  style = {},
  onFavoriteChange
}) => {
  // 状态管理
  const [isFavorited, setIsFavorited] = useState(false);
  const [currentFolder, setCurrentFolder] = useState(null);
  const [loading, setLoading] = useState(false);
  const [checkLoading, setCheckLoading] = useState(true);

  // 检查收藏状态
  const checkFavoriteStatus = async () => {
    if (!targetId || !targetType) return;
    
    setCheckLoading(true);
    try {
      const response = await favoriteAPI.checkFavorite(targetId, targetType);
      if (response.code === 200) {
        setIsFavorited(response.data.isFavorited);
        setCurrentFolder(response.data.folderName);
      }
    } catch (error) {
      console.error('检查收藏状态失败:', error);
    } finally {
      setCheckLoading(false);
    }
  };

  // 组件初始化
  useEffect(() => {
    checkFavoriteStatus();
  }, [targetId, targetType]);

  // 快速收藏/取消收藏
  const handleQuickFavorite = async () => {
    if (!targetId || !targetType) {
      message.error('缺少必要参数');
      return;
    }

    setLoading(true);
    try {
      if (isFavorited) {
        // 取消收藏
        const response = await favoriteAPI.removeFavorite(targetId, targetType);
        if (response.code === 200) {
          setIsFavorited(false);
          setCurrentFolder(null);
          message.success('已取消收藏');
          onFavoriteChange && onFavoriteChange(false, null);
        } else {
          message.error(response.message || '取消收藏失败');
        }
      } else {
        // 添加到默认收藏夹
        const response = await favoriteAPI.addFavorite({
          targetId,
          targetType,
          folderName: '默认收藏夹'
        });
        if (response.code === 200) {
          setIsFavorited(true);
          setCurrentFolder('默认收藏夹');
          message.success('已收藏到默认收藏夹');
          onFavoriteChange && onFavoriteChange(true, '默认收藏夹');
        } else {
          message.error(response.message || '收藏失败');
        }
      }
    } catch (error) {
      console.error('收藏操作失败:', error);
      message.error('操作失败，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  // 收藏到指定收藏夹
  const handleFavoriteToFolder = async (folderName) => {
    if (!targetId || !targetType || !folderName) {
      message.error('缺少必要参数');
      return;
    }

    setLoading(true);
    try {
      if (isFavorited && currentFolder === folderName) {
        // 如果已经在该收藏夹中，则取消收藏
        const response = await favoriteAPI.removeFavorite(targetId, targetType);
        if (response.code === 200) {
          setIsFavorited(false);
          setCurrentFolder(null);
          message.success('已取消收藏');
          onFavoriteChange && onFavoriteChange(false, null);
        } else {
          message.error(response.message || '取消收藏失败');
        }
      } else {
        // 收藏到指定收藏夹
        const response = await favoriteAPI.addFavorite({
          targetId,
          targetType,
          folderName
        });
        if (response.code === 200) {
          setIsFavorited(true);
          setCurrentFolder(folderName);
          message.success(`已收藏到${folderName}`);
          onFavoriteChange && onFavoriteChange(true, folderName);
        } else {
          message.error(response.message || '收藏失败');
        }
      }
    } catch (error) {
      console.error('收藏操作失败:', error);
      message.error('操作失败，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  // 构建下拉菜单项
  const getDropdownItems = () => {
    const items = [
      {
        key: 'quick',
        label: (
          <Space>
            <StarOutlined />
            {isFavorited ? '取消收藏' : '收藏到默认收藏夹'}
          </Space>
        ),
        onClick: handleQuickFavorite
      },
      {
        type: 'divider'
      },
      {
        key: 'select-folder',
        label: (
          <div style={{ padding: '8px 0' }}>
            <div style={{ marginBottom: 8, fontWeight: 'bold' }}>选择收藏夹：</div>
            <FolderSelector
              value={currentFolder}
              onChange={handleFavoriteToFolder}
              placeholder="选择收藏夹"
              size="small"
              style={{ width: '200px' }}
            />
          </div>
        ),
        onClick: (e) => e.preventDefault() // 防止点击时关闭下拉菜单
      }
    ];

    return { items };
  };

  // 渲染主按钮
  const renderMainButton = () => {
    const buttonProps = {
      size,
      type: isFavorited ? 'primary' : type,
      loading: loading || checkLoading,
      icon: isFavorited ? <StarFilled /> : <StarOutlined />,
      style: {
        ...style,
        ...(isFavorited ? { backgroundColor: '#faad14', borderColor: '#faad14' } : {})
      }
    };

    const buttonText = showText ? (
      isFavorited ? 
        (currentFolder ? `已收藏到${currentFolder}` : '已收藏') : 
        '收藏'
    ) : null;

    return (
      <Button {...buttonProps} onClick={handleQuickFavorite}>
        {buttonText}
      </Button>
    );
  };

  // 渲染下拉按钮
  const renderDropdownButton = () => (
    <Dropdown
      menu={getDropdownItems()}
      trigger={['click']}
      placement="bottomRight"
      disabled={loading || checkLoading}
    >
      <Button
        size={size}
        icon={<DownOutlined />}
        style={{ marginLeft: 0, borderLeft: 'none' }}
        loading={loading || checkLoading}
      />
    </Dropdown>
  );

  // 如果没有必要参数，返回禁用状态
  if (!targetId || !targetType) {
    return (
      <Tooltip title="缺少必要参数">
        <Button size={size} icon={<StarOutlined />} disabled>
          {showText && '收藏'}
        </Button>
      </Tooltip>
    );
  }

  return (
    <Button.Group>
      {renderMainButton()}
      {renderDropdownButton()}
    </Button.Group>
  );
};

export default FavoriteButton;
// {{END_MODIFICATIONS}}
