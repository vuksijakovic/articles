<template>
  <div class="container mt-5">
    <h1>{{ article.naslov }}</h1>
    <p>Autor: {{ article.autorIme }}</p>
    <p>Datum kreiranja: {{ formatDate(article.vremeKreiranja) }}</p>
    <p>Tekst članka: {{ article.tekst }}</p>
    <p>Aktivnosti: </p>
    <ul class="list-group">
      <li v-for="aktivnost in aktivnosti" :key="aktivnost.id" class="list-group-item">
        <p>{{aktivnost.naziv}}</p>
      </li>
    </ul>
    <h2>Komentari</h2>
    <form @submit.prevent="submitComment">
      <div class="form-group">
        <label for="ime">Ime:</label>
        <input type="text" class="form-control" id="ime" v-model="newComment.ime" required>
      </div>
      <div class="form-group">
        <label for="tekst">Komentar:</label>
        <textarea class="form-control" id="tekst" v-model="newComment.tekst" required></textarea>
      </div>
      <button type="submit" class="btn btn-primary">Dodaj komentar</button>
    </form>
    <ul class="list-group">
      <li v-for="komentar in sortedKomentari" :key="komentar.id" class="list-group-item">
        <p>Autor: {{ komentar.autorIme }}</p>
        <p>Datum: {{ formatDate(komentar.datumKreiranja) }}</p>
        <p>{{ komentar.tekst }}</p>
      </li>
    </ul>
    
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      article: {},
      komentari: [],
      aktivnosti:[],
      newComment: {
        ime: '',
        tekst: ''
      }
    };
  },
  created() {
    const articleId = this.$route.params.id;
    this.fetchArticle(articleId);
    this.fetchComments(articleId);
    this.incrementPosjete(articleId);
    this.fetchAktivnosti(articleId);
  },
  methods: {
    async fetchAktivnosti(id){
        try {
        const response = await axios.get(`http://localhost:8080/webprojekat/api/activities/article/${id}`);
        this.aktivnosti = response.data;
      } catch (error) {
        console.error('Error fetching article:', error);
      } 
    },
    async incrementPosjete(id) {
         try {
        const response = await axios.post(`http://localhost:8080/webprojekat/api/articles/${id}/incrementVisits`);
    
      } catch (error) {
        console.error('Error fetching article:', error);
      }
    },
    async fetchArticle(id) {
      try {
        const response = await axios.get(`http://localhost:8080/webprojekat/api/articles/${id}`);
        var artikal = response.data;
        artikal.autorIme = await this.fetchName(artikal.autorId);
        this.article = response.data;
      } catch (error) {
        console.error('Error fetching article:', error);
      }
    },
    async fetchComments(articleId) {
  try {
    const response = await axios.get(`http://localhost:8080/webprojekat/api/comments/article/${articleId}`);
    this.komentari = response.data.sort((a, b) => new Date(b.datumKreiranja) - new Date(a.datumKreiranja));
  } catch (error) {
    console.error('Error fetching comments:', error);
  }
},
async fetchName(id) {
     try {
        const response = await axios.get(`http://localhost:8080/webprojekat/api/users/${id}`);
        return response.data.ime + " " + response.data.prezime;
      } catch (error) {
        console.error('Error fetching article:', error);
      }
},
    async submitComment() {
      try {
        const response = await axios.post(`http://localhost:8080/webprojekat/api/comments`, {
          clanakId: this.article.id,
          autorIme: this.newComment.ime,
          tekst: this.newComment.tekst
        });
        this.komentari.push(response.data);
        this.komentari.sort((a, b) => new Date(b.datumKreiranja) - new Date(a.datumKreiranja)   )
        this.newComment.ime = '';
        this.newComment.tekst = '';
      } catch (error) {
        console.error('Error submitting comment:', error);
      }
    },
    formatDate(date) {
      const options = { year: 'numeric', month: 'long', day: 'numeric' };
      return new Date(date).toLocaleDateString('sr-RS', options);
    }
  },
  computed: {
    sortedKomentari() {
      return this.komentari;
    }
  }
};
</script>

<style scoped>
/* Dodaj stilove po potrebi */
</style>
