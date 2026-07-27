<script setup lang="ts">
defineProps<{
  title: string
  value: string
  icon: string
  color: string
}>()
</script>

<template>
  <div class="stats-card">
    <div class="icon-wrapper" :style="{ backgroundColor: color + '20', borderColor: color }">
      <i :class="`pi ${icon}`" :style="{ color: color }"></i>
    </div>
    <div class="content">
      <p class="title">{{ title }}</p>
      <h3 class="value">{{ value }}</h3>
    </div>
  </div>
</template>

<style scoped>
.stats-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 0.5rem 0.875rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  max-height: 90px;
}

.stats-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  transition: left 0.5s ease;
}

.stats-card:hover::before {
  left: 100%;
}

.stats-card:hover {
  border-color: #d1d5db;
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.icon-wrapper {
  width: 45px;
  height: 45px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid;
  font-size: 1.25rem;
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.stats-card:hover .icon-wrapper {
  transform: scale(1.08) rotate(3deg);
}

.content {
  flex: 1;
}

.title {
  font-size: 0.65rem;
  color: #6b7280;
  margin: 0 0 0.35rem 0;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-weight: 700;
}

.value {
  font-size: 1.35rem;
  font-weight: 800;
  color: #1f2937;
  margin: 0;
  letter-spacing: -0.5px;
}
 
</style>
