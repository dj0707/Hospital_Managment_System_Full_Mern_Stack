import axios from 'axios';
import { handleMockRequest } from './mockAdapter';

const isProduction = import.meta.env.PROD;
const customApiUrl = import.meta.env.VITE_API_BASE_URL;

// If VITE_API_BASE_URL is not explicitly set in Vercel/Production or starts with /mock, we enable the built-in Mock backend
const useMockBackend = !customApiUrl || customApiUrl.includes('mock') || (isProduction && !customApiUrl.startsWith('http'));

const api = axios.create({
  baseURL: customApiUrl || 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
  adapter: useMockBackend
    ? (config) => handleMockRequest(config)
    : undefined,
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('medicore_token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('medicore_token');
      localStorage.removeItem('medicore_user');
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default api;
