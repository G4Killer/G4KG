import axios from 'axios';

// 创建 Axios 实例
const apiClient = axios.create({
  baseURL: '/api', // 生产环境下代替proxy使用，需要配置跨域问题，之前的设置为：'http://10.201.12.44:8081/api'，这是实验室的ip，当在外面时改为localhst，部署时用相对路径直接用/api
  headers: {
    'Content-Type': 'application/json', // 默认 Content-Type
  },
});

// 请求拦截器（移除用户登录功能相关逻辑）
apiClient.interceptors.request.use(
    (config) => {
      // 无需添加 Token 或其他身份验证逻辑，直接返回配置
      return config;
    },
    (error) => {
      console.error('Request Error:', error);
      return Promise.reject(error);
    }
);

// 响应拦截器
apiClient.interceptors.response.use(
    (response) => {
      // 返回后端的数据部分
      return response.data;
    },
    (error) => {
      // 捕获响应错误并输出日志
      console.error('Response error:', error);
      return Promise.reject(error);
    }
);

export default apiClient;
