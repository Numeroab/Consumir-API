<template>
  <div class="search-container">
    <div class="search-box">
      <i class="pi pi-search search-icon"></i>
      <input
        v-model="localTerm"
        @keyup.enter="performSearch"
        type="text"
        placeholder="Ex: Desenvolvedor Java, Analista de Sistemas, Engenheiro..."
        class="search-input"
      />
      <button @click="performSearch" class="search-button">
        Buscar Vagas
      </button>
    </div>
    <div class="search-suggestions">
      <span>Sugestões:</span>
      <button
        v-for="suggestion in suggestions"
        :key="suggestion"
        @click="searchSuggestion(suggestion)"
        class="suggestion-tag"
      >
        {{ suggestion }}
      </button>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'

export default {
  emits: ['search'],
  setup(props, { emit }) {
    const localTerm = ref('')
    const suggestions = [
      'Desenvolvedor',
      'Analista',
      'Engenheiro',
      'Designer',
      'Marketing',
      'Vendas',
      'Recepcionista',
      'Professor'
    ]
   
    const performSearch = () => {
      if (localTerm.value.trim()) {
        emit('search', localTerm.value.trim())
      }
    }
   
    const searchSuggestion = (suggestion) => {
      localTerm.value = suggestion
      performSearch()
    }
   
    return {
      localTerm,
      suggestions,
      performSearch,
      searchSuggestion
    }
  }
}
</script>

<style scoped>
.search-container {
  background: white;
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.search-box {
  display: flex;
  align-items: center;
  background: #f7fafc;
  border-radius: 12px;
  padding: 0.5rem;
  border: 2px solid #e2e8f0;
  transition: border-color 0.3s;
}

.search-box:focus-within {
  border-color: #667eea;
}

.search-icon {
  padding: 0 1rem;
  color: #a0aec0;
}

.search-input {
  flex: 1;
  border: none;
  background: transparent;
  padding: 1rem;
  font-size: 1.1rem;
  outline: none;
}

.search-input::placeholder {
  color: #a0aec0;
}

.search-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 1rem 2rem;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.search-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.search-suggestions {
  margin-top: 1rem;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.search-suggestions span {
  color: #718096;
  font-size: 0.9rem;
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

.suggestion-tag:hover {
  background: #667eea;
  color: white;
}
</style>