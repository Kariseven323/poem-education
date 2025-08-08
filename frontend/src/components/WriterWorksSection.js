import React, { useState, useEffect } from 'react';
import { 
  List, 
  Card, 
  Tag, 
  Space, 
  Spin, 
  Empty, 
  Button, 
  Typography,
  Modal,
  message
} from 'antd';
import { 
  BookOutlined, 
  EyeOutlined, 
  HeartOutlined, 
  StarOutlined,
  LoadingOutlined
} from '@ant-design/icons';
import { writerAPI } from '../utils/api';
import PoemDetailModal from './PoemDetailModal';
import { normalizeType } from '../utils/dataUtils';
import FavoriteButton from './FavoriteButton';

const { Text } = Typography;

// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "writer-works-section"
//   Timestamp: "2025-08-08T15:25:53+08:00"
//   Authoring_Subagent: "PM-内置顾问团"
//   Principle_Applied: "SOLID-S (单一职责原则)"
//   Quality_Check: "组件专注于展示作者作品列表，支持精确搜索和诗词详情弹窗。"
// }}
// {{START_MODIFICATIONS}}

/**
 * 作者作品展示组件
 * 根据作者名称精确搜索并展示该作者的所有诗词作品
 */
const WriterWorksSection = ({ writerId, writerName }) => {
  const [works, setWorks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showAll, setShowAll] = useState(false);
  const [selectedPoemId, setSelectedPoemId] = useState(null);
  const [poemModalVisible, setPoemModalVisible] = useState(false);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 6,
    total: 0
  });

  // 加载作者作品
  const loadWriterWorks = async (page = 1, size = 6) => {
    if (!writerName) return;

    setLoading(true);
    try {
      const response = await writerAPI.getWorksByName(writerName, {
        page,
        size
      });
      
      if (response.code === 200) {
        const result = response.data;
        setWorks(result.list || []);
        setPagination({
          current: page,
          pageSize: size,
          total: result.total || 0
        });
      } else {
        console.error('获取作者作品失败:', response.message);
        setWorks([]);
      }
    } catch (error) {
      console.error('Failed to load writer works:', error);
      message.error('加载作者作品失败');
      setWorks([]);
    } finally {
      setLoading(false);
    }
  };

  // 组件挂载时加载作品
  useEffect(() => {
    if (writerName) {
      loadWriterWorks();
    }
  }, [writerName]);

  // 处理诗词点击
  const handlePoemClick = (poemId) => {
    setSelectedPoemId(poemId);
    setPoemModalVisible(true);
  };

  // 关闭诗词详情弹窗
  const handlePoemModalClose = () => {
    setPoemModalVisible(false);
    setSelectedPoemId(null);
  };

  // 展示更多作品
  const handleShowMore = () => {
    if (showAll) {
      // 收起，重新加载前6个
      setShowAll(false);
      loadWriterWorks(1, 6);
    } else {
      // 展示所有，加载更多
      setShowAll(true);
      loadWriterWorks(1, Math.min(pagination.total, 20)); // 最多显示20个
    }
  };

  if (loading && works.length === 0) {
    return (
      <div style={{ textAlign: 'center', padding: '20px' }}>
        <Spin indicator={<LoadingOutlined style={{ fontSize: 24 }} spin />} />
        <div style={{ marginTop: 8 }}>正在加载作品...</div>
      </div>
    );
  }

  if (works.length === 0) {
    return (
      <Empty
        description={`暂无${writerName}的作品信息`}
        image={Empty.PRESENTED_IMAGE_SIMPLE}
        style={{ padding: '20px' }}
      />
    );
  }

  return (
    <>
      <div>
        <List
          grid={{ gutter: 8, xs: 1, sm: 1, md: 2, lg: 2, xl: 2 }}
          dataSource={works}
          renderItem={(poem) => (
            <List.Item>
              <Card
                size="small"
                hoverable
                onClick={() => handlePoemClick(poem.id)}
                style={{ 
                  cursor: 'pointer',
                  marginBottom: 8,
                  borderRadius: 6
                }}
                bodyStyle={{ padding: '12px' }}
              >
                <div>
                  <Text strong style={{ fontSize: '14px', color: '#1890ff' }}>
                    {poem.title}
                  </Text>
                  <div style={{ marginTop: 4 }}>
                    <Space size="small">
                      <Tag color="blue" size="small">{poem.dynasty}</Tag>
                      {normalizeType(poem.type).map(t => (
                        <Tag key={t} color="orange" size="small">{t}</Tag>
                      ))}
                    </Space>
                  </div>
                  <div 
                    style={{ 
                      marginTop: 8,
                      fontSize: '12px',
                      color: '#666',
                      lineHeight: '1.4',
                      maxHeight: '40px',
                      overflow: 'hidden',
                      display: '-webkit-box',
                      WebkitLineClamp: 2,
                      WebkitBoxOrient: 'vertical'
                    }}
                  >
                    {poem.content}
                  </div>
                  {poem.stats && (
                    <div style={{
                      marginTop: 8,
                      fontSize: '11px',
                      color: '#999',
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center'
                    }}>
                      <Space size="small">
                        <span><EyeOutlined /> {poem.stats.viewCount || 0}</span>
                        <span><HeartOutlined /> {poem.stats.likeCount || 0}</span>
                        <span><StarOutlined /> {poem.stats.favoriteCount || 0}</span>
                      </Space>
                      <FavoriteButton
                        targetId={poem.id}
                        targetType="guwen"
                        size="small"
                        type="text"
                        showText={false}
                        style={{
                          border: 'none',
                          padding: 0,
                          height: 'auto',
                          fontSize: '11px'
                        }}
                      />
                    </div>
                  )}
                </div>
              </Card>
            </List.Item>
          )}
        />
        
        {/* 显示更多按钮 */}
        {pagination.total > 6 && (
          <div style={{ textAlign: 'center', marginTop: 16 }}>
            <Button 
              type="link" 
              onClick={handleShowMore}
              loading={loading}
              icon={<BookOutlined />}
            >
              {showAll ? '收起作品' : `查看更多作品 (共${pagination.total}首)`}
            </Button>
          </div>
        )}
      </div>

      {/* 诗词详情弹窗 */}
      <PoemDetailModal
        visible={poemModalVisible}
        onClose={handlePoemModalClose}
        poemId={selectedPoemId}
      />
    </>
  );
};

// {{END_MODIFICATIONS}}

export default WriterWorksSection;
