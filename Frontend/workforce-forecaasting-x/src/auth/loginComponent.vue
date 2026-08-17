<template>
  <div class="loginContainer">
    <div class="loginCard">
      <div class="loginHeader">
        <div class="logo">
          <i class="pi pi-briefcase"></i>
        </div>
        <h1>Workforce Management</h1>
        <p>Sign in to your account</p>
      </div>

      <form @submit.prevent="handleLogin" class="loginForm">
        <div class="formGroup">
          <label>Username</label>
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
          <label>Password</label>
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
        />

        <div v-if="error" class="errorMessage">
          {{ error }}
        </div>
      </form>

      <div class="loginFooter">
        <p>Test Accounts:</p>
        <div class="testAccounts">
          <div class="testAccount">
            <span class="accountRole">Admin:</span>
            <span>admin / admin123</span>
          </div>
          <div class="testAccount">
            <span class="accountRole">Manager:</span>
            <span>manager / admin123</span>
          </div>
          <div class="testAccount">
            <span class="accountRole">Viewer:</span>
            <span>viewer / admin123</span>
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.loginCard {
  background: white;
  border-radius: 16px;
  padding: 40px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.loginHeader {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  color: white;
  font-size: 28px;
}

.loginHeader h1 {
  margin: 0 0 10px;
  color: #1f2937;
  font-size: 24px;
}

.loginHeader p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.loginForm {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.formGroup {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.formGroup label {
  font-weight: 600;
  color: #374151;
  font-size: 14px;
}

.loginInput {
  width: 100%;
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
}

.loginInput.error {
  border-color: #dc2626;
}

.fieldError {
  color: #dc2626;
  font-size: 12px;
  margin-top: 4px;
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
}

.forgotPasswordLink {
  color: #667eea;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
}

.forgotPasswordLink:hover {
  text-decoration: underline;
}

.loginButton {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  color: white;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s;
}

.loginButton:hover:not(:disabled) {
  transform: translateY(-2px);
}

.loginButton:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.errorMessage {
  background: #fee2e2;
  color: #dc2626;
  padding: 12px;
  border-radius: 8px;
  font-size: 14px;
  text-align: center;
}

.loginFooter {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.loginFooter p {
  margin: 0 0 15px;
  color: #6b7280;
  font-size: 12px;
  text-align: center;
}

.testAccounts {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.testAccount {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #6b7280;
  background: #f9fafb;
  padding: 8px 12px;
  border-radius: 6px;
}

.accountRole {
  font-weight: 600;
  color: #374151;
}
</style>
