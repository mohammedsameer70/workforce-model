<script setup lang="ts">
import { ref, inject } from 'vue'
import { lbl } from '@/assets/constants/labels'
import InputText from 'primevue/inputtext'
import Badge from 'primevue/badge'

const searchText = ref('')
const isDarkMode = inject('isDarkMode', ref(true))
const toggleTheme = inject('toggleTheme', () => {})
</script>

<template>
  <div class="topMenuBar">
    <div class="navbar-left">
      <div class="logo">
        <i class="pi pi-briefcase"></i>
        <span>WorkForce AI</span>
      </div>
    </div>

    <div class="navbar-center">
      <div class="search-box">
        <span class="p-input-icon-left">
          <i class="pi pi-search"></i>
          <InputText v-model="searchText" placeholder="Search operations..." class="searchInput" />
        </span>
      </div>
    </div>

    <div class="navbar-right">
      <button
        class="theme-toggle"
        @click="toggleTheme"
        :title="isDarkMode ? 'Light Mode' : 'Dark Mode'"
      >
        <i :class="isDarkMode ? 'pi pi-sun' : 'pi pi-moon'"></i>
      </button>

      <Badge value="Live" severity="success" class="status-badge"></Badge>

      <div class="user-profile">
        <div class="roleIcon">
          <i class="pi pi-user" style="font-size: 0.87rem"></i>
        </div>
        <span>Operations Admin</span>
      </div>
    </div>
  </div>
</template>

<style scoped src="./topMenuBarComponent.css"></style>
