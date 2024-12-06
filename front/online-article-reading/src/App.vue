<template>
  <div id="app">
    <nav>
      <router-link to="/">Početna</router-link> |
      <router-link to="/most-read">Najčitaniji</router-link> <br>
      <span v-for="destinacija in destinacije" :key="destinacija.id">
        <router-link :to="'/articles-destination/' + destinacija.id">{{ destinacija.ime }}</router-link>
        <span v-if="destinacija !== destinacije[destinacije.length - 1]"> | </span>
      </span>
      <br>
      <span v-for="destinacija in aktivnosti" :key="destinacija.id">
        <router-link :to="'/articles-activities/' + destinacija.id">{{ destinacija.naziv }}</router-link>
        <span v-if="destinacija !== destinacije[destinacije.length - 1]"> | </span>
      </span>
    </nav>
    <router-view/>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      destinacije: [],
      aktivnosti:[]
    };
  },
  created() {
    this.fetchDestinacije();
    this.fetchAktivnost();
  },
  methods: {
    async fetchDestinacije() {
      try {
        const response = await axios.get('http://localhost:8080/webprojekat/api/destinations');
        this.destinacije = response.data;
      } catch (error) {
        console.error('Error fetching destinations:', error);
      }
    },
    async fetchAktivnost() {
      try {
        const response = await axios.get('http://localhost:8080/webprojekat/api/activities');
        this.aktivnosti = response.data;
      } catch (error) {
        console.error('Error fetching destinations:', error);
      }
    }
  }
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}
</style>
