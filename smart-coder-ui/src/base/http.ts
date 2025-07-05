// src/utils/httpService.ts
import api from './api';

type QueryParams = Record<string, string | number | boolean | undefined>;
type ApiHeaders = Record<string, string>;

export default {
  get<T = any>(url: string, params?: QueryParams, headers?: ApiHeaders) {
    return api.get<T>(url, {
      params,
      headers,
    });
  },

  post<T = any>(url: string, data?: any, headers?: ApiHeaders) {
    return api.post<T>(url, data, {
      headers,
    });
  },

  put<T = any>(url: string, data?: any, headers?: ApiHeaders) {
    return api.put<T>(url, data, {
      headers,
    });
  },

  delete<T = any>(url: string, headers?: ApiHeaders) {
    return api.delete<T>(url, {
      headers,
    });
  },
};
