import React, { useState, useEffect } from 'react';
import {
  Modal,
  Card,
  Typography,
  Tag,
  Space,
  Button,
  Spin,
  Row,
  Col,
  Avatar,
  Divider,
  Empty
} from 'antd';
import {
  UserOutlined,
  BookOutlined,
  CalendarOutlined,
  EnvironmentOutlined,
  CloseOutlined,
  StarOutlined
} from '@ant-design/icons';
import { writerAPI } from '../utils/api';
import { sanitizeHtmlContent, formatTextWithLineBreaks } from '../utils/dataUtils';
import './WriterDetailModal.css';

const { Title, Paragraph, Text } = Typography;

/**
 * 安全地渲染HTML内容，支持换行符和基本格式化
 * @param {string} htmlContent - 包含HTML标签的内容
 * @returns {object} - React元素或null
 */
const renderFormattedContent = (htmlContent) => {
  if (!htmlContent || typeof htmlContent !== 'string') {
    return null;
  }

  // 使用工具函数进行安全的HTML内容处理
  const sanitizedContent = sanitizeHtmlContent(htmlContent, {
    allowLineBreaks: true,
    allowBasicFormatting: true
  });

  return (
    <div
      className="formatted-content"
      dangerouslySetInnerHTML={{ __html: sanitizedContent }}
    />
  );
};

/**
 * 解析并渲染结构化的详细介绍内容
 * @param {string} detailIntro - 详细介绍内容
 * @returns {object} - React元素
 */
const renderDetailIntro = (detailIntro) => {
  if (!detailIntro) {
    return (
      <Empty
        description="暂无详细介绍"
        image={Empty.PRESENTED_IMAGE_SIMPLE}
        style={{ padding: '20px' }}
      />
    );
  }

  // 检查是否是结构化的HTML内容（包含我们的CSS类）
  if (detailIntro.includes('detail-section') && detailIntro.includes('section-title')) {
    return renderFormattedContent(detailIntro);
  }

  // 如果是普通文本，使用工具函数进行换行处理
  const formattedText = formatTextWithLineBreaks(detailIntro);

  return renderFormattedContent(formattedText);
};

// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "writer-detail-modal-component"
//   Timestamp: "2025-08-08T12:17:47+08:00"
//   Authoring_Subagent: "PM-内置顾问团"
//   Principle_Applied: "SOLID-S (单一职责原则)"
//   Quality_Check: "组件功能完整，支持弹窗显示文人详细信息。"
// }}
// {{START_MODIFICATIONS}}

const WriterDetailModal = ({ visible, onClose, writerId }) => {
  const [writer, setWriter] = useState(null);
  const [loading, setLoading] = useState(false);

  // 当writerId变化时加载作者详情
  useEffect(() => {
    if (visible && writerId) {
      loadWriterDetail();
    }
  }, [visible, writerId]);

  // 加载作者详情
  const loadWriterDetail = async () => {
    if (!writerId) return;

    setLoading(true);
    try {
      const response = await writerAPI.getById(writerId);
      if (response.code === 200) {
        setWriter(response.data);
      } else {
        console.error('获取作者详情失败:', response.message);
        setWriter(null);
      }
    } catch (error) {
      console.error('Failed to load writer detail:', error);
      setWriter(null);
    } finally {
      setLoading(false);
    }
  };

  // 关闭弹窗时重置状态
  const handleClose = () => {
    setWriter(null);
    onClose();
  };

  return (
    <Modal
      title={null}
      open={visible}
      onCancel={handleClose}
      footer={null}
      width={1000}
      style={{ top: 20 }}
      bodyStyle={{
        height: '80vh',
        maxHeight: '700px',
        minHeight: '500px',
        padding: 0,
        overflow: 'hidden'
      }}
      className="writer-detail-modal"
      closeIcon={<CloseOutlined style={{ fontSize: 18, color: '#666' }} />}
    >
      {loading ? (
        <div style={{ textAlign: 'center', padding: '50px 0' }}>
          <Spin size="large" />
        </div>
      ) : writer ? (
        <div style={{ height: '100%', padding: '16px', boxSizing: 'border-box' }}>
          <Row gutter={16} style={{ height: '100%' }}>
            {/* 左侧：作者基本信息 */}
            <Col span={10} style={{ height: '100%' }}>
              <Card
                className="scrollable-card"
                style={{
                  height: '100%',
                  border: 'none'
                }}
              >
                <div style={{ textAlign: 'center', marginBottom: 24 }}>
                {writer.headImageUrl ? (
                  <img 
                    src={writer.headImageUrl} 
                    alt={writer.name}
                    style={{ 
                      width: 120, 
                      height: 120, 
                      borderRadius: '50%',
                      objectFit: 'cover',
                      border: '4px solid #f0f0f0'
                    }}
                  />
                ) : (
                  <Avatar 
                    size={120} 
                    icon={<UserOutlined />}
                    style={{ 
                      backgroundColor: '#1890ff',
                      fontSize: '36px'
                    }}
                  >
                    {writer.name?.charAt(0)}
                  </Avatar>
                )}
                
                <Title level={3} style={{ marginTop: 16, marginBottom: 8 }}>
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
                  
                  {writer.birthplace && (
                    <Space>
                      <EnvironmentOutlined />
                      <Text type="secondary">{writer.birthplace}</Text>
                    </Space>
                  )}
                </Space>
              </div>

              {/* 简介 */}
              {writer.simpleIntro && (
                <div>
                  <Title level={5}>简介</Title>
                  <Paragraph style={{ fontSize: '14px', lineHeight: '1.6' }}>
                    {writer.simpleIntro}
                  </Paragraph>
                </div>
              )}

              {/* 主要成就 */}
              {writer.achievements && writer.achievements.length > 0 && (
                <div style={{ marginTop: 16 }}>
                  <Title level={5}>主要成就</Title>
                  <div>
                    {writer.achievements.map((achievement, index) => (
                      <Tag key={index} color="green" style={{ marginBottom: 4 }}>
                        <StarOutlined /> {achievement}
                      </Tag>
                    ))}
                  </div>
                </div>
              )}
              </Card>
            </Col>

            {/* 右侧：详细信息 */}
            <Col span={14} style={{ height: '100%' }}>
              <Card
                className="scrollable-card"
                style={{
                  height: '100%',
                  border: 'none'
                }}
              >
                  {/* 详细介绍 */}
                  <div style={{ marginBottom: 16 }}>
                    <Title level={4} style={{ marginBottom: 12, color: '#1890ff' }}>详细介绍</Title>
                    {renderDetailIntro(writer.detailIntro)}
                  </div>

                  {/* 代表作品 */}
                  <div style={{ marginBottom: 16 }}>
                    <Title level={4} style={{ marginBottom: 12, color: '#1890ff' }}>代表作品</Title>
                    {writer.masterpieces && writer.masterpieces.length > 0 ? (
                      <div>
                        {writer.masterpieces.map((work, index) => (
                          <Tag key={index} color="orange" style={{ marginBottom: 8, marginRight: 8 }}>
                            <BookOutlined /> {work}
                          </Tag>
                        ))}
                      </div>
                    ) : (
                      <Empty
                        description="暂无代表作品信息"
                        image={Empty.PRESENTED_IMAGE_SIMPLE}
                        style={{ padding: '20px' }}
                      />
                    )}
                  </div>

                  {/* 操作按钮 */}
                  <div style={{ textAlign: 'center', marginTop: 24 }}>
                    <Space>
                      <Button
                        type="primary"
                        href={`/poems?writer=${encodeURIComponent(writer.name)}`}
                        target="_blank"
                      >
                        查看相关诗词
                      </Button>
                      <Button
                        href={`/writers/${writer.id}`}
                        target="_blank"
                      >
                        查看完整资料
                      </Button>
                    </Space>
                  </div>
              </Card>
            </Col>
          </Row>
        </div>
      ) : (
        <div style={{ textAlign: 'center', padding: '50px 0' }}>
          <Empty description="未找到作者信息" />
        </div>
      )}
    </Modal>
  );
};

export default WriterDetailModal;

// {{END_MODIFICATIONS}}
