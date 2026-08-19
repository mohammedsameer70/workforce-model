<template>
  <div class="settingsContainer">
    <div class="pageHeader">
      <h1>System Settings</h1>
      <p>Configure platform preferences and integrations</p>
    </div>

    <!-- Profile -->
    <div v-if="loading" class="page-loading-overlay">
      <div class="page-loading-panel">
        <div class="page-loading-spinner"></div>
        <div>Loading settings...</div>
      </div>
    </div>

    <div class="settingsCard">
      <div class="cardHeader">
        <div class="iconBox">
          <i class="pi pi-user"></i>
        </div>

        <div>
          <h3>Profile Settings</h3>
          <p>Manage your account details</p>
        </div>
      </div>

      <div class="cardBody">
        <div class="formGrid">
          <div class="field">
            <label>Full Name</label>
            <InputText v-model="profile.fullName" fluid />
          </div>

          <div class="field">
            <label>Email</label>
            <InputText v-model="profile.email" fluid />
          </div>

          <div class="field">
            <label>Role</label>
            <Dropdown v-model="profile.role" :options="roles" fluid />
          </div>

          <div class="field">
            <label>Department</label>
            <Dropdown v-model="profile.department" :options="departments" fluid />
          </div>
        </div>
      </div>
    </div>

    <!-- AI Model Configuration -->
    <div class="settingsCard">
      <div class="cardHeader">
        <div class="iconBox">
          <i class="pi pi-brain"></i>
        </div>

        <div>
          <h3>AI Model Configuration</h3>
          <p>Machine learning model settings and parameters</p>
        </div>
      </div>

      <div class="cardBody">
        <div class="formGrid">
          <div class="field">
            <label>Active Model</label>
            <Dropdown
              v-model="aiModel.activeModel"
              :options="aiModelOptions"
              fluid
              :disabled="isAiModelDisabled"
            />
          </div>

          <div class="field">
            <label>Model Version</label>
            <InputText v-model="aiModel.version" fluid disabled />
          </div>

          <div class="field">
            <label>Training Frequency</label>
            <Dropdown
              v-model="aiModel.trainingFrequency"
              :options="trainingOptions"
              fluid
              :disabled="isAiModelDisabled"
            />
          </div>

          <div class="field">
            <label>Confidence Threshold</label>
            <InputNumber
              v-model="aiModel.confidenceThreshold"
              :min="0"
              :max="100"
              suffix="%"
              fluid
              :disabled="isAiModelDisabled"
            />
          </div>
        </div>

        <div class="settingRow">
          <div>
            <h4>Auto-Retrain</h4>
            <span>Automatically retrain model with new data</span>
          </div>

          <InputSwitch v-model="aiModel.autoRetrain" :disabled="isAiModelDisabled" />
        </div>

        <div class="settingRow">
          <div>
            <h4>Model Monitoring</h4>
            <span>Track model performance metrics</span>
          </div>

          <InputSwitch v-model="aiModel.monitoring" :disabled="isAiModelDisabled" />
        </div>

        <div class="settingRow">
          <div>
            <h4>Feature Importance</h4>
            <span>Display feature importance in predictions</span>
          </div>

          <InputSwitch v-model="aiModel.featureImportance" :disabled="isAiModelDisabled" />
        </div>
      </div>
    </div>

    <!-- Appearance -->
    <div class="settingsCard">
      <div class="cardHeader">
        <div class="iconBox">
          <i class="pi pi-palette"></i>
        </div>

        <div>
          <h3>Appearance</h3>
          <p>Customize display preferences</p>
        </div>
      </div>

      <div class="cardBody">
        <!-- Dark Mode removed; app uses single light theme -->

        <div class="settingRow">
          <div>
            <h4>Compact View</h4>
            <span>Reduce spacing for information density</span>
          </div>

          <InputSwitch v-model="appearance.compactView" />
        </div>

        <div class="settingRow">
          <div>
            <h4>Animations</h4>
            <span>Enable smooth transitions and effects</span>
          </div>

          <InputSwitch v-model="appearance.animations" />
        </div>
      </div>
    </div>

    <!-- Notifications -->
    <div class="settingsCard">
      <div class="cardHeader">
        <div class="iconBox">
          <i class="pi pi-bell"></i>
        </div>

        <div>
          <h3>Notifications</h3>
          <p>Configure alert preferences</p>
        </div>
      </div>

      <div class="cardBody">
        <div class="settingRow">
          <div>
            <h4>Critical Alerts</h4>
            <span>Staffing shortages & failures</span>
          </div>

          <InputSwitch v-model="notifications.critical" />
        </div>

        <div class="settingRow">
          <div>
            <h4>Shift Recommendations</h4>
            <span>AI optimization suggestions</span>
          </div>

          <InputSwitch v-model="notifications.shift" />
        </div>

        <div class="settingRow">
          <div>
            <h4>System Monitoring</h4>
            <span>CPU & memory threshold alerts</span>
          </div>

          <InputSwitch v-model="notifications.monitoring" />
        </div>

        <div class="settingRow">
          <div>
            <h4>Email Digest</h4>
            <span>Daily summary emails</span>
          </div>

          <InputSwitch v-model="notifications.email" />
        </div>
      </div>
    </div>

    <!-- System Configuration -->
    <div class="settingsCard">
      <div class="cardHeader">
        <div class="iconBox">
          <i class="pi pi-server"></i>
        </div>

        <div>
          <h3>System Configuration</h3>
          <p>Platform and infrastructure settings</p>
        </div>
      </div>

      <div class="cardBody">
        <div class="formGrid">
          <div class="field">
            <label>Refresh Interval</label>
            <Dropdown v-model="config.refresh" :options="refreshOptions" fluid />
          </div>

          <div class="field">
            <label>API Gateway URL</label>
            <InputText v-model="config.api" fluid />
          </div>

          <div class="field">
            <label>ML Service URL</label>
            <InputText v-model="config.ml" fluid />
          </div>

          <div class="field">
            <label>Data Retention (days)</label>
            <InputNumber v-model="config.dataRetention" :min="7" :max="365" fluid />
          </div>
        </div>
      </div>
    </div>

    <div class="footerActions">
      <Button label="Cancel" outlined @click="cancelChanges" />

      <Button label="Save Changes" icon="pi pi-check" @click="saveChanges" :loading="loading" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'

import Card from 'primevue/card'
import Button from 'primevue/button'
import Dropdown from 'primevue/dropdown'
import InputText from 'primevue/inputtext'
import InputSwitch from 'primevue/inputswitch'
import InputNumber from 'primevue/inputnumber'
import SettingsService from './settingsService'
import type { SettingsDTO } from './settingsService'
import { aiModelReady, isTraining, isPredicting } from '@/state/aiModelGate'

const roles = ['Super Admin', 'Operations Admin', 'Manager']
const departments = ['All Departments', 'Inbound', 'Outbound', 'Packing', 'Sortation']
const models = ['LSTM v3.2 (Active)', 'XGBoost v2.1', 'Prophet v1.0']
const refreshOptions = ['30 seconds', '1 minute', '5 minutes']
const aiModelOptions = [
  'LSTM v3.2',
  'XGBoost v2.1',
  'Prophet v1.0',
  'Random Forest v1.5',
  'Transformer v2.0',
]
const trainingOptions = ['Daily', 'Weekly', 'Monthly', 'Manual']

const profile = ref({
  fullName: '',
  email: '',
  role: '',
  department: '',
})

const appearance = ref({
  darkMode: false,
  compactView: false,
  animations: false,
})

const notifications = ref({
  critical: false,
  shift: false,
  monitoring: false,
  email: false,
})

const config = ref({
  model: '',
  refresh: '',
  api: '',
  ml: '',
  dataRetention: 90,
})

const aiModel = ref({
  activeModel: 'LSTM v3.2',
  version: '3.2.1',
  trainingFrequency: 'Weekly',
  confidenceThreshold: 75,
  autoRetrain: false,
  monitoring: true,
  featureImportance: true,
})

const loading = ref(false)
const message = ref('')

const isAiModelDisabled = computed(() => false)
const loadSettings = async () => {
  loading.value = true
  message.value = ''

  try {
    const data = await SettingsService.getSettings()
    profile.value = data.profile
    appearance.value = data.appearance
    notifications.value = {
      critical: data.notifications.criticalAlerts,
      shift: data.notifications.shiftRecommendations,
      monitoring: data.notifications.systemMonitoring,
      email: data.notifications.emailDigest,
    }
    config.value = {
      model: data.config.model,
      refresh: data.config.refresh,
      api: data.config.apiUrl,
      ml: data.config.mlUrl,
      dataRetention: data.config.dataRetention || 90,
    }
  } catch (err) {
    console.error('Failed to load settings', err)
    message.value = 'Unable to load settings.'
  } finally {
    loading.value = false
  }
}

const saveChanges = async () => {
  loading.value = true
  message.value = ''

  try {
    const payload: SettingsDTO = {
      profile: profile.value,
      appearance: appearance.value,
      notifications: {
        criticalAlerts: notifications.value.critical,
        shiftRecommendations: notifications.value.shift,
        systemMonitoring: notifications.value.monitoring,
        emailDigest: notifications.value.email,
      },
      config: {
        model: config.value.model,
        refresh: config.value.refresh,
        apiUrl: config.value.api,
        mlUrl: config.value.ml,
        dataRetention: config.value.dataRetention,
      },
    }
    await SettingsService.saveSettings(payload)
    message.value = 'Settings saved successfully.'
  } catch (err) {
    console.error('Failed to save settings', err)
    message.value = 'Unable to save settings.'
  } finally {
    loading.value = false
  }
}

const cancelChanges = () => {
  loadSettings()
}

/* ==========================================================
   PERFORMANCE COMPARISON CHART
========================================================== */

const loadModelComparisons = async () => {
  try {
    const comparisons = await AIModelService.getModelComparisons();
    if (comparisons && Array.isArray(comparisons) && comparisons.length > 0) {
      await nextTick();
      renderPerformanceChart(comparisons);
    }
  } catch (error) {
    console.error('Failed to load model comparisons:', error);
  }
};

const renderPerformanceChart = (comparisons: any[]) => {
  const canvas = document.getElementById('performanceChart') as HTMLCanvasElement;
  if (!canvas) return;

  const modelNames = comparisons.map((m: any) => m.modelName || m.algorithm);
  
  new ChartJS(canvas, {
    type: 'bar',
    data: {
      labels: modelNames,
      datasets: [
        {
          label: 'RMSE',
          data: comparisons.map((m: any) => m.rmse),
          backgroundColor: '#3498db',
          borderColor: '#3498db',
          borderWidth: 1
        },
        {
          label: 'MAE',
          data: comparisons.map((m: any) => m.mae),
          backgroundColor: '#2ecc71',
          borderColor: '#2ecc71',
          borderWidth: 1
        },
        {
          label: 'MAPE %',
          data: comparisons.map((m: any) => m.mape),
          backgroundColor: '#e74c3c',
          borderColor: '#e74c3c',
          borderWidth: 1
        },
        {
          label: 'R²',
          data: comparisons.map((m: any) => m.rSquared),
          backgroundColor: '#f39c12',
          borderColor: '#f39c12',
          borderWidth: 1
        }
      ]
    },
    options: {
      responsive: true,
      plugins: {
        legend: {
          position: 'top',
          labels: {
            font: { size: 12 }
          }
        },
        title: {
          display: true,
          text: 'RMSE, MAE, MAPE (Lower is Better) | R² (Higher is Better)',
          font: { size: 14 }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          title: {
            display: true,
            text: 'Metric Value'
          }
        },
        x: {
          title: {
            display: true,
            text: 'Machine Learning Models'
          }
        }
      }
    }
  });
};

onMounted(() => {
  loadSettings()
  loadModelComparisons()
})
</script>

<style scoped src="./settingsComponent.css"></style>
