import { createRouter, createWebHistory } from 'vue-router'
import { ROUTES } from './routes'
import HomeSettingComponent from '@/homeSetting/homeSettingComponent.vue'
import dashboardComponent from '@/settings/dashboard/dashboardComponent.vue'
import forecastingComponent from '../settings/forecasting/foreCastingComponent.vue'
import AIModelsComponent from '@/settings/aiModels/aiModelsComponent.vue'
import ShiftOptimizationComponent from '@/settings/shiftOptimization/shiftOptimizationComponent.vue'
import EmployeesComponent from '@/settings/employees/employeesComponent.vue'
import AnalyticsComponent from '@/settings/analyze/analyticsComponent.vue'
import CapacityPlanningComponent from '@/settings/capacityPlanning/capacityPlanningComponent.vue'
import MonitorComponent from '@/settings/monitor/monitorComponent.vue'
import BenchmarkComponent from '@/settings/benchmark/benchmarkComponent.vue'
import ReportComponent from '@/settings/reports/reportComponent.vue'
import { aiModelReady, isTraining, isPredicting } from '@/state/aiModelGate'
import NotificationComponent from '@/settings/notification/notificationComponent.vue'
import SetttingsComponent from '@/settings/settings/setttingsComponent.vue'
import LoginComponent from '@/auth/loginComponent.vue'
import AuthService from '@/auth/authService'
const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/login',
    },
    {
      path: '/login',
      name: 'login',
      component: LoginComponent,
    },
    {
      path: ROUTES.HOMESETTINGS,
      name: 'homeSettings',
      component: HomeSettingComponent,
      meta: { requiresAuth: true }
    },
    {
      path: ROUTES.DASHBOARD,
      name: 'dashboard',
      component: dashboardComponent,
      meta: { requiresAuth: true, roles: ['ADMIN', 'MANAGER', 'VIEWER'] }
    },
    {
      path: ROUTES.FORECASTING,
      name: 'forecasting',
      component: forecastingComponent,
      meta: { requiresAuth: true, roles: ['ADMIN', 'MANAGER', 'VIEWER'] }
    },
    {
      path: ROUTES.AI_MODELS,
      name: 'ai-models',
      component: AIModelsComponent,
      meta: { requiresAuth: true, roles: ['ADMIN', 'MANAGER'] }
    },
    {
      path: ROUTES.SHIFTOPTIMIZATION,
      name: 'shiftoptimization',
      component: ShiftOptimizationComponent,
      meta: { requiresAuth: true, roles: ['ADMIN', 'MANAGER', 'VIEWER'] }
    },
    {
      path: ROUTES.EMPLOYEES,
      name: 'employees',
      component: EmployeesComponent,
      meta: { requiresAuth: true, roles: ['ADMIN', 'MANAGER', 'VIEWER'] }
    },
    {
      path: ROUTES.ANALYTICS,
      name: 'analytics',
      component: AnalyticsComponent,
      meta: { requiresAuth: true, roles: ['ADMIN', 'MANAGER', 'VIEWER'] }
    },
    {
      path: ROUTES.CAPACITYPLANNING,
      name: 'capacity-planning',
      component: CapacityPlanningComponent,
      meta: { requiresAuth: true, roles: ['ADMIN', 'MANAGER', 'VIEWER'] }
    },
    {
      path: ROUTES.MONITOR,
      name: 'monitoring',
      component: MonitorComponent,
      meta: { requiresAuth: true, roles: ['ADMIN'] }
    },
    {
      path: ROUTES.BENCHMARK,
      name: 'benchmarks',
      component: BenchmarkComponent,
      meta: { requiresAuth: true, roles: ['ADMIN', 'MANAGER', 'VIEWER'] }
    },
    {
      path: ROUTES.REPORT,
      name: 'reports',
      component: ReportComponent,
      meta: { requiresAuth: true, roles: ['ADMIN', 'MANAGER', 'VIEWER'] }
    },
    {
      path: ROUTES.NOTIFICATION,
      name: 'notifications',
      component: NotificationComponent,
      meta: { requiresAuth: true, roles: ['ADMIN', 'MANAGER', 'VIEWER'] }
    },
    {
      path: ROUTES.SETTINGS,
      name: 'settings',
      component: SetttingsComponent,
      meta: { requiresAuth: true, roles: ['ADMIN'] }
    },
  ],
})
router.beforeEach(async (to, from, next) => {
  console.log('Router guard checking route:', to.path)
  console.log('Is authenticated:', AuthService.isAuthenticated())
  console.log('Current user:', AuthService.getCurrentUser())
  
  // Check authentication
  if (to.meta.requiresAuth && !AuthService.isAuthenticated()) {
    console.log('Route requires auth but user is not authenticated, redirecting to login')
    return next('/login')
  }

  // Check role-based access
  if (to.meta.roles && AuthService.isAuthenticated()) {
    const userRole = AuthService.getCurrentUser().role
    const requiredRoles = to.meta.roles as string[]
    
    console.log('Checking role access. User role:', userRole, 'Required roles:', requiredRoles)
    
    if (!requiredRoles.includes(userRole || '')) {
      alert('You do not have permission to access this page.')
      return next('/homeSettings')
    }
  }

  // Redirect authenticated users away from login
  if (to.path === '/login' && AuthService.isAuthenticated()) {
    console.log('User is authenticated but trying to access login, redirecting to home settings')
    return next('/homeSettings')
  }

  const allowedPaths = [ROUTES.HOMESETTINGS, ROUTES.AI_MODELS, ROUTES.DASHBOARD]

  // Disable all screens except AI_MODELS during training or prediction
  if ((isTraining.value || isPredicting.value)) {
    if (to.path !== ROUTES.AI_MODELS && to.path !== '/login') {
      alert('Please wait for training/prediction to complete before navigating to other screens.')
      return next(false)
    }
  }

  // Disable all screens except HOMESETTINGS, AI_MODELS, and DASHBOARD until model is ready
  if (!aiModelReady.value && !allowedPaths.includes(to.path) && to.path !== '/login') {
    alert('Please train an AI model first before accessing other screens.')
    return next(ROUTES.HOMESETTINGS)
  }

  console.log('Route guard passed, proceeding to:', to.path)
  next()
})

export default router
