<script setup lang="ts">
defineProps<{
  history: Array<{
    date: string
    dataset: string
    algorithmsUsed: string[]
    bestModel: string
    rmse: string | number
    r2: string | number
    status: string
    actions: string[]
  }>
}>()


</script>

<template>
  
</template>

<style scoped src="./TrainingHistory.css">

</style>
