<script setup lang="ts">
import { ref } from 'vue'

defineProps<{
  datasetName: string
  selectedAlgorithms: string[]
  isTraining: boolean
  trainingProgress: number
  trainingError: string
}>()

const emit = defineEmits<{
  'file-upload': [file: File]
  'algorithm-toggle': [algo: string]
  'train-model': []
  'cancel-training': []
}>()

const fileInput = ref<HTMLInputElement>()

const algorithms = ['Linear Regression', 'Random Forest', 'XGBoost', 'LSTM']

const handleFileClick = () => {
  fileInput.value?.click()
}

const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files && target.files[0]) {
    emit('file-upload', target.files[0])
  }
}

const toggleAlgorithm = (algo: string) => {
  emit('algorithm-toggle', algo)
}

const trainModel = () => {
  emit('train-model')
}

const cancelTraining = () => {
  emit('cancel-training')
}
</script>

<template>
  <div class="training-section">
    <div class="section-header">
      <div class="header-content">
        <i class="pi pi-info-circle"></i>
        <div>
          <h2>Train New AI Model</h2>
          <p>Upload a dataset, select algorithms, and run training</p>
        </div>
      </div>
    </div>

    <div class="training-content">
      <!-- Upload Section -->
      <div class="upload-section">
        <h3>Upload Training Dataset</h3>
        <div class="upload-box" @click="handleFileClick">
          <input
            ref="fileInput"
            type="file"
            accept=".csv"
            @change="handleFileChange"
            style="display: none"
          />
          <i class="pi pi-upload"></i>
          <p class="upload-text">Choose Training CSV</p>
          <p class="upload-subtext">Click to browse</p>
          <p v-if="datasetName" class="dataset-name">{{ datasetName }}</p>
        </div>
      </div>

      <!-- Algorithm Selection -->
      <div class="algorithm-section">
        <h3>Algorithm Selection</h3>
        <div class="algorithm-grid">
          <label v-for="algo in algorithms" :key="algo" class="algorithm-checkbox">
            <input
              type="checkbox"
              :checked="selectedAlgorithms.includes(algo)"
              @change="toggleAlgorithm(algo)"
            />
            <span>{{ algo }}</span>
          </label>
        </div>
      </div>

      <!-- Training Progress -->
      <div v-if="isTraining" class="progress-section">
        <p>Training Progress: {{ Math.round(trainingProgress) }}%</p>
        <div class="progress-bar">
          <div class="progress-fill" :style="{ width: trainingProgress + '%' }"></div>
        </div>
      </div>

      <!-- Error Message -->
      <div v-if="trainingError" class="error-message">
        <i class="pi pi-exclamation-circle"></i>
        {{ trainingError }}
      </div>

      <!-- Actions -->
      <div class="actions">
        <button
          :disabled="isTraining || !datasetName || selectedAlgorithms.length === 0"
          @click="trainModel"
          class="train-btn"
        >
          <i class="pi pi-play"></i>
          Train Model
        </button>
        <button v-if="isTraining" @click="cancelTraining" class="cancel-btn">
          <i class="pi pi-times"></i>
          Cancel
        </button>
        <button class="refresh-btn" title="Refresh training">
          <i class="pi pi-refresh"></i>
        </button>
      </div>

      <!-- Info Text -->
      <p v-if="!isTraining && !datasetName" class="info-text">
        Upload a CSV and start training to see live progress here
      </p>
    </div>
  </div>
</template>

<style scoped>
.training-section {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  margin-bottom: 2rem;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  animation: slideUp 0.6s ease-out;
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

.header-content {
  display: flex;
  gap: 1.25rem;
  align-items: flex-start;
}

.section-header i {
  color: #60a5fa;
  font-size: 1.75rem;
  margin-top: 0.25rem;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
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

.training-content {
  padding: 2.5rem;
}

.upload-section,
.algorithm-section {
  margin-bottom: 2.5rem;
}

.upload-section h3,
.algorithm-section h3 {
  font-size: 1.1rem;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 1.25rem 0;
}

.upload-box {
  border: 2px dashed #93c5fd;
  border-radius: 12px;
  padding: 2.5rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  background: #f9fafb;
  position: relative;
  overflow: hidden;
}

.upload-box::before {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle, rgba(96, 165, 250, 0.1) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.upload-box:hover::before {
  opacity: 1;
}

.upload-box:hover {
  border-color: #60a5fa;
  background: #f0f9ff;
  transform: translateY(-4px);
  box-shadow: 0 15px 35px rgba(96, 165, 250, 0.1);
}

.upload-box i {
  font-size: 3rem;
  color: #60a5fa;
  margin-bottom: 1rem;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.upload-text {
  font-size: 1.1rem;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 0.5rem 0;
}

.upload-subtext {
  font-size: 0.95rem;
  color: #6b7280;
  margin: 0;
}

.dataset-name {
  font-size: 0.9rem;
  color: #4ade80;
  margin-top: 1rem;
  font-weight: 700;
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.algorithm-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 1.25rem;
}

.algorithm-checkbox {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1.25rem;
  background: #f9fafb;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 600;
  color: #4b5563;
}

.algorithm-checkbox:hover {
  background: #f0f9ff;
  border-color: #93c5fd;
  transform: translateY(-3px);
  box-shadow: 0 10px 25px rgba(96, 165, 250, 0.1);
}

.algorithm-checkbox input[type='checkbox'] {
  width: 20px;
  height: 20px;
  cursor: pointer;
  accent-color: #60a5fa;
}

.algorithm-checkbox input[type='checkbox']:checked + span {
  color: #60a5fa;
  font-weight: 800;
}

.progress-section {
  margin-bottom: 1.5rem;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.progress-section p {
  color: #1f2937;
  margin: 0 0 1rem 0;
  font-size: 0.95rem;
  font-weight: 600;
}

.progress-bar {
  width: 100%;
  height: 10px;
  background: #e5e7eb;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.06);
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #60a5fa 0%, #3b82f6 50%, #06b6d4 100%);
  border-radius: 10px;
  transition: width 0.3s ease;
  box-shadow: 0 0 20px rgba(96, 165, 250, 0.5);
}

.error-message {
  background: #fee2e2;
  border: 2px solid #fecaca;
  border-radius: 10px;
  padding: 1rem;
  color: #dc2626;
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
  animation: shake 0.5s ease;
}

@keyframes shake {
  0%, 100% {
    transform: translateX(0);
  }
  25% {
    transform: translateX(-5px);
  }
  75% {
    transform: translateX(5px);
  }
}

.error-message i {
  font-size: 1.5rem;
}

.actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

.train-btn {
  flex: 1;
  background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 50%, #2563eb 100%);
  border: none;
  color: white;
  padding: 1rem 1.5rem;
  border-radius: 10px;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 1.05rem;
  box-shadow: 0 10px 25px rgba(96, 165, 250, 0.2);
}

.train-btn:hover:not(:disabled) {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(96, 165, 250, 0.4);
}

.train-btn:active:not(:disabled) {
  transform: translateY(-2px);
}

.train-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.cancel-btn {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 50%, #b91c1c 100%);
  border: none;
  color: white;
  padding: 1rem 1.5rem;
  border-radius: 10px;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 10px 25px rgba(239, 68, 68, 0.2);
}

.cancel-btn:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(239, 68, 68, 0.4);
}

.refresh-btn {
  background: #f3f4f6;
  border: 2px solid #d1d5db;
  color: #4b5563;
  padding: 1rem 1.5rem;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
}

.refresh-btn:hover {
  background: #e5e7eb;
  border-color: #9ca3af;
  transform: rotate(90deg);
}

.info-text {
  font-size: 0.95rem;
  color: #6b7280;
  margin-top: 2rem;
  text-align: center;
  font-style: italic;
}

@media (max-width: 768px) {
  .training-content {
    padding: 1.5rem;
  }

  .algorithm-grid {
    grid-template-columns: 1fr;
  }

  .actions {
    flex-direction: column;
  }

  .train-btn,
  .cancel-btn,
  .refresh-btn {
    width: 100%;
  }
}
</style>
