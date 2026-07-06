<template>
  <div class="notificationsContainer">
    <!-- Header -->
    <div class="headerSection">
      <div>
        <h1>Notifications</h1>
        <p>System alerts, updates, and operational notifications</p>
      </div>

      <div class="headerActions">
        <Tag value="3 unread" severity="info" />

        <Button icon="pi pi-check" label="Mark All Read" outlined />
      </div>
    </div>

    <!-- Tabs -->
    <div class="tabsWrapper">
      <SelectButton v-model="selectedTab" :options="tabs" />
    </div>

    <!-- Notification List -->
    <div class="notificationPanel">
      <div v-for="item in filteredNotifications" :key="item.title" class="notificationItem">
        <div class="notificationIcon" :class="item.type">
          <i :class="item.icon"></i>
        </div>

        <div class="notificationContent">
          <div class="notificationTitle">
            {{ item.title }}

            <span v-if="item.unread" class="unreadDot" />
          </div>

          <div class="notificationMessage">
            {{ item.message }}
          </div>
        </div>

        <div class="notificationTime">
          {{ item.time }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

import Button from 'primevue/button'
import Tag from 'primevue/tag'
import SelectButton from 'primevue/selectbutton'

const selectedTab = ref('All')

const tabs = ['All', 'Unread', 'Critical', 'Warnings', 'Info']

const notifications = ref([
  {
    title: 'Staffing Shortage Alert',
    message:
      'Outbound department understaffed by 12 workers for afternoon shift. Immediate action required.',
    type: 'critical',
    unread: true,
    time: '2 min ago',
    icon: 'pi pi-exclamation-triangle',
  },
  {
    title: 'High CPU Usage',
    message: 'Analytics-engine service CPU usage above 65% threshold. Auto-scaling triggered.',
    type: 'warning',
    unread: true,
    time: '8 min ago',
    icon: 'pi pi-clock',
  },
  {
    title: 'Model Deployed',
    message: 'ML model LSTM v3.2 deployed successfully. Forecast accuracy improved to 94.2%.',
    type: 'success',
    unread: true,
    time: '15 min ago',
    icon: 'pi pi-check-circle',
  },
  {
    title: 'Overtime Projection',
    message: 'Projected overtime breach for Packing department. 14:00–22:00 shift.',
    type: 'warning',
    unread: false,
    time: '22 min ago',
    icon: 'pi pi-clock',
  },
  {
    title: 'Optimization Complete',
    message: 'Shift optimization completed for next 7-day window. Recommendations generated.',
    type: 'info',
    unread: false,
    time: '35 min ago',
    icon: 'pi pi-info-circle',
  },
  {
    title: 'Night Shift Coverage',
    message: 'Night shift coverage at 68%. Below minimum threshold.',
    type: 'critical',
    unread: false,
    time: '1 hour ago',
    icon: 'pi pi-exclamation-triangle',
  },
  {
    title: 'Benchmark Completed',
    message: 'Load test completed. P99 latency below 200ms.',
    type: 'info',
    unread: false,
    time: '2 hours ago',
    icon: 'pi pi-info-circle',
  },
  {
    title: 'Report Ready',
    message: 'Weekly Workforce Demand Report generated and ready for download.',
    type: 'success',
    unread: false,
    time: '3 hours ago',
    icon: 'pi pi-check-circle',
  },
])

const filteredNotifications = computed(() => {
  switch (selectedTab.value) {
    case 'Unread':
      return notifications.value.filter((n) => n.unread)

    case 'Critical':
      return notifications.value.filter((n) => n.type === 'critical')

    case 'Warnings':
      return notifications.value.filter((n) => n.type === 'warning')

    case 'Info':
      return notifications.value.filter((n) => n.type === 'info' || n.type === 'success')

    default:
      return notifications.value
  }
})
</script>

<style scoped src="./notificationComponent.css"></style>
