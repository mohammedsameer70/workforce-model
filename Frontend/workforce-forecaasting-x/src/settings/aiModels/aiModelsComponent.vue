<template>
  <div class="ai-models-container">

    <!-- ================= HEADER ================= -->

    <div class="header">
      <h1>AI Model Management</h1>
      <p class="subtitle">
        {{ lbl.aiModelsDesc }}
      </p>
    </div>

    <!-- ================= KPI CARDS ================= -->

    <div class="cardsInfo">
      <div class="card" v-for="metric in dashboardMetrics" :key="metric.title">
        <div class="iconBox">
          <i :class="metric.icon"></i>
        </div>

        <h3>{{ metric.title }}</h3>
        <p>{{ metric.value }}</p>
      </div>
    </div>

    <!-- ================= TRAINING ================= -->

    <div class="training-section">

      <div class="section-header">

        <div class="header-content">

          <i class="pi pi-cog"></i>

          <div>

            <h2>
              Train AI Models
            </h2>

            <p>
              Upload a workforce dataset and train one or more AI forecasting
              algorithms.
            </p>

          </div>

        </div>

      </div>

      <div class="training-content">

        <!-- DATASET -->

        <div class="upload-section">

          <h3>
            Dataset
          </h3>

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

        <!-- ALGORITHMS -->

        <div class="algorithm-section">

          <h3>
            Algorithms
          </h3>

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

              <span>
                {{ algorithm }}
              </span>

            </label>

          </div>

        </div>

        <!-- PROGRESS -->

        <div
            v-if="isTraining"
            class="progress-section"
        >

          <p>
            Training AI models...
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

        <!-- ERROR -->

        <div
            v-if="trainingError"
            class="error-message"
        >

          <i class="pi pi-exclamation-circle"></i>

          <span>
            {{ trainingError }}
          </span>

        </div>

        <!-- ACTIONS -->

        <div class="actions">

          <button
              class="train-btn"
              @click="trainModel"
              :disabled="isTraining"
          >

            <i class="pi pi-play"></i>

            {{
              isTraining
                ? "Training..."
                : "Train Models"
            }}

          </button>

          <button
              v-if="isTraining"
              class="cancel-btn"
              @click="cancelTraining"
          >

            <i class="pi pi-times"></i>

            Cancel

          </button>

        </div>

      </div>

    </div>

    <!-- ================= MODEL COMPARISON ================= -->

    <div class="model-comparison">

      <div class="section-header">

        <h2>
          Model Comparison
        </h2>

        <p>
          Performance metrics of trained models.
        </p>

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

          <tr v-if="models.length === 0">

            <td
                colspan="7"
                class="empty-table"
            >

              No training results available.

            </td>

          </tr>

          <tr
              v-for="model in models"
              :key="model.name"
          >

            <td>

              <span
                  v-if="model.status === 'Best'"
                  class="best-badge"
              >

                <i class="pi pi-star-fill"></i>

              </span>

              {{ model.name }}

            </td>

            <td>
              {{ model.rmse }}
            </td>

            <td>
              {{ model.mae }}
            </td>

            <td>
              {{ model.mape }}%
            </td>

            <td>
              {{ model.r2 }}
            </td>

            <td>
              {{ model.trainingTime }}
            </td>

            <td>

              <span
                  class="status-badge"
                  :class="'status-' + model.status.toLowerCase()"
              >

                {{ model.status }}

              </span>

            </td>

          </tr>

          </tbody>

        </table>

      </div>

    </div>

    <!-- ================= TRAINING RUN COMPARISON ================= -->

    <div
        v-if="trainingHistory.length > 1"
        class="training-run-comparison"
    >

      <div class="section-header">

        <h2>
          Training Run Comparison
        </h2>

        <p>
          Compare performance across different dataset training runs.
        </p>

      </div>

      <div class="table-wrapper">

        <table class="comparison-table">

          <thead>

          <tr>

            <th>Run #</th>

            <th>Dataset</th>

            <th>Date</th>

            <th>Best Model</th>

            <th>RMSE</th>

            <th>R²</th>

            <th>Status</th>

            <th>Improvement</th>

          </tr>

          </thead>

          <tbody>

          <tr
              v-for="(record, index) in trainingHistory.slice().reverse()"
              :key="record.date"
          >

            <td>
              {{ trainingHistory.length - index }}
            </td>

            <td>
              {{ record.dataset }}
            </td>

            <td>
              {{ record.date }}
            </td>

            <td>
              <strong>
                {{ record.bestModel }}
              </strong>
            </td>

            <td>
              {{ record.rmse?.toFixed(4) || "-" }}
            </td>

            <td>
              {{ record.r2?.toFixed(4) || "-" }}
            </td>

            <td>

              <span
                  class="status-badge"
                  :class="'status-' + record.status"
              >
                {{ record.status }}
              </span>

            </td>

            <td>
              <span
                  v-if="index < trainingHistory.length - 1"
                  :class="getImprovementClass(record, trainingHistory[trainingHistory.length - 1 - index - 1])"
              >
                {{ getImprovementText(record, trainingHistory[trainingHistory.length - 1 - index - 1]) }}
              </span>
              <span v-else>
                -
              </span>
            </td>

          </tr>

          </tbody>

        </table>

      </div>

    </div>

    <!-- ================= CLEANED CSV COMPARISON ================= -->

    <div
        v-if="rawDatasetValues.length || cleanedDatasetValues.length"
        class="data-comparison-section"
    >

      <div class="section-header">

        <h2>
          Data Cleaning Comparison
        </h2>

        <p>
          Compare raw uploaded data versus cleaned dataset output.
        </p>

      </div>

      <div class="chart-container">

        <canvas
            id="dataComparisonChart"
            height="420"
        ></canvas>

      </div>

    </div>


    <div class="prediction-container">

      <!-- Prediction Upload -->

      <div class="prediction-section">

        <div class="section-header">

          <h2>
            Prediction
          </h2>

          <p>
            Upload a workforce dataset to generate AI predictions.
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
              v-if="isPredicting"
              class="progress-section"
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
              class="predict-btn"
              @click="predict"
              :disabled="!predictionFile || isPredicting"
          >

            <i class="pi pi-play"></i>

            {{
              isPredicting
                  ? "Predicting..."
                  : "Predict"
            }}

          </button>

        </div>

      </div>

      <!-- Prediction Summary -->

      <div class="prediction-summary">

        <div class="cardsInfo">

          <div class="card">
            <div class="iconBox">
              <i class="pi pi-cog"></i>
            </div>

            <h3>MODEL</h3>
            <p>{{ predictionSummary.model || "Awaiting prediction" }}</p>
          </div>

          <div class="card">
            <div class="iconBox">
              <i class="pi pi-database"></i>
            </div>

            <h3>RECORDS</h3>
            <p>{{ predictionSummary.totalRecords > 0 ? predictionSummary.totalRecords : "-" }}</p>
          </div>

          <div class="card">
            <div class="iconBox">
              <i class="pi pi-chart-line"></i>
            </div>

            <h3>AVERAGE</h3>
            <p>{{ predictionSummary?.averagePrediction && typeof predictionSummary.averagePrediction === 'number' ? predictionSummary.averagePrediction.toFixed(2) : "-" }}</p>
          </div>

          <div class="card">
            <div class="iconBox">
              <i class="pi pi-chart-bar"></i>
            </div>

            <h3>MAXIMUM</h3>
            <p>{{ predictionSummary?.maximumPrediction && typeof predictionSummary.maximumPrediction === 'number' ? predictionSummary.maximumPrediction.toFixed(2) : "-" }}</p>
          </div>

        </div>

      </div>

      <!-- Prediction Chart -->

      <div class="prediction-column">
        <div class="graph-section">

        <div class="section-header">

          <h2>
            Prediction Graph
          </h2>

          <p>
            Workforce Demand Prediction
          </p>

        </div>

        <div class="chart-container ">

          <canvas
              id="predictionChart"
              class="flex flex-col items-center justify-center"
              height="420"
          ></canvas>

          <div
              v-if="predictionTableRows.length === 0"
              class="empty-chart text-center "
          >

            <i class="pi pi-chart-line "></i>

            <p>
              No prediction data available.
            </p>

          </div>

        </div>

      </div>

      <!-- Prediction Results -->

      <div class="prediction-table">

        <div class="section-header">

          <h2>
            Prediction Results
          </h2>

        </div>

        <div class="table-wrapper">

          <table class="comparison-table">

            <thead>

            <tr>

              <th>#</th>

              <th>Date</th>

              <th>Department</th>

              <th>Team</th>

              <th>Shift</th>
            <th>Raw Demand</th>
            <th>Predicted Demand</th>
          </tr>
          </thead>
          <tbody>
          <tr
              v-if="predictionTableRows.length===0"
          >

              <td
                  colspan="7"
                  class="empty-table text-center"
              >

                No dataset rows available. Upload a CSV file or run prediction to load data.

              </td>

            </tr>

            <tr
                v-for="(prediction,index) in predictionTableRows.slice(0, 20)"
                :key="index"
            >

              <td>

                {{ index + 1 }}

              </td>

              <td>
                {{ getFieldValue(prediction.__raw ?? prediction, ["AttendanceDate", "attendance_date", "Date", "date"]) || "-" }}
              </td>
              <td>
                {{ getFieldValue(prediction.__raw ?? prediction, ["Department", "department"]) || "-" }}
              </td>
              <td>
                {{ getFieldValue(prediction.__raw ?? prediction, ["Team", "team"]) || "-" }}
              </td>
              <td>
                {{ getFieldValue(prediction.__raw ?? prediction, ["Shift", "shift"]) || "-" }}
              </td>
              <td>
                {{ getDisplayNumber(prediction.__raw ?? prediction, demandFieldCandidates) }}
              </td>
              <td>
                {{ getDisplayNumber(prediction, ["PredictedDemand", "predictedDemand", "predicted_demand"]) }}
              </td>

            </tr>

            </tbody>

          </table>

          <div
              v-if="predictionError"
              class="error-message"
          >

            <i class="pi pi-exclamation-circle"></i>

            <span>
              {{ predictionError }}
            </span>

          </div>

        </div>

      </div>

      </div>
    </div>
        <!-- ================= TRAINING HISTORY ================= -->

    <div class="training-history">

      <div class="section-header">

        <h2>
          Training History
        </h2>

        <p>
          Previous model training sessions.
        </p>

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
              v-if="trainingHistory.length===0"
          >

            <td
                colspan="8"
                class="empty-table"
            >

              No training history available.

            </td>

          </tr>

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
                    v-for="algo in record.algorithmsUsed"
                    :key="algo"
                    class="algo-tag"
                >

                  {{ algo }}

                </span>

              </div>

            </td>

            <td>

              <strong>

                {{ record.bestModel }}

              </strong>

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
                  class="action-btn danger"
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

import { ref, nextTick, computed, onMounted, watch } from "vue";
import { Chart, registerables } from "chart.js";
import FileUpload from "primevue/fileupload";
import type { FileUploadSelectEvent } from "primevue/fileupload";
import { useToast } from 'primevue/usetoast';

import { lbl } from "@/assets/constants/labels";
import AIModelService from "./aiModelService";
import CLDashboardService from "../dashboard/dashboardService";
import { aiModelReady, isTraining as globalIsTraining, isPredicting as globalIsPredicting } from '@/state/aiModelGate'

import type {
    TrainingResponseDTO,
    TrainingHistoryDTO,
    TrainingResultDTO
} from "./trainingDTO";

Chart.register(...registerables);

const toast = useToast();

/* ==========================================================
   KPI
========================================================== */

const trainingResult = ref<TrainingResultDTO | null>(null);

const trainingStatus = ref("Ready to train");

const dashboardMetrics = ref<{ title: string; value: string; icon: string }[]>([
  { title: "Best Model", value: "Awaiting training", icon: "pi pi-star" },
  { title: "R² Score", value: "Pending", icon: "pi pi-chart-line" },
  { title: "RMSE", value: "Pending", icon: "pi pi-chart-bar" },
  { title: "Status", value: "Ready to train", icon: "pi pi-check-circle" }
]);

const getMetricIcon = (title: string) => {
  if (/best/i.test(title)) return "pi pi-star";
  if (/accuracy|forecast|r²|r2/i.test(title)) return "pi pi-chart-line";
  if (/rmse|error|loss/i.test(title)) return "pi pi-chart-bar";
  if (/status|ready|complete|pending/i.test(title)) return "pi pi-check-circle";
  if (/alert|critical|uptime/i.test(title)) return "pi pi-exclamation-triangle";
  return "pi pi-chart-line";
};

const formatMetricValue = (value: unknown) => {
  if (value === null || value === undefined) return '-';
  if (Array.isArray(value)) {
    return value
      .map((item) => (typeof item === 'object' ? JSON.stringify(item) : String(item)))
      .join(', ');
  }
  if (typeof value === 'object') {
    return JSON.stringify(value);
  }
  return String(value);
};

const mapDashboardMetrics = (data: any) => {
  if (!data) {
    return [];
  }

  if (Array.isArray(data)) {
    return data.flatMap((item, index) => {
      if (item == null) {
        return [];
      }

      if (
        typeof item === 'string' ||
        typeof item === 'number' ||
        typeof item === 'boolean'
      ) {
        return [
          {
            title: String(item),
            value: String(item),
            icon: getMetricIcon(String(item)),
          },
        ]
      }

      if (typeof item === 'object') {
        if ('title' in item || 'value' in item || 'label' in item) {
          return [
            {
              title: String(item.title ?? item.label ?? item.name ?? `Metric ${index + 1}`),
              value: formatMetricValue((item as any).value),
              icon: (item as any).icon ?? getMetricIcon(String(item.title ?? item.label ?? item.name ?? 'Metric')),
            },
          ]
        }

        return Object.entries(item).map(([title, value]) => ({
          title,
          value: formatMetricValue(value),
          icon: getMetricIcon(title),
        }))
      }

      return [
        {
          title: `Metric ${index + 1}`,
          value: String(item),
          icon: getMetricIcon(String(item)),
        },
      ]
    })
  }

  if (typeof data === 'object') {
    return Object.entries(data).map(([title, value]) => ({
      title,
      value: formatMetricValue(value),
      icon: getMetricIcon(title),
    }))
  }

  return [
    {
      title: 'Metrics',
      value: String(data),
      icon: 'pi pi-chart-line',
    },
  ]
};

const setDashboardTrainingMetrics = (result: TrainingResponseDTO) => {
  dashboardMetrics.value = [
    {
      title: "Best Model",
      value: result.bestModel || "Awaiting training",
      icon: "pi pi-star"
    },
    {
      title: "R² Score",
      value:
        result.metrics?.R2 != null
          ? result.metrics.R2.toFixed(4)
          : "Pending",
      icon: "pi pi-chart-line"
    },
    {
      title: "RMSE",
      value:
        result.metrics?.RMSE != null
          ? result.metrics.RMSE.toFixed(4)
          : "Pending",
      icon: "pi pi-chart-bar"
    },
    {
      title: "Status",
      value: result.status || "Ready to train",
      icon: "pi pi-check-circle"
    }
  ];
};

const fetchDashboardMetrics = async () => {
  try {
    const response = await CLDashboardService.getDashboardData();
    console.log('AI Model dashboard response:', response);
    const dashboardResponse = response as unknown as { metrics?: unknown }

    if (dashboardResponse?.metrics) {
      dashboardMetrics.value = mapDashboardMetrics(dashboardResponse.metrics)
    } else {
      console.log('No metrics in AI Model dashboard response')
    }
  } catch (error) {
    console.error('Dashboard metrics endpoint not available - using local metrics:', error)
    // Dashboard endpoint not implemented yet, skip gracefully
  }
};

const loadFromDatabase = async () => {
  try {
    // Load latest prediction from database
    const latestPrediction = await AIModelService.getLatestPrediction();
    if (latestPrediction && latestPrediction.results) {
      predictionResults.value = latestPrediction.results;
      predictionMetadata.value = {
        model: latestPrediction.model,
        totalRecords: latestPrediction.total_records,
        averagePrediction: latestPrediction.average_prediction,
        maximumPrediction: latestPrediction.maximum_prediction,
        minimumPrediction: latestPrediction.minimum_prediction
      };
      // Also update predictionSummary for UI display
      predictionSummary.value = {
        model: latestPrediction.model || "Unknown",
        totalRecords: latestPrediction.total_records || 0,
        averagePrediction: latestPrediction.average_prediction || 0,
        maximumPrediction: latestPrediction.maximum_prediction || 0,
        minimumPrediction: latestPrediction.minimum_prediction || 0
      };
      console.log("Loaded prediction data from database");
    }

    // Load latest model from database
    const latestModel = await AIModelService.getLatestModel();
    if (latestModel) {
      trainingResult.value = {
        fileName: latestModel.name || "Unknown",
        bestModel: latestModel.algorithm || "Unknown",
        rmse: latestModel.rmse,
        r2: latestModel.rSquared,
        status: latestModel.status || "Ready",
        actions: ["view", "download", "delete"]
      };
      trainingStatus.value = latestModel.status || "Ready";

      // Update dashboard metrics with loaded data
      setDashboardTrainingMetrics({
        bestModel: latestModel.algorithm || "Unknown",
        metrics: {
          RMSE: latestModel.rmse,
          MAE: null,
          MAPE: null,
          R2: latestModel.rSquared
        },
        status: latestModel.status || "Ready"
      });

      // Check if this model is already in training history to avoid duplicates
      const modelExists = trainingHistory.value.some(
        h => h.dataset === latestModel.name && h.bestModel === latestModel.algorithm
      );

      if (!modelExists) {
        trainingHistory.value.unshift({
          date: latestModel.lastTrained ? new Date(latestModel.lastTrained).toLocaleString() : new Date().toLocaleString(),
          dataset: latestModel.name || "Unknown",
          algorithmsUsed: [latestModel.algorithm || "Unknown"],
          bestModel: latestModel.algorithm || "Unknown",
          rmse: latestModel.rmse,
          r2: latestModel.rSquared,
          status: (latestModel.status || "Ready").toLowerCase(),
          actions: ["view", "download", "delete"]
        });
      }
      console.log("Loaded model data from database");
    }

    // Load model comparisons from database
    const comparisons = await AIModelService.getModelComparisons();
    if (comparisons && Array.isArray(comparisons)) {
      models.value = comparisons.map((m: any) => ({
        name: m.modelName || m.algorithm,
        rmse: m.rmse,
        mae: m.mae,
        mape: m.mape,
        r2: m.rSquared,
        trainingTime: m.trainingTime ? `${m.trainingTime}ms` : "-",
        status: m.status || "Good"
      }));
      console.log("Loaded model comparisons from database");
    }
  } catch (error) {
    console.log("No previous data found in database:", error);
  }
};

onMounted(() => {
  loadFromDatabase();
  fetchDashboardMetrics();
});

/* ==========================================================
   DATASET
========================================================== */

const selectedFile = ref<File | null>(null);

const predictionFile = ref<File | null>(null);

const datasetName = ref("");

const predictionDatasetName = ref("");

/* ==========================================================
   ALGORITHMS
========================================================== */

const algorithms = [

    "Linear Regression",

    "Random Forest",

    "XGBoost",

    "LSTM"

];

const selectedAlgorithms = ref<string[]>([]);

/* ==========================================================
   TRAINING
========================================================== */

const isTraining = computed({
  get: () => globalIsTraining.value,
  set: (value) => { globalIsTraining.value = value }
});

const trainingProgress = ref(0);

const trainingError = ref("");

const trainingAbortController = ref<AbortController | null>(null);

/* ==========================================================
   PREDICTION
========================================================== */

const isPredicting = computed({
  get: () => globalIsPredicting.value,
  set: (value) => { globalIsPredicting.value = value }
});

const predictionProgress = ref(0);

const predictionError = ref("");

const predictionRawRows = ref<any[]>([]);

const predictionShowColumns = [
    "AttendanceDate",
    "Department",
    "Team",
    "Shift",
    "ActualDemand",
    "PredictedDemand",
    "Actual Demand",
    "Predicted Demand"
];

const demandFieldCandidates = [
    "WorkforceDemand",
    "Workforce Demand",
    "workforceDemand",
    "workforce demand",
    "HistoricalDemand",
    "Historical Demand",
    "historicalDemand",
    "historical demand",
    "TargetDemand",
    "Target Demand",
    "targetDemand",
    "target demand",
    "ActualDemand",
    "Actual Demand",
    "actualDemand",
    "actual demand",
    "Demand",
    "demand"
];

const predictedFieldCandidates = [
    "PredictedDemand",
    "Predicted Demand",
    "predictedDemand",
    "predicted demand",
    "predicted_demand",
    "predicted-demand"
];

/* ==========================================================
   MODEL COMPARISON
========================================================== */

const models = ref<any[]>([]);

/* ==========================================================
   PREDICTION SUMMARY
========================================================== */

const predictionSummary = ref({

    model: "",

    totalRecords: 0,

    averagePrediction: 0,

    maximumPrediction: 0,

    minimumPrediction: 0

});

const predictionMetadata = ref({

    model: "",

    totalRecords: 0,

    averagePrediction: 0,

    maximumPrediction: 0,

    minimumPrediction: 0

});

/* ==========================================================
   PREDICTION RESULTS
========================================================== */

const predictionResults = ref<any[]>([]);

// Watch for prediction results to change and redraw chart
watch(() => predictionResults.value, async (newResults) => {
  if (newResults.length > 0) {
    await nextTick();
    await drawPredictionChart();
  }
});

const predictionTableRows = computed(() => {
    if (predictionRawRows.value.length && predictionResults.value.length) {
        return predictionRawRows.value.map((rawRow, index) => {
            const predictionRow = predictionResults.value[index] ?? {};
            return {
                ...rawRow,
                ...predictionRow,
                __raw: rawRow,
                __prediction: predictionRow
            };
        });
    }

    if (predictionResults.value.length) {
        return predictionResults.value;
    }

    return predictionRawRows.value;
});

/* ==========================================================
   TRAINING HISTORY
========================================================== */

const trainingHistory = ref<TrainingHistoryDTO[]>([]);

/* ==========================================================
   CHART
========================================================== */

let predictionChart: Chart | null = null;
let dataComparisonChart: Chart | null = null;

const rawDatasetValues = ref<number[]>([]);
const cleanedDatasetValues = ref<number[]>([]);
const dataComparisonLabels = ref<string[]>([]);

/* ==========================================================
   FILE SELECTION
========================================================== */

const onTrainingFileSelect = (
    event: FileUploadSelectEvent
) => {

    const file = event.files[0];

    if (!file) return;

    selectedFile.value = file;
    datasetName.value = file.name;

    // Disable other app tabs until retraining completes
    aiModelReady.value = false;
};

const parseCsvRows = async (file: File): Promise<any[]> => {
    const text = await file.text();
    const rows = text.replace(/\r/g, "").split("\n").filter(row => row.trim().length > 0);
    if (rows.length === 0) {
        return [];
    }

    const headerLine = rows[0]!;
    const headers = headerLine.split(",").map(header => header.trim());
    return rows.slice(1).map(row => {
        const cells = row.split(",").map(cell => cell.trim());
        const item: Record<string, string> = {};
        headers.forEach((header, index) => {
            item[header] = cells[index] ?? "";
        });
        return item;
    });
};

const onPredictionFileSelect = async (
    event: FileUploadSelectEvent
) => {

    const file = event.files[0];

    if (!file) return;

    predictionFile.value = file;

    predictionDatasetName.value = file.name;

    predictionRawRows.value = await parseCsvRows(file);

};

const getFieldValue = (item: any, keys: string[]) => {
    for (const key of keys) {
        if (item && item[key] !== undefined && item[key] !== null && item[key] !== "") {
            return item[key];
        }
    }
    return undefined;
};

const getDisplayNumber = (item: any, keys: string[]) => {
    const value = getFieldValue(item, keys);
    const numberValue = Number(value);
    return Number.isFinite(numberValue) ? numberValue.toFixed(2) : "-";
};

const formatPredictionValue = (value: any) => {
    if (value === null || value === undefined || value === 0) {
        return "-";
    }
    const numberValue = Number(value);
    return Number.isFinite(numberValue) && numberValue > 0 ? numberValue.toFixed(2) : "-";
};

const getComparisonKey = (item: any) => {
    const candidateKeys = [
        "ActualDemand",
        "Actual Demand",
        "WorkforceDemand",
        "Workforce Demand",
        "Demand",
        "demand",
        "PredictedDemand",
        "Predicted Demand"
    ];
    for (const key of candidateKeys) {
        if (item && item[key] !== undefined && item[key] !== null && item[key] !== "") {
            return key;
        }
    }
    return Object.keys(item).find(key => {
        const value = Number(item[key]);
        return key !== "PredictedDemand" && Number.isFinite(value);
    });
};

const drawPredictionChart = async () => {
    await nextTick();

    const canvas = document.getElementById(
        "predictionChart"
    ) as HTMLCanvasElement | null;

    if (!canvas) {
        return;
    }

    if (predictionChart) {
        predictionChart.destroy();
    }

    const chartRows = predictionTableRows.value.slice(0, 20);
    const chartLabels = chartRows.map((item, index) => {
        const row = item.__raw ?? item;
        return getFieldValue(row, ["AttendanceDate", "attendance_date", "Date", "date"]) || `Record ${index + 1}`;
    });

    const predictedData = chartRows.map(item => {
        const value = getFieldValue(item, predictedFieldCandidates);
        const normalized = Number(value);
        return Number.isFinite(normalized) ? normalized : null;
    });

    const actualData = chartRows.map(item => {
        const raw = item.__raw ?? item;
        const value = getFieldValue(raw, demandFieldCandidates);
        const normalized = Number(value);
        return Number.isFinite(normalized) ? normalized : null;
    });

    if (predictedData.every(value => value === null) && actualData.every(value => value === null)) {
        return;
    }

    const datasets = [];

    if (actualData.some(value => value !== null)) {
        datasets.push({
            label: "Raw Demand",
            data: actualData,
            borderColor: "#10b981",
            backgroundColor: (context: any) => {
                const ctx = context.chart.ctx;
                const gradient = ctx.createLinearGradient(0, 0, 0, 400);
                gradient.addColorStop(0, 'rgba(16,185,129,0.4)');
                gradient.addColorStop(1, 'rgba(16,185,129,0.05)');
                return gradient;
            },
            borderWidth: 3,
            tension: 0.4,
            fill: true,
            pointRadius: 4,
            pointBackgroundColor: "#10b981",
            pointBorderColor: "#ffffff",
            pointBorderWidth: 2,
            pointHoverRadius: 6
        });
    }

    if (predictedData.some(value => value !== null)) {
        datasets.push({
            label: "Predicted Demand",
            data: predictedData,
            borderColor: "#3b82f6",
            backgroundColor: (context: any) => {
                const ctx = context.chart.ctx;
                const gradient = ctx.createLinearGradient(0, 0, 0, 400);
                gradient.addColorStop(0, 'rgba(59,130,246,0.4)');
                gradient.addColorStop(1, 'rgba(59,130,246,0.05)');
                return gradient;
            },
            borderWidth: 3,
            tension: 0.4,
            fill: true,
            pointRadius: 4,
            pointBackgroundColor: "#3b82f6",
            pointBorderColor: "#ffffff",
            pointBorderWidth: 2,
            pointHoverRadius: 6
        });
    }

    predictionChart = new Chart(canvas, {
        type: "line",
        data: {
            labels: chartLabels,
            datasets
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                mode: 'index',
                intersect: false
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                    labels: {
                        usePointStyle: true,
                        padding: 20,
                        font: {
                            size: 12,
                            weight: '600'
                        }
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    padding: 12,
                    titleFont: {
                        size: 14,
                        weight: 'bold'
                    },
                    bodyFont: {
                        size: 13
                    },
                    cornerRadius: 8,
                    displayColors: true
                }
            },
            scales: {
                x: {
                    title: {
                        display: true,
                        text: "Attendance Date",
                        font: {
                            size: 13,
                            weight: '600'
                        },
                        color: '#6b7280'
                    },
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    },
                    ticks: {
                        font: {
                            size: 11
                        },
                        color: '#6b7280'
                    }
                },
                y: {
                    beginAtZero: false,
                    title: {
                        display: true,
                        text: "Predicted Demand",
                        font: {
                            size: 13,
                            weight: '600'
                        },
                        color: '#6b7280'
                    },
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    },
                    ticks: {
                        font: {
                            size: 11
                        },
                        color: '#6b7280'
                    }
                }
            }
        }
    });

};

const extractDemandValuesFromCsv = (text: string): number[] => {
    const rows = text.replace(/\r/g, "").split("\n").filter(row => row.trim().length > 0);
    if (rows.length === 0) {
        return [];
    }

    const headerLine = rows[0]!;
    const headers = headerLine.split(",").map(header => header.trim());
    const demandColumnIndex = headers.findIndex(header =>
        demandFieldCandidates.includes(header)
    );

    return rows.slice(1).map(row => {
        const cells = row.split(",").map(cell => cell.trim());
        if (demandColumnIndex >= 0 && demandColumnIndex < cells.length) {
            const value = Number(cells[demandColumnIndex]);
            if (Number.isFinite(value)) {
                return value;
            }
        }

        const fallbackValue = cells.map(cell => Number(cell.trim())).find(val => Number.isFinite(val));
        return fallbackValue ?? NaN;
    }).filter((value): value is number => Number.isFinite(value));
};

const readCsvFile = async (file: File): Promise<number[]> => {
    const text = await file.text();
    return extractDemandValuesFromCsv(text);
};

const readCsvBlob = async (blob: Blob): Promise<number[]> => {
    const text = await blob.text();
    return extractDemandValuesFromCsv(text);
};

const drawDataComparisonChart = async () => {
    await nextTick();

    const canvas = document.getElementById(
        "dataComparisonChart"
    ) as HTMLCanvasElement | null;

    if (!canvas) {
        return;
    }

    if (dataComparisonChart) {
        dataComparisonChart.destroy();
    }

    dataComparisonLabels.value = Array.from(
        { length: Math.max(rawDatasetValues.value.length, cleanedDatasetValues.value.length) },
        (_, index) => `Record ${index + 1}`
    );

    dataComparisonChart = new Chart(canvas, {
        type: "line",
        data: {
            labels: dataComparisonLabels.value,
            datasets: [
                {
                    label: "Raw Uploaded CSV",
                    data: rawDatasetValues.value,
                    borderColor: "#6366f1",
                    backgroundColor: (context: any) => {
                        const ctx = context.chart.ctx;
                        const gradient = ctx.createLinearGradient(0, 0, 0, 400);
                        gradient.addColorStop(0, 'rgba(99,102,241,0.4)');
                        gradient.addColorStop(1, 'rgba(99,102,241,0.05)');
                        return gradient;
                    },
                    borderWidth: 2,
                    tension: 0.4,
                    fill: true,
                    pointRadius: 3,
                    pointBackgroundColor: "#6366f1",
                    pointBorderColor: "#ffffff",
                    pointBorderWidth: 2,
                    pointHoverRadius: 5
                },
                {
                    label: "Cleaned CSV",
                    data: cleanedDatasetValues.value,
                    borderColor: "#10b981",
                    backgroundColor: (context: any) => {
                        const ctx = context.chart.ctx;
                        const gradient = ctx.createLinearGradient(0, 0, 0, 400);
                        gradient.addColorStop(0, 'rgba(16,185,129,0.4)');
                        gradient.addColorStop(1, 'rgba(16,185,129,0.05)');
                        return gradient;
                    },
                    borderWidth: 2,
                    tension: 0.4,
                    fill: true,
                    pointRadius: 3,
                    pointBackgroundColor: "#10b981",
                    pointBorderColor: "#ffffff",
                    pointBorderWidth: 2,
                    pointHoverRadius: 5
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                mode: 'index',
                intersect: false
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                    labels: {
                        usePointStyle: true,
                        padding: 20,
                        font: {
                            size: 12,
                            weight: '600'
                        }
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    padding: 12,
                    titleFont: {
                        size: 14,
                        weight: 'bold'
                    },
                    bodyFont: {
                        size: 13
                    },
                    cornerRadius: 8,
                    displayColors: true
                }
            },
            scales: {
                x: {
                    title: {
                        display: true,
                        text: "Record",
                        font: {
                            size: 13,
                            weight: '600'
                        },
                        color: '#6b7280'
                    },
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    },
                    ticks: {
                        font: {
                            size: 11
                        },
                        color: '#6b7280'
                    }
                },
                y: {
                    beginAtZero: false,
                    title: {
                        display: true,
                        text: "Value",
                        font: {
                            size: 13,
                            weight: '600'
                        },
                        color: '#6b7280'
                    },
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    },
                    ticks: {
                        font: {
                            size: 11
                        },
                        color: '#6b7280'
                    }
                }
            }
        }
    });
};

const resetDataComparison = () => {
    rawDatasetValues.value = [];
    cleanedDatasetValues.value = [];
    dataComparisonLabels.value = [];

    if (dataComparisonChart) {
        dataComparisonChart.destroy();
        dataComparisonChart = null;
    }
};

/* ==========================================================
   RESET PREDICTION
========================================================== */

const resetPrediction = () => {

    predictionSummary.value = {

        model: "",

        totalRecords: 0,

        averagePrediction: 0,

        maximumPrediction: 0,

        minimumPrediction: 0

    };

    predictionResults.value = [];

    predictionDatasetName.value = "";

    predictionFile.value = null;

    if (predictionChart) {

        predictionChart.destroy();

        predictionChart = null;

    }

};
/* ==========================================================
   TRAIN MODEL
========================================================== */

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
    aiModelReady.value = false;

    trainingProgress.value = 0;

    trainingAbortController.value = new AbortController();

    const formData = new FormData();

    formData.append(
        "file",
        selectedFile.value
    );

    formData.append(
        "algorithms",
        selectedAlgorithms.value.join(",")
    );

    const timer = setInterval(() => {

        if (trainingProgress.value < 95) {

            trainingProgress.value += 5;

        }

    }, 250);

    try {

        const response = await AIModelService.train(
            formData,
            trainingAbortController.value?.signal
        );

        clearInterval(timer);

        trainingProgress.value = 100;

        await new Promise(resolve => setTimeout(resolve, 500));

        /* ==========================================
           Download cleaned dataset
        ========================================== */

        const cleanedDataset =
            await AIModelService.downloadCleanedDataset();

        const rawValues = await readCsvFile(selectedFile.value);
        const cleanedValues = await readCsvBlob(cleanedDataset);
        rawDatasetValues.value = rawValues;
        cleanedDatasetValues.value = cleanedValues;

        await drawDataComparisonChart();

        const url =
            window.URL.createObjectURL(cleanedDataset);

        const link =
            document.createElement("a");

        link.href = url;

        link.download = "cleaned_dataset.csv";

        document.body.appendChild(link);

        link.click();

        document.body.removeChild(link);

        window.URL.revokeObjectURL(url);

        /* ==========================================
           KPI
        ========================================== */

        trainingStatus.value = response.status;

        trainingResult.value = {

            fileName: selectedFile.value.name,

            bestModel: response.bestModel,

            metrics: response.metrics,

            rmse: response.metrics.RMSE,

            r2: response.metrics.R2,

            status: response.status,

            actions: [

                "view",

                "download",

                "delete"

            ]

        };

        setDashboardTrainingMetrics(response);

        await fetchDashboardMetrics();
        aiModelReady.value = true;

        toast.add({
            severity: 'success',
            summary: 'Training Successful',
            detail: `Model trained successfully with ${response.bestModel} (RMSE: ${response.metrics.RMSE?.toFixed(4)}, R²: ${response.metrics.R2?.toFixed(4)})`,
            life: 5000
        });

        /* ==========================================
           Model Comparison
        ========================================== */

        models.value = [];

        if (response.comparison) {

            response.comparison.forEach((model: any) => {

                models.value.push({

                    name: model.Model,

                    rmse: model.RMSE,

                    mae: model.MAE,

                    mape: model.MAPE,

                    r2: model.R2,

                    trainingTime: "-",

                    status:

                        model.Model === response.bestModel

                            ? "Best"

                            : "Good"

                });

            });

        }

        /* ==========================================
           History
        ========================================== */

        trainingHistory.value.unshift({

            date: new Date().toLocaleString(),

            dataset: selectedFile.value.name,

            algorithmsUsed: [...selectedAlgorithms.value],

            bestModel: response.bestModel,

            rmse: response.metrics.RMSE,

            r2: response.metrics.R2,

            status: response.status.toLowerCase(),

            actions: [

                "view",

                "download",

                "delete"

            ]

        });

        /* ==========================================
           Reset
        ========================================== */

        datasetName.value = "";

        selectedFile.value = null;

        selectedAlgorithms.value = [];

    }

    catch (error: any) {

        clearInterval(timer);

        console.error(error);

        if (
            error?.code === "ERR_CANCELED" ||
            error?.name === "CanceledError" ||
            error?.message?.includes("canceled")
        ) {
            trainingStatus.value = "Cancelled";
            trainingError.value = "Training was cancelled.";
        }
        else {
            trainingStatus.value = "Failed";
            trainingError.value =
                error?.response?.data?.detail ||
                error?.response?.data?.message ||
                error?.message ||
                "Training failed.";
        }

    }

    finally {

        isTraining.value = false;

        trainingProgress.value = 0;

        trainingAbortController.value = null;

    }

};
/* ==========================================================
   PREDICT
========================================================== */

const predict = async () => {

    if (!predictionFile.value) {

        alert("Please choose a prediction dataset.");

        return;

    }

    isPredicting.value = true;

    predictionProgress.value = 0;

    const formData = new FormData();

    formData.append(
        "file",
        predictionFile.value
    );

    const timer = setInterval(() => {

        if (predictionProgress.value < 95) {

            predictionProgress.value += 5;

        }

    }, 250);

    predictionError.value = "";

    predictionRawRows.value = predictionFile.value ? await parseCsvRows(predictionFile.value) : [];

    try {

        const response =
            await AIModelService.predict(formData);

        clearInterval(timer);

        predictionProgress.value = 100;

        /* ==========================================
           Summary Cards
        ========================================== */

        predictionSummary.value = {

            model: response.model || "Unknown",

            totalRecords: response.total_records || 0,

            averagePrediction: response.average_prediction || 0,

            maximumPrediction: response.maximum_prediction || 0,

            minimumPrediction: response.minimum_prediction || 0

        };

        /* ==========================================
           Prediction Table
        ========================================== */

        predictionResults.value = [];

        if (response.results) {
            predictionResults.value = [...response.results];
        }
        else if (response.predictions) {
            predictionResults.value = response.predictions.map((value: number) => ({
                PredictedDemand: value
            }));
        }

        /* ==========================================
           Draw Chart
        ========================================== */

        await drawPredictionChart();

        await fetchDashboardMetrics();
        aiModelReady.value = true;

        toast.add({
            severity: 'success',
            summary: 'Prediction Successful',
            detail: `Prediction completed for ${response.total_records} records using ${response.model}`,
            life: 5000
        });

        /* ==========================================
           Reset Upload
        ========================================== */

        predictionDatasetName.value = "";

        predictionFile.value = null;

        await new Promise(resolve =>
            setTimeout(resolve, 500)
        );

    }

    catch (error: any) {

        clearInterval(timer);

        console.error(error);

        predictionError.value =
            error?.response?.data?.detail ||
            error?.message ||
            "Prediction failed.";

    }

    finally {

        isPredicting.value = false;

        predictionProgress.value = 0;

    }

};
/* ==========================================================
   CANCEL TRAINING
========================================================== */

const cancelTraining = () => {

    trainingAbortController.value?.abort();

    isTraining.value = false;

    trainingProgress.value = 0;

    trainingStatus.value = "Cancelled";

    trainingError.value = "Training was cancelled.";

    trainingAbortController.value = null;

};

/* ==========================================================
   TRAINING RUN COMPARISON HELPERS
========================================================== */

const getImprovementClass = (current: TrainingHistoryDTO, previous: TrainingHistoryDTO) => {
  if (!current.rmse || !previous.rmse) return '';
  
  const rmseImprovement = previous.rmse - current.rmse;
  const r2Improvement = (current.r2 || 0) - (previous.r2 || 0);
  
  if (rmseImprovement > 0 && r2Improvement > 0) {
    return 'improvement-positive';
  } else if (rmseImprovement < 0 && r2Improvement < 0) {
    return 'improvement-negative';
  } else {
    return 'improvement-neutral';
  }
};

const getImprovementText = (current: TrainingHistoryDTO, previous: TrainingHistoryDTO) => {
  if (!current.rmse || !previous.rmse) return '-';
  
  const rmseImprovement = previous.rmse - current.rmse;
  const r2Improvement = (current.r2 || 0) - (previous.r2 || 0);
  
  if (rmseImprovement > 0 && r2Improvement > 0) {
    return `↓ RMSE: ${rmseImprovement.toFixed(4)} | ↑ R²: ${r2Improvement.toFixed(4)}`;
  } else if (rmseImprovement < 0 && r2Improvement < 0) {
    return `↑ RMSE: ${Math.abs(rmseImprovement).toFixed(4)} | ↓ R²: ${Math.abs(r2Improvement).toFixed(4)}`;
  } else {
    return 'Mixed';
  }
};

/* ==========================================================
   HISTORY ACTIONS
========================================================== */

const viewRecord = (date: string) => {

    const record = trainingHistory.value.find(
        item => item.date === date
    );

    if (!record) {

        return;

    }

    alert(

        `Training Details\n\n` +

        `Date : ${record.date}\n` +

        `Dataset : ${record.dataset}\n` +

        `Best Model : ${record.bestModel}\n` +

        `RMSE : ${record.rmse}\n` +

        `R² : ${record.r2}`

    );

};

/* ==========================================================
   DOWNLOAD RECORD
========================================================== */

const downloadRecord = (date: string) => {

    const record = trainingHistory.value.find(
        item => item.date === date
    );

    if (!record) {

        return;

    }

    const json = JSON.stringify(
        record,
        null,
        4
    );

    const blob = new Blob(
        [json],
        {
            type: "application/json"
        }
    );

    const url =
        URL.createObjectURL(blob);

    const link =
        document.createElement("a");

    link.href = url;

    link.download =
        `training_${record.bestModel}_${Date.now()}.json`;

    document.body.appendChild(link);

    link.click();

    document.body.removeChild(link);

    URL.revokeObjectURL(url);

};

/* ==========================================================
   DELETE RECORD
========================================================== */

const deleteRecord = (date: string) => {

    trainingHistory.value =
        trainingHistory.value.filter(

            record => record.date !== date

        );

};

/* ==========================================================
   RESET TRAINING
========================================================== */

const resetTraining = () => {

    trainingResult.value = null;

    trainingStatus.value = "Ready";

    trainingError.value = "";

    models.value = [];

    datasetName.value = "";

    selectedAlgorithms.value = [];

    selectedFile.value = null;

    resetDataComparison();

};

/* ==========================================================
   RESET EVERYTHING
========================================================== */

const resetDashboard = () => {

    resetTraining();

    resetPrediction();

};

/* ==========================================================
   END SCRIPT
========================================================== */

</script>

<style scoped src="./aiModel.css"></style>