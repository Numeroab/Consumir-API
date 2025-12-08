<template>
  <div class="home">
    <!-- Busca -->
    <div class="search-box">
      <input
        v-model="searchTerm"
        @keyup.enter="searchJobs"
        type="text"
        placeholder="Digite o cargo..."
      />
      <button @click="searchJobs" :disabled="loading">
        {{ loading ? 'Buscando...' : 'Buscar' }}
      </button>
    </div>

    <!-- Sugestões -->
    <div class="suggestions">
      <span 
        v-for="suggestion in suggestions"
        :key="suggestion"
        @click="searchSuggestion(suggestion)"
        class="suggestion"
      >
        {{ suggestion }}
      </span>
    </div>

    <!-- Status -->
    <div v-if="message" :class="['message', messageType]">
      {{ message }}
    </div>

    <!-- Loading -->
    <div v-if="loading" class="loading">
      Buscando vagas... Aguarde.
    </div>

    <!-- Resultados -->
    <div v-if="jobs.length > 0" class="results">
      <h3>{{ jobs.length }} vagas encontradas</h3>
      
      <div class="job-list">
        <div v-for="job in jobs" :key="job.titulo" class="job-card">
          <h4>{{ job.titulo }}</h4>
          <p><strong>Empresa:</strong> {{ job.empresa || 'Não informada' }}</p>
          <p><strong>Salário:</strong> {{ formatSalary(job.salario) }}</p>
          <span class="source">{{ job.fonte }}</span>
        </div>
      </div>
    </div>

    <!-- Sem resultados -->
    <div v-else-if="searched && !loading" class="no-results">
      Nenhuma vaga encontrada para "{{ lastSearchTerm }}"
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import api from '../services/api'

export default {
  name: 'HomeView',
  setup() {
    const searchTerm = ref('')
    const jobs = ref([])
    const loading = ref(false)
    const searched = ref(false)
    const lastSearchTerm = ref('')
    const message = ref('')
    const messageType = ref('')
    const suggestions = ['Desenvolvedor', 'Analista', 'Vendedor']

    const searchJobs = async () => {
      const term = searchTerm.value.trim()
      if (!term) return

      loading.value = true
      jobs.value = []
      searched.value = true
      lastSearchTerm.value = term
      message.value = ''

      try {
        const data = await api.buscarVagas(term)
        
        if (data.vagas && data.vagas.length > 0) {
          jobs.value = data.vagas
          message.value = `Encontradas ${data.vagas.length} vagas`
          messageType.value = 'success'
        } else {
          message.value = 'Nenhuma vaga encontrada'
          messageType.value = 'error'
        }
      } catch (error) {
        message.value = 'Erro ao buscar vagas'
        messageType.value = 'error'
      } finally {
        loading.value = false
      }
    }

    const searchSuggestion = (term) => {
      searchTerm.value = term
      searchJobs()
    }

    const formatSalary = (salary) => {
      if (!salary) return 'A combinar'
      return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
      }).format(salary)
    }

    onMounted(() => {
      // Testa API
      api.testarConexao()
        .then(() => console.log('API OK'))
        .catch(() => console.log('API erro'))
    })

    return {
      searchTerm,
      jobs,
      loading,
      searched,
      lastSearchTerm,
      message,
      messageType,
      suggestions,
      searchJobs,
      searchSuggestion,
      formatSalary
    }
  }
}
</script>

<style scoped>
.home {
  padding: 20px;
}

.search-box {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-box input {
  flex: 1;
  padding: 12px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 16px;
}

.search-box button {
  padding: 12px 24px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.search-box button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.suggestions {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.suggestion {
  background: #e2e8f0;
  padding: 6px 12px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
}

.suggestion:hover {
  background: #667eea;
  color: white;
}

.message {
  padding: 12px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.message.success {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.message.error {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #666;
}

.results h3 {
  margin-bottom: 20px;
  color: #333;
}

.job-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.job-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  border-left: 4px solid #667eea;
}

.job-card h4 {
  margin-bottom: 10px;
  color: #2d3748;
}

.job-card p {
  margin: 8px 0;
  color: #4a5568;
}

.source {
  display: inline-block;
  background: #c6f6d5;
  color: #22543d;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  margin-top: 10px;
}

.no-results {
  text-align: center;
  padding: 40px;
  color: #718096;
}

@media (max-width: 768px) {
  .search-box {
    flex-direction: column;
  }
  
  .job-list {
    grid-template-columns: 1fr;
  }
}
</style>