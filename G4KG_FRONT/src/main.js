// main.js
import { createApp } from 'vue';
import App from './App.vue';

// 引入 Element Plus 和样式
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';

// 引入 ECharts
import * as echarts from 'echarts';

// 引入全局 css 样式
import './assets/css/tooltip.css';
import './assets/css/button.css';
import './assets/css/queryDrop.css'
import './assets/css/global.css'

// 引入路由配置
import router from './router';

// 创建 Vue 应用实例
const app = createApp(App);

// 注册 Element Plus 的图标组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component);
}

// 使用 Element Plus 和路由
app.use(ElementPlus);

// 将 ECharts 挂载到全局
app.config.globalProperties.$echarts = echarts;

// 挂载应用
app.use(router);
app.mount('#app');
