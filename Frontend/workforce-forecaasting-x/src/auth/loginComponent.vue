<template>
  <div class="loginContainer">
    <div class="backgroundPattern"></div>
    <div class="floatingShapes">
      <div class="shape shape1"></div>
      <div class="shape shape2"></div>
      <div class="shape shape3"></div>
    </div>
    
    <div class="loginWrapper">
      <div class="infoPanel">
        <div class="brandSection">
          <div class="brandLogo">
            <i class="pi pi-users"></i>
          </div>
          <h2 class="brandName">WorkForce Pro</h2>
          <p class="brandTagline">Intelligent Workforce Management</p>
        </div>
        
        <div class="features">
          <div class="featureItem">
            <div class="featureIcon">
              <i class="pi pi-chart-line"></i>
            </div>
            <div class="featureText">
              <h4>Real-time Analytics</h4>
              <p>Monitor workforce metrics in real-time</p>
            </div>
          </div>
          <div class="featureItem">
            <div class="featureIcon">
              <i class="pi pi-calendar"></i>
            </div>
            <div class="featureText">
              <h4>Smart Scheduling</h4>
              <p>AI-powered shift optimization</p>
            </div>
          </div>
          <div class="featureItem">
            <div class="featureIcon">
              <i class="pi pi-shield"></i>
            </div>
            <div class="featureText">
              <h4>Enterprise Security</h4>
              <p>Bank-grade data protection</p>
            </div>
          </div>
        </div>
        
        <div class="stats">
          <div class="statItem">
            <div class="statValue">10K+</div>
            <div class="statLabel">Employees Managed</div>
          </div>
          <div class="statItem">
            <div class="statValue">99.9%</div>
            <div class="statLabel">Uptime</div>
          </div>
          <div class="statItem">
            <div class="statValue">24/7</div>
            <div class="statLabel">Support</div>
          </div>
        </div>
      </div>
      
      <div class="loginCard">
        <div class="loginHeader">
          <div class="logo">
            <i class="pi pi-lock"></i>
          </div>
          <h1>Welcome Back</h1>
          <p>Sign in to access your dashboard</p>
        </div>

      <form @submit.prevent="handleLogin" class="loginForm">
        <div class="formGroup">
          <label>
            <i class="pi pi-user"></i>
            Username
          </label>
          <InputText 
            v-model="username" 
            placeholder="Enter your username" 
            class="loginInput"
            :class="{ 'error': validationErrors.username }"
            :disabled="loading"
          />
          <div v-if="validationErrors.username" class="fieldError">
            {{ validationErrors.username }}
          </div>
        </div>

        <div class="formGroup">
          <label>
            <i class="pi pi-key"></i>
            Password
          </label>
          <InputText 
            v-model="password" 
            type="password" 
            placeholder="Enter your password" 
            class="loginInput"
            :class="{ 'error': validationErrors.password }"
            :disabled="loading"
          />
          <div v-if="validationErrors.password" class="fieldError">
            {{ validationErrors.password }}
          </div>
        </div>

        <div class="formGroup checkboxContainer">
          <label class="checkboxLabel">
            <input type="checkbox" v-model="rememberMe" :disabled="loading" />
            <span>Remember me</span>
          </label>
          <a href="#" @click.prevent="handleForgotPassword" class="forgotPasswordLink">
            Forgot password?
          </a>
        </div>

        <Button 
          type="submit" 
          label="Sign In" 
          class="loginButton"
          :loading="loading"
          :disabled="!username || !password"
        >
          <template v-if="!loading">
            <i class="pi pi-sign-in"></i>
            Sign In
          </template>
        </Button>

        <div v-if="error" class="errorMessage">
          <i class="pi pi-exclamation-circle"></i>
          {{ error }}
        </div>
      </form>

      <div class="loginFooter">
        <div class="footerHeader">
          <i class="pi pi-info-circle"></i>
          <span>Demo Credentials</span>
        </div>
        <div class="testAccounts">
          <div class="testAccount" @click="fillCredentials('admin')">
            <div class="accountBadge admin">Admin</div>
            <div class="accountCreds">admin / admin123</div>
            <i class="pi pi-arrow-right"></i>
          </div>
          <div class="testAccount" @click="fillCredentials('manager')">
            <div class="accountBadge manager">Manager</div>
            <div class="accountCreds">manager / admin123</div>
            <i class="pi pi-arrow-right"></i>
          </div>
          <div class="testAccount" @click="fillCredentials('viewer')">
            <div class="accountBadge viewer">Viewer</div>
            <div class="accountCreds">viewer / admin123</div>
            <i class="pi pi-arrow-right"></i>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import InputText from 'primevue/inputtext'
import Button from 'primevue/button'
import AuthService from './authService'

const router = useRouter()
const username = ref('')
const password = ref('')
const rememberMe = ref(false)
const loading = ref(false)
const error = ref('')
const validationErrors = ref<{ username?: string; password?: string }>({})

const validateForm = (): boolean => {
  validationErrors.value = {}
  let isValid = true

  if (!username.value.trim()) {
    validationErrors.value.username = 'Username is required'
    isValid = false
  } else if (username.value.length < 3) {
    validationErrors.value.username = 'Username must be at least 3 characters'
    isValid = false
  }

  if (!password.value) {
    validationErrors.value.password = 'Password is required'
    isValid = false
  } else if (password.value.length < 6) {
    validationErrors.value.password = 'Password must be at least 6 characters'
    isValid = false
  }

  return isValid
}

const handleLogin = async () => {
  error.value = ''
  
  if (!validateForm()) {
    return
  }

  loading.value = true

  try {
    console.log('Attempting login with username:', username.value)
    const response = await AuthService.login(username.value, password.value)
    console.log('Login response:', response)
    
    if (response.token) {
      localStorage.setItem('token', response.token)
      localStorage.setItem('username', response.username)
      localStorage.setItem('role', response.role)
      console.log('Token stored:', response.token)
      console.log('Username stored:', response.username)
      console.log('Role stored:', response.role)
      
      if (rememberMe.value) {
        localStorage.setItem('rememberMe', 'true')
      } else {
        localStorage.removeItem('rememberMe')
      }
      
      console.log('Redirecting to home settings...')
      await router.push('/homeSettings')
    } else {
      error.value = 'Invalid username or password'
    }
  } catch (err: any) {
    console.error('Login error:', err)
    if (err.response?.status === 401) {
      error.value = 'Invalid username or password'
    } else if (err.response?.status === 500) {
      error.value = 'Server error. Please try again later.'
    } else {
      error.value = 'Login failed. Please check your connection and try again.'
    }
  } finally {
    loading.value = false
  }
}

const handleForgotPassword = () => {
  error.value = 'Password reset feature coming soon. Please contact your administrator.'
}

const fillCredentials = (role: string) => {
  const credentials: Record<string, { username: string; password: string }> = {
    admin: { username: 'admin', password: 'admin123' },
    manager: { username: 'manager', password: 'admin123' },
    viewer: { username: 'viewer', password: 'admin123' }
  }
  
  if (credentials[role]) {
    username.value = credentials[role].username
    password.value = credentials[role].password
  }
}

onMounted(() => {
  const rememberedUser = localStorage.getItem('rememberMe')
  if (rememberedUser === 'true') {
    const savedUsername = localStorage.getItem('username')
    if (savedUsername) {
      username.value = savedUsername
      rememberMe.value = true
    }
  }
})
</script>

<style scoped>
.loginContainer {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #334155 100%);
  position: relative;
  overflow: hidden;
}

.backgroundPattern {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(circle at 20% 50%, rgba(59, 130, 246, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(139, 92, 246, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 40% 80%, rgba(16, 185, 129, 0.1) 0%, transparent 50%);
  pointer-events: none;
}

.floatingShapes {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  overflow: hidden;
}

.shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
  animation: float 20s infinite ease-in-out;
}

.shape1 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.shape2 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #10b981, #3b82f6);
  bottom: -50px;
  right: -50px;
  animation-delay: 5s;
}

.shape3 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #f59e0b, #ef4444);
  top: 50%;
  left: 50%;
  animation-delay: 10s;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  25% {
    transform: translate(50px, 50px) rotate(90deg);
  }
  50% {
    transform: translate(0, 100px) rotate(180deg);
  }
  75% {
    transform: translate(-50px, 50px) rotate(270deg);
  }
}

.loginWrapper {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0;
  max-width: 1200px;
  width: 100%;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
  position: relative;
  z-index: 1;
}

.infoPanel {
  background: linear-gradient(135deg, #1e3a8a 0%, #3b82f6 50%, #8b5cf6 100%);
  padding: 60px;
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.brandSection {
  text-align: center;
  margin-bottom: 40px;
}

.brandLogo {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  color: white;
  font-size: 36px;
  backdrop-filter: blur(10px);
}

.brandName {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 10px;
  letter-spacing: -0.5px;
}

.brandTagline {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.featureItem {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  transition: transform 0.3s ease;
}

.featureItem:hover {
  transform: translateX(8px);
}

.featureIcon {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.featureText h4 {
  margin: 0 0 4px;
  font-size: 16px;
  font-weight: 600;
}

.featureText p {
  margin: 0;
  font-size: 13px;
  opacity: 0.85;
}

.stats {
  display: flex;
  justify-content: space-around;
  padding-top: 40px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.statItem {
  text-align: center;
}

.statValue {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 4px;
}

.statLabel {
  font-size: 12px;
  opacity: 0.85;
}

.loginCard {
  padding: 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.loginHeader {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  color: white;
  font-size: 28px;
  box-shadow: 0 10px 30px rgba(59, 130, 246, 0.3);
}

.loginHeader h1 {
  margin: 0 0 8px;
  color: #1e293b;
  font-size: 28px;
  font-weight: 700;
}

.loginHeader p {
  margin: 0;
  color: #64748b;
  font-size: 15px;
}

.loginForm {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.formGroup {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.formGroup label {
  font-weight: 600;
  color: #475569;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.formGroup label i {
  color: #64748b;
  font-size: 16px;
}

.loginInput {
  width: 100%;
  padding: 14px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 15px;
  transition: all 0.3s ease;
  background: #f8fafc;
}

.loginInput:focus {
  border-color: #3b82f6;
  background: white;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
}

.loginInput.error {
  border-color: #ef4444;
}

.loginInput.error:focus {
  box-shadow: 0 0 0 4px rgba(239, 68, 68, 0.1);
}

.fieldError {
  color: #ef4444;
  font-size: 13px;
  margin-top: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.checkboxContainer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.checkboxLabel {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 400;
  cursor: pointer;
  color: #64748b;
  font-size: 14px;
}

.checkboxLabel input[type="checkbox"] {
  width: 18px;
  height: 18px;
  accent-color: #3b82f6;
}

.forgotPasswordLink {
  color: #3b82f6;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
}

.forgotPasswordLink:hover {
  text-decoration: underline;
}

.loginButton {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  border: none;
  border-radius: 12px;
  color: white;
  font-weight: 600;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  box-shadow: 0 10px 30px rgba(59, 130, 246, 0.3);
}

.loginButton:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 15px 40px rgba(59, 130, 246, 0.4);
}

.loginButton:active:not(:disabled) {
  transform: translateY(0);
}

.loginButton:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.errorMessage {
  background: #fef2f2;
  color: #dc2626;
  padding: 14px 16px;
  border-radius: 12px;
  font-size: 14px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: 1px solid #fecaca;
}

.loginFooter {
  margin-top: 40px;
  padding-top: 24px;
  border-top: 1px solid #e2e8f0;
}

.footerHeader {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  color: #64748b;
  font-size: 13px;
  font-weight: 500;
}

.footerHeader i {
  color: #3b82f6;
}

.testAccounts {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.content{
  padding:0rem !important;
}
.testAccount {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  background: #f8fafc;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.testAccount:hover {
  background: #eff6ff;
  border-color: #3b82f6;
  transform: translateX(4px);
}

.accountBadge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.accountBadge.admin {
  background: #dbeafe;
  color: #1e40af;
}

.accountBadge.manager {
  background: #fef3c7;
  color: #92400e;
}

.accountBadge.viewer {
  background: #e0e7ff;
  color: #3730a3;
}

.accountCreds {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

.testAccount i {
  color: #94a3b8;
  transition: color 0.3s ease;
}

.testAccount:hover i {
  color: #3b82f6;
}

@media (max-width: 1024px) {
  .loginWrapper {
    grid-template-columns: 1fr;
    max-width: 500px;
  }
  
  .infoPanel {
    display: none;
  }
  
  .loginCard {
    padding: 40px;
  }
}
</style>
