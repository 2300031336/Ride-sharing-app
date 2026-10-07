import axios from 'axios';

const envBase = (import.meta as ImportMeta).env?.VITE_API_URL as string | undefined;

const api = axios.create({ baseURL: envBase || undefined });

export default api;
