// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "event-bubble-fix-test"
//   Timestamp: "2025-08-08T19:13:46+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "事件冒泡测试，用户体验验证"
//   Quality_Check: "测试收藏按钮点击事件不会触发父组件事件。"
// }}
// {{START_MODIFICATIONS}}

import React, { useState } from 'react';
import { Card, message } from 'antd';
import FavoriteButton from '../components/FavoriteButton';

/**
 * 收藏按钮事件冒泡测试组件
 * 用于验证收藏按钮点击时不会触发父组件的点击事件
 */
const FavoriteButtonEventTest = () => {
  const [clickCount, setClickCount] = useState(0);
  const [favoriteClickCount, setFavoriteClickCount] = useState(0);

  // 模拟父组件的点击事件（诗词卡片点击）
  const handleCardClick = () => {
    setClickCount(prev => prev + 1);
    message.info(`卡片被点击了 ${clickCount + 1} 次`);
    console.log('Card clicked - this should NOT happen when clicking favorite button');
  };

  // 收藏状态变化回调
  const handleFavoriteChange = (isFavorited, folderName) => {
    setFavoriteClickCount(prev => prev + 1);
    message.success(`收藏操作 ${favoriteClickCount + 1} 次: ${isFavorited ? '已收藏' : '已取消收藏'}`);
    console.log('Favorite changed:', isFavorited, folderName);
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2>收藏按钮事件冒泡测试</h2>
      <p>
        <strong>测试说明：</strong>
        点击收藏按钮时，应该只触发收藏操作，不应该触发卡片的点击事件。
      </p>
      
      <div style={{ marginBottom: '20px' }}>
        <p>卡片点击次数: <strong>{clickCount}</strong></p>
        <p>收藏操作次数: <strong>{favoriteClickCount}</strong></p>
      </div>

      <Card
        title="测试诗词卡片"
        hoverable
        onClick={handleCardClick}
        style={{ 
          width: 300, 
          cursor: 'pointer',
          border: '2px solid #1890ff'
        }}
        actions={[
          <FavoriteButton
            targetId="test-poem-123"
            targetType="guwen"
            size="small"
            type="text"
            showText={true}
            onFavoriteChange={handleFavoriteChange}
            style={{ border: 'none', padding: 0, height: 'auto' }}
          />
        ]}
      >
        <Card.Meta
          title="春晓"
          description="春眠不觉晓，处处闻啼鸟。夜来风雨声，花落知多少。"
        />
        <div style={{ marginTop: '10px', fontSize: '12px', color: '#666' }}>
          点击卡片区域会触发卡片点击事件，点击收藏按钮（包括下拉选择）应该只触发收藏操作。
        </div>
      </Card>

      <div style={{ marginTop: '20px', padding: '10px', backgroundColor: '#f5f5f5', borderRadius: '4px' }}>
        <h4>测试结果验证：</h4>
        <ul>
          <li>✅ 点击卡片内容区域 → 卡片点击次数增加</li>
          <li>✅ 点击收藏按钮主按钮 → 只有收藏操作次数增加，卡片点击次数不变</li>
          <li>✅ 点击收藏按钮下拉箭头 → 显示下拉菜单，卡片点击次数不变</li>
          <li>✅ 在下拉菜单中选择收藏夹 → 只有收藏操作次数增加，卡片点击次数不变</li>
          <li>❌ 如果点击收藏相关按钮时卡片点击次数也增加 → 说明事件冒泡未被阻止</li>
        </ul>
      </div>
    </div>
  );
};

export default FavoriteButtonEventTest;

// {{END_MODIFICATIONS}}
