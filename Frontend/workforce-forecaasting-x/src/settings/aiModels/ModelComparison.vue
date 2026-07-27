<script setup lang="ts">
defineProps<{
  models: Array<{
    name: string
    rmse: number
    mae: number
    mape: number
    r2: number
    trainingTime: string
    status: string
  }>
}>()
</script>

<template>
  <div class="model-comparison">
    <div class="section-header">
      <h2>Model Comparison</h2>
      <p>Performance metrics across all trained algorithms</p>
    </div>

    <div class="table-wrapper">
      <table class="comparison-table">
        <thead>
          <tr>
            <th>Model</th>
            <th>RMSE</th>
            <th>MAE</th>
            <th>MAPE</th>
            <th>R²</th>
            <th>Training Time</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="model in models" :key="model.name" :class="`status-${model.status.toLowerCase()}`">
            <td class="model-name">
              <span v-if="model.status === 'Best'" class="best-badge">
                <i class="pi pi-star-fill"></i>
              </span>
              {{ model.name }}
            </td>
            <td>{{ model.rmse }}</td>
            <td>{{ model.mae }}</td>
            <td>{{ model.mape }}%</td>
            <td class="r2-value">{{ model.r2 }}</td>
            <td>{{ model.trainingTime }}</td>
            <td>
              <span class="status-badge" :class="`status-${model.status.toLowerCase()}`">
                {{ model.status }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.model-comparison {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  margin-bottom: 2rem;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  animation: slideUp 0.6s ease-out 0.1s both;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.section-header {
  background: #f0f9ff;
  border-bottom: 2px solid #e0e7ff;
  padding: 2rem;
  backdrop-filter: blur(10px);
}

.section-header h2 {
  font-size: 1.5rem;
  font-weight: 800;
  color: #1f2937;
  margin: 0 0 0.35rem 0;
}

.section-header p {
  font-size: 0.95rem;
  color: #6b7280;
  margin: 0;
}

.table-wrapper {
  overflow-x: auto;
}

.comparison-table {
  width: 100%;
  border-collapse: collapse;
  padding: 1.5rem;
}

.comparison-table thead {
  background: #f9fafb;
  border-bottom: 2px solid #d1d5db;
}

.comparison-table th {
  padding: 1.25rem;
  text-align: left;
  font-weight: 700;
  color: #374151;
  font-size: 0.8rem;
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

.comparison-table td {
  padding: 1.25rem;
  border-bottom: 1px solid #e5e7eb;
  color: #4b5563;
  font-weight: 600;
  transition: all 0.3s ease;
}

.comparison-table tbody tr {
  transition: all 0.3s ease;
}

.comparison-table tbody tr:hover {
  background: #f0f9ff;
  transform: scale(1.01);
}

.model-name {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-weight: 700;
  color: #1f2937;
  font-size: 1.05rem;
}

.best-badge {
  color: #fbbf24;
  font-size: 1.25rem;
  animation: twinkle 1.5s ease-in-out infinite;
}

@keyframes twinkle {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.6;
    transform: scale(1.1);
  }
}

.r2-value {
  color: #2563eb;
  font-weight: 800;
  font-size: 1.1rem;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  transition: all 0.3s ease;
}

.status-badge.status-best {
  background: #fef3c7;
  color: #b45309;
  border: 1.5px solid #fde68a;
}

.status-badge.status-good {
  background: #dcfce7;
  color: #166534;
  border: 1.5px solid #bbf7d0;
}

.status-badge.status-poor {
  background: #fee2e2;
  color: #991b1b;
  border: 1.5px solid #fecaca;
}

.status-best {
  background: #fffbeb;
  border-color: #fef3c7;
}

.status-good {
  background: #f0fdf4;
  border-color: #dcfce7;
}

.status-poor {
  background: #fef2f2;
  border-color: #fee2e2;
}

@media (max-width: 768px) {
  .comparison-table th,
  .comparison-table td {
    padding: 0.75rem;
    font-size: 0.85rem;
  }

  .section-header {
    padding: 1.5rem;
  }

  .section-header h2 {
    font-size: 1.25rem;
  }
}
</style>
