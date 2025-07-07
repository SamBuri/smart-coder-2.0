import axios from 'axios';
const apiUrl = import.meta.env.VITE_API_BASE_URL;
// const apiUrl ="http://localhost:9797/api/"
const api = 
     axios.create({
      baseURL: apiUrl,
    });

console.log("api", apiUrl)
export default api