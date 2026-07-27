<script setup lang="ts">
defineProps<{
  history: Array<{
    date: string
    dataset: string
    algorithmsUsed: string[]
    bestModel: string
    rmse: string | number
    r2: string | number
    status: string
    actions: string[]
  }>
}>()

const viewRecord = (date: string) => {
  console.log('View record:', date)
}

const downloadRecord = (date: string) => {
  console.log('Download record:', date)
}

const deleteRecord = (date: string) => {
  console.log('Delete record:', date)
}
</script>

<template>
  <div class="training-history">
    <div class="section-header">
      <h2>Training History</h2>
      <p>All past training runs with results and actions</p>
    </div>

    <div class="table-wrapper">
      <table class="history-table">
        <thead>
          <tr>
            <th>Training Date</th>
            <th>Dataset</th>
            <th>Algorithms Used</th>
            <th>Best Model</th>
            <th>RMSE</th>
            <th>R²</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="record in history" :key="record.date" :class="`status-${record.status}`">
            <td class="date-cell">{{ record.date }}</td>
            <td>{{ record.dataset }}</td>
            <td class="algorithms-cell">
              <div class="algorithm-tags">
                <span v-for="algo in record.algorithmsUsed" :key="algo" class="algo-tag">
                  {{ algo }}
                </span>
              </div>
            </td>
            <td class="best-model">{{ record.bestModel }}</td>
            <td>{{ record.rmse }}</td>
            <td class="r2-value">{{ record.r2 }}</td>
            <td>
              <span class="status-badge" :class="`status-${record.status}`">
                <i v-if="record.status === 'success'" class="pi pi-check"></i>
                <i v-else class="pi pi-times"></i>
                {{ record.status }}
              </span>
            </td>
            <td class="actions-cell">
              <button
                v-if="record.actions.includes('view')"
                @click="viewRecord(record.date)"
                class="action-btn view-btn"
                title="View"
              >
                <i class="pi pi-eye"></i>
              </button>
              <button
                v-if="record.actions.includes('download')"
                @click="downloadRecord(record.date)"
                class="action-btn download-btn"
                title="Download"
              >
                <i class="pi pi-download"></i>
              </button>
              <button
                v-if="record.actions.includes('delete')"
                @click="deleteRecord(record.date)"
                class="action-btn delete-btn"
                title="Delete"
              >
                <i class="pi pi-trash"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.training-history {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  animation: slideUp 0.6s ease-out 0.3s both;
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

.history-table {
  width: 100%;
  border-collapse: collapse;
  padding: 1.5rem;
}

.history-table thead {
  background: #f9fafb;
  border-bottom: 2px solid #d1d5db;
}

.history-table th {
  padding: 1.25rem;
  text-align: left;
  font-weight: 700;
  color: #374151;
  font-size: 0.8rem;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  white-space: nowrap;
}

.history-table td {
  padding: 1.25rem;
  border-bottom: 1px solid #e5e7eb;
  color: #4b5563;
  font-weight: 600;
  transition: all 0.3s ease;
}

.history-table tbody tr {
  transition: all 0.3s ease;
}

.history-table tbody tr:hover {
  transform: scale(1.01);
}

.date-cell {
  color: #1f2937;
  font-weight: 800;
  font-size: 1.05rem;
}

.algorithms-cell {
  padding: 1.25rem;
}

.algorithm-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.algo-tag {
  display: inline-block;
  color: #2563eb;
  padding: 0.35rem 0.85rem;
  border-radius: 16px;
  font-size: 0.75rem;
  font-weight: 700;
  border: 1.5px solid #93c5fd;
  background: #eff6ff;
  transition: all 0.3s ease;
  text-transform: capitalize;
}

.algo-tag:hover {
  border-color: #60a5fa;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(96, 165, 250, 0.15);
}

.best-model {
  color: #1f2937;
  font-weight: 800;
  font-size: 1.05rem;
}

.r2-value {
  color: #2563eb;
  font-weight: 800;
  font-size: 1.1rem;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 0.85rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  transition: all 0.3s ease;
}

.status-badge.status-success {
  color: #166534;
  border: 1.5px solid #86efac;
}

.status-badge.status-failed {
  color: #991b1b;
  border: 1.5px solid #fecaca;
}

.status-badge i {
  font-size: 0.85rem;
  margin-right: 0.25rem;
}

.status-success {
  background: #f0fdf4;
  border-color: #dcfce7;
}

.status-failed {
  background: #fef2f2;
  border-color: #fee2e2;
}

.actions-cell {
  display: flex;
  gap: 0.75rem;
}

.action-btn {
  width: 38px;
  height: 38px;
  border-radius: 8px;
  border: none;
  color: #6b7280;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 1rem;
  border: 1.5px solid #d1d5db;
  background: #f3f4f6;
}

.action-btn:hover {
  border-color: #9ca3af;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.action-btn.view-btn:hover {
  color: #2563eb;
  border-color: #93c5fd;
  background: #eff6ff;
}

.action-btn.download-btn:hover {
  color: #16a34a;
  border-color: #86efac;
  background: #f0fdf4;
}

.action-btn.delete-btn:hover {
  color: #dc2626;
  border-color: #fecaca;
  background: #fee2e2;
}

@media (max-width: 1024px) {
  .history-table {
    font-size: 0.875rem;
  }

  .history-table th,
  .history-table td {
    padding: 1rem;
  }

  .action-btn {
    width: 34px;
    height: 34px;
    font-size: 0.9rem;
  }
}

@media (max-width: 640px) {
  .training-history {
    border-radius: 12px;
  }

  .section-header {
    padding: 1.25rem;
  }

  .section-header h2 {
    font-size: 1.2rem;
  }
}
</style>
