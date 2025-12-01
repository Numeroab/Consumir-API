<template>
  <div :class="['job-card', { 'list-view': !gridView }]">
    <div class="job-header">
      <div class="job-title-section">
        <h3>{{ vaga.titulo }}</h3>
        <span class="company">{{ vaga.empresa }}</span>
      </div>
      <div class="job-source">
        <span class="source-badge" :class="vaga.fonte.toLowerCase()">
          {{ vaga.fonte }}
        </span>
      </div>
    </div>
    
    <div class="job-details">
      <div class="detail-item">
        <i class="pi pi-money-bill"></i>
        <div>
          <span class="detail-label">Salário</span>
          <span class="detail-value salary">{{ vaga.salarioFormatado }}</span>
        </div>
      </div>
      
      <div class="detail-item">
        <i class="pi pi-building"></i>
        <div>
          <span class="detail-label">Fonte</span>
          <span class="detail-value">{{ vaga.fonte }}</span>
        </div>
      </div>
    </div>
    
    <div class="job-actions">
      <button class="action-btn apply">
        <i class="pi pi-external-link"></i>
        Candidatar-se
      </button>
      <button class="action-btn save">
        <i class="pi pi-bookmark"></i>
        Salvar
      </button>
      <button class="action-btn share" @click="shareJob">
        <i class="pi pi-share-alt"></i>
      </button>
    </div>
  </div>
</template>

<script>
import { useToast } from 'primevue/usetoast'

export default {
  props: {
    vaga: {
      type: Object,
      required: true
    },
    gridView: {
      type: Boolean,
      default: true
    }
  },
  setup(props) {
    const toast = useToast()
    
    const shareJob = () => {
      const shareText = `Vaga: ${props.vaga.titulo} na ${props.vaga.empresa} - ${props.vaga.salarioFormatado}`
      
      if (navigator.share) {
        navigator.share({
          title: props.vaga.titulo,
          text: shareText,
          url: window.location.href
        })
      } else {
        navigator.clipboard.writeText(shareText)
        toast.add({
          severity: 'info',
          summary: 'Copiado!',
          detail: 'Informações da vaga copiadas',
          life: 2000
        })
      }
    }
    
    return {
      shareJob
    }
  }
}
</script>

<style scoped>
.job-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.job-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
}

.job-card.list-view {
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}

.job-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.job-title-section h3 {
  margin: 0 0 0.5rem 0;
  color: #2d3748;
  font-size: 1.2rem;
}

.company {
  color: #718096;
  font-size: 0.9rem;
}

.source-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
}

.source-badge.bne {
  background: #c6f6d5;
  color: #22543d;
}

.source-badge.glassdoor {
  background: #bee3f8;
  color: #2a4365;
}

.source-badge.indeed {
  background: #fed7d7;
  color: #742a2a;
}

.job-details {
  display: flex;
  gap: 1.5rem;
}

.job-card.list-view .job-details {
  flex: 1;
  max-width: 400px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.detail-item i {
  color: #667eea;
  font-size: 1.2rem;
}

.detail-label {
  display: block;
  font-size: 0.8rem;
  color: #718096;
}

.detail-value {
  display: block;
  font-weight: 600;
  color: #2d3748;
}

.salary {
  color: #38a169;
  font-size: 1.1rem;
}

.job-actions {
  display: flex;
  gap: 0.5rem;
  margin-top: auto;
}

.job-card.list-view .job-actions {
  margin-top: 0;
}

.action-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 500;
  transition: all 0.3s;
}

.apply {
  background: #667eea;
  color: white;
  flex: 1;
}

.apply:hover {
  background: #5a67d8;
}

.save {
  background: #edf2f7;
  color: #4a5568;
}

.save:hover {
  background: #e2e8f0;
}

.share {
  background: transparent;
  color: #718096;
  padding: 0.5rem;
}

.share:hover {
  color: #667eea;
}
</style>