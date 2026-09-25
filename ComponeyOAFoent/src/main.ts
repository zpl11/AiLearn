
import { createApp } from 'vue'
import { createPinia } from 'pinia'


// 1. 引入 Element Plus 及其核心样式
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 可选：如果是国内中文 OA 系统，建议顺手引入中文语言包
import zhCn from 'element-plus/es/locale/lang/zh-cn'


import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.use(ElementPlus, {
    locale: zhCn
})

app.mount('#app')
