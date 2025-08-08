// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "view-tracking-implementation"
//   Timestamp: "2025-08-08"
//   Authoring_Subagent: "frontend-expert"
//   Principle_Applied: "SOLID-S (单一职责原则)"
//   Quality_Check: "编译通过，功能完整。"
// }}
// {{START_MODIFICATIONS}}

import { userActionAPI } from './api';

/**
 * 页面访问追踪工具
 * 用于记录用户的页面访问行为
 */
class ViewTracker {
  constructor() {
    this.viewedItems = new Set(); // 防止重复记录同一项目的访问
    this.isEnabled = true; // 是否启用访问追踪
  }

  /**
   * 记录页面访问
   * @param {string} targetId - 目标ID (MongoDB ObjectId)
   * @param {string} targetType - 目标类型 (guwen/sentence/creation/comment)
   * @param {Object} options - 可选参数
   * @param {boolean} options.force - 是否强制记录（忽略重复检查）
   * @param {boolean} options.silent - 是否静默记录（不显示错误信息）
   */
  async recordView(targetId, targetType, options = {}) {
    if (!this.isEnabled) {
      return;
    }

    // 验证参数
    if (!targetId || !targetType) {
      console.warn('ViewTracker: targetId and targetType are required');
      return;
    }

    // 验证targetId格式（MongoDB ObjectId应该是24个字符）
    if (typeof targetId !== 'string' || targetId.length !== 24) {
      console.warn('ViewTracker: targetId should be a 24-character MongoDB ObjectId');
      return;
    }

    // 验证targetType
    const validTypes = ['guwen', 'sentence', 'creation', 'comment'];
    if (!validTypes.includes(targetType)) {
      console.warn('ViewTracker: targetType should be one of:', validTypes);
      return;
    }

    // 检查是否已经记录过（防止重复记录）
    const viewKey = `${targetType}:${targetId}`;
    if (!options.force && this.viewedItems.has(viewKey)) {
      console.debug('ViewTracker: Already recorded view for', viewKey);
      return;
    }

    try {
      // 记录访问行为
      const response = await userActionAPI.recordAction({
        targetId,
        targetType,
        actionType: 'view'
      });

      if (response.code === 200) {
        // 标记为已记录
        this.viewedItems.add(viewKey);
        console.debug('ViewTracker: Successfully recorded view for', viewKey);
      } else {
        if (!options.silent) {
          console.warn('ViewTracker: Failed to record view:', response.message);
        }
      }
    } catch (error) {
      if (!options.silent) {
        console.error('ViewTracker: Error recording view:', error);
      }
    }
  }

  /**
   * 记录诗词访问
   * @param {string} poemId - 诗词ID
   * @param {Object} options - 可选参数
   */
  async recordPoemView(poemId, options = {}) {
    return this.recordView(poemId, 'guwen', options);
  }

  /**
   * 记录名句访问
   * @param {string} sentenceId - 名句ID
   * @param {Object} options - 可选参数
   */
  async recordSentenceView(sentenceId, options = {}) {
    return this.recordView(sentenceId, 'sentence', options);
  }

  /**
   * 记录创作访问
   * @param {string} creationId - 创作ID
   * @param {Object} options - 可选参数
   */
  async recordCreationView(creationId, options = {}) {
    return this.recordView(creationId, 'creation', options);
  }

  /**
   * 记录首页访问
   * 首页访问使用特殊的ID和类型
   */
  async recordHomeView() {
    // 为首页创建一个特殊的标识
    const homeId = '000000000000000000000000'; // 24个0作为首页的特殊ID
    return this.recordView(homeId, 'guwen', { 
      force: true, // 首页访问总是记录
      silent: true  // 首页访问错误不显示
    });
  }

  /**
   * 启用访问追踪
   */
  enable() {
    this.isEnabled = true;
    console.debug('ViewTracker: Enabled');
  }

  /**
   * 禁用访问追踪
   */
  disable() {
    this.isEnabled = false;
    console.debug('ViewTracker: Disabled');
  }

  /**
   * 清除已记录的访问缓存
   */
  clearCache() {
    this.viewedItems.clear();
    console.debug('ViewTracker: Cache cleared');
  }

  /**
   * 获取已记录的访问数量
   */
  getViewedCount() {
    return this.viewedItems.size;
  }
}

// 创建全局实例
const viewTracker = new ViewTracker();

// 导出实例和类
export default viewTracker;
export { ViewTracker };

// {{END_MODIFICATIONS}}
