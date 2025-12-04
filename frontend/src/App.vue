<template>
  <div id="app">
    <header class="app-header">
      <div class="container">
        <h1>JobScraper</h1>
        <p class="subtitle">Busca de vagas em tempo real</p>
      </div>
    </header>
    
    <main class="app-main">
      <div class="container">
        <HomeView />
      </div>
    </main>
    
    <footer class="app-footer">
      <div class="container">
        <p>© 2024 JobScraper - API de Scraping</p>
        <p class="api-status">
          API Status: 
          <span :class="apiStatusClass">{{ apiStatusText }}</span>
        </p>
      </div>
    </footer>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import HomeView from './views/HomeView.vue'
import api from './services/api'

export default {
  name: 'App',
  components: {
    HomeView
  },
  setup() {
    const apiStatusText = ref('Testando...')
    const apiStatusClass = ref('status-testing')
    
    const testApiConnection = async () => {
      try {
        const result = await api.testConnection()
        apiStatusText.value = 'Online '
        apiStatusClass.value = 'status-online'
        console.log('API conectada:', result)
      } catch (error) {
        apiStatusText.value = 'Offline '
        apiStatusClass.value = 'status-offline'
        console.error('API offline:', error)
      }
    }
    
    onMounted(() => {
      testApiConnection()
    })
    
    return {
      apiStatusText,
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
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
  background-color: #f5f7fa;
  color: #333;
  line-height: 1.6;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.app-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 2rem 0;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.app-header h1 {
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
}

.subtitle {
  font-size: 1.2rem;
  opacity: 0.9;
}

.app-main {
  min-height: calc(100vh - 200px);
  padding: 2rem 0;
}

.app-footer {
  background: #2d3748;
  color: white;
  padding: 1.5rem 0;
  text-align: center;
}

.app-footer p {
  margin: 0.5rem 0;
}

.api-status {
  font-size: 0.9rem;
  margin-top: 0.5rem;
}

.status-testing {
  color: #fbbf24;
}

.status-online {
  color: #10b981;
}

.status-offline {
  color: #ef4444;
}
</style>