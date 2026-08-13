<script lang="ts">
import { computed, defineComponent, ref } from 'vue'
import NConstants from '@/assets/constants/constants'
import { lbl } from '@/assets/constants/labels'
import Card from 'primevue/card'
import InputText from 'primevue/inputtext'
import { useRouter } from 'vue-router'
import { aiModelReady } from '@/state/aiModelGate'

export default defineComponent({
  components: { Card, InputText },

  setup() {
    const router = useRouter()
    const searchText = ref('')

    const r_ArrHomeSettInfo = ref([
      {
        key: NConstants.SETTINGS.DASHBOARD,
        value: lbl.DASHBOARD,
        icon: 'pi-desktop',
        description: lbl.dashboardDesc,
        color: NConstants.colors[0],
      },
      {
        key: NConstants.SETTINGS.FORECASTING,
        value: lbl.FORECASTING,
        icon: 'pi-chart-line',
        description: lbl.forecastingDesc,
        color: NConstants.colors[1],
      },
      {
        key: NConstants.SETTINGS.SHIFTOPTIMIZATION,
        value: lbl.SHIFTOPTIMIZATION,
        icon: 'pi-calendar',
        description: lbl.shiftOptimizationDesc,
        color: NConstants.colors[2],
      },
      {
        key: NConstants.SETTINGS.EMPLOYEES,
        value: lbl.EMPLOYEES,
        icon: 'pi-users',
        description: lbl.employeesDesc,
        color: NConstants.colors[3],
      },
      {
        key: NConstants.SETTINGS.ANALYTICS,
        value: lbl.ANALYTICS,
        icon: 'pi-chart-bar',
        description: lbl.analyticsDesc,
        color: NConstants.colors[4],
      },
      {
        key: NConstants.SETTINGS.CAPACITYPLANNIG,
        value: lbl.CAPACITYPLANNING,
        icon: 'pi-chart-line',
        description: lbl.capacityPlanningDesc,
        color: NConstants.colors[5],
      },
      {
        key: NConstants.SETTINGS.MONITORING,
        value: lbl.MONITORING,
        icon: 'pi-eye',
        description: lbl.monitoringDesc,
        color: NConstants.colors[6],
      },
      {
        key: NConstants.SETTINGS.BENCHMARKS,
        value: lbl.BENCHMARKS,
        icon: 'pi-trophy',
        description: lbl.benchmarksDesc,
        color: NConstants.colors[7],
      },
      {
        key: NConstants.SETTINGS.REPORTS,
        value: lbl.REPORTS,
        icon: 'pi-file-pdf',
        description: lbl.reportsDesc,
        color: NConstants.colors[8],
      },
      {
        key: NConstants.SETTINGS.NOTIFICATIONS,
        value: lbl.NOTIFICATIONS,
        icon: 'pi-bell',
        description: lbl.notificationsDesc,
        color: NConstants.colors[9],
      },
      {
        key: NConstants.SETTINGS.SETTINGS,
        value: lbl.SETTINGS,
        icon: 'pi-cog',
        description: lbl.settingsDesc,
        color: NConstants.colors[10],
      },
    ])

    const settingsCards = computed(() =>
      r_ArrHomeSettInfo.value.map((item) => ({
        ...item,
        disabled: !aiModelReady.value && item.key !== NConstants.SETTINGS.AI_MODELS,
      })),
    )

    const filteredSettings = computed(() => {
      return settingsCards.value.filter((item) =>
        item.value.toLowerCase().includes(searchText.value.toLowerCase()),
      )
    })

    function openSettings(setting: any) {
      if (setting.disabled) {
        alert('Please train or predict a model first to unlock the other modules.')
        return
      }

      switch (setting.key) {
        case NConstants.SETTINGS.DASHBOARD:
          router.push('/dashboard')
          break
        case NConstants.SETTINGS.FORECASTING:
          router.push('/forecasting')
          break
        case NConstants.SETTINGS.SHIFTOPTIMIZATION:
          router.push('/shiftoptimization')
          break
        case NConstants.SETTINGS.EMPLOYEES:
          router.push('/employees')
          break
        case NConstants.SETTINGS.ANALYTICS:
          router.push('/analytics')
          break
        case NConstants.SETTINGS.CAPACITYPLANNIG:
          router.push('/capacity-planning')
          break
        case NConstants.SETTINGS.MONITORING:
          router.push('/monitoring')
          break
        case NConstants.SETTINGS.BENCHMARKS:
          router.push('/benchmarks')
          break
        case NConstants.SETTINGS.REPORTS:
          router.push('/reports')
          break
        case NConstants.SETTINGS.NOTIFICATIONS:
          console.log('Open Notifications settings')
          break
        case NConstants.SETTINGS.SETTINGS:
          console.log('Open General Settings')
          break
      }
    }

    return {
      searchText,
      openSettings,
      filteredSettings,
      aiModelReady,
      NConstants,
    }
  },
})
</script>
<template>
  <div class="settingsContainer">
    <div class="pageHeader">
      <h2>Settings</h2>
      <p>Configure and manage application modules.</p>
    </div>

    <div class="filter">
      <span class="p-input-icon-left">
        <i class="pi pi-search ml-3"></i>
        <InputText v-model="searchText" placeholder="Search settings..." class="searchInput" />
      </span>
    </div>

    <div class="settingsTile">
      <Card
        @click="openSettings(cardsItem)"
        v-for="cardsItem in filteredSettings"
        :key="cardsItem.key"
        class="cardItems"
        :class="{ disabled: !aiModelReady && cardsItem.key !== NConstants.SETTINGS.AI_MODELS }"
      >
        <template #header>
          <div class="cardHeader">
            <div class="cardIcon" :style="{ backgroundColor: cardsItem.color }">
              <i class="pi icon" :class="cardsItem.icon"></i>
            </div>
          </div>
        </template>

        <template #title>
          {{ cardsItem.value }}
        </template>

        <template #content>
          <div class="cardDesc">
            <p>{{ cardsItem.description }}</p>
          </div>
        </template>
      </Card>
    </div>
  </div>
</template>

<style scoped src="./homeSettingComponent.css"></style>
