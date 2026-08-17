<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Badge from 'primevue/badge'
import Button from 'primevue/button'
import AuthService from '@/auth/authService'

const router = useRouter()

const currentUser = computed(() => AuthService.getCurrentUser())
const username = computed(() => currentUser.value.username || 'Guest')
const userRole = computed(() => currentUser.value.role || 'Unknown')

const handleLogout = () => {
  AuthService.logout()
  router.push('/login')
}

const goToHomeSettings = () => {
  router.push('/homeSettings')
}

onMounted(() => {
  // Refresh user data on mount
  const token = AuthService.getToken()
  if (!token) {
    router.push('/login')
  }
})
</script>

<template>
  <div class="topMenuBar">
    <div class="navbar-left">
      <div class="logo">
        <i class="pi pi-briefcase"></i>
        <span>WorkForce AI</span>
      </div>
    </div>

    <div class="navbar-right">
      <Button icon="pi pi-cog" severity="secondary" text rounded @click="goToHomeSettings" class="home-settings-button" />
      
      <Badge value="Live" severity="success" class="status-badge"></Badge>

      <div class="user-profile">
        <div class="roleIcon">
          <i class="pi pi-user" style="font-size: 0.87rem"></i>
        </div>
        <div class="user-info">
          <span class="username">{{ username }}</span>
          <span class="role">{{ userRole }}</span>
        </div>
        <Button icon="pi pi-sign-out" severity="danger" text rounded @click="handleLogout" class="logout-button" />
      </div>
    </div>
  </div>
</template>

<style scoped src="./topMenuBarComponent.css"></style>
