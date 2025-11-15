import { useEffect, useMemo, useState } from 'react';
import { apiClient } from '../api/client';
import { AuthContext } from './AuthContext.js';
const TOKEN_KEY = 'dorm-maintenance-token';

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY));
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(!!token);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!token) {
      return;
    }
    let active = true;
    apiClient
      .profile(token)
      .then((data) => {
        if (!active) return;
        setProfile(data);
        setError(null);
      })
      .catch((err) => {
        console.error(err);
        if (!active) return;
        setError(err.message);
        setProfile(null);
        setToken(null);
        localStorage.removeItem(TOKEN_KEY);
      })
      .finally(() => {
        if (active) {
          setLoading(false);
        }
      });
    return () => {
      active = false;
    };
  }, [token]);

  const login = async (username, password) => {
    const auth = await apiClient.login({ username, password });
    setToken(auth.token);
    localStorage.setItem(TOKEN_KEY, auth.token);
    const data = await apiClient.profile(auth.token);
    setProfile(data);
    setError(null);
    setLoading(false);
    return data;
  };

  const logout = () => {
    setToken(null);
    setProfile(null);
    setError(null);
    setLoading(false);
    localStorage.removeItem(TOKEN_KEY);
  };

  const value = useMemo(
    () => ({ token, profile, login, logout, loading, error }),
    [token, profile, loading, error]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
