import Vue from 'vue';
import VueRouter from 'vue-router';
import LoginView from '../views/LoginView.vue';
import DestinationView from '../views/DestinationView.vue';
import AddDestinationView from '../views/AddDestinationView.vue';
import EditDestinationView from '../views/EditDestinationView.vue';
import ArticleView from '../views/ArticleView.vue';
import AddArticleView from '../views/AddArticleView.vue';
import EditArticleView from '../views/EditArticleView.vue';
import UsersView from '../views/UsersView.vue';
import EditUserView from '../views/EditUserView.vue';
import AddUserView from '../views/AddUserView.vue';
import ArticleDestinationView from '../views/ArticleViewDestination.vue';

Vue.use(VueRouter);

const routes = [
  {
    path: '/',
    name: 'home',
    component: LoginView
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    component: () => import(/* webpackChunkName: "dashboard" */ '../views/DashboardView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/destinations',
    name: 'destinations',
    component: DestinationView,
    meta: { requiresAuth: true }
  },
  {
    path: '/destinations/add',
    name: 'addDestinations',
    component: AddDestinationView,
    meta: { requiresAuth: true }
  },
  {
    path: '/destinations/edit/:id',
    name: 'editDestination',
    component: EditDestinationView,
    meta: { requiresAuth: true }
  },
  {
    path: '/articles',
    name: 'articles',
    component: ArticleView,
    meta: { requiresAuth: true }
  },
  {
    path: '/article/add',
    name: 'articlesAdd',
    component: AddArticleView,
    meta: { requiresAuth: true }
  }
  ,
  {
    path: '/article/edit/:id',
    name: 'articlesEdit',
    component: EditArticleView,
    meta: { requiresAuth: true }
  },
  {
    path: '/members',
    name: 'members',
    component: UsersView,
    meta: { requiresAuth:true, requiresAdmin: true }
  },
  {
    path: '/member/add',
    name: 'memberAdd',
    component: AddUserView,
    meta: { requiresAuth:true, requiresAdmin: true }
  },
  {
    path: '/member/edit/:id',
    name: 'memberEdit',
    component: EditUserView,
    meta: { requiresAuth:true, requiresAdmin: true }
  },
  {
    path: '/articles/:id',
    name: 'articleDestination',
    component: ArticleDestinationView,
    meta: { requiresAuth:true}
  }
];

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
});

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  const admin = localStorage.getItem('isAdmin');
  if (to.matched.some(record => record.meta.requiresAuth) && !token) {
    next('/login');
  }
  else if (to.matched.some(record => record.meta.requiresAdmin) && admin=="false") {
    console.log(admin);

    next('/destinations');
  }
   else {
    next();
  }
});

export default router;
