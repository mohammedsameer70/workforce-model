<script setup lang="ts">
import { ref } from 'vue'

const datasetName = ref('')
const isPredicting = ref(false)
const predictionProgress = ref(0)

const fileInput = ref<HTMLInputElement>()

const handleFileClick = () => {
  fileInput.value?.click()
}

const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files && target.files[0]) {
    datasetName.value = target.files[0].name
  }
}

const predict = () => {
  if (!datasetName.value) return

  isPredicting.value = true
  predictionProgress.value = 0

  const interval = setInterval(() => {
    predictionProgress.value += Math.random() * 20
    if (predictionProgress.value >= 100) {
      predictionProgress.value = 100
      clearInterval(interval)
      setTimeout(() => {
        isPredicting.value = false
      }, 500)
    }
  }, 300)
}
</script>

<template>
  <div class="prediction-container">
    <!-- Prediction Section -->
    <div class="prediction-section">
      <div class="section-header">
        <h2>Predict Workforce Demand</h2>
        <p>Upload a dataset and run inference with the best model</p>
      </div>

      <div class="prediction-content">
        <div class="upload-box" @click="handleFileClick">
          <input
            ref="fileInput"
            type="file"
            accept=".csv"
            @change="handleFileChange"
            style="display: none"
          />
          <i class="pi pi-upload"></i>
          <p class="upload-text">Choose Prediction Dataset</p>
          <p v-if="datasetName" class="dataset-name">{{ datasetName }}</p>
        </div>

        <div v-if="isPredicting" class="progress-section">
          <p>Running inference...</p>
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: predictionProgress + '%' }"></div>
          </div>
        </div>

        <button :disabled="!datasetName || isPredicting" @click="predict" class="predict-btn">
          <i class="pi pi-play"></i>
          Predict
        </button>
      </div>
    </div>

    <!-- Prediction Graph -->
    <div class="graph-section">
      <div class="section-header">
        <h2>Prediction Graph</h2>
        <p>Actual vs predicted workforce demand with confidence interval</p>
      </div>

      <div class="chart-placeholder">
        <svg viewBox="0 0 800 300" class="prediction-chart">
          <!-- Grid lines -->
          <line x1="50" y1="250" x2="750" y2="250" stroke="rgba(148, 163, 184, 0.2)" stroke-width="1" />
          <line
            x1="50"
            y1="200"
            x2="750"
            y2="200"
            stroke="rgba(148, 163, 184, 0.1)"
            stroke-width="1"
          />
          <line
            x1="50"
            y1="150"
            x2="750"
            y2="150"
            stroke="rgba(148, 163, 184, 0.1)"
            stroke-width="1"
          />
          <line
            x1="50"
            y1="100"
            x2="750"
            y2="100"
            stroke="rgba(148, 163, 184, 0.1)"
            stroke-width="1"
          />
          <line
            x1="50"
            y1="50"
            x2="750"
            y2="50"
            stroke="rgba(148, 163, 184, 0.1)"
            stroke-width="1"
          />

          <!-- Upper bound (confidence interval) -->
          <path
            d="M 70 100 Q 150 60, 230 50 T 390 70 T 550 80 T 710 120"
            fill="none"
            stroke="rgba(96, 165, 250, 0.2)"
            stroke-width="20"
            opacity="0.3"
          />

          <!-- Predicted workforce -->
          <path
            d="M 70 120 Q 150 80, 230 70 T 390 90 T 550 100 T 710 140"
            fill="none"
            stroke="#22d3ee"
            stroke-width="2.5"
            stroke-dasharray="5,5"
          />

          <!-- Actual workforce -->
          <path
            d="M 70 130 Q 150 90, 230 80 T 390 100 T 550 110 T 710 150"
            fill="none"
            stroke="#06b6d4"
            stroke-width="2.5"
          />

          <!-- Lower bound -->
          <path
            d="M 70 160 Q 150 120, 230 110 T 390 130 T 550 140 T 710 180"
            fill="none"
            stroke="rgba(96, 165, 250, 0.2)"
            stroke-width="20"
            opacity="0.3"
          />

          <!-- Y-axis labels -->
          <text x="25" y="255" font-size="12" fill="#6b7280">0</text>
          <text x="15" y="55" font-size="12" fill="#6b7280">200</text>

          <!-- X-axis labels -->
          <text x="50" y="275" font-size="12" fill="#6b7280">00:00</text>
          <text x="150" y="275" font-size="12" fill="#6b7280">04:00</text>
          <text x="250" y="275" font-size="12" fill="#6b7280">08:00</text>
          <text x="350" y="275" font-size="12" fill="#6b7280">12:00</text>
          <text x="450" y="275" font-size="12" fill="#6b7280">16:00</text>
          <text x="550" y="275" font-size="12" fill="#6b7280">20:00</text>
          <text x="650" y="275" font-size="12" fill="#6b7280">00:00</text>
        </svg>
      </div>

      <!-- Legend -->
      <div class="legend">
        <div class="legend-item">
          <span class="legend-color upper-bound"></span>
          <span>Upper Bound</span>
        </div>
        <div class="legend-item">
          <span class="legend-color lower-bound"></span>
          <span>Lower Bound</span>
        </div>
        <div class="legend-item">
          <span class="legend-color actual"></span>
          <span>Actual Workforce</span>
        </div>
        <div class="legend-item">
          <span class="legend-color predicted"></span>
          <span>Predicted Workforce</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.prediction-container {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 2rem;
  margin-bottom: 2rem;
  animation: slideUp 0.6s ease-out 0.2s both;
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

.prediction-section,
.graph-section {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.prediction-section:hover,
.graph-section:hover {
  border-color: #93c5fd;
  box-shadow: 0 8px 16px rgba(96, 165, 250, 0.1);
}

.section-header {
  background: #f0f9ff;
  border-bottom: 2px solid #e0e7ff;
  padding: 1.75rem;
  backdrop-filter: blur(10px);
}

.section-header h2 {
  font-size: 1.25rem;
  font-weight: 800;
  color: #1f2937;
  margin: 0 0 0.25rem 0;
}

.section-header p {
  font-size: 0.9rem;
  color: #6b7280;
  margin: 0;
}

.prediction-content {
  padding: 2rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.upload-box {
  border: 2px dashed #93c5fd;
  border-radius: 12px;
  padding: 2rem;
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
  transform: translateY(-3px);
  box-shadow: 0 15px 35px rgba(96, 165, 250, 0.1);
}

.upload-box i {
  font-size: 2.5rem;
  color: #60a5fa;
  margin-bottom: 0.75rem;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8px);
  }
}

.upload-text {
  font-size: 1rem;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
}

.dataset-name {
  font-size: 0.85rem;
  color: #4ade80;
  margin-top: 0.75rem;
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

.progress-section {
  margin: 0.75rem 0;
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
  font-size: 0.9rem;
  color: #1f2937;
  margin: 0 0 0.75rem 0;
  font-weight: 600;
}

.progress-bar {
  width: 100%;
  height: 8px;
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

.predict-btn {
  background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 50%, #2563eb 100%);
  border: none;
  color: white;
  padding: 0.85rem;
  border-radius: 10px;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 1rem;
  box-shadow: 0 10px 25px rgba(96, 165, 250, 0.2);
}

.predict-btn:hover:not(:disabled) {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(96, 165, 250, 0.4);
}

.predict-btn:active:not(:disabled) {
  transform: translateY(-2px);
}

.predict-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.chart-placeholder {
  padding: 2rem;
  background: #f9fafb;
}

.prediction-chart {
  width: 100%;
  height: auto;
  filter: drop-shadow(0 0 20px rgba(96, 165, 250, 0.1));
}

.legend {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
  padding: 1.5rem;
  border-top: 2px solid #e5e7eb;
  background: #ffffff;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 0.9rem;
  color: #4b5563;
  font-weight: 600;
}

.legend-color {
  width: 24px;
  height: 3px;
  border-radius: 2px;
  flex-shrink: 0;
}

.legend-color.upper-bound,
.legend-color.lower-bound {
  background: linear-gradient(90deg, rgba(96, 165, 250, 0.5) 0%, rgba(96, 165, 250, 0.3) 100%);
}

.legend-color.predicted {
  background: linear-gradient(90deg, #22d3ee 0%, #06b6d4 100%);
}

.legend-color.actual {
  background: linear-gradient(90deg, #06b6d4 0%, #0891b2 100%);
}

@media (max-width: 1024px) {
  .prediction-container {
    grid-template-columns: 1fr;
  }

  .legend {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .prediction-container {
    grid-template-columns: 1fr;
    gap: 1rem;
    margin-bottom: 1.5rem;
  }

  .prediction-section,
  .graph-section {
    border-radius: 12px;
  }

  .section-header {
    padding: 1.25rem;
  }

  .section-header h2 {
    font-size: 1.15rem;
  }

  .prediction-content {
    padding: 1.5rem;
  }

  .legend {
    grid-template-columns: 1fr;
    gap: 0.75rem;
    padding: 1rem;
  }
}
</style>
