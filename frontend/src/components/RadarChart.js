// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "101b6519-4a13-4a9d-badd-9bc1e2dbfdbb"
//   Timestamp: "2025-08-08T14:10:00+08:00"
//   Authoring_Subagent: "PM-标准协作模式"
//   Principle_Applied: "React组件设计最佳实践，ECharts集成规范"
//   Quality_Check: "编译通过，雷达图渲染正常。"
// }}
// {{START_MODIFICATIONS}}
import React, { useMemo } from 'react';
import ReactECharts from 'echarts-for-react';
import { Card, Empty, Spin, Typography } from 'antd';
import { RadarChartOutlined } from '@ant-design/icons';

const { Title, Text } = Typography;

/**
 * 雷达图组件
 * 用于展示诗词创作的五维度AI评分结果
 * 
 * @param {Object} props - 组件属性
 * @param {Object} props.data - 雷达图数据
 * @param {string} props.data.id - 创作ID
 * @param {string} props.data.title - 创作标题
 * @param {boolean} props.data.hasScore - 是否有评分数据
 * @param {number} props.data.totalScore - 总分
 * @param {Array} props.data.indicators - 指标配置
 * @param {Array} props.data.series - 数据系列
 * @param {boolean} props.loading - 加载状态
 * @param {string} props.height - 图表高度，默认400px
 * @param {boolean} props.showTitle - 是否显示标题，默认true
 * @param {boolean} props.showCard - 是否使用Card包装，默认true
 */
const RadarChart = ({ 
  data, 
  loading = false, 
  height = '400px', 
  showTitle = true, 
  showCard = true 
}) => {
  
  // 生成ECharts配置选项
  const option = useMemo(() => {
    console.log('RadarChart接收到的数据:', data);

    if (!data || !data.hasScore || !data.indicators || !data.series) {
      console.log('雷达图数据不完整，显示空状态');
      return null;
    }

    return {
      title: showTitle ? {
        text: `《${data.title}》评分雷达图`,
        subtext: `总分：${data.totalScore || 0}分`,
        left: 'center',
        textStyle: {
          fontSize: 16,
          fontWeight: 'bold',
          color: '#262626'
        },
        subtextStyle: {
          fontSize: 14,
          color: '#8c8c8c'
        }
      } : undefined,
      
      tooltip: {
        trigger: 'item',
        formatter: function(params) {
          const { name, value } = params;
          const indicators = data.indicators;
          let content = `<div style="padding: 8px;">`;
          content += `<div style="font-weight: bold; margin-bottom: 8px;">${name}</div>`;
          
          value.forEach((score, index) => {
            const indicator = indicators[index];
            content += `<div style="margin: 4px 0;">`;
            content += `<span style="display: inline-block; width: 60px;">${indicator.name}:</span>`;
            content += `<span style="font-weight: bold; color: #1890ff;">${score}分</span>`;
            content += `</div>`;
          });
          
          content += `</div>`;
          return content;
        }
      },
      
      legend: {
        data: data.series.map(s => s.name),
        bottom: 10,
        textStyle: {
          color: '#595959'
        }
      },
      
      radar: {
        indicator: data.indicators.map(indicator => ({
          name: indicator.name,
          max: indicator.max || 100,
          nameGap: 8,
          axisName: {
            color: '#262626',
            fontSize: 12,
            fontWeight: 'bold'
          }
        })),
        shape: 'polygon',
        radius: '65%',
        center: ['50%', '55%'],
        splitNumber: 5,
        axisLine: {
          lineStyle: {
            color: '#d9d9d9'
          }
        },
        splitLine: {
          lineStyle: {
            color: '#f0f0f0'
          }
        },
        splitArea: {
          show: true,
          areaStyle: {
            color: ['rgba(24, 144, 255, 0.05)', 'rgba(24, 144, 255, 0.02)']
          }
        }
      },
      
      series: data.series.map(seriesItem => ({
        name: seriesItem.name,
        type: 'radar',
        data: [{
          value: seriesItem.values || seriesItem.value, // 兼容两种字段名
          name: seriesItem.name,
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: {
            width: 2,
            color: seriesItem.itemStyle?.color || '#1890ff'
          },
          areaStyle: {
            color: seriesItem.itemStyle?.color || '#1890ff',
            opacity: 0.1
          },
          itemStyle: {
            color: seriesItem.itemStyle?.color || '#1890ff',
            borderColor: '#fff',
            borderWidth: 2
          }
        }]
      })),
      
      animation: true,
      animationDuration: 1000,
      animationEasing: 'cubicOut'
    };
  }, [data, showTitle]);

  // 渲染空状态
  const renderEmpty = () => (
    <div style={{ 
      height: height, 
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: 'center',
      flexDirection: 'column',
      color: '#8c8c8c'
    }}>
      <RadarChartOutlined style={{ fontSize: '48px', marginBottom: '16px' }} />
      <Text type="secondary">暂无评分数据</Text>
      <Text type="secondary" style={{ fontSize: '12px', marginTop: '8px' }}>
        请先触发AI评分
      </Text>
    </div>
  );

  // 渲染加载状态
  const renderLoading = () => (
    <div style={{ 
      height: height, 
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: 'center' 
    }}>
      <Spin size="large" tip="正在加载评分数据..." />
    </div>
  );

  // 渲染图表内容
  const renderChart = () => {
    if (loading) {
      return renderLoading();
    }

    if (!data || !data.hasScore || !option) {
      return renderEmpty();
    }

    return (
      <ReactECharts
        option={option}
        style={{ height: height, width: '100%' }}
        opts={{ renderer: 'canvas' }}
        notMerge={true}
        lazyUpdate={true}
      />
    );
  };

  // 根据showCard决定是否使用Card包装
  if (showCard) {
    return (
      <Card 
        title={
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <RadarChartOutlined style={{ marginRight: '8px', color: '#1890ff' }} />
            <span>AI评分雷达图</span>
          </div>
        }
        style={{ height: 'auto' }}
      >
        {renderChart()}
      </Card>
    );
  }

  return renderChart();
};

export default RadarChart;
// {{END_MODIFICATIONS}}
