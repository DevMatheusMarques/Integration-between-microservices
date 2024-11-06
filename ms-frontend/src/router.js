import { createRouter, createWebHistory } from 'vue-router';
import Login from './Pages/Login.vue';
import Register from './Pages/Register.vue';
import Users from "./Pages/Users.vue";

let router = [
    { path: '/auth', component: Login, name: 'login' },
    { path: '/register', component: Register, name: 'register' },
    { path: '/users', component: Users, name: 'users' },
];

router = createRouter({
    history: createWebHistory(),
    routes: router,
});

export default router;
