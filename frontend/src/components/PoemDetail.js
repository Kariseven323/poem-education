import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Card, Typography, Tag, Space, Button, Divider, Spin, Alert, Row, Col } from 'antd';
import { 
  ArrowLeftOutlined, 
  EyeOutlined, 
  HeartOutlined, 
  StarOutlined, 
  ShareAltOutlined,
  BookOutlined,
  UserOutlined
} from '@ant-design/icons';
import { guwenAPI } from '../utils/api';
import viewTracker from '../utils/viewTracker';
import { normalizeType } from '../utils/dataUtils';

const { Title, Paragraph, Text } = Typography;

const PoemDetail = () => {
  const { id } = useParams();
  const [poem, setPoem] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (id) {
      loadPoemDetail();
    }
  }, [id]);

  const loadPoemDetail = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await guwenAPI.getById(id);
      if (response.code === 200) {
        setPoem(response.data);
        // 记录诗词访问
        viewTracker.recordPoemView(id);
      } else {
        setError(response.message || '获取诗词详情失败');
      }
    } catch (error) {
      console.error('Failed to load poem detail:', error);
      setError('网络错误，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  const handleBack = () => {
    window.history.back();
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '50px' }}>
        <Spin size="large" />
        <div style={{ marginTop: 16 }}>加载中...</div>
      </div>
    );
  }

  if (error) {
    return (
      <Alert
        message="加载失败"
        description={error}
        type="error"
        showIcon
        action={
          <Space>
            <Button size="small" onClick={loadPoemDetail}>
              重试
            </Button>
            <Button size="small" onClick={handleBack}>
              返回
            </Button>
          </Space>
        }
      />
    );
  }

  if (!poem) {
    return (
      <Alert
        message="诗词不存在"
        description="您访问的诗词可能已被删除或不存在"
        type="warning"
        showIcon
        action={
          <Button onClick={handleBack}>返回</Button>
        }
      />
    );
  }

  return (
    <div>
      {/* 返回按钮 */}
      <Button 
        icon={<ArrowLeftOutlined />} 
        onClick={handleBack}
        style={{ marginBottom: 16 }}
      >
        返回列表
      </Button>

      <Row gutter={24}>
        <Col span={16}>
          {/* 诗词主体 */}
          <Card>
            <div style={{ textAlign: 'center', marginBottom: 24 }}>
              <Title level={1} style={{ marginBottom: 8 }}>
                {poem.title}
              </Title>
              <Space>
                <Tag color="blue" icon={<BookOutlined />}>{poem.dynasty}</Tag>
                <Tag color="green" icon={<UserOutlined />}>{poem.writer}</Tag>
                {normalizeType(poem.type).map(t => (
                  <Tag key={t} color="orange">{t}</Tag>
                ))}
              </Space>
            </div>

            {/* 诗词内容 */}
            <div style={{ 
              textAlign: 'center', 
              padding: '24px',
              background: '#fafafa',
              borderRadius: '8px',
              marginBottom: 24
            }}>
              <div 
                className="poem-content" 
                style={{ 
                  fontSize: '20px',
                  lineHeight: '2',
                  fontFamily: 'KaiTi, 楷体, serif'
                }}
              >
                {poem.content}
              </div>
            </div>

            {/* 统计信息 */}
            <div style={{ textAlign: 'center', marginBottom: 24 }}>
              <Space size="large">
                <Space>
                  <EyeOutlined />
                  <Text>阅读 {poem.stats?.viewCount || 0}</Text>
                </Space>
                <Space>
                  <HeartOutlined />
                  <Text>点赞 {poem.stats?.likeCount || 0}</Text>
                </Space>
                <Space>
                  <StarOutlined />
                  <Text>收藏 {poem.stats?.favoriteCount || 0}</Text>
                </Space>
              </Space>
            </div>

            {/* 操作按钮 */}
            <div style={{ textAlign: 'center', marginBottom: 24 }}>
              <Space>
                <Button type="primary" icon={<HeartOutlined />}>
                  点赞
                </Button>
                <Button icon={<StarOutlined />}>
                  收藏
                </Button>
                <Button icon={<ShareAltOutlined />}>
                  分享
                </Button>
              </Space>
            </div>

            <Divider />

            {/* 注释 */}
            {poem.remark && (
              <div style={{ marginBottom: 24 }}>
                <Title level={4}>注释</Title>
                <Paragraph style={{ fontSize: '14px', lineHeight: '1.8' }}>
                  {poem.remark}
                </Paragraph>
              </div>
            )}

            {/* 翻译 */}
            {poem.translation && (
              <div style={{ marginBottom: 24 }}>
                <Title level={4}>翻译</Title>
                <Paragraph style={{ fontSize: '14px', lineHeight: '1.8' }}>
                  {poem.translation}
                </Paragraph>
              </div>
            )}

            {/* 赏析 */}
            {poem.shangxi && (
              <div style={{ marginBottom: 24 }}>
                <Title level={4}>赏析</Title>
                <Paragraph style={{ fontSize: '14px', lineHeight: '1.8' }}>
                  {poem.shangxi}
                </Paragraph>
              </div>
            )}
          </Card>
        </Col>

        <Col span={8}>
          {/* 作者信息 */}
          {poem.writerInfo && (
            <Card title="作者简介" style={{ marginBottom: 16 }}>
              <div style={{ textAlign: 'center', marginBottom: 16 }}>
                {poem.writerInfo.headImageUrl ? (
                  <img 
                    src={poem.writerInfo.headImageUrl} 
                    alt={poem.writer}
                    style={{ width: 80, height: 80, borderRadius: '50%' }}
                  />
                ) : (
                  <div style={{ 
                    width: 80, 
                    height: 80, 
                    borderRadius: '50%', 
                    background: '#f0f0f0',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    margin: '0 auto',
                    fontSize: '24px',
                    color: '#999'
                  }}>
                    <UserOutlined />
                  </div>
                )}
                <Title level={4} style={{ marginTop: 8, marginBottom: 4 }}>
                  {poem.writer}
                </Title>
                <Text type="secondary">{poem.dynasty}</Text>
              </div>
              
              {poem.writerInfo.simpleIntro && (
                <Paragraph style={{ fontSize: '14px' }}>
                  {poem.writerInfo.simpleIntro}
                </Paragraph>
              )}
              
              <Button type="link" href={`/writers/${poem.writerId}`} block>
                查看详细信息
              </Button>
            </Card>
          )}

          {/* 相关推荐 */}
          <Card title="相关推荐" size="small">
            <div style={{ textAlign: 'center', color: '#999', padding: '20px' }}>
              暂无相关推荐
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default PoemDetail;
