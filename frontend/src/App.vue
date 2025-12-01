<template>
  <div id="app">
    <Toast />
    <nav class="navbar">
      <div class="nav-container">
        <div class="logo">
          <i class="pi pi-search" style="font-size: 1.5rem"></i>
          <h1>JobScraper</h1>
        </div>
        <div class="nav-links">
          <router-link to="/" class="nav-link">
            <i class="pi pi-home"></i> Início
          </router-link>
          <a href="#" class="nav-link" @click="showStats = !showStats">
            <i class="pi pi-chart-bar"></i> Estatísticas
          </a>
        </div>
      </div>
    </nav>
    
    <router-view />
    
    <footer class="footer">
      <p>© 2024 JobScraper - Todos os direitos reservados</p>
      <p>API de scraping de vagas em tempo real</p>
    </footer>
  </div>
</template>

<script>
import { ref } from 'vue'
import Toast from 'primevue/toast'

export default {
  components: {
    Toast
  },
  setup() {
    const showStats = ref(false)
    
    return {
      showStats
    }
  }
}
</script>

<style scoped>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1rem 0;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.logo h1 {
  margin: 0;
  font-size: 1.8rem;
  font-weight: 700;
}

.nav-links {
  display: flex;
  gap: 2rem;
}

.nav-link {
  color: white;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 500;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  transition: background-color 0.3s;
}

.nav-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.footer {
  background: #2d3748;
  color: white;
  text-align: center;
  padding: 2rem;
  margin-top: auto;
}

.footer p {
  margin: 0.5rem 0;
  opacity: 0.8;
}
</style>