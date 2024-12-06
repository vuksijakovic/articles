<template>
  <div>
    <h1>{{this.ime}}</h1>
    <ul>
      <li v-for="clanak in pagedClanci" :key="clanak.id">
        <router-link :to="'/article/' + clanak.id">
          <h2>{{ clanak.naslov }}</h2>
          <p>{{ clanak.tekst.substring(0, 100) }}...</p>
        <p>Destinacija: {{ clanak.destinacijaIme }}</p>
          <p>Datum objave: {{ formatDate(clanak.vremeKreiranja) }}</p>
        </router-link>
      </li>
    </ul>
<div class="pagination-container">
      <b-pagination v-model="currentPage" :total-rows="clanci.length" :per-page="perPage"></b-pagination>
    </div>  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      clanci: [], 
      currentPage: 1,
      ime: "",
      perPage: 10    };
  },
  created() {
    this.fetchClanci();
  },
  watch: {
    '$route.params.id': 'fetchClanci'
  },
  computed: {
    pagedClanci() {
      const start = (this.currentPage - 1) * this.perPage;
      const end = start + this.perPage;
      return this.clanci.slice(start, end);
    }
  },
  methods: {
    async naziv(id) {
        const ime = await this.fetchDestinacija(id);
        return ime;
    },
    async fetchClanci() {
      try {
        const response = await axios.get('http://localhost:8080/webprojekat/api/articles/activity/'+this.$route.params.id);
        const clanci = response.data;
        this.ime="";
       for (let i = 0; i < clanci.length; i++) {
          const destinacija = await this.fetchDestinacija(clanci[i].destinacijaId);
          clanci[i].destinacijaIme = destinacija;
          const aktivnost = await this.fetchAktivnost(this.$route.params.id);
          this.ime = aktivnost;
        }

        this.clanci = clanci;
      } catch (error) {
        console.error('Error fetching articles:', error);
      }
    },
    formatDate(date) {
      const options = { year: 'numeric', month: 'long', day: 'numeric' };
      return new Date(date).toLocaleDateString('sr-RS', options);
    },
    async fetchDestinacija(id) {
      try {
        const response = await axios.get('http://localhost:8080/webprojekat/api/destinations/' + id);
        return response.data.ime;
      } catch (error) {
        console.error('Error fetching destination:', error);
        return ''; // Vratite prazan string u slučaju greške
      }
    },
    async fetchAktivnost(id) {
      try {
        const response = await axios.get('http://localhost:8080/webprojekat/api/activities/' + id);
        return response.data.naziv;
      } catch (error) {
        console.error('Error fetching destination:', error);
        return ''; // Vratite prazan string u slučaju greške
      }
    }
  }
};
</script>

<style scoped>
ul {
  list-style-type: none;
  padding: 0;
}
li {
  margin-bottom: 20px;
}
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
