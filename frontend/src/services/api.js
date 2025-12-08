import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 60000
})

export default {
  async buscarVagas(termo) {
    try {
      const response = await api.post('/scrap', { termo })
      return response.data
    } catch {
      const response = await api.get('/scrap', { params: { termo } })
      return response.data
    }
  },
  
  async testarConexao() {
    const response = await api.get('/teste')
    return response.data
  }
}