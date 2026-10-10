import axios, { AxiosError } from 'axios';
import { handleMockRequest } from './mockAdapter';

const customApiUrl = import.meta.env.VITE_API_BASE_URL;

// Base Axios instance
const api = axios.create({
  baseURL: customApiUrl || 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 5000,
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

// Seamless Fallback Interceptor:
// If the request fails due to network error (e.g., Spring Boot not running or no remote backend),
// automatically fall back to the built-in mock database so the live Vercel deployment NEVER breaks!
api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    // If it's a network error (backend unreachable / CORS failure / offline)
    if (!error.response && error.config) {
      console.warn('Backend server offline or unreachable. Seamlessly activating built-in mock engine for request:', error.config.url);
      try {
        return await handleMockRequest(error.config);
      } catch (mockErr) {
        return Promise.reject(mockErr);
      }
    }

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
