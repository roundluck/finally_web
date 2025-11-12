const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

async function apiFetch(path, { method = 'GET', body, token, query } = {}) {
  const headers = { 'Content-Type': 'application/json' };
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  const url = new URL(path, API_BASE_URL);
  if (query) {
    Object.entries(query).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        url.searchParams.append(key, value);
      }
    });
  }
  const response = await fetch(url, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });
  if (!response.ok) {
    let message = `Request failed with status ${response.status}`;
    try {
      const payload = await response.json();
      message = payload.message || payload.error || message;
    } catch (_) {
      const text = await response.text();
      if (text) {
        message = text;
      }
    }
    throw new Error(message);
  }
  if (response.status === 204) {
    return null;
  }
  return response.json();
}

export const apiClient = {
  login: (credentials) => apiFetch('/api/auth/login', { method: 'POST', body: credentials }),
  profile: (token) => apiFetch('/api/auth/me', { token }),
  listRequests: (token, query) => apiFetch('/api/requests', { token, query }),
  createRequest: (token, payload) => apiFetch('/api/requests', { method: 'POST', token, body: payload }),
  assignTechnician: (token, requestId, payload) =>
    apiFetch(`/api/requests/${requestId}/assign`, { method: 'PATCH', token, body: payload }),
  updateStatus: (token, requestId, payload) =>
    apiFetch(`/api/requests/${requestId}/status`, { method: 'PATCH', token, body: payload }),
  listTechnicians: (token) => apiFetch('/api/users', { token, query: { role: 'TECHNICIAN' } }),
};
