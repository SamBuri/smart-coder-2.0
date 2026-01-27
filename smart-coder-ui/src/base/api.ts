import axios from 'axios';

// Log this so you can see it in the terminal/console
const rawUrl = import.meta.env.VITE_API_BASE_URL;
console.log("Raw Env Value:", rawUrl);

// Ensure there is a valid string, otherwise default to localhost
const apiUrl = rawUrl && rawUrl !== '' ? rawUrl : 'http://localhost:9797/api/';

const api = axios.create({
  baseURL: apiUrl,
  headers: {
    'Content-Type': 'application/json'
  }
});

console.log("Final API BaseURL:", api.defaults.baseURL);

export default api;
