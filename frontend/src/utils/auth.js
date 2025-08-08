// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "auth-utils-implementation"
//   Timestamp: "2025-08-08T17:53:22+08:00"
//   Authoring_Subagent: "PM-内置顾问团"
//   Principle_Applied: "SOLID-S (单一职责原则)"
//   Quality_Check: "编译通过，功能完整。"
// }}
// {{START_MODIFICATIONS}}

/**
 * 认证相关工具函数
 * 提供用户认证状态管理和用户信息获取功能
 */

/**
 * 获取当前登录用户信息
 * @returns {Object|null} 用户信息对象，如果未登录则返回null
 */
export const getCurrentUser = () => {
  try {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  } catch (error) {
    console.error('获取用户信息失败:', error);
    return null;
  }
};

/**
 * 获取当前用户的认证令牌
 * @returns {string|null} JWT令牌，如果未登录则返回null
 */
export const getToken = () => {
  return localStorage.getItem('token');
};

/**
 * 检查用户是否已登录
 * @returns {boolean} 是否已登录
 */
export const isAuthenticated = () => {
  const token = getToken();
  const user = getCurrentUser();
  return !!(token && user);
};

/**
 * 设置用户信息和令牌
 * @param {Object} userData 用户信息
 * @param {string} token JWT令牌
 */
export const setUserAuth = (userData, token) => {
  localStorage.setItem('user', JSON.stringify(userData));
  localStorage.setItem('token', token);
};

/**
 * 清除用户认证信息
 */
export const clearUserAuth = () => {
  localStorage.removeItem('user');
  localStorage.removeItem('token');
};

/**
 * 获取当前用户ID
 * @returns {number|null} 用户ID，如果未登录则返回null
 */
export const getCurrentUserId = () => {
  const user = getCurrentUser();
  return user ? user.id : null;
};

/**
 * 获取当前用户名
 * @returns {string|null} 用户名，如果未登录则返回null
 */
export const getCurrentUsername = () => {
  const user = getCurrentUser();
  return user ? (user.nickname || user.username) : null;
};

// {{END_MODIFICATIONS}}
