import axios from 'axios';

// 创建axios实例
const api = axios.create({
  baseURL: '/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 从localStorage获取token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    if (error.response?.status === 401) {
      // 清除token并跳转到登录页
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// 认证相关API
export const authAPI = {
  // 用户注册
  register: (data) => api.post('/auth/register', data),
  
  // 用户登录
  login: (data) => api.post('/auth/login', data),
  
  // 检查用户名是否可用
  checkUsername: (username) => api.get(`/auth/check-username?username=${username}`),
};

// 用户相关API
export const userAPI = {
  // 获取当前用户信息
  getProfile: () => api.get('/users/profile'),
  
  // 根据ID获取用户信息
  getUserById: (userId) => api.get(`/users/${userId}`),
  
  // 更新用户信息
  updateProfile: (data) => api.put('/users/profile', data),
};

// 古文相关API
export const guwenAPI = {
  // 获取古文列表
  getList: (params) => api.get('/guwen', { params }),

  // 获取古文详情
  getById: (id) => api.get(`/guwen/${id}`),

  // 搜索古文
  search: (data) => api.post('/guwen/search', data),

  // 获取热门古文
  getHot: (params) => api.get('/guwen/hot', { params }),

  // 获取所有朝代列表
  getDynasties: () => api.get('/guwen/dynasties'),

  // 获取所有作者列表
  getWriters: () => api.get('/guwen/writers'),

  // 获取古文统计信息
  getStats: () => api.get('/guwen/stats'),
};

// 作者相关API
export const writerAPI = {
  // 获取作者详情
  getById: (id) => api.get(`/writers/${id}`),

  // 获取作者列表
  getList: (params) => api.get('/writers', { params }),

  // 搜索作者
  search: (params) => api.get('/writers/search', { params }),

  // 获取所有朝代列表
  getDynasties: () => api.get('/writers/dynasties'),

  // 获取作者作品列表（根据作者名称）
  getWorksByName: (writerName, params) => api.get(`/guwen/by-writer/${encodeURIComponent(writerName)}`, { params }),

  // 获取作者统计信息
  getStats: () => api.get('/writers/stats'),
};

// 名句相关API
export const sentenceAPI = {
  // 获取名句列表
  getList: (params) => api.get('/sentences', { params }),

  // 获取名句详情
  getById: (id) => api.get(`/sentences/${id}`),

  // 搜索名句
  search: (params) => api.get('/sentences/search', { params }),

  // 获取热门名句
  getHot: (params) => api.get('/sentences/hot', { params }),

  // 获取随机名句
  getRandom: (params) => api.get('/sentences/random', { params }),

  // 获取所有朝代列表
  getDynasties: () => api.get('/sentences/dynasties'),

  // 获取所有作者列表
  getAuthors: () => api.get('/sentences/authors'),

  // 获取所有出处列表
  getSources: () => api.get('/sentences/sources'),

  // 获取名句统计信息
  getStats: () => api.get('/sentences/stats'),
};

// 评论相关API
export const commentAPI = {
  // 获取评论列表
  getList: (params) => api.get('/comments', { params }),

  // 创建评论
  create: (data) => api.post('/comments', data),

  // 删除评论
  delete: (id) => api.delete(`/comments/${id}`),

  // 点赞评论
  like: (id) => api.post(`/comments/${id}/like`),

  // 取消点赞
  unlike: (id) => api.delete(`/comments/${id}/like`),
};

// 创作相关API
export const creationAPI = {
  // 创建新的诗词创作
  create: (data) => api.post('/creations', data),

  // 根据ID获取创作详情
  getById: (id) => api.get(`/creations/${id}`),

  // 获取当前用户的创作列表
  getMyList: (params) => api.get('/creations/my', { params }),

  // 获取公开创作列表
  getPublicList: (params) => api.get('/creations/public', { params }),

  // 更新创作信息
  update: (id, data) => api.put(`/creations/${id}`, data),

  // 删除创作
  delete: (id) => api.delete(`/creations/${id}`),

  // 触发AI评分
  requestScore: (id) => api.post(`/creations/${id}/score`),

  // 获取AI修改建议
  requestSuggestions: (id) => api.post(`/creations/${id}/suggestions`),

  // 获取雷达图数据
  getRadarData: (id) => api.get(`/creations/${id}/radar`),

  // 切换创作公开状态
  togglePublic: (id, isPublic) => api.put(`/creations/${id}/public`, null, {
    params: { isPublic }
  }),

  // 点赞/取消点赞创作
  toggleLike: (id) => api.post(`/creations/${id}/like`),

  // 搜索创作
  search: (params) => api.get('/creations/search', { params }),
};

// 便捷API函数 - 为组件提供简化的调用接口
export const searchCreations = async (keyword, page = 1, size = 20, style = '') => {
  const params = { keyword, page, size };
  if (style) params.style = style;
  return await creationAPI.search(params);
};

export const getUserCreations = async (userId, page = 1, size = 20, style = '', status = null) => {
  const params = { page, size };
  if (style) params.style = style;
  if (status !== null) params.status = status;
  return await creationAPI.getMyList(params);
};

export const toggleLike = async (userId, creationId) => {
  return await creationAPI.toggleLike(creationId);
};

// 用户行为相关API
export const userActionAPI = {
  // 记录用户行为
  recordAction: (data) => api.post('/actions', data),

  // 取消用户行为
  cancelAction: (params) => api.delete('/actions', { params }),

  // 检查用户是否已执行某行为
  hasAction: (params) => api.get('/actions/check', { params }),

  // 获取用户行为列表
  getUserActions: (params) => api.get('/actions/user', { params }),

  // 获取目标的行为统计
  getTargetStats: (params) => api.get('/actions/stats', { params }),

  // 获取热门内容
  getHotContent: (params) => api.get('/actions/hot', { params }),
};

// 全局统计相关API
export const statsAPI = {
  // 获取全局统计信息
  getGlobalStats: () => api.get('/stats/global'),

  // 获取内容统计信息
  getContentStats: (contentId, contentType) => {
    // 参数验证
    if (!contentId || !contentType) {
      return Promise.reject(new Error('contentId and contentType are required'));
    }

    // 验证contentId格式（MongoDB ObjectId应该是24个字符）
    if (typeof contentId !== 'string' || contentId.length !== 24) {
      return Promise.reject(new Error('contentId should be a 24-character MongoDB ObjectId'));
    }

    // 验证contentType
    const validTypes = ['guwen', 'sentence', 'writer', 'creation'];
    if (!validTypes.includes(contentType)) {
      return Promise.reject(new Error('contentType should be one of: ' + validTypes.join(', ')));
    }

    return api.get('/stats/content', {
      params: {
        contentId,
        contentType
      }
    });
  },
};

// 收藏夹相关API
export const favoriteAPI = {
  /**
   * 获取用户收藏夹列表
   * @returns {Promise} 收藏夹列表
   */
  getFolders: () => api.get('/favorites/folders'),

  /**
   * 创建收藏夹
   * @param {string} folderName - 收藏夹名称
   * @returns {Promise} 创建结果
   */
  createFolder: (folderName) => api.post('/favorites/folders', null, {
    params: { folderName }
  }),

  /**
   * 重命名收藏夹
   * @param {string} oldName - 旧收藏夹名称
   * @param {string} newName - 新收藏夹名称
   * @returns {Promise} 重命名结果
   */
  renameFolder: (oldName, newName) => api.put('/favorites/folders', null, {
    params: { oldFolderName: oldName, newFolderName: newName }
  }),

  /**
   * 删除收藏夹
   * @param {string} folderName - 收藏夹名称
   * @returns {Promise} 删除结果
   */
  deleteFolder: (folderName) => api.delete('/favorites/folders', {
    params: { folderName }
  }),

  /**
   * 移动收藏项到指定收藏夹
   * @param {number} favoriteId - 收藏项ID
   * @param {string} targetFolder - 目标收藏夹名称
   * @returns {Promise} 移动结果
   */
  moveFavorite: (favoriteId, targetFolder) => api.put(`/favorites/${favoriteId}/move`, null, {
    params: { newFolderName: targetFolder }
  }),

  /**
   * 获取收藏夹统计信息
   * @returns {Promise} 统计信息
   */
  getFolderStats: () => api.get('/favorites/folders/stats'),

  /**
   * 添加收藏
   * @param {Object} data - 收藏数据
   * @param {string} data.targetId - 目标ID
   * @param {string} data.targetType - 目标类型
   * @param {string} [data.folderName] - 收藏夹名称，默认为"默认收藏夹"
   * @param {string} [data.notes] - 收藏备注
   * @returns {Promise} 收藏结果
   */
  addFavorite: (data) => api.post('/favorites', data),

  /**
   * 删除收藏
   * @param {string} targetId - 目标ID
   * @param {string} targetType - 目标类型
   * @returns {Promise} 删除结果
   */
  removeFavorite: (targetId, targetType) => api.delete('/favorites', {
    params: { targetId, targetType }
  }),

  /**
   * 获取用户收藏列表
   * @param {Object} params - 查询参数
   * @param {number} [params.page] - 页码
   * @param {number} [params.size] - 每页大小
   * @param {string} [params.folderName] - 收藏夹名称
   * @param {string} [params.targetType] - 目标类型
   * @returns {Promise} 收藏列表
   */
  getFavorites: (params = {}) => {
    const { folderName, targetType, ...otherParams } = params;

    if (folderName) {
      // 按收藏夹获取收藏
      return api.get('/favorites/folder', { params: { folderName, ...otherParams } });
    } else if (targetType) {
      // 按类型获取收藏
      return api.get('/favorites/user/type', { params: { targetType, ...otherParams } });
    } else {
      // 获取所有收藏
      return api.get('/favorites/user', { params: otherParams });
    }
  },

  /**
   * 检查是否已收藏
   * @param {string} targetId - 目标ID
   * @param {string} targetType - 目标类型
   * @returns {Promise} 收藏状态
   */
  checkFavorite: (targetId, targetType) => api.get('/favorites/check', {
    params: { targetId, targetType }
  }),

  /**
   * 获取最近收藏的内容
   * @param {number} [limit=10] - 限制数量
   * @returns {Promise} 最近收藏列表
   */
  getRecentFavorites: (limit = 10) => api.get('/favorites/recent', {
    params: { limit }
  }),

  /**
   * 获取用户收藏数量
   * @param {string} [targetType] - 目标类型（可选）
   * @returns {Promise} 收藏数量
   */
  getFavoriteCount: (targetType) => api.get('/favorites/count', {
    params: targetType ? { targetType } : {}
  })
};

export default api;
