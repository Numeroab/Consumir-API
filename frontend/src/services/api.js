import axios from 'axios'

// Configuração do axios
const api = axios.create({
  baseURL: '/api',
  timeout: 120000, // 2 minutos para o scraping
  headers: {
    'Content-Type': 'application/json'
  }
})

export default {
  // Buscar vagas (tenta POST, depois GET)
  async searchJobs(term) {
    try {
      // Primeiro tenta POST
      const response = await api.post('/scrap', { termo: term })
      return response.data
    } catch (postError) {
      console.log('POST falhou, tentando GET...')
      try {
        // Se POST falhar, tenta GET
        const response = await api.get('/scrap', {
          params: { termo: term }
        })
        return response.data
      } catch (getError) {
        console.error('GET também falhou:', getError)
        throw getError
      }
    }
  },
  
  // Testar conexão
  async testConnection() {
    try {
      const response = await api.get('/teste')
      return response.data
    } catch (error) {
      throw error
    }
  },
  
  // Saudação
  async getGreeting(nome) {
    try {
      const response = await api.get(`/saudacao/${nome}`)
      return response.data
    } catch (error) {
      throw error
    }
  }
}