import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Typography, Button, Space, Statistic, List, Avatar } from 'antd';
import { 
  BookOutlined, 
  UserOutlined, 
  EditOutlined, 
  EyeOutlined,
  HeartOutlined,
  StarOutlined,
  RightOutlined
} from '@ant-design/icons';
import { guwenAPI, writerAPI, sentenceAPI, statsAPI } from '../utils/api';
import viewTracker from '../utils/viewTracker';

const { Title, Paragraph, Text } = Typography;

const Home = () => {
  const [hotPoems, setHotPoems] = useState([]);
  const [hotSentences, setHotSentences] = useState([]);
  const [featuredWriters, setFeaturedWriters] = useState([]);
  const [loading, setLoading] = useState(true);
  const [globalStats, setGlobalStats] = useState({
    poemCount: 0,
    writerCount: 0,
    sentenceCount: 0,
    todayViews: 0,
    poemCountDisplay: '0',
    writerCountDisplay: '0',
    sentenceCountDisplay: '0',
    todayViewsDisplay: '0'
  });

  useEffect(() => {
    loadHomeData();
    // 记录首页访问
    viewTracker.recordHomeView();
  }, []);

  const loadHomeData = async () => {
    try {
      setLoading(true);

      // 并行加载数据 - 包括统计数据、诗词列表和随机名句
      const [statsRes, poemsRes, sentencesRes] = await Promise.allSettled([
        statsAPI.getGlobalStats(),
        guwenAPI.getList({ page: 1, size: 6 }),
        sentenceAPI.getRandom({ limit: 6 })
      ]);

      // 处理统计数据
      if (statsRes.status === 'fulfilled' && statsRes.value.code === 200) {
        setGlobalStats(statsRes.value.data);
      }

      // 处理诗词列表
      if (poemsRes.status === 'fulfilled' && poemsRes.value.code === 200) {
        setHotPoems(poemsRes.value.data?.list || []);
      }

      // 处理随机名句
      if (sentencesRes.status === 'fulfilled' && sentencesRes.value.code === 200) {
        // 随机名句API返回的是数组，不是分页对象
        setHotSentences(sentencesRes.value.data || []);
      }

    } catch (error) {
      console.error('Failed to load home data:', error);
    } finally {
      setLoading(false);
    }
  };

  const StatCard = ({ title, value, icon, color }) => (
    <Card>
      <Statistic
        title={title}
        value={value}
        prefix={React.cloneElement(icon, { style: { color } })}
        valueStyle={{ color }}
      />
    </Card>
  );

  return (
    <div>
      {/* 欢迎区域 */}
      <Card style={{ marginBottom: 24, background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', color: 'white' }}>
        <Row align="middle">
          <Col span={16}>
            <Title level={1} style={{ color: 'white', marginBottom: 8 }}>
              诗词交流鉴赏平台
            </Title>
            <Paragraph style={{ color: 'rgba(255,255,255,0.9)', fontSize: '16px', marginBottom: 16 }}>
              传承中华文化，品味诗词之美。在这里，您可以欣赏经典诗词，了解文人墨客，感受千年文化的魅力。
            </Paragraph>
            <Space>
              <Button type="primary" size="large" href="/poems">
                开始探索
              </Button>
              <Button size="large" style={{ color: 'white', borderColor: 'white' }} href="/api-test">
                API测试
              </Button>
            </Space>
          </Col>
          <Col span={8} style={{ textAlign: 'center' }}>
            <BookOutlined style={{ fontSize: '120px', color: 'rgba(255,255,255,0.3)' }} />
          </Col>
        </Row>
      </Card>

      {/* 统计数据 */}
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <StatCard
            title="诗词总数"
            value={globalStats.poemCountDisplay}
            icon={<BookOutlined />}
            color="#1890ff"
          />
        </Col>
        <Col span={6}>
          <StatCard
            title="文人墨客"
            value={globalStats.writerCountDisplay}
            icon={<UserOutlined />}
            color="#52c41a"
          />
        </Col>
        <Col span={6}>
          <StatCard
            title="名句摘录"
            value={globalStats.sentenceCountDisplay}
            icon={<EditOutlined />}
            color="#faad14"
          />
        </Col>
        <Col span={6}>
          <StatCard
            title="今日访问"
            value={globalStats.todayViewsDisplay}
            icon={<EyeOutlined />}
            color="#f5222d"
          />
        </Col>
      </Row>

      <Row gutter={16}>
        {/* 热门诗词 */}
        <Col span={12}>
          <Card 
            title="热门诗词" 
            extra={<Button type="link" href="/poems">查看更多 <RightOutlined /></Button>}
            loading={loading}
          >
            <List
              dataSource={hotPoems}
              renderItem={(poem) => (
                <List.Item
                  actions={[
                    <Space>
                      <EyeOutlined /> {poem.stats?.viewCount || 0}
                      <HeartOutlined /> {poem.stats?.likeCount || 0}
                    </Space>
                  ]}
                >
                  <List.Item.Meta
                    title={
                      <a href={`/poems/${poem.id}`}>
                        {poem.title}
                      </a>
                    }
                    description={
                      <div>
                        <Text type="secondary">{poem.dynasty} · {poem.writer}</Text>
                        <div className="poem-content" style={{ 
                          marginTop: 8, 
                          fontSize: '14px',
                          maxHeight: '60px',
                          overflow: 'hidden'
                        }}>
                          {poem.content?.substring(0, 50)}...
                        </div>
                      </div>
                    }
                  />
                </List.Item>
              )}
            />
          </Card>
        </Col>

        {/* 精选名句 */}
        <Col span={12}>
          <Card
            title="精选名句"
            extra={
              <div>
                <Button
                  type="text"
                  size="small"
                  onClick={loadHomeData}
                  style={{ marginRight: 8 }}
                >
                  换一批
                </Button>
                <Button type="link" href="/sentences">查看更多 <RightOutlined /></Button>
              </div>
            }
            loading={loading}
          >
            <List
              dataSource={hotSentences}
              renderItem={(sentence) => (
                <List.Item>
                  <List.Item.Meta
                    title={
                      <div className="poem-content" style={{ fontSize: '16px' }}>
                        {sentence.name}
                      </div>
                    }
                    description={
                      <div>
                        <Text type="secondary">
                          出自《{sentence.from}》· {sentence.dynasty} · {sentence.author}
                        </Text>
                        {sentence.meaning && (
                          <div style={{ marginTop: 4, fontSize: '12px', color: '#666' }}>
                            {sentence.meaning.substring(0, 60)}...
                          </div>
                        )}
                      </div>
                    }
                  />
                </List.Item>
              )}
            />
          </Card>
        </Col>
      </Row>

      {/* 功能介绍 */}
      <Row gutter={16} style={{ marginTop: 24 }}>
        <Col span={8}>
          <Card hoverable>
            <div style={{ textAlign: 'center' }}>
              <BookOutlined style={{ fontSize: '48px', color: '#1890ff', marginBottom: 16 }} />
              <Title level={4}>诗词鉴赏</Title>
              <Paragraph>
                收录历代经典诗词，提供详细的注释、翻译和赏析，让您深入理解诗词的内涵和美感。
              </Paragraph>
              <Button type="primary" href="/poems">立即体验</Button>
            </div>
          </Card>
        </Col>
        <Col span={8}>
          <Card hoverable>
            <div style={{ textAlign: 'center' }}>
              <UserOutlined style={{ fontSize: '48px', color: '#52c41a', marginBottom: 16 }} />
              <Title level={4}>文人墨客</Title>
              <Paragraph>
                了解历代文人的生平事迹、创作背景，感受他们的人生智慧和文学造诣。
              </Paragraph>
              <Button type="primary" href="/writers">立即体验</Button>
            </div>
          </Card>
        </Col>
        <Col span={8}>
          <Card hoverable>
            <div style={{ textAlign: 'center' }}>
              <EditOutlined style={{ fontSize: '48px', color: '#faad14', marginBottom: 16 }} />
              <Title level={4}>名句摘录</Title>
              <Paragraph>
                精选千古名句，配以深度解析，让您在品味中感受中华文化的博大精深。
              </Paragraph>
              <Button type="primary" href="/sentences">立即体验</Button>
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Home;
