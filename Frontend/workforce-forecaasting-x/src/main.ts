import { createApp } from 'vue'
import App from './App.vue'

import router from '@/router'

import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'

/* PrimeVue Components */
import Chart from 'primevue/chart'
import SelectButton from 'primevue/selectbutton'
import Panel from 'primevue/panel'
import Card from 'primevue/card'
import Button from 'primevue/button'
import Tag from 'primevue/tag'
import Divider from 'primevue/divider'
import Toast from 'primevue/toast'
import ToastService from 'primevue/toastservice'
import FileUpload from 'primevue/fileupload'

/* Employee Page Components */
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import ProgressBar from 'primevue/progressbar'

/* Settings Components */
import Dropdown from 'primevue/dropdown'
import InputNumber from 'primevue/inputnumber'
import InputSwitch from 'primevue/inputswitch'

/* ChartJS */
import {
  Chart as ChartJS,
  LineController,
  LineElement,
  BarElement,
  BarController,
  CategoryScale,
  LinearScale,
  PointElement,
  RadialLinearScale,
  Filler,
  Tooltip,
  Legend,
} from 'chart.js'

import { lbl } from '@/assets/constants/labels'

/* Styles */
import 'primeicons/primeicons.css'
import 'bootstrap/dist/css/bootstrap.css'
import '@/assets/styles/main.css'

ChartJS.register(
  LineController,
  LineElement,
  BarElement,
  BarController,
  CategoryScale,
  LinearScale,
  PointElement,
  RadialLinearScale,
  Filler,
  Tooltip,
  Legend,
)

const app = createApp(App)

/* PrimeVue */

app.use(PrimeVue, {
  theme: {
    preset: Aura,
    options: {
      darkModeSelector: '.dark'
    }
  }
})

app.use(ToastService)

/* Router */
app.use(router)
/* Global PrimeVue Components */
app.component('Chart', Chart)
app.component('SelectButton', SelectButton)
app.component('Panel', Panel)
app.component('Card', Card)
app.component('Button', Button)
app.component('Tag', Tag)
app.component('Divider', Divider)
app.component('Toast', Toast)
app.component('FileUpload', FileUpload)

/* Employee Components */
app.component('DataTable', DataTable)
app.component('Column', Column)
app.component('InputText', InputText)
app.component('Select', Select)
app.component('ProgressBar', ProgressBar)

/* Settings Components */
app.component('Dropdown', Dropdown)
app.component('InputNumber', InputNumber)
app.component('InputSwitch', InputSwitch)

/* Global Labels */
app.config.globalProperties.$lbl = lbl

app.mount('#app')
