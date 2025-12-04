<template>
  <div class="home-view">
    <!-- Barra de busca -->
    <div class="search-section">
      <div class="search-card">
        <div class="search-input-group">
          <input
            v-model="searchTerm"
            @keyup.enter="handleSearch"
            type="text"
            placeholder="Ex: Desenvolvedor, Analista, Vendedor..."
            class="search-input"
            :disabled="loading"
          />
          <button 
            @click="handleSearch" 
            class="search-button"
            :disabled="loading || !searchTerm.trim()"
          >
            <span v-if="!loading"> Buscar Vagas</span>
            <span v-else>⏳ Buscando...</span>
          </button>
        </div>
        
        <div class="suggestions">
          <small>Termos populares:</small>
          <button
            v-for="suggestion in suggestions"
            :key="suggestion"
            @click="searchSuggestion(suggestion)"
            class="suggestion-tag"
            :disabled="loading"
          >
            {{ suggestion }}
          </button>
        </div>
      </div>
    </div>
    
    <!-- Status/Erro -->
    <div v-if="statusMessage" class="status-message" :class="statusType">
      {{ statusMessage }}
    </div>
    
    <!-- Estatísticas -->
    <div v-if="jobs.length > 0" class="stats-section">
      <div class="stat-card">
        <div class="stat-icon">📊</div>
        <div class="stat-content">
          <h3>{{ jobs.length }}</h3>
          <p>Vagas Encontradas</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">💰</div>
        <div class="stat-content">
          <h3>{{ averageSalary }}</h3>
          <p>Salário Médio</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🏢</div>
        <div class="stat-content">
          <h3>{{ uniqueCompanies }}</h3>
          <p>Empresas</p>
        </div>
      </div>
    </div>
    
    <!-- Loading -->
    <div v-if="loading" class="loading-overlay">
      <div class="loading-content">
        <div class="spinner"></div>
        <p>Buscando vagas...</p>
        <p class="loading-note">Isso pode levar até 30 segundos ⏳</p>
      </div>
    </div>
    
    <!-- Resultados -->
    <div v-if="jobs.length > 0" class="results-section">
      <div class="results-header">
        <h2>Vagas para "{{ lastSearchTerm }}"</h2>
        <div class="results-controls">
          <label>Ordenar por:</label>
          <select v-model="sortBy" class="sort-select">
            <option value="relevance">Relevância</option>
            <option value="salary-desc">Maior Salário</option>
            <option value="salary-asc">Menor Salário</option>
            <option value="title">Título (A-Z)</option>
          </select>
        </div>
      </div>
      
      <div class="jobs-grid">
        <div 
          v-for="job in sortedJobs" 
          :key="job.titulo + job.empresa + job.fonte"
          class="job-card"
        >
          <div class="job-header">
            <h3 class="job-title">{{ job.titulo }}</h3>
            <span class="job-source" :class="getSourceClass(job.fonte)">
              {{ job.fonte }}
            </span>
          </div>
          
          <div class="job-body">
            <div class="job-info">
              <div class="info-item">
                <span class="info-label">🏢 Empresa:</span>
                <span class="info-value">{{ job.empresa || 'Não informada' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">💰 Salário:</span>
                <span class="info-value salary">{{ formatSalary(job.salario) }}</span>
              </div>
            </div>
          </div>
          
          <div class="job-actions">
            <button class="action-btn primary" @click="viewJobDetails(job)">
              Ver Detalhes
            </button>
            <button class="action-btn secondary" @click="saveJob(job)">
              Salvar
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Sem resultados -->
    <div v-else-if="hasSearched && !loading" class="no-results">
      <div class="no-results-content">
        <div class="no-results-icon">📭</div>
        <h3>Nenhuma vaga encontrada</h3>
        <p>Tente buscar com outros termos</p>
        <button @click="resetSearch" class="retry-button">
          Nova Busca
        </button>
      </div>
    </div>
    
    <!-- Tutorial inicial -->
    <div v-else class="welcome-section">
      <div class="welcome-card">
        <h2>Como usar o JobScraper</h2>
        <div class="instructions">
          <div class="instruction">
            <div class="instruction-number">1</div>
            <div class="instruction-text">
              <strong>Digite o cargo</strong> que está procurando
            </div>
          </div>
          <div class="instruction">
            <div class="instruction-number">2</div>
            <div class="instruction-text">
              <strong>Clique em "Buscar Vagas"</strong> ou pressione Enter
            </div>
          </div>
          <div class="instruction">
            <div class="instruction-number">3</div>
            <div class="instruction-text">
              <strong>Aguarde alguns segundos</strong> enquanto buscamos nas fontes
            </div>
          </div>
          <div class="instruction">
            <div class="instruction-number">4</div>
            <div class="instruction-text">
              <strong>Visualize as vagas</strong> com salários e empresas
            </div>
          </div>
        </div>
        
        <div class="example-searches">
          <p>Exemplos para testar:</p>
          <div class="example-tags">
            <span class="example-tag" @click="searchSuggestion('Desenvolvedor')">Desenvolvedor</span>
            <span class="example-tag" @click="searchSuggestion('Analista')">Analista</span>
            <span class="example-tag" @click="searchSuggestion('Vendedor')">Vendedor</span>
            <span class="example-tag" @click="searchSuggestion('Recepcionista')">Recepcionista</span>
            <span class="example-tag" @click="searchSuggestion('Professor')">Professor</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import api from '@/services/api'

export default {
  name: 'HomeView',
  setup() {
    // Estados reativos
    const searchTerm = ref('')
    const jobs = ref([])
    const loading = ref(false)
    const hasSearched = ref(false)
    const lastSearchTerm = ref('')
    const sortBy = ref('relevance')
    const statusMessage = ref('')
    const statusType = ref('')
    
    // Sugestões
    const suggestions = [
      'Desenvolvedor', 'Analista', 'Engenheiro', 
      'Designer', 'Vendedor', 'Recepcionista',
      'Professor', 'Administrativo', 'Marketing'
    ]
    
    // Função principal de busca
    const handleSearch = async () => {
      const term = searchTerm.value.trim()
      
      if (!term) {
        showStatus('Digite um termo para buscar', 'error')
        return
      }
      
      // Reset estados
      loading.value = true
      jobs.value = []
      statusMessage.value = ''
      lastSearchTerm.value = term
      hasSearched.value = true
      
      try {
        showStatus(`Buscando vagas para "${term}"...`, 'info')
        
        // Chamada para API
        const data = await api.searchJobs(term)
        
        // Processa resposta
        processApiResponse(data)
        
      } catch (error) {
        console.error('Erro na busca:', error)
        showStatus(`Erro: ${error.message || 'Falha na conexão'}`, 'error')
        jobs.value = []
      } finally {
        loading.value = false
      }
    }
    
    // Processa resposta da API
    const processApiResponse = (data) => {
      let vagas = []
      
      // Verifica diferentes formatos de resposta
      if (Array.isArray(data)) {
        vagas = data
      } else if (data && data.vagas) {
        vagas = data.vagas
      } else if (data && data.sucesso && data.vagas) {
        vagas = data.vagas
      } else {
        vagas = data || []
      }
      
      // Filtra vagas inválidas
      vagas = vagas.filter(job => job && job.titulo)
      
      if (vagas.length === 0) {
        showStatus(`Nenhuma vaga encontrada para "${lastSearchTerm.value}"`, 'warning')
      } else {
        showStatus(`Encontradas ${vagas.length} vagas para "${lastSearchTerm.value}"`, 'success')
        jobs.value = vagas
      }
    }
    
    // Funções auxiliares
    const searchSuggestion = (term) => {
      searchTerm.value = term
      handleSearch()
    }
    
    const formatSalary = (salary) => {
      if (!salary || salary === 0 || salary === '0') {
        return 'A combinar'
      }
      
      // Converte para número se for string
      const numSalary = typeof salary === 'string' ? parseFloat(salary) : salary
      
      if (isNaN(numSalary)) {
        return 'A combinar'
      }
      
      return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
      }).format(numSalary)
    }
    
    const getSourceClass = (source) => {
      if (!source) return 'source-default'
      const sourceLower = source.toLowerCase()
      if (sourceLower.includes('bne')) return 'source-bne'
      if (sourceLower.includes('indeed')) return 'source-indeed'
      if (sourceLower.includes('glassdoor')) return 'source-glassdoor'
      return 'source-default'
    }
    
    const showStatus = (message, type) => {
      statusMessage.value = message
      statusType.value = type
      
      // Limpa mensagem após 5 segundos
      setTimeout(() => {
        if (statusMessage.value === message) {
          statusMessage.value = ''
        }
      }, 5000)
    }
    
    const resetSearch = () => {
      searchTerm.value = ''
      jobs.value = []
      hasSearched.value = false
      statusMessage.value = ''
    }
    
    const viewJobDetails = (job) => {
      alert(`Detalhes da vaga:\n\nTítulo: ${job.titulo}\nEmpresa: ${job.empresa}\nSalário: ${formatSalary(job.salario)}\nFonte: ${job.fonte}`)
    }
    
    const saveJob = (job) => {
      // Simula salvamento
      showStatus(`Vaga "${job.titulo}" salva!`, 'success')
    }
    
    // Computed properties
    const sortedJobs = computed(() => {
      const jobsArray = [...jobs.value]
      
      switch (sortBy.value) {
        case 'salary-desc':
          return jobsArray.sort((a, b) => {
            const salA = a.salario || 0
            const salB = b.salario || 0
            return salB - salA
          })
        
        case 'salary-asc':
          return jobsArray.sort((a, b) => {
            const salA = a.salario || 0
            const salB = b.salario || 0
            return salA - salB
          })
        
        case 'title':
          return jobsArray.sort((a, b) => 
            a.titulo.localeCompare(b.titulo)
          )
        
        default:
          return jobsArray
      }
    })
    
    const averageSalary = computed(() => {
      const salarios = jobs.value
        .map(job => job.salario)
        .filter(salario => salario && salario > 0)
      
      if (salarios.length === 0) return 'Não informado'
      
      const soma = salarios.reduce((total, salario) => total + salario, 0)
      const media = soma / salarios.length
      
      return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
      }).format(media)
    })
    
    const uniqueCompanies = computed(() => {
      const empresas = jobs.value.map(job => job.empresa).filter(Boolean)
      return new Set(empresas).size
    })
    
    // Inicialização
    onMounted(() => {
      // Testa conexão com API
      api.testConnection()
        .then(data => {
          console.log('API conectada:', data)
        })
        .catch(error => {
          console.warn('API pode estar offline:', error)
          showStatus('API de scraping pode estar offline', 'warning')
        })
    })
    
    return {
      // Estados
      searchTerm,
      jobs,
      loading,
      hasSearched,
      lastSearchTerm,
      sortBy,
      statusMessage,
      statusType,
      
      // Dados
      suggestions,
      
      // Computed
      sortedJobs,
      averageSalary,
      uniqueCompanies,
      
      // Métodos
      handleSearch,
      searchSuggestion,
      formatSalary,
      getSourceClass,
      resetSearch,
      viewJobDetails,
      saveJob
    }
  }
}
</script>

<style scoped>
.home-view {
  min-height: 100%;
}

/* Seção de busca */
.search-section {
  margin-bottom: 2rem;
}

.search-card {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.search-input-group {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.search-input {
  flex: 1;
  padding: 1rem 1.5rem;
  font-size: 1.1rem;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  transition: border-color 0.3s;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.search-input:disabled {
  background: #f7fafc;
  cursor: not-allowed;
}

.search-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 1rem 2rem;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.search-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.search-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.suggestions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.suggestions small {
  color: #718096;
  margin-right: 0.5rem;
}

.suggestion-tag {
  background: #edf2f7;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  color: #4a5568;
  cursor: pointer;
  transition: all 0.3s;
}

.suggestion-tag:hover:not(:disabled) {
  background: #667eea;
  color: white;
}

.suggestion-tag:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Status messages */
.status-message {
  padding: 1rem 1.5rem;
  border-radius: 8px;
  margin-bottom: 1.5rem;
  font-weight: 500;
}

.status-message.info {
  background: #ebf8ff;
  color: #2a4365;
  border-left: 4px solid #4299e1;
}

.status-message.success {
  background: #f0fff4;
  color: #22543d;
  border-left: 4px solid #48bb78;
}

.status-message.warning {
  background: #fffaf0;
  color: #744210;
  border-left: 4px solid #ed8936;
}

.status-message.error {
  background: #fff5f5;
  color: #c53030;
  border-left: 4px solid #fc8181;
}

/* Estatísticas */
.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: white;
  border-radius: 10px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  font-size: 2rem;
}

.stat-content h3 {
  margin: 0;
  font-size: 1.8rem;
  color: #2d3748;
}

.stat-content p {
  margin: 0.25rem 0 0 0;
  color: #718096;
  font-size: 0.9rem;
}

/* Loading */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.loading-content {
  text-align: center;
  background: white;
  padding: 3rem;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.spinner {
  width: 60px;
  height: 60px;
  border: 4px solid #e2e8f0;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1.5rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-note {
  color: #718096;
  font-size: 0.9rem;
  margin-top: 0.5rem;
}

/* Resultados */
.results-section {
  margin-top: 2rem;
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.results-header h2 {
  color: #2d3748;
  font-size: 1.5rem;
}

.results-controls {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.sort-select {
  padding: 0.5rem 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background: white;
  font-size: 0.9rem;
}

.jobs-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
}

.job-card {
  background: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s, box-shadow 0.3s;
}

.job-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
}

.job-header {
  padding: 1.5rem 1.5rem 1rem;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.job-title {
  margin: 0;
  font-size: 1.2rem;
  color: #2d3748;
  flex: 1;
}

.job-source {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
}

.source-bne {
  background: #c6f6d5;
  color: #22543d;
}

.source-indeed {
  background: #fed7d7;
  color: #742a2a;
}

.source-glassdoor {
  background: #bee3f8;
  color: #2a4365;
}

.source-default {
  background: #e2e8f0;
  color: #4a5568;
}

.job-body {
  padding: 1rem 1.5rem;
}

.job-info {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-label {
  color: #718096;
  font-size: 0.9rem;
}

.info-value {
  font-weight: 500;
  color: #2d3748;
}

.info-value.salary {
  color: #38a169;
  font-weight: 600;
}

.job-actions {
  padding: 1rem 1.5rem;
  border-top: 1px solid #e2e8f0;
  display: flex;
  gap: 0.5rem;
}

.action-btn {
  flex: 1;
  padding: 0.75rem;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.action-btn.primary {
  background: #667eea;
  color: white;
}

.action-btn.primary:hover {
  background: #5a67d8;
}

.action-btn.secondary {
  background: #edf2f7;
  color: #4a5568;
}

.action-btn.secondary:hover {
  background: #e2e8f0;
}

/* Sem resultados */
.no-results {
  text-align: center;
  padding: 4rem 2rem;
}

.no-results-content {
  max-width: 400px;
  margin: 0 auto;
}

.no-results-icon {
  font-size: 4rem;
  margin-bottom: 1.5rem;
  opacity: 0.5;
}

.no-results h3 {
  color: #2d3748;
  margin-bottom: 0.5rem;
}

.no-results p {
  color: #718096;
  margin-bottom: 1.5rem;
}

.retry-button {
  background: #667eea;
  color: white;
  border: none;
  padding: 0.75rem 2rem;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
}

/* Welcome section */
.welcome-section {
  margin-top: 2rem;
}

.welcome-card {
  background: white;
  border-radius: 12px;
  padding: 3rem;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.welcome-card h2 {
  color: #2d3748;
  margin-bottom: 2rem;
}

.instructions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
  text-align: left;
}

.instruction {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
}

.instruction-number {
  background: #667eea;
  color: white;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  flex-shrink: 0;
}

.instruction-text {
  color: #4a5568;
  line-height: 1.5;
}

.example-searches {
  margin-top: 2rem;
}

.example-searches p {
  color: #718096;
  margin-bottom: 1rem;
}

.example-tags {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.example-tag {
  background: #edf2f7;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
}
/* Adicione no final do <style scoped> do HomeView.vue */

.job-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 1.5rem;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.job-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 25px rgba(102, 126, 234, 0.15);
  border-color: #667eea;
}

.job-title {
  color: #2d3748;
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
  padding-bottom: 0.75rem;
  border-bottom: 2px solid #e2e8f0;
}

.job-company {
  color: #4a5568;
  font-size: 1rem;
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.job-salary {
  color: #38a169;
  font-weight: 600;
  font-size: 1.1rem;
  margin-bottom: 0.5rem;
}

.job-source {
  display: inline-block;
  background: #c6f6d5;
  color: #22543d;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
}
<style scoped>

.example-tag:hover {
  background: #667eea;
  color: white;
}

/* Responsividade */
@media (max-width: 768px) {
  .search-input-group {
    flex-direction: column;
  }
  
  .results-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .jobs-grid {
    grid-template-columns: 1fr;
  }
  
  .instructions {
    grid-template-columns: 1fr;
  }
}
</style>