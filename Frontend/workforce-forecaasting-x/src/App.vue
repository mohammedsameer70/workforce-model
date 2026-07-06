<script setup lang="ts">
import { computed, ref, provide, watch } from 'vue'
import { useRoute } from 'vue-router'
import sideBarComponent from '@/sideBar/sideBarComponent.vue'
import topMenuBarComponent from '@/component/topMenuBarComponent.vue'

const route = useRoute()
const isDarkMode = ref(
  localStorage.getItem('theme') ? localStorage.getItem('theme') === 'dark' : true,
)
const showSidebar = computed(() => {
  return route.name !== 'homeSettings'
})

const toggleTheme = () => {
  isDarkMode.value = !isDarkMode.value
  localStorage.setItem('theme', isDarkMode.value ? 'dark' : 'light')
}

// Provide theme to all child components
provide('isDarkMode', isDarkMode)
provide('toggleTheme', toggleTheme)

// Watch for theme changes and update document
watch(
  isDarkMode,
  (newValue) => {
    document.documentElement.setAttribute('data-theme', newValue ? 'dark' : 'light')
  },
  { immediate: true },
)
</script>

<template>
  <div class="app-wrapper" :data-theme="isDarkMode ? 'dark' : 'light'">
    <topMenuBarComponent v-if="showSidebar" class="top-bar" />

    <div class="layout">
      <div class="sideBar" v-if="showSidebar">
        <sideBarComponent />
      </div>

      <div class="content" :class="{ 'full-width': !showSidebar }">
        <router-view />
      </div>
    </div>
  </div>
</template>

<style>
.app-wrapper {
  background: var(--app-bg);
}

.p-panel {
  background: var(--panel-bg) !important;
}

.p-panel-header {
  background: var(--panel-bg) !important;
  color: var(--text-color) !important;
}

.p-panel-content {
  background: var(--panel-bg) !important;
  color: var(--text-color) !important;
}
.app-wrapper {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

.app-wrapper[data-theme='dark'] {
  background-color: #0f1419;
}

.app-wrapper[data-theme='light'] {
  background-color: #ffffff;
}

.top-bar {
  flex-shrink: 0;
  width: 100%;
  height: 70px;
  z-index: 1000;
}

.layout {
  display: flex;
  flex: 1;
  overflow: hidden;
  width: 100%;
}

.sideBar {
  flex-shrink: 0;
  height: calc(100vh - 70px);
  display: flex;
  flex-direction: column;
  background-color: #0b0f1a;
  border-right: 1px solid #1a1f2e;
  overflow: hidden;
}

.sideBar::-webkit-scrollbar {
  width: 6px;
}

.sideBar::-webkit-scrollbar-track {
  background: transparent;
}

.sideBar::-webkit-scrollbar-thumb {
  background: #2a2f3f;
  border-radius: 3px;
}

.sideBar::-webkit-scrollbar-thumb:hover {
  background: #3a3f4f;
}

.content {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 2rem;
  height: calc(100vh - 70px);
}

.content[data-theme='dark'] {
  background-color: #0f1419;
  color: #e5e7eb;
}

.content[data-theme='light'] {
  background-color: #f9fafb;
  color: #1f2937;
}

.content.full-width {
  width: 100%;
}

.content::-webkit-scrollbar {
  width: 8px;
}

.content::-webkit-scrollbar-track {
  background: transparent;
}

.content::-webkit-scrollbar-thumb {
  background: #4a5568;
  border-radius: 4px;
}

.content::-webkit-scrollbar-thumb:hover {
  background: #5a6578;
}
</style>
