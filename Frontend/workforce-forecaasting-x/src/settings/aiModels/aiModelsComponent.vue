<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { lbl } from '@/assets/constants/labels'
import ModelStatsCard from './ModelStatsCard.vue'
import TrainingSection from './TrainingSection.vue'
import ModelComparison from './ModelComparison.vue'
import PredictionSection from './PredictionSection.vue'
import TrainingHistory from './TrainingHistory.vue'

// State
const bestModel = ref('XGBoost')
const r2Score = ref(0.94)
const rmse = ref(3.76)
const trainingStatus = ref('Ready')

const selectedAlgorithms = reactive<string[]>([])
const uploadedDataset = ref<File | null>(null)
const datasetName = ref('')
const trainingProgress = ref(0)
const isTraining = ref(false)
const trainingError = ref('')

const models = ref([
  {
    name: 'XGBoost',
    rmse: 3.76,
    mae: 2.78,
    mape: 2.37,
    r2: 0.94,
    trainingTime: '2m 14s',
    status: 'Best',
  },
  {
    name: 'LSTM',
    rmse: 4.1,
    mae: 3.01,
    mape: 2.91,
    r2: 0.91,
    trainingTime: '8m 32s',
    status: 'Good',
  },
  {
    name: 'Random Forest',
    rmse: 4.58,
    mae: 3.44,
    mape: 3.12,
    r2: 0.88,
    trainingTime: '1m 47s',
    status: 'Good',
  },
  {
    name: 'Linear Regression',
    rmse: 7.23,
    mae: 5.89,
    mape: 6.45,
    r2: 0.74,
    trainingTime: '0m 08s',
    status: 'Poor',
  },
])

const trainingHistory = ref([
  {
    date: '2026-07-25 14:32',
    dataset: 'warehouse_q2_2026.csv',
    algorithmsUsed: ['XGBoost', 'LSTM', 'Random Forest'],
    bestModel: 'XGBoost',
    rmse: 3.76,
    r2: 0.94,
    status: 'success',
    actions: ['view', 'download', 'delete'],
  },
  {
    date: '2026-07-20 09:15',
    dataset: 'warehouse_q1_2026.csv',
    algorithmsUsed: ['Linear Regression', 'XGBoost'],
    bestModel: 'XGBoost',
    rmse: 4.12,
    r2: 0.91,
    status: 'success',
    actions: ['view', 'download', 'delete'],
  },
  {
    date: '2026-07-14 16:45',
    dataset: 'warehouse_2025_full.csv',
    algorithmsUsed: ['LSTM', 'Random Forest', 'XGBoost', 'Linear Regression'],
    bestModel: 'LSTM',
    rmse: 3.42,
    r2: 0.96,
    status: 'success',
    actions: ['view', 'download', 'delete'],
  },
  {
    date: '2026-07-10 11:20',
    dataset: 'test_dataset_small.csv',
    algorithmsUsed: ['Linear Regression'],
    bestModel: '-',
    rmse: '-',
    r2: '-',
    status: 'failed',
    actions: ['view', 'delete'],
  },
])

// Methods
const handleFileUpload = (file: File) => {
  uploadedDataset.value = file
  datasetName.value = file.name
}

const toggleAlgorithm = (algo: string) => {
  const index = selectedAlgorithms.indexOf(algo)
  if (index > -1) {
    selectedAlgorithms.splice(index, 1)
  } else {
    selectedAlgorithms.push(algo)
  }
}

const trainModel = async () => {
  if (!uploadedDataset.value || selectedAlgorithms.length === 0) {
    trainingError.value = 'Please upload a dataset and select at least one algorithm'
    return
  }

  isTraining.value = true
  trainingError.value = ''
  trainingProgress.value = 0
  trainingStatus.value = 'Training...'

  // Simulate training progress
  const interval = setInterval(() => {
    trainingProgress.value += Math.random() * 15
    if (trainingProgress.value >= 100) {
      trainingProgress.value = 100
      clearInterval(interval)
      isTraining.value = false
      trainingStatus.value = 'Ready'

      // Add new model to history
      trainingHistory.value.unshift({
        date: new Date().toLocaleString(),
        dataset: datasetName.value,
        algorithmsUsed: selectedAlgorithms,
        bestModel: 'XGBoost',
        rmse: 3.76,
        r2: 0.94,
        status: 'success',
        actions: ['view', 'download', 'delete'],
      })

      // Reset form
      uploadedDataset.value = null
      datasetName.value = ''
      selectedAlgorithms.length = 0
    }
  }, 500)
}

const cancelTraining = () => {
  isTraining.value = false
  trainingProgress.value = 0
  trainingStatus.value = 'Ready'
  trainingError.value = ''
}
</script>

<template>
  <div class="ai-models-container">
    <!-- Header -->
    <div class="header">
      <h1>AI Model Management</h1>
      <p class="subtitle">{{ lbl.aiModelsDesc }}</p>
    </div>

    <!-- Best Model Stats -->
    <div class="stats-grid">
      <ModelStatsCard
        :title="'BEST MODEL'"
        :value="bestModel"
        :icon="'pi-star'"
        :color="'#a78bfa'"
      />
      <ModelStatsCard
        :title="'R² SCORE'"
        :value="r2Score.toString()"
        :icon="'pi-chart-line'"
        :color="'#60a5fa'"
      />
      <ModelStatsCard
        :title="'RMSE'"
        :value="rmse.toString()"
        :icon="'pi-chart-bar'"
        :color="'#4ade80'"
      />
      <ModelStatsCard
        :title="'TRAINING STATUS'"
        :value="trainingStatus"
        :icon="'pi-circle-fill'"
        :color="'#fbbf24'"
      />
    </div>

    <!-- Training Section -->
    <TrainingSection
      :datasetName="datasetName"
      :selectedAlgorithms="selectedAlgorithms"
      :isTraining="isTraining"
      :trainingProgress="trainingProgress"
      :trainingError="trainingError"
      @file-upload="handleFileUpload"
      @algorithm-toggle="toggleAlgorithm"
      @train-model="trainModel"
      @cancel-training="cancelTraining"
    />

    <!-- Model Comparison -->
    <ModelComparison :models="models" />

    <!-- Prediction Section -->
    <PredictionSection />

    <!-- Training History -->
    <TrainingHistory :history="trainingHistory" />
  </div>
</template>

<style scoped>
.ai-models-container {
  padding: 2rem;
  background: #ffffff;
  min-height: 100vh;
  color: #1f2937;
  animation: fadeIn 0.5s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.header {
  margin-bottom: 2.5rem;
  animation: slideDown 0.6s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.header h1 {
  font-size: 2.5rem;
  font-weight: 800;
  color: #1f2937;
  margin: 0 0 0.75rem 0;
  letter-spacing: -1px;
}

.subtitle {
  font-size: 1rem;
  color: #6b7280;
  margin: 0;
  font-weight: 500;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2.5rem;
}

@media (max-width: 768px) {
  .ai-models-container {
    padding: 1rem;
  }

  .header h1 {
    font-size: 1.75rem;
  }

  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 1rem;
  }
}
</style>
