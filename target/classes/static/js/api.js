// Shared API helper. Matches com.wellbeing.exception.GlobalExceptionHandler's
// response shapes exactly:
//   404 / 409 -> { timestamp, status, error }
//   400 (bean validation) -> { timestamp, status, error, fields: { field: message } }
const API_BASE = "/api";

class ApiError extends Error {
  constructor(status, message, fields) {
    super(message);
    this.status = status;
    this.fields = fields || null;
  }
}

async function apiRequest(path, options = {}) {
  const res = await fetch(API_BASE + path, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });

  if (res.status === 204) return null;

  let body = null;
  try {
    body = await res.json();
  } catch (_) {
    // no body (shouldn't happen for our endpoints, but don't crash on it)
  }

  if (!res.ok) {
    const message = (body && body.error) || `Request failed with status ${res.status}`;
    throw new ApiError(res.status, message, body && body.fields);
  }

  return body;
}

const api = {
  get: (path) => apiRequest(path),
  post: (path, data) => apiRequest(path, { method: "POST", body: JSON.stringify(data) }),
  put: (path, data) => apiRequest(path, { method: "PUT", body: JSON.stringify(data) }),
  del: (path) => apiRequest(path, { method: "DELETE" }),
};
