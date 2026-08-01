<script setup lang="ts">
import { ref } from 'vue'

const datasetName = ref('')
const isPredicting = ref(false)
const predictionProgress = ref(0)

const fileInput = ref<HTMLInputElement>()

const handleFileClick = () => {
  fileInput.value?.click()
}

const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files && target.files[0]) {
    datasetName.value = target.files[0].name
  }
}

const predict = () => {
  if (!datasetName.value) return

  isPredicting.value = true
  predictionProgress.value = 0

  const interval = setInterval(() => {
    predictionProgress.value += Math.random() * 20
    if (predictionProgress.value >= 100) {
      predictionProgress.value = 100
      clearInterval(interval)
      setTimeout(() => {
        isPredicting.value = false
      }, 500)
    }
  }, 300)
}
</script>

<template>
  
</template>

<style scoped>

</style>
