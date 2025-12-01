import axios from 'axios'

const api = axios.create({
  baseURL: '/api', // Usando proxy do Vite
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Interceptores para tratamento de erros
api.interceptors.response.use(
  response => response,
  error => {
    if (error.code === 'ECONNABORTED') {
      throw new Error('Tempo de requisição esgotado')
    }
    if (!error.response) {
      throw new Error('Erro de conexão com o servidor')
    }
    throw error
  }
)

export default {
  // Buscar vagas
  async searchJobs(term) {
    try {
      return await api.post('/scrap', { termo: term })
    } catch (error) {
      // Fallback para GET se POST falhar
      return await api.get('/scrap', { params: { termo: term } })
    }
  },
  
  // Testar conexão com API
  async testConnection() {
    return await api.get('/teste')
  },
  
  // Saudação
  async getGreeting(nome) {
    return await api.get(`/saudacao/${nome}`)
  }
}