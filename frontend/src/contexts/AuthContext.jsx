import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { apiClient } from '../api/client';

const AuthContext = createContext(null);
const TOKEN_KEY = 'dorm-maintenance-token';

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY));
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(!!token);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!token) {
      setProfile(null);
      setLoading(false);
      return;
    }
    setLoading(true);
    apiClient
      .profile(token)
      .then((data) => {
        setProfile(data);
        setError(null);
      })
      .catch((err) => {
        console.error(err);
        setProfile(null);
        setToken(null);
        localStorage.removeItem(TOKEN_KEY);
      })
      .finally(() => setLoading(false));
  }, [token]);

  const login = async (username, password) => {
    const auth = await apiClient.login({ username, password });
    setToken(auth.token);
    localStorage.setItem(TOKEN_KEY, auth.token);
    const data = await apiClient.profile(auth.token);
    setProfile(data);
    return data;
  };

  const logout = () => {
    setToken(null);
    setProfile(null);
    localStorage.removeItem(TOKEN_KEY);
  };

  const value = useMemo(
    () => ({ token, profile, login, logout, loading, error }),
    [token, profile, loading, error]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuth must be used inside AuthProvider');
  }
  return ctx;
};
