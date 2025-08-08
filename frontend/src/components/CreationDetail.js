// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "bdb83786-54c5-4564-bf9b-decddca99cd2"
//   Timestamp: "2025-08-08T14:20:00+08:00"
//   Authoring_Subagent: "PM-标准协作模式"
//   Principle_Applied: "React组件设计最佳实践，参考PoemDetailModal布局"
//   Quality_Check: "编译通过，详情展示完整。"
// }}
// {{START_MODIFICATIONS}}
import React, { useState, useEffect } from 'react';
import {
  Card,
  Typography,
  Tag,
  Space,
  Button,
  Spin,
  Empty,
  Divider,
  Row,
  Col,
  message,
  Descriptions,
  Alert,
  Tooltip,
  Collapse
} from 'antd';
import {
  BookOutlined,
  UserOutlined,
  HeartOutlined,
  HeartFilled,
  ShareAltOutlined,
  ThunderboltOutlined,
  ReloadOutlined,
  EditOutlined,
  EyeOutlined,
  CalendarOutlined,
  TagOutlined,
  BulbOutlined,
  GlobalOutlined,
  LockOutlined
} from '@ant-design/icons';
import { useParams, useNavigate } from 'react-router-dom';
import { creationAPI } from '../utils/api';
import RadarChart from './RadarChart';
import moment from 'moment';

const { Title, Paragraph, Text } = Typography;

/**
 * 创作详情展示组件
 * 展示诗词创作详情和AI评分结果，集成雷达图可视化
 */
const CreationDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [creation, setCreation] = useState(null);
  const [radarData, setRadarData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [radarLoading, setRadarLoading] = useState(false);
  const [aiScoring, setAiScoring] = useState(false);
  const [liking, setLiking] = useState(false);
  const [publishing, setPublishing] = useState(false);

  // 获取当前用户信息
  const getCurrentUser = () => {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  };

  const currentUser = getCurrentUser();

  // 加载创作详情
  const loadCreation = async () => {
    if (!id) return;
    
    setLoading(true);
    try {
      const response = await creationAPI.getById(id);
      if (response.code === 200) {
        setCreation(response.data);
        // 如果有AI评分，同时加载雷达图数据
        if (response.data.aiScore) {
          loadRadarData();
        }
      } else {
        message.error(response.message || '获取创作详情失败');
      }
    } catch (error) {
      console.error('Failed to load creation:', error);
      message.error('获取创作详情失败，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  // 加载雷达图数据
  const loadRadarData = async () => {
    if (!id) return;
    
    setRadarLoading(true);
    try {
      const response = await creationAPI.getRadarData(id);
      if (response.code === 200) {
        console.log('雷达图数据加载成功:', response.data);
        setRadarData(response.data);
      } else {
        console.warn('获取雷达图数据失败:', response.message);
      }
    } catch (error) {
      console.error('Failed to load radar data:', error);
    } finally {
      setRadarLoading(false);
    }
  };

  // 触发AI评分
  const handleAIScore = async () => {
    if (!currentUser) {
      message.warning('请先登录');
      return;
    }

    setAiScoring(true);
    try {
      const response = await creationAPI.requestScore(id);
      if (response.code === 200) {
        message.success('AI评分请求已提交，正在分析中...');
        // 3秒后刷新数据
        setTimeout(() => {
          loadCreation();
        }, 3000);
      } else {
        message.error(response.message || 'AI评分请求失败');
      }
    } catch (error) {
      console.error('Failed to request AI score:', error);
      message.error('AI评分请求失败');
    } finally {
      setAiScoring(false);
    }
  };

  // 点赞/取消点赞
  const handleToggleLike = async () => {
    if (!currentUser) {
      message.warning('请先登录');
      return;
    }

    setLiking(true);
    try {
      const response = await creationAPI.toggleLike(id);
      if (response.code === 200) {
        setCreation(response.data);
        message.success('操作成功');
      } else {
        message.error(response.message || '操作失败');
      }
    } catch (error) {
      console.error('Failed to toggle like:', error);
      message.error('操作失败');
    } finally {
      setLiking(false);
    }
  };

  // 分享功能
  const handleShare = () => {
    const url = window.location.href;
    if (navigator.share) {
      navigator.share({
        title: creation.title,
        text: `分享一首诗词：${creation.title}`,
        url: url
      });
    } else {
      // 复制到剪贴板
      navigator.clipboard.writeText(url).then(() => {
        message.success('链接已复制到剪贴板');
      }).catch(() => {
        message.error('分享失败');
      });
    }
  };

  // 编辑创作
  const handleEdit = () => {
    navigate(`/creations/${id}/edit`);
  };

  // 刷新数据
  const handleRefresh = () => {
    loadCreation();
  };

  // 发布到社区/取消发布
  const handleTogglePublish = async () => {
    if (!creation) return;

    setPublishing(true);
    try {
      const newPublicStatus = !creation.isPublic;
      const response = await creationAPI.togglePublic(id, newPublicStatus);

      if (response.code === 200) {
        setCreation(prev => ({
          ...prev,
          isPublic: newPublicStatus
        }));
        message.success(newPublicStatus ? '已发布到诗词社区' : '已取消发布');
      } else {
        message.error(response.message || '操作失败');
      }
    } catch (error) {
      console.error('Failed to toggle publish status:', error);
      message.error('操作失败，请稍后重试');
    } finally {
      setPublishing(false);
    }
  };

  useEffect(() => {
    loadCreation();
  }, [id]);

  if (loading) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        minHeight: '400px' 
      }}>
        <Spin size="large" tip="正在加载创作详情..." />
      </div>
    );
  }

  if (!creation) {
    return (
      <div style={{ padding: '24px', textAlign: 'center' }}>
        <Empty description="创作不存在或已被删除" />
        <Button type="primary" onClick={() => navigate('/creations')}>
          返回创作列表
        </Button>
      </div>
    );
  }

  // 检查是否为作者
  const isAuthor = currentUser && currentUser.id === creation.authorId;

  return (
    <div style={{ padding: '24px', maxWidth: '1200px', margin: '0 auto' }}>
      <Row gutter={24}>
        {/* 左侧：创作详情 */}
        <Col xs={24} lg={14}>
          <Card>
            {/* 标题和操作按钮 */}
            <div style={{ marginBottom: 24 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <div style={{ flex: 1 }}>
                  <Title level={1} style={{ marginBottom: 8 }}>
                    {creation.title}
                  </Title>
                  <Space wrap>
                    {creation.style && (
                      <Tag color="blue" icon={<TagOutlined />}>
                        {creation.style}
                      </Tag>
                    )}
                    <Tag color="green" icon={<UserOutlined />}>
                      作者ID: {creation.authorId}
                    </Tag>
                    <Tag color="orange" icon={<CalendarOutlined />}>
                      {moment(creation.createdAt).format('YYYY-MM-DD')}
                    </Tag>
                  </Space>
                </div>
                
                <Space>
                  <Tooltip title="刷新">
                    <Button 
                      icon={<ReloadOutlined />} 
                      onClick={handleRefresh}
                    />
                  </Tooltip>
                  <Tooltip title="分享">
                    <Button 
                      icon={<ShareAltOutlined />} 
                      onClick={handleShare}
                    />
                  </Tooltip>
                  {isAuthor && (
                    <>
                      <Tooltip title="编辑">
                        <Button
                          icon={<EditOutlined />}
                          onClick={handleEdit}
                        />
                      </Tooltip>
                      <Tooltip title={creation.isPublic ? "取消发布" : "发布到社区"}>
                        <Button
                          type={creation.isPublic ? "default" : "primary"}
                          icon={creation.isPublic ? <LockOutlined /> : <GlobalOutlined />}
                          onClick={handleTogglePublish}
                          loading={publishing}
                        >
                          {creation.isPublic ? "取消发布" : "发布到社区"}
                        </Button>
                      </Tooltip>
                    </>
                  )}
                </Space>
              </div>
            </div>

            {/* 作品内容 */}
            <div style={{ 
              textAlign: 'center', 
              padding: '32px',
              background: '#fafafa',
              borderRadius: '8px',
              marginBottom: 24
            }}>
              <div 
                className="poem-content" 
                style={{ 
                  fontSize: '20px',
                  lineHeight: '2.2',
                  fontFamily: 'KaiTi, 楷体, serif',
                  whiteSpace: 'pre-line',
                  color: '#262626'
                }}
              >
                {creation.content}
              </div>
            </div>

            {/* 创作说明 */}
            {creation.description && (
              <div style={{ marginBottom: 24 }}>
                <Title level={4}>
                  <BookOutlined style={{ marginRight: 8, color: '#1890ff' }} />
                  创作说明
                </Title>
                <div style={{
                  background: '#f6ffed',
                  border: '1px solid #b7eb8f',
                  borderRadius: '8px',
                  padding: '16px'
                }}>
                  <Paragraph style={{
                    fontSize: '14px',
                    lineHeight: '1.8',
                    margin: 0,
                    whiteSpace: 'pre-line'
                  }}>
                    {creation.description}
                  </Paragraph>
                </div>
              </div>
            )}

            {/* 基本信息 */}
            <div style={{ marginBottom: 24 }}>
              <Title level={4}>基本信息</Title>
              <Descriptions bordered size="small">
                <Descriptions.Item label="创作时间" span={2}>
                  {moment(creation.createdAt).format('YYYY-MM-DD HH:mm:ss')}
                </Descriptions.Item>
                <Descriptions.Item label="更新时间" span={1}>
                  {moment(creation.updatedAt).format('YYYY-MM-DD HH:mm:ss')}
                </Descriptions.Item>
                <Descriptions.Item label="创作风格" span={1}>
                  {creation.style || '未分类'}
                </Descriptions.Item>
                <Descriptions.Item label="点赞数" span={1}>
                  {creation.likeCount || 0}
                </Descriptions.Item>
                <Descriptions.Item label="评论数" span={1}>
                  {creation.commentCount || 0}
                </Descriptions.Item>
                <Descriptions.Item label="公开状态" span={3}>
                  <Space>
                    {creation.isPublic ? (
                      <>
                        <GlobalOutlined style={{ color: '#52c41a' }} />
                        <span style={{ color: '#52c41a' }}>已发布到社区</span>
                      </>
                    ) : (
                      <>
                        <LockOutlined style={{ color: '#faad14' }} />
                        <span style={{ color: '#faad14' }}>私有作品</span>
                      </>
                    )}
                  </Space>
                </Descriptions.Item>
              </Descriptions>
            </div>

            {/* 操作按钮 */}
            <div style={{ textAlign: 'center' }}>
              <Space size="middle">
                <Button
                  type={creation.likeCount > 0 ? "primary" : "default"}
                  icon={creation.likeCount > 0 ? <HeartFilled /> : <HeartOutlined />}
                  loading={liking}
                  onClick={handleToggleLike}
                  disabled={!currentUser}
                >
                  点赞 ({creation.likeCount || 0})
                </Button>
                
                <Button
                  type="primary"
                  icon={<ThunderboltOutlined />}
                  loading={aiScoring}
                  onClick={handleAIScore}
                  disabled={!currentUser || !isAuthor}
                >
                  {aiScoring ? 'AI评分中...' : 'AI智能评分'}
                </Button>
              </Space>
            </div>
          </Card>
        </Col>

        {/* 右侧：AI评分雷达图 */}
        <Col xs={24} lg={10}>
          {creation.aiScore ? (
            <RadarChart
              data={radarData}
              loading={radarLoading}
              showTitle={true}
              showCard={true}
              height="500px"
            />
          ) : (
            <Card 
              title={
                <div style={{ display: 'flex', alignItems: 'center' }}>
                  <ThunderboltOutlined style={{ marginRight: '8px', color: '#1890ff' }} />
                  <span>AI评分雷达图</span>
                </div>
              }
            >
              <div style={{ 
                textAlign: 'center', 
                padding: '60px 20px',
                color: '#8c8c8c'
              }}>
                <ThunderboltOutlined style={{ fontSize: '48px', marginBottom: '16px' }} />
                <div style={{ marginBottom: '16px' }}>暂无AI评分数据</div>
                <Text type="secondary" style={{ fontSize: '12px' }}>
                  {isAuthor ? '点击左侧"AI智能评分"按钮获取评分' : '作者可以触发AI评分'}
                </Text>
              </div>
            </Card>
          )}

          {/* AI评分详情 */}
          {creation.aiScore && (
            <Card 
              title="AI评分详情" 
              style={{ marginTop: 16 }}
              size="small"
            >
              <Descriptions size="small" column={1}>
                <Descriptions.Item label="总分">
                  <Text strong style={{ fontSize: '16px', color: '#1890ff' }}>
                    {creation.aiScore.totalScore}分
                  </Text>
                </Descriptions.Item>
                {creation.aiScore.dimensions && (
                  <>
                    <Descriptions.Item label="韵律">
                      {creation.aiScore.dimensions.rhythm}分
                    </Descriptions.Item>
                    <Descriptions.Item label="意象">
                      {creation.aiScore.dimensions.imagery}分
                    </Descriptions.Item>
                    <Descriptions.Item label="情感">
                      {creation.aiScore.dimensions.emotion}分
                    </Descriptions.Item>
                    <Descriptions.Item label="技法">
                      {creation.aiScore.dimensions.technique}分
                    </Descriptions.Item>
                    <Descriptions.Item label="创新">
                      {creation.aiScore.dimensions.innovation}分
                    </Descriptions.Item>
                  </>
                )}
                {creation.aiScore.feedback && (
                  <Descriptions.Item label="AI点评">
                    <Paragraph style={{ margin: 0, fontSize: '12px' }}>
                      {creation.aiScore.feedback}
                    </Paragraph>
                  </Descriptions.Item>
                )}
              </Descriptions>

              {/* AI思考过程 */}
              {creation.aiScore.thinkingProcess && (
                <div style={{ marginTop: '16px' }}>
                  <Collapse
                    size="small"
                    items={[
                      {
                        key: 'thinking',
                        label: (
                          <Space>
                            <BulbOutlined style={{ color: '#faad14' }} />
                            <span>AI思考过程</span>
                          </Space>
                        ),
                        children: (
                          <div style={{
                            background: '#fafafa',
                            padding: '12px',
                            borderRadius: '4px',
                            fontSize: '12px',
                            lineHeight: '1.6',
                            whiteSpace: 'pre-wrap'
                          }}>
                            {creation.aiScore.thinkingProcess}
                          </div>
                        )
                      }
                    ]}
                  />
                </div>
              )}
            </Card>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default CreationDetail;
// {{END_MODIFICATIONS}}
