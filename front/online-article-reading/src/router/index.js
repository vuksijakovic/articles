import Vue from 'vue'
import VueRouter from 'vue-router'
import HomeView from '../views/HomeView.vue'
import ArticleDetailView from '../views/ArticleDetailView.vue'
import MostReadView from '../views/MostReadView.vue'
import ArticleDestinationView from '../views/ArticleDestinationView.vue'
import ArticleActivitiesView from '../views/ArticleActivitiesView.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/about',
    name: 'about',
    component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
  },
  {
    path: '/article/:id', 
    name: 'ArticleDetail',
    component: ArticleDetailView
  },
  {
    path: '/articles-destination/:id', 
    name: 'ArticleDestination',
    component: ArticleDestinationView
  },
  {
    path: '/articles-activities/:id', 
    name: 'ArticleActivities',
    component: ArticleActivitiesView
  },
  {
    path: '/most-read', 
    name: 'ArticleDetail',
    component: MostReadView
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
