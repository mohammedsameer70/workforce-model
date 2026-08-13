<template>
  <div class="ai-models-container">

    <!-- ================= HEADER ================= -->

    <div class="header">
      <div class="header-content">
        <div class="header-text">
          <h1>AI Model Management</h1>
          <p class="subtitle">{{ lbl.aiModelsDesc }}</p>
        </div>
      </div>
    </div>

    <!-- ================= KPI CARDS ================= -->

    <div class="cardsInfo">

      <div class="card">
        <div class="iconBox">
          <i class="pi pi-star icon"></i>
        </div>
        <h3>BEST MODEL</h3>
        <p>{{ trainingResult?.bestModel ?? "-" }}</p>
      </div>

      <div class="card">
        <div class="iconBox">
          <i class="pi pi-percentage icon"></i>
        </div>
        <h3>R² SCORE</h3>
        <p>{{ trainingResult?.r2 ? (trainingResult.r2 * 100).toFixed(1) + '%' : '-' }}</p>
      </div>

      <div class="card">
        <div class="iconBox">
          <i class="pi pi-chart-bar icon"></i>
        </div>
        <h3>RMSE</h3>
        <p>{{ trainingResult?.rmse?.toFixed(2) ?? "-" }}</p>
      </div>

      <div class="card">
        <div class="iconBox">
          <i class="pi pi-clock icon"></i>
        </div>
        <h3>TRAINING TIME</h3>
        <p>{{ trainingResult?.trainingTime ?? "-" }}</p>
      </div>

      <div class="card">
        <div class="iconBox">
          <i class="pi pi-check-circle icon"></i>
        </div>
        <h3>STATUS</h3>
        <p>{{ trainingStatus }}</p>
      </div>

    </div>

    <!-- ================= TRAINING SECTION ================= -->

    <div class="training-section">

      <div class="section-header">
        <div class="header-content">
          <i class="pi pi-cog"></i>

          <div>
            <h2>Train AI Models</h2>

            <p>
              Upload your dataset and select one or more machine learning
              algorithms for workforce forecasting.
            </p>
          </div>
        </div>
      </div>

      <div class="training-content">

        <!-- Upload -->

        <div class="upload-section">

          <h3>Dataset</h3>

         <FileUpload
    mode="basic"
    name="dataset"
    accept=".csv"
    chooseLabel="Browse Dataset"
    :auto="false"
    :customUpload="true"
    @select="onTrainingFileSelect"
/>

<p
    v-if="datasetName"
    class="dataset-name"
>
    {{ datasetName }}
</p>
        </div>

        <!-- Algorithms -->

        <div class="algorithm-section">

          <h3>Select Algorithms</h3>

          <div class="algorithm-grid">

            <label
              class="algorithm-checkbox"
              v-for="algorithm in algorithms"
              :key="algorithm"
            >

              <input
                type="checkbox"
                :value="algorithm"
                v-model="selectedAlgorithms"
              />

              <span>{{ algorithm }}</span>

            </label>

          </div>

        </div>

        <!-- Progress -->

        <div
          class="progress-section"
          v-if="isTraining"
        >

          <p>
            Training Models...
          </p>

          <div class="progress-bar">

            <div
              class="progress-fill"
              :style="{
                width: trainingProgress + '%'
              }"
            ></div>

          </div>

        </div>

        <!-- Error -->

        <div
          class="error-message"
          v-if="trainingError"
        >

          <i class="pi pi-exclamation-circle"></i>

          <span>
            {{ trainingError }}
          </span>

        </div>

        <!-- Buttons -->

        <div class="actions">

          <button
            class="train-btn"
            @click="trainModel"
            :disabled="isTraining"
          >

            <i class="pi pi-play"></i>

            {{ isTraining ? "Training..." : "Train Models" }}

          </button>

          <button
            class="cancel-btn"
            @click="cancelTraining"
            v-if="isTraining"
          >

            <i class="pi pi-times"></i>

            Cancel

          </button>

        </div>

      </div>

    </div>

    <!-- PART 2 STARTS HERE -->
         <!-- ================= MODEL COMPARISON ================= -->

    <div class="model-comparison">

      <div class="section-header">
        <h2>Model Comparison</h2>
        <p>Performance metrics of trained algorithms</p>
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

          <tr
              v-for="model in models"
              :key="model.name"
          >

            <td>

              <span
                  v-if="model.status==='Best'"
                  class="best-badge"
              >
                <i class="pi pi-star-fill"></i>
              </span>

              {{ model.name }}

            </td>

            <td>{{ model.rmse }}</td>

            <td>{{ model.mae }}</td>

            <td>{{ model.mape }}%</td>

            <td>{{ model.r2 }}</td>

            <td>{{ model.trainingTime }}</td>

            <td>

              <span
                  class="status-badge"
                  :class="'status-'+model.status.toLowerCase()"
              >
                {{ model.status }}
              </span>

            </td>

          </tr>

          </tbody>

        </table>

      </div>

    </div>



    <!-- ================= PREDICTION ================= -->

    <div class="prediction-container">

      <div class="prediction-section">

        <div class="section-header">

          <h2>Prediction</h2>

          <p>
            Upload a dataset to predict workforce demand.
          </p>

        </div>

        <div class="prediction-content">

          <div class="upload-box">
            <FileUpload
    mode="basic"
    name="predictionDataset"
    accept=".csv"
    chooseLabel="Choose Dataset"
    :auto="false"
    :customUpload="true"
    @select="onPredictionFileSelect"
/>

<p
    v-if="predictionDatasetName"
    class="dataset-name"
>
    {{ predictionDatasetName }}
</p>

          </div>

          <div
              class="progress-section"
              v-if="isPredicting"
          >

            <p>

              Predicting...

            </p>

            <div class="progress-bar">

              <div
                  class="progress-fill"
                  :style="{
                        width: predictionProgress + '%'
                  }"
              ></div>

            </div>

          </div>

          <button
              class="train-btn"
              @click="predict"
              :disabled="!predictionFile || isPredicting"
          >

            <i class="pi pi-play"></i>

            {{ isPredicting ? "Predicting..." : "Predict" }}

          </button>

        </div>

      </div>



      <!-- Graph -->

      <div class="graph-section">

        <div class="section-header">

          <h2>Prediction Graph</h2>

          <p>
            Actual vs Predicted Workforce Demand
          </p>

        </div>

        <div class="chart-placeholder">

          <div v-if="predictionResults.length === 0" class="empty-state">
            <p>No prediction data available. Run a prediction to see results.</p>
          </div>

          <svg
              v-else
              class="prediction-chart"
              viewBox="0 0 800 300"
          >

            <line
                x1="50"
                y1="250"
                x2="750"
                y2="250"
                stroke="#d1d5db"
            />

            <!-- Predicted Demand Line -->
            <path
                :d="generatePredictionPath()"
                fill="none"
                stroke="#3b82f6"
                stroke-width="3"
            />

            <!-- Actual Demand Line (if available) -->
            <path
                v-if="hasActualData()"
                :d="generateActualPath()"
                fill="none"
                stroke="#06b6d4"
                stroke-width="3"
                stroke-dasharray="8,5"
            />

          </svg>

        </div>

      </div>

    </div>



    <!-- ================= TRAINING HISTORY ================= -->

    <div class="training-history">

      <div class="section-header">

        <h2>

          Training History

        </h2>

      </div>

      <div class="table-wrapper">

        <table class="history-table">

          <thead>

          <tr>

            <th>Date</th>

            <th>Dataset</th>

            <th>Algorithms</th>

            <th>Best Model</th>

            <th>RMSE</th>

            <th>R²</th>

            <th>Status</th>

            <th>Actions</th>

          </tr>

          </thead>

          <tbody>

          <tr
              v-for="record in trainingHistory"
              :key="record.date"
          >

            <td>

              {{ record.date }}

            </td>

            <td>

              {{ record.dataset }}

            </td>

            <td>

              <div class="algorithm-tags">

                <span
                    class="algo-tag"
                    v-for="algo in record.algorithmsUsed"
                    :key="algo"
                >

                  {{ algo }}

                </span>

              </div>

            </td>

            <td>

              {{ record.bestModel }}

            </td>

            <td>

              {{ record.rmse }}

            </td>

            <td>

              {{ record.r2 }}

            </td>

            <td>

              <span
                  class="status-badge"
                  :class="'status-'+record.status"
              >

                {{ record.status }}

              </span>

            </td>

            <td>

              <button
                  v-if="record.actions.includes('view')"
                  class="action-btn"
                  @click="viewRecord(record.date)"
              >

                <i class="pi pi-eye"></i>

              </button>

              <button
                  v-if="record.actions.includes('download')"
                  class="action-btn"
                  @click="downloadRecord(record.date)"
              >

                <i class="pi pi-download"></i>

              </button>

              <button
                  v-if="record.actions.includes('delete')"
                  class="action-btn"
                  @click="deleteRecord(record.date)"
              >

                <i class="pi pi-trash"></i>

              </button>

            </td>

          </tr>

          </tbody>

        </table>

      </div>

    </div>

  </div>

</template>
<script setup lang="ts">
import { ref } from "vue";
import { lbl } from "@/assets/constants/labels";
import FileUpload, { FileUploadSelectEvent } from "primevue/fileupload";
import AIModelService from "./aiModelService.ts";
import type {
    TrainingResponseDTO,
    TrainingHistoryDTO
} from "./trainingDTO";

/* =======================
   KPI Result
======================= */

const trainingResult = ref<TrainingResponseDTO | null>(null);

const trainingStatus = ref("Ready");

/* =======================
   Dataset
======================= */

const trainingFileInput = ref<HTMLInputElement | null>(null);

const predictionFileInput = ref<HTMLInputElement | null>(null);

const selectedFile = ref<File | null>(null);

const predictionFile = ref<File | null>(null);

const datasetName = ref("");

const predictionDatasetName = ref("");

/* =======================
   Algorithms
======================= */

const algorithms = [

    "Linear Regression",

    "Random Forest",

    "XGBoost",

    "LSTM"

];
const onTrainingFileSelect = (event: FileUploadSelectEvent) => {

    const file = event.files[0];

    if (!file) return;

    selectedFile.value = file;
    datasetName.value = file.name;

};

const selectedAlgorithms = ref<string[]>([]);

/* =======================
   Progress
======================= */

const isTraining = ref(false);

const trainingProgress = ref(0);

const trainingError = ref("");

const isPredicting = ref(false);

const predictionProgress = ref(0);

/* =======================
   Model Comparison
======================= */

const models = ref<Array<{
    name: string;
    rmse: number;
    mae: number;
    mape: number;
    r2: number;
    trainingTime: string;
    status: string;
}>>([]);
const onPredictionFileSelect = (event: FileUploadSelectEvent) => {

    const file = event.files[0];

    if (!file) return;

    predictionFile.value = file;
    predictionDatasetName.value = file.name;

};

/* =======================
   History
======================= */

const trainingHistory = ref<TrainingHistoryDTO[]>([]);

/* =======================
   File Upload
======================= */

const openPredictionFileDialog = () => {
    predictionFileInput.value?.click();
};
const openTrainingFileDialog = () => {
    console.log(trainingFileInput.value);
    trainingFileInput.value?.click();
};


const handlePredictionFileChange = (event: Event) => {

    const input = event.target as HTMLInputElement;

    if (!input.files?.length) return;

   const file = input.files?.item(0);

    if (!file) return;

    predictionFile.value = file;
    predictionDatasetName.value = file.name;

};
const getSelectedFile = (event: Event): File | null => {
    const input = event.target as HTMLInputElement;
    return input.files?.item(0) ?? null;
};
const handleTrainingFileChange = (event: Event) => {
    const file = getSelectedFile(event);

    if (!file) return;

    selectedFile.value = file;
    datasetName.value = file.name;
};

 
/* =======================
   Train
======================= */
const trainModel = async () => {

    if (!selectedFile.value) {
        trainingError.value = "Please upload a dataset.";
        return;
    }

    if (selectedAlgorithms.value.length === 0) {
        trainingError.value = "Please select at least one algorithm.";
        return;
    }

    trainingError.value = "";
    trainingStatus.value = "Training...";
    isTraining.value = true;

    const formData = new FormData();

    formData.append("file", selectedFile.value);

    selectedAlgorithms.value.forEach(algo => {
        formData.append("algorithms", algo);
    });

    try {

        const response = await AIModelService.train(formData);

        console.log("Training Response:", response);
        // Automatically download cleaned dataset
        const cleanedDataset = await AIModelService.downloadCleanedDataset();

        const url = window.URL.createObjectURL(cleanedDataset);

        const link = document.createElement("a");

        link.href = url;
        link.download = "cleaned_dataset.csv";

        document.body.appendChild(link);

        link.click();

        document.body.removeChild(link);

        window.URL.revokeObjectURL(url);

        trainingStatus.value = response.status;

        trainingResult.value = {
            fileName: selectedFile.value.name,
            bestModel: response.bestModel,
            rmse: response.metrics.RMSE,
            r2: response.metrics.R2,
            status: response.status,
            actions: ["view", "download", "delete"]
        };

        // Update comparison table from backend
        if (response.comparison) {
            models.value = response.comparison.map((m: any) => ({
                name: m.Model,
                rmse: m.RMSE,
                mae: m.MAE,
                mape: m.MAPE,
                r2: m.R2,
                trainingTime: "-",
                status: m.Model === response.bestModel ? "Best" : "Good"
            }));
        }

        // Add training history
        trainingHistory.value.unshift({
            date: new Date().toLocaleString(),
            dataset: selectedFile.value.name,
            algorithmsUsed: [...selectedAlgorithms.value],
            bestModel: response.bestModel,
            rmse: response.metrics.RMSE,
            r2: response.metrics.R2,
            status: response.status.toLowerCase(),
            actions: ["view", "download", "delete"]
        });

        // Reset UI
        isTraining.value = false;

        selectedAlgorithms.value = [];
        selectedFile.value = null;
        datasetName.value = "";

    } catch (error: any) {

        console.error(error);
        trainingStatus.value = "Failed";
        trainingError.value =
            error?.response?.data?.message ||
            error?.message ||
            "Training failed.";

        isTraining.value = false;
    }
};
/* =======================
   Prediction
======================= */

const predictionResults = ref<any[]>([]);

const predict = async () => {

    if (!predictionFile.value) return;

    isPredicting.value = true;

    predictionProgress.value = 0;

    const formData = new FormData();

    formData.append("file", predictionFile.value);

    try {

        const response = await AIModelService.predict(formData);

        console.log("Prediction Response:", response);

        predictionProgress.value = 100;

        // Store prediction results for graph
        if (response.results && Array.isArray(response.results)) {
            predictionResults.value = response.results;
        }

        // Reset UI
        isPredicting.value = false;

        predictionProgress.value = 0;

        predictionFile.value = null;

        predictionDatasetName.value = "";

    } catch (error: any) {

        console.error(error);

        isPredicting.value = false;

        predictionProgress.value = 0;

        trainingError.value =
            error?.response?.data?.detail ||
            error?.message ||
            "Prediction failed.";

    }

};

/* =======================
   Cancel
======================= */

const cancelTraining = () => {
    // Note: Backend does not currently support cancellation
    // This only resets the UI state
    isTraining.value = false;

    trainingStatus.value = "Cancelled";

    trainingError.value = "Training was cancelled. Note: The backend may still be processing.";
};

/* =======================
   History Actions
======================= */

const viewRecord = (date: string) => {

    console.log("View", date);

};

const downloadRecord = (date: string) => {

    console.log("Download", date);

};

const deleteRecord = (date: string) => {

    trainingHistory.value = trainingHistory.value.filter(

        record => record.date !== date

    );

};

/* =======================
   Graph Helpers
======================= */

const hasActualData = () => {
    return predictionResults.value.some((r: any) => r.ActualDemand != null);
};

const generatePredictionPath = () => {
    if (predictionResults.value.length === 0) return "";

    const data = predictionResults.value.slice(0, 50); // Limit to 50 points
    const width = 700;
    const height = 200;
    const padding = 50;

    const maxVal = Math.max(...data.map((r: any) => r.PredictedDemand || 0));
    const minVal = Math.min(...data.map((r: any) => r.PredictedDemand || 0));
    const range = maxVal - minVal || 1;

    return data.map((r: any, i: number) => {
        const x = padding + (i / (data.length - 1)) * width;
        const y = 250 - ((r.PredictedDemand - minVal) / range) * height;
        return `${i === 0 ? 'M' : 'L'} ${x} ${y}`;
    }).join(' ');
};

const generateActualPath = () => {
    if (predictionResults.value.length === 0) return "";

    const data = predictionResults.value.slice(0, 50);
    const width = 700;
    const height = 200;
    const padding = 50;

    const maxVal = Math.max(...data.map((r: any) => r.ActualDemand || r.PredictedDemand || 0));
    const minVal = Math.min(...data.map((r: any) => r.ActualDemand || r.PredictedDemand || 0));
    const range = maxVal - minVal || 1;

    return data.map((r: any, i: number) => {
        const x = padding + (i / (data.length - 1)) * width;
        const y = 250 - ((r.ActualDemand - minVal) / range) * height;
        return `${i === 0 ? 'M' : 'L'} ${x} ${y}`;
    }).join(' ');
};
</script>
<style scoped src=./aiModel.css></style>