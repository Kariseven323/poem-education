// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "4634d760-65a3-4128-901c-c09532256a97"
//   Timestamp: "2025-08-08T13:30:50+08:00"
//   Authoring_Subagent: "PM-标准协作模式"
//   Principle_Applied: "DTO设计最佳实践，专门为雷达图可视化设计"
//   Quality_Check: "编译通过，数据结构适配ECharts雷达图。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.dto.response;

import java.util.List;

/**
 * 雷达图数据DTO
 * 专门用于ECharts雷达图可视化
 * 
 * @author poem-education-team
 * @since 2025-08-08
 */
public class RadarDataDTO {
    
    /**
     * 创作ID
     */
    private String creationId;
    
    /**
     * 创作标题
     */
    private String title;
    
    /**
     * 雷达图指标配置
     */
    private List<IndicatorDTO> indicators;
    
    /**
     * 雷达图数据系列
     */
    private List<SeriesDataDTO> series;
    
    /**
     * 总分
     */
    private Integer totalScore;
    
    /**
     * 是否有评分数据
     */
    private Boolean hasScore;
    
    // 默认构造函数
    public RadarDataDTO() {
    }
    
    // 构造函数
    public RadarDataDTO(String creationId, String title) {
        this.creationId = creationId;
        this.title = title;
        this.hasScore = false;
    }
    
    // Getter and Setter methods
    public String getCreationId() {
        return creationId;
    }
    
    public void setCreationId(String creationId) {
        this.creationId = creationId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public List<IndicatorDTO> getIndicators() {
        return indicators;
    }
    
    public void setIndicators(List<IndicatorDTO> indicators) {
        this.indicators = indicators;
    }
    
    public List<SeriesDataDTO> getSeries() {
        return series;
    }
    
    public void setSeries(List<SeriesDataDTO> series) {
        this.series = series;
    }
    
    public Integer getTotalScore() {
        return totalScore;
    }
    
    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }
    
    public Boolean getHasScore() {
        return hasScore;
    }
    
    public void setHasScore(Boolean hasScore) {
        this.hasScore = hasScore;
    }
    
    @Override
    public String toString() {
        return "RadarDataDTO{" +
                "creationId='" + creationId + '\'' +
                ", title='" + title + '\'' +
                ", indicators=" + indicators +
                ", series=" + series +
                ", totalScore=" + totalScore +
                ", hasScore=" + hasScore +
                '}';
    }
    
    /**
     * 雷达图指标DTO
     * 对应ECharts radar.indicator配置
     */
    public static class IndicatorDTO {
        /**
         * 指标名称
         */
        private String name;
        
        /**
         * 最大值
         */
        private Integer max;
        
        /**
         * 最小值
         */
        private Integer min;
        
        // 构造函数
        public IndicatorDTO() {
        }
        
        public IndicatorDTO(String name, Integer max) {
            this.name = name;
            this.max = max;
            this.min = 0;
        }
        
        public IndicatorDTO(String name, Integer max, Integer min) {
            this.name = name;
            this.max = max;
            this.min = min;
        }
        
        // Getter and Setter methods
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public Integer getMax() {
            return max;
        }
        
        public void setMax(Integer max) {
            this.max = max;
        }
        
        public Integer getMin() {
            return min;
        }
        
        public void setMin(Integer min) {
            this.min = min;
        }
        
        @Override
        public String toString() {
            return "IndicatorDTO{" +
                    "name='" + name + '\'' +
                    ", max=" + max +
                    ", min=" + min +
                    '}';
        }
    }
    
    /**
     * 雷达图系列数据DTO
     * 对应ECharts series.data配置
     */
    public static class SeriesDataDTO {
        /**
         * 数据名称
         */
        private String name;
        
        /**
         * 数据值数组
         * 按照indicators的顺序：[韵律, 意象, 情感, 技法, 创新]
         */
        private List<Integer> value;

        /**
         * 兼容性字段：数据值数组（复数形式）
         * 与前端RadarChart组件期望的字段名保持一致
         */
        private List<Integer> values;
        
        /**
         * 数据项样式配置
         */
        private ItemStyleDTO itemStyle;
        
        // 构造函数
        public SeriesDataDTO() {
        }
        
        public SeriesDataDTO(String name, List<Integer> value) {
            this.name = name;
            this.value = value;
        }
        
        // Getter and Setter methods
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public List<Integer> getValue() {
            return value;
        }
        
        public void setValue(List<Integer> value) {
            this.value = value;
            this.values = value; // 同时设置兼容性字段
        }

        public List<Integer> getValues() {
            return values;
        }

        public void setValues(List<Integer> values) {
            this.values = values;
            this.value = values; // 同时设置原字段
        }

        public ItemStyleDTO getItemStyle() {
            return itemStyle;
        }
        
        public void setItemStyle(ItemStyleDTO itemStyle) {
            this.itemStyle = itemStyle;
        }
        
        @Override
        public String toString() {
            return "SeriesDataDTO{" +
                    "name='" + name + '\'' +
                    ", value=" + value +
                    ", itemStyle=" + itemStyle +
                    '}';
        }
    }
    
    /**
     * 数据项样式DTO
     * 对应ECharts itemStyle配置
     */
    public static class ItemStyleDTO {
        /**
         * 颜色
         */
        private String color;
        
        // 构造函数
        public ItemStyleDTO() {
        }
        
        public ItemStyleDTO(String color) {
            this.color = color;
        }
        
        // Getter and Setter methods
        public String getColor() {
            return color;
        }
        
        public void setColor(String color) {
            this.color = color;
        }
        
        @Override
        public String toString() {
            return "ItemStyleDTO{" +
                    "color='" + color + '\'' +
                    '}';
        }
    }
}
// {{END_MODIFICATIONS}}
