import React, { useState } from 'react';
import { Card, Button, Space, Row, Col, Switch } from 'antd';
import RadarChart from './RadarChart';

/**
 * RadarChart组件演示页面
 * 用于测试和展示雷达图组件的各种状态
 */
const RadarChartDemo = () => {
  const [loading, setLoading] = useState(false);
  const [showTitle, setShowTitle] = useState(true);
  const [showCard, setShowCard] = useState(true);
  const [dataType, setDataType] = useState('withScore');

  // 模拟有评分数据的情况
  const dataWithScore = {
    id: 'demo-001',
    title: '春晓',
    hasScore: true,
    totalScore: 85,
    indicators: [
      { name: '韵律', max: 100 },
      { name: '意象', max: 100 },
      { name: '情感', max: 100 },
      { name: '技法', max: 100 },
      { name: '创新', max: 100 }
    ],
    series: [{
      name: '评分',
      values: [88, 92, 78, 85, 82],
      itemStyle: { color: '#1890ff' }
    }]
  };

  // 模拟无评分数据的情况
  const dataWithoutScore = {
    id: 'demo-002',
    title: '无评分作品',
    hasScore: false
  };

  // 模拟空数据的情况
  const emptyData = null;

  // 获取当前数据
  const getCurrentData = () => {
    switch (dataType) {
      case 'withScore':
        return dataWithScore;
      case 'withoutScore':
        return dataWithoutScore;
      case 'empty':
        return emptyData;
      default:
        return dataWithScore;
    }
  };

  // 模拟加载状态
  const handleLoadingTest = () => {
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
    }, 2000);
  };

  return (
    <div style={{ padding: '24px' }}>
      <Card title="RadarChart 组件演示" style={{ marginBottom: '24px' }}>
        <Space wrap>
          <Button 
            type={dataType === 'withScore' ? 'primary' : 'default'}
            onClick={() => setDataType('withScore')}
          >
            有评分数据
          </Button>
          <Button 
            type={dataType === 'withoutScore' ? 'primary' : 'default'}
            onClick={() => setDataType('withoutScore')}
          >
            无评分数据
          </Button>
          <Button 
            type={dataType === 'empty' ? 'primary' : 'default'}
            onClick={() => setDataType('empty')}
          >
            空数据
          </Button>
          <Button onClick={handleLoadingTest}>
            测试加载状态
          </Button>
        </Space>
        
        <div style={{ marginTop: '16px' }}>
          <Space>
            <span>显示标题:</span>
            <Switch checked={showTitle} onChange={setShowTitle} />
            <span>使用Card包装:</span>
            <Switch checked={showCard} onChange={setShowCard} />
          </Space>
        </div>
      </Card>

      <Row gutter={24}>
        <Col span={12}>
          <RadarChart
            data={getCurrentData()}
            loading={loading}
            showTitle={showTitle}
            showCard={showCard}
            height="400px"
          />
        </Col>
        <Col span={12}>
          <Card title="数据结构示例">
            <pre style={{ 
              background: '#f5f5f5', 
              padding: '16px', 
              borderRadius: '4px',
              fontSize: '12px',
              overflow: 'auto',
              maxHeight: '350px'
            }}>
              {JSON.stringify(getCurrentData(), null, 2)}
            </pre>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default RadarChartDemo;
