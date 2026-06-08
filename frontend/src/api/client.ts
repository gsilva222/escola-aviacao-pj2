const BASE = import.meta.env.VITE_API_BASE_URL || '/api';

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export function getToken(): string | null {
  return localStorage.getItem('token');
}

export function setToken(token: string) {
  localStorage.setItem('token', token);
}

export function clearToken() {
  localStorage.removeItem('token');
}

export function getStoredAuth(): AuthResponse | null {
  const raw = localStorage.getItem('auth');
  return raw ? (JSON.parse(raw) as AuthResponse) : null;
}

export function setStoredAuth(auth: AuthResponse) {
  localStorage.setItem('auth', JSON.stringify(auth));
  setToken(auth.token);
}

export function clearStoredAuth() {
  localStorage.removeItem('auth');
  clearToken();
}

export interface AuthResponse {
  token: string;
  username: string;
  role: 'ADMIN' | 'STUDENT';
  studentId: number | null;
  staffProfile?: string | null;
}

export class ApiError extends Error {
  status: number;
  constructor(status: number, message: string) {
    super(message);
    this.status = status;
  }
}

export async function api<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers);
  if (!(options.body instanceof FormData) && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json');
  }
  const token = getToken();
  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  const response = await fetch(`${BASE}${path}`, { ...options, headers });
  if (!response.ok) {
    const body = await response.json().catch(() => ({ message: response.statusText }));
    throw new ApiError(response.status, body.message || 'Erro na API');
  }
  if (response.status === 204) {
    return undefined as T;
  }
  const text = await response.text();
  return text ? (JSON.parse(text) as T) : (undefined as T);
}

export async function apiList<T>(path: string): Promise<T[]> {
  const data = await api<Page<T> | T[]>(path);
  return Array.isArray(data) ? data : data.content;
}

export async function uploadFile<T>(path: string, file: File, category?: string): Promise<T> {
  const form = new FormData();
  form.append('file', file);
  if (category) {
    form.append('category', category);
  }
  return api<T>(path, { method: 'POST', body: form });
}

export async function downloadFile(path: string, fileName: string) {
  const token = getToken();
  const response = await fetch(`${BASE}${path}`, {
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  });
  if (!response.ok) {
    throw new ApiError(response.status, 'Erro no download');
  }
  const blob = await response.blob();
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = fileName;
  a.click();
  URL.revokeObjectURL(url);
}
