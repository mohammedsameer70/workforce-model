<script setup lang="ts">
import { ref } from 'vue'
import type { TrainingDTO } from "@/dto/TrainingDTO";

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

<style scoped src="./aiModel.css"

</style>
