import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Typography, Tag, Space, Button, Divider, Spin, Alert, Row, Col, Avatar } from 'antd';
import { 
  ArrowLeftOutlined, 
  UserOutlined,
  BookOutlined,
  CalendarOutlined,
  EnvironmentOutlined
} from '@ant-design/icons';
import { writerAPI } from '../utils/api';

const { Title, Paragraph, Text } = Typography;

const WriterDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [writer, setWriter] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (id) {
      loadWriterDetail();
    }
  }, [id]);

  const loadWriterDetail = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await writerAPI.getById(id);
      if (response.code === 200) {
        setWriter(response.data);
      } else {
        setError(response.message || '获取作者详情失败');
      }
    } catch (error) {
      console.error('Failed to load writer detail:', error);
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
            <Button size="small" onClick={loadWriterDetail}>
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

  if (!writer) {
    return (
      <Alert
        message="作者不存在"
        description="您访问的作者可能已被删除或不存在"
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
        <Col span={8}>
          {/* 作者基本信息 */}
          <Card>
            <div style={{ textAlign: 'center', marginBottom: 24 }}>
              {writer.headImageUrl ? (
                <img 
                  src={writer.headImageUrl} 
                  alt={writer.name}
                  style={{ 
                    width: 150, 
                    height: 150, 
                    borderRadius: '50%',
                    objectFit: 'cover',
                    border: '4px solid #f0f0f0'
                  }}
                />
              ) : (
                <Avatar 
                  size={150} 
                  icon={<UserOutlined />}
                  style={{ 
                    backgroundColor: '#1890ff',
                    fontSize: '48px'
                  }}
                >
                  {writer.name?.charAt(0)}
                </Avatar>
              )}
              
              <Title level={2} style={{ marginTop: 16, marginBottom: 8 }}>
                {writer.name}
              </Title>
              
              <Space direction="vertical" size="small">
                <Tag color="blue" icon={<BookOutlined />}>
                  {writer.dynasty}
                </Tag>
                
                {writer.lifespan && (
                  <Space>
                    <CalendarOutlined />
                    <Text type="secondary">{writer.lifespan}</Text>
                  </Space>
                )}
                
                {writer.alias && (
                  <div>
                    <Text type="secondary">字号：{writer.alias}</Text>
                  </div>
                )}
              </Space>
            </div>

            {/* 简介 */}
            {writer.simpleIntro && (
              <div>
                <Title level={4}>简介</Title>
                <Paragraph style={{ fontSize: '14px', lineHeight: '1.6' }}>
                  {writer.simpleIntro}
                </Paragraph>
              </div>
            )}
          </Card>
        </Col>

        <Col span={16}>
          {/* 详细介绍 */}
          <Card title="详细介绍">
            {writer.detailIntro ? (
              <div>
                <Paragraph style={{ fontSize: '14px', lineHeight: '1.8' }}>
                  {writer.detailIntro}
                </Paragraph>
              </div>
            ) : (
              <div style={{ 
                textAlign: 'center', 
                color: '#999', 
                padding: '40px',
                background: '#fafafa',
                borderRadius: '8px'
              }}>
                <BookOutlined style={{ fontSize: '48px', marginBottom: 16 }} />
                <div>暂无详细介绍</div>
              </div>
            )}
          </Card>

          {/* 代表作品 */}
          <Card title="代表作品" style={{ marginTop: 16 }}>
            <div style={{ 
              textAlign: 'center', 
              color: '#999', 
              padding: '40px',
              background: '#fafafa',
              borderRadius: '8px'
            }}>
              <BookOutlined style={{ fontSize: '48px', marginBottom: 16 }} />
              <div>作品列表功能开发中...</div>
              <Button
                type="link"
                onClick={() => navigate(`/poems?writer=${encodeURIComponent(writer.name)}`)}
                style={{ marginTop: 8 }}
              >
                查看相关诗词
              </Button>
            </div>
          </Card>

          {/* 生平年表 */}
          <Card title="生平年表" style={{ marginTop: 16 }}>
            <div style={{ 
              textAlign: 'center', 
              color: '#999', 
              padding: '40px',
              background: '#fafafa',
              borderRadius: '8px'
            }}>
              <CalendarOutlined style={{ fontSize: '48px', marginBottom: 16 }} />
              <div>生平年表功能开发中...</div>
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default WriterDetail;
