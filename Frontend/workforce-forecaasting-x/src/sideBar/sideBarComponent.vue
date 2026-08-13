<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { lbl } from '@/assets/constants/labels'
import Divider from 'primevue/divider'

const route = useRoute()

const isCollapsed = ref(false)

const toggleMenu = () => {
  isCollapsed.value = !isCollapsed.value
}

const items = ref([
  { label: lbl.DASHBOARD, icon: 'pi pi-desktop', route: '/dashboard' },
  { label: lbl.FORECASTING, icon: 'pi pi-chart-line', route: '/forecasting' },
  { label: lbl.AI_MODELS, icon: 'pi pi-brain', route: '/ai-models' },
  { label: lbl.SHIFTOPTIMIZATION, icon: 'pi pi-calendar-clock', route: '/shiftoptimization' },
  { label: lbl.EMPLOYEES, icon: 'pi pi-users', route: '/employees' },
  { label: lbl.ANALYTICS, icon: 'pi pi-chart-scatter', route: '/analytics' },
  { label: lbl.CAPACITYPLANNING, icon: 'pi pi-gauge', route: '/capacity-planning' },
  { label: lbl.MONITORING, icon: 'pi pi-eye', route: '/monitoring' },
  { label: lbl.BENCHMARKS, icon: 'pi pi-trophy', route: '/benchmarks' },
  { label: lbl.REPORTS, icon: 'pi pi-clipboard', route: '/reports' },
  { label: lbl.NOTIFICATIONS, icon: 'pi pi-bell', route: '/notifications' },
  { label: lbl.SETTINGS, icon: 'pi pi-cog', route: '/settings' },
])

const isActive = (itemRoute: string) => route.path === itemRoute
</script>

<template>
  <div class="sidebar" :class="{ collapsed: isCollapsed }">
    <!-- MENU -->
    <div class="menu">
      <router-link
        v-for="item in items"
        :key="item.route"
        :to="item.route"
        class="menu-item"
        :class="{ active: isActive(item.route) }"
      >
        <div class="icon-box">
          <i :class="item.icon"></i>
          <span v-if="isActive(item.route)" class="dot"></span>
        </div>

        <!-- hide label when collapsed -->
        <span v-if="!isCollapsed" class="label">
          {{ item.label }}
        </span>
      </router-link>
    </div>

    <!-- TOGGLE BUTTON -->
    <div class="toggle-btn" @click="toggleMenu">
      <i class="pi" :class="isCollapsed ? 'pi-arrow-right' : 'pi-arrow-left'"></i>
    </div>
  </div>
</template>

<style scoped>
.divider {
  margin: 0.5rem 0;
  background-color: rgba(255, 255, 255, 0.06);
}
.sidebar {
  width: 260px;
  height: 100vh;
  background: linear-gradient(180deg, #0f172a 0%, #0b1220 100%);
  border-right: 1px solid rgba(255, 255, 255, 0.06);
  display: flex;
  flex-direction: column;
  transition: width 0.25s ease;
  position: relative;
}

/* COLLAPSED MODE */
.sidebar.collapsed {
  width: 80px;
}

/* HEADER */
.sidebar-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 18px 16px;
}

.title {
  display: flex;
  flex-direction: column;
  font-size: 13px;
  font-weight: 700;
  color: white;
}

.title span {
  font-size: 11px;
  font-weight: 400;
  color: #94a3b8;
}

/* ICON */
.icon-card {
  width: 42px;
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(34, 197, 94, 0.12);
  color: #22c55e;
  border-radius: 12px;
  font-size: 18px;
}

/* MENU */
.menu {
  display: flex;
  flex-direction: column;
  padding: 10px 8px;
  gap: 4px;
}

/* ITEM */
.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 10px;
  color: #cbd5e1;
  text-decoration: none;
  transition: all 0.2s ease;
}

.menu-item:hover {
  background: rgba(255, 255, 255, 0.06);
  color: white;
}

.menu-item.active {
  background: rgba(34, 197, 94, 0.12);
  color: #22c55e;
}

/* ICON BOX */
.icon-box {
  position: relative;
  width: 28px;
  display: flex;
  justify-content: center;
}

/* GREEN DOT */
.dot {
  position: absolute;
  right: -4px;
  top: 2px;
  width: 6px;
  height: 6px;
  background: #22c55e;
  border-radius: 50%;
  box-shadow: 0 0 8px #22c55e;
}

/* TOGGLE BUTTON */
.toggle-btn {
  position: absolute;
  bottom: 16px;
  right: 10px;
  width: 34px;
  height: 34px;
  background: #1f2937;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: 0.2s;
}

.toggle-btn:hover {
  background: #22c55e;
  color: black;
}
</style>
