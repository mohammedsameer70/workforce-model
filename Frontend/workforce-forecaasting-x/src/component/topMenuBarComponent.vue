<script setup lang="ts">
import { ref } from 'vue'
import { useTheme } from './useTheme'
import InputText from 'primevue/inputtext'
import Badge from 'primevue/badge'
import ToggleSwitch from 'primevue/toggleswitch'

const searchText = ref('')

const { isDark } = useTheme()
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
      
  <ToggleSwitch v-model="isDark" />

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
