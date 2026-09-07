const BASE_URL = 'http://localhost:8080';

async function request(path, options = {}) {
  return fetch(`${BASE_URL}${path}`, {
    credentials: 'include',
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options,
  });
}

async function getJson(path) {
  const res = await request(path);
  return res.json();
}

async function getText(path) {
  const res = await request(path);
  return res.text();
}

async function postJson(path, body) {
  return request(path, { method: 'POST', body: JSON.stringify(body) });
}

export default { getJson, getText, postJson };