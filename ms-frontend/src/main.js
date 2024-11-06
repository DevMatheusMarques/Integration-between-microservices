import { createApp } from 'vue';
import './style.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import App from './App.vue';
import axios from "axios";

import router from './router';

const app = createApp(App);

app.use(router);

app.mount('#app');

axios.defaults.baseURL = 'http://localhost:8080/api';

export { axios };
