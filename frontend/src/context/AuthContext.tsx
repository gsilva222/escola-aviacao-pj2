import { createContext, useContext, useMemo, useState, type ReactNode } from 'react';
import {
  api,
  clearStoredAuth,
  getStoredAuth,
  setStoredAuth,
  type AuthResponse,
} from '../api/client';

interface AuthContextValue {
  auth: AuthResponse | null;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  isAdmin: boolean;
  isStudent: boolean;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [auth, setAuth] = useState<AuthResponse | null>(() => getStoredAuth());

  const value = useMemo<AuthContextValue>(
    () => ({
      auth,
      login: async (username, password) => {
        const response = await api<AuthResponse>('/auth/login', {
          method: 'POST',
          body: JSON.stringify({ username, password }),
        });
        setStoredAuth(response);
        setAuth(response);
      },
      logout: () => {
        clearStoredAuth();
        setAuth(null);
      },
      isAdmin: auth?.role === 'ADMIN',
      isStudent: auth?.role === 'STUDENT',
    }),
    [auth],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return ctx;
}
