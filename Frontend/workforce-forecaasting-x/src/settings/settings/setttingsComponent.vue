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
            <label>Forecast Model</label>
            <Dropdown v-model="config.model" :options="models" fluid />
          </div>

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
        </div>
      </div>
    </div>

    <div class="footerActions">
      <Button label="Cancel" outlined />

      <Button label="Save Changes" icon="pi pi-check" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

import Card from 'primevue/card'
import Button from 'primevue/button'
import Dropdown from 'primevue/dropdown'
import InputText from 'primevue/inputtext'
import InputSwitch from 'primevue/inputswitch'
import SettingsService from './settingsService'
import type { SettingsDTO } from './settingsService'

const roles = ['Super Admin', 'Operations Admin', 'Manager']
const departments = ['All Departments', 'Inbound', 'Outbound', 'Packing', 'Sortation']
const models = ['LSTM v3.2 (Active)', 'XGBoost v2.1', 'Prophet v1.0']
const refreshOptions = ['30 seconds', '1 minute', '5 minutes']

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
})

const loading = ref(false)
const message = ref('')

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

onMounted(loadSettings)
</script>

<style scoped src="./settingsComponent.css"></style>
