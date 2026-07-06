import { createRouter, createWebHistory } from 'vue-router'
import { ROUTES } from './routes'
import HomeSettingComponent from '@/homeSetting/homeSettingComponent.vue'
import dashboardComponent from '@/settings/dashboard/dashboardComponent.vue'
import forecastingComponent from '../settings/forecasting/foreCastingComponent.vue'
import ShiftOptimizationComponent from '@/settings/shiftOptimization/shiftOptimizationComponent.vue'
import EmployeesComponent from '@/settings/employees/employeesComponent.vue'
import AnalyticsComponent from '@/settings/analyze/analyticsComponent.vue'
import CapacityPlanningComponent from '@/settings/capacityPlanning/capacityPlanningComponent.vue'
import MonitorComponent from '@/settings/monitor/monitorComponent.vue'
import BenchmarkComponent from '@/settings/benchmark/benchmarkComponent.vue'
import ReportComponent from '@/settings/reports/reportComponent.vue'
import NotificationComponent from '@/settings/notification/notificationComponent.vue'
import SetttingsComponent from '@/settings/settings/setttingsComponent.vue'
const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: ROUTES.HOMESETTINGS,
    },
    {
      path: ROUTES.HOMESETTINGS,
      name: 'homeSettings',
      component: HomeSettingComponent,
    },
    {
      path: ROUTES.DASHBOARD,
      name: 'dashboard',
      component: dashboardComponent,
    },
    {
      path: ROUTES.FORECASTING,
      name: 'forecasting',
      component: forecastingComponent,
    },
    {
      path: ROUTES.SHIFTOPTIMIZATION,
      name: 'shiftoptimization',
      component: ShiftOptimizationComponent,
    },
    {
      path: ROUTES.EMPLOYEES,
      name: 'employees',
      component: EmployeesComponent,
    },
    {
      path: ROUTES.ANALYTICS,
      name: 'analytics',
      component: AnalyticsComponent,
    },
    {
      path: ROUTES.CAPACITYPLANNING,
      name: 'capacity-planning',
      component: CapacityPlanningComponent,
    },
    {
      path: ROUTES.MONITOR,
      name: 'monitoring',
      component: MonitorComponent,
    },
    {
      path: ROUTES.BENCHMARK,
      name: 'benchmarks',
      component: BenchmarkComponent,
    },
    {
      path: ROUTES.REPORT,
      name: 'reports',
      component: ReportComponent,
    },
    {
      path: ROUTES.NOTIFICATION,
      name: 'notifications',
      component: NotificationComponent,
    },
    {
      path: ROUTES.SETTINGS,
      name: 'settings',
      component: SetttingsComponent,
    },
  ],
})
export default router
