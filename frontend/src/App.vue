<template>
  <div id="app">
    <header>
      <h1>JobScraper</h1>
      <p>Buscador de vagas de emprego</p>
    </header>
    
    <main>
      <HomeView />
    </main>
    
    <footer>
      <p>Sistema de scraping de vagas</p>
      <p v-if="apiStatus" :class="apiStatusClass">{{ apiStatus }}</p>
    </footer>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import HomeView from './views/HomeView.vue'

export default {
  name: 'App',
  components: {
    HomeView
  },
  setup() {
    const apiStatus = ref('')
    const apiStatusClass = ref('')
    
    const checkApi = async () => {
      try {
        const response = await fetch('/api/teste')
        if (response.ok) {
          apiStatus.value = 'API Online'
          apiStatusClass.value = 'success'
        } else {
          apiStatus.value = 'API Offline'
          apiStatusClass.value = 'error'
        }
      } catch {
        apiStatus.value = 'Erro de conexão'
        apiStatusClass.value = 'error'
      }
    }
    
    onMounted(() => {
      checkApi()
    })
    
    return {
      apiStatus,
      apiStatusClass
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: Arial, sans-serif;
  background: #f5f7fa;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-align: center;
  padding: 2rem 1rem;
}

header h1 {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

main {
  flex: 1;
  padding: 2rem 1rem;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

footer {
  background: #2d3748;
  color: white;
  text-align: center;
  padding: 1rem;
}

footer p {
  margin: 0.25rem 0;
}

.success {
  color: #10b981;
}

.error {
  color: #ef4444;
}
</style>