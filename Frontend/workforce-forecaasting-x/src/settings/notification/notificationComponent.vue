<template>
  <div class="notificationsContainer">
    <!-- Header -->
    <div class="headerSection">
      <div>
        <h1>Notifications</h1>
        <p>System alerts, updates, and operational notifications</p>
      </div>

      <div class="headerActions">
        <Tag :value="`${unreadCount} unread`" severity="info" />

        <Button icon="pi pi-check" label="Mark All Read" outlined @click="markAllAsRead" :disabled="unreadCount === 0" />
      </div>
    </div>

    <!-- Tabs -->
    <div class="tabsWrapper">
      <SelectButton v-model="selectedTab" :options="tabs">
        <template #option="slotProps">
          <div class="tab-option">
            <i :class="slotProps.option.icon"></i>
            <span>{{ slotProps.option.label }}</span>
          </div>
        </template>
      </SelectButton>
    </div>

    <div v-if="loading" class="page-loading-overlay">
      <div class="page-loading-panel">
        <div class="page-loading-spinner"></div>
        <div>Loading notifications...</div>
      </div>
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
import { ref, computed, onMounted } from 'vue'

import Button from 'primevue/button'
import Tag from 'primevue/tag'
import SelectButton from 'primevue/selectbutton'
import NotificationService from './notificationService'
import type { NotificationDTO } from './notificationService'

const selectedTab = ref('All')
const tabs = [
  { label: 'All', icon: 'pi pi-inbox' },
  { label: 'Unread', icon: 'pi pi-envelope' },
  { label: 'Critical', icon: 'pi pi-exclamation-triangle' },
  { label: 'Warnings', icon: 'pi pi-exclamation-circle' },
  { label: 'Info', icon: 'pi pi-info-circle' }
]
const notifications = ref<NotificationDTO[]>([])
const loading = ref(false)
const error = ref('')

const loadNotifications = async () => {
  loading.value = true
  error.value = ''

  try {
    notifications.value = await NotificationService.getNotifications()
  } catch (err) {
    console.error('Failed to load notifications', err)
    error.value = 'Unable to load notifications.'
  } finally {
    loading.value = false
  }
}

const unreadCount = computed(() => {
  return notifications.value.filter((n) => n.unread).length
})

const markAllAsRead = async () => {
  try {
    await NotificationService.markAllAsRead()
    await loadNotifications()
  } catch (err) {
    console.error('Failed to mark all as read', err)
  }
}

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

onMounted(loadNotifications)
</script>

<style scoped src="./notificationComponent.css"></style>
