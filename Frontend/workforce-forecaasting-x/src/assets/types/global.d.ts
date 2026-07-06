import { lbl } from '@/constants/labels'

declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $lbl: typeof lbl
  }
}
