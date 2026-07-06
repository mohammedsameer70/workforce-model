<template>
  <div class="settingsContainer">
    <div class="pageHeader">
      <h1>System Settings</h1>
      <p>Configure platform preferences and integrations</p>
    </div>

    <!-- Profile -->
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
        <div class="settingRow">
          <div>
            <h4>Dark Mode</h4>
            <span>Toggle between light and dark theme</span>
          </div>

          <InputSwitch v-model="appearance.darkMode" />
        </div>

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
import { ref } from 'vue'

import Card from 'primevue/card'
import Button from 'primevue/button'
import Dropdown from 'primevue/dropdown'
import InputText from 'primevue/inputtext'
import InputSwitch from 'primevue/inputswitch'

const roles = ['Super Admin', 'Operations Admin', 'Manager']
const departments = ['All Departments', 'Inbound', 'Outbound', 'Packing', 'Sortation']

const models = ['LSTM v3.2 (Active)', 'XGBoost v2.1', 'Prophet v1.0']

const refreshOptions = ['30 seconds', '1 minute', '5 minutes']

const profile = ref({
  fullName: 'Operations Admin',
  email: 'admin@workforceai.com',
  role: 'Super Admin',
  department: 'All Departments',
})

const appearance = ref({
  darkMode: true,
  compactView: false,
  animations: true,
})

const notifications = ref({
  criticalAlerts: true,
  shiftRecommendations: true,
  systemMonitoring: true,
  emailDigest: false,
})

const config = ref({
  model: 'LSTM v3.2 (Active)',
  refresh: '30 seconds',
  apiUrl: 'https://api.workforceai.internal',
  mlUrl: 'https://ml.workforceai.internal',
})
</script>

<style scoped src="./settingsComponent.css"></style>
