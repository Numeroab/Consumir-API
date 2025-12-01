<template>
  <main class="home-container">
    <div class="hero-section">
      <div class="hero-content">
        <h2>Encontre as melhores vagas</h2>
        <p>Pesquise vagas em tempo real de múltiplas fontes</p>
      </div>
    </div>
    
    <div class="content-container">
      <SearchBar @search="handleSearch" />
      
      <div v-if="loading" class="loading-container">
        <LoadingSpinner />
        <p>Buscando vagas...</p>
      </div>
      
      <div v-if="error" class="error-message">
        <i class="pi pi-exclamation-triangle"></i>
        <p>{{ error }}</p>
      </div>
      
      <div v-if="showStats && vagas.length > 0" class="stats-section">
        <div class="stat-card">
          <i class="pi pi-briefcase"></i>
          <div>
            <h3>{{ vagas.length }}</h3>
            <p>Vagas Encontradas</p>
          </div>
        </div>
        <div class="stat-card">
          <i class="pi pi-chart-line"></i>
          <div>
            <h3>{{ avgSalary }}</h3>
            <p>Salário Médio</p>
          </div>
        </div>
        <div class="stat-card">
          <i class="pi pi-building"></i>
          <div>
            <h3>{{ uniqueCompanies }}</h3>
            <p>Empresas Diferentes</p>
          </div>
        </div>
      </div>
      
      <div v-if="vagas.length > 0" class="results-section">
        <div class="results-header">
          <h3>Resultados para "{{ searchTerm }}"</h3>
          <div class="filter-controls">
            <select v-model="sortBy" class="filter-select">
              <option value="relevance">Relevância</option>
              <option value="salary">Maior Salário</option>
              <option value="title">Título</option>
            </select>
            <button @click="toggleView" class="view-toggle">
              <i :class="gridView ? 'pi pi-th-large' : 'pi pi-list'"></i>
            </button>
          </div>
        </div>
        
        <div :class="['results-grid', { 'list-view': !gridView }]">
          <JobCard 
            v-for="vaga in sortedVagas" 
            :key="vaga.titulo + vaga.empresa"
            :vaga="vaga"
            :grid-view="gridView"
          />
        </div>
      </div>
      
      <div v-else-if="!loading && searchTerm" class="no-results">
        <i class="pi pi-inbox"></i>
        <h3>Nenhuma vaga encontrada</h3>
        <p>Tente buscar com outros termos</p>
      </div>
    </div>
  </main>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useToast } from 'primevue/usetoast'
import SearchBar from '@/components/SearchBar.vue'
import JobCard from '@/components/JobCard.vue'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import api from '@/services/api'

export default {
  components: {
    SearchBar,
    JobCard,
    LoadingSpinner
  },
  setup() {
    const toast = useToast()
    const searchTerm = ref('')
    const vagas = ref([])
    const loading = ref(false)
    const error = ref('')
    const showStats = ref(true)
    const sortBy = ref('relevance')
    const gridView = ref(true)
    
    const handleSearch = async (term) => {
      if (!term.trim()) {
        toast.add({
          severity: 'warn',
          summary: 'Atenção',
          detail: 'Digite um termo para buscar',
          life: 3000
        })
        return
      }
      
      searchTerm.value = term
      loading.value = true
      error.value = ''
      
      try {
        const response = await api.searchJobs(term)
        if (response.data.sucesso) {
          vagas.value = response.data.vagas
          toast.add({
            severity: 'success',
            summary: 'Sucesso!',
            detail: `Encontradas ${response.data.totalVagas} vagas`,
            life: 3000
          })
        } else {
          error.value = response.data.erro || 'Erro na busca'
        }
      } catch (err) {
        error.value = 'Erro ao conectar com o servidor'
        console.error(err)
      } finally {
        loading.value = false
      }
    }
    
    const sortedVagas = computed(() => {
      const vagasArray = [...vagas.value]
      switch (sortBy.value) {
        case 'salary':
          return vagasArray.sort((a, b) => (b.salario || 0) - (a.salario || 0))
        case 'title':
          return vagasArray.sort((a, b) => a.titulo.localeCompare(b.titulo))
        default:
          return vagasArray
      }
    })
    
    const avgSalary = computed(() => {
      const salarios = vagas.value
        .map(v => v.salario)
        .filter(s => s !== null && s > 0)
      
      if (salarios.length === 0) return 'Não informado'
      
      const avg = salarios.reduce((a, b) => a + b, 0) / salarios.length
      return `R$ ${avg.toFixed(2)}`
    })
    
    const uniqueCompanies = computed(() => {
      const companies = new Set(vagas.value.map(v => v.empresa))
      return companies.size
    })
    
    const toggleView = () => {
      gridView.value = !gridView.value
    }
    
    onMounted(() => {
      // Exemplo de busca inicial
      // handleSearch('desenvolvedor')
    })
    
    return {
      searchTerm,
      vagas,
      loading,
      error,
      showStats,
      sortBy,
      gridView,
      handleSearch,
      sortedVagas,
      avgSalary,
      uniqueCompanies,
      toggleView
    }
  }
}
</script>

<style scoped>
.home-container {
  flex: 1;
}

.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4rem 1rem;
  text-align: center;
}

.hero-content h2 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.hero-content p {
  font-size: 1.2rem;
  opacity: 0.9;
}

.content-container {
  max-width: 1200px;
  margin: -2rem auto 2rem;
  padding: 0 1rem;
}

.loading-container {
  text-align: center;
  padding: 3rem;
  color: #4a5568;
}

.error-message {
  background: #fed7d7;
  border: 1px solid #fc8181;
  color: #c53030;
  padding: 1rem;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin: 1rem 0;
}

.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin: 2rem 0;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 1rem;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-card i {
  font-size: 2rem;
  color: #667eea;
}

.stat-card h3 {
  margin: 0;
  font-size: 1.8rem;
}

.stat-card p {
  margin: 0;
  color: #718096;
  font-size: 0.9rem;
}

.results-section {
  margin-top: 2rem;
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.filter-controls {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.filter-select {
  padding: 0.5rem 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background: white;
}

.view-toggle {
  background: #667eea;
  color: white;
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.results-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.results-grid.list-view {
  grid-template-columns: 1fr;
}

.no-results {
  text-align: center;
  padding: 4rem;
  color: #718096;
}

.no-results i {
  font-size: 3rem;
  margin-bottom: 1rem;
  color: #cbd5e0;
}
</style>