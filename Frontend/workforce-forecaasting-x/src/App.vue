<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import sideBarComponent from '@/sideBar/sideBarComponent.vue'
import topMenuBarComponent from '@/component/topMenuBarComponent.vue'
import Toast from 'primevue/toast'

const route = useRoute()
// Default light theme only; removed dark-mode toggle
const showSidebar = computed(() => {
  return route.name !== 'homeSettings'
})

// note: dark mode support removed — app defaults to light styles
</script>

<template>
  <div class="app-wrapper">
    <Toast />
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
  background-color: #ffffff;
  border-right: 1px solid #e5e7eb;
  overflow: hidden;
}

.sideBar::-webkit-scrollbar {
  width: 6px;
}

.sideBar::-webkit-scrollbar-track {
  background: transparent;
}

.sideBar::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 3px;
}

.sideBar::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

.content {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 2rem;
  height: calc(100vh - 70px);
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
  background: #d1d5db;
  border-radius: 4px;
}

.content::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}
:root {
    --app-bg: #ffffff;
    --content-bg: #f8fafc;
    --text-color: #1f2937;
}
</style>
