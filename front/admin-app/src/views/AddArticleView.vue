<template>
  <div class="container mt-5">
    <h2>Dodaj Novi Članak</h2>
    <form @submit.prevent="addArticle" class="needs-validation" novalidate>
      <div class="form-group mb-3">
        <label for="naslov">Naslov:</label>
        <input type="text" v-model="naslov" class="form-control" id="naslov" required>
        <div class="invalid-feedback">
          Unesite naslov članka.
        </div>
      </div>
      <div class="form-group mb-3">
        <label for="destinacija">Destinacija:</label>
        <select v-model="destinacijaId" class="form-control" id="destinacija" required>
          <option v-for="destinacija in destinacije" :key="destinacija.id" :value="destinacija.id">{{ destinacija.ime }}</option>
        </select>
        <div class="invalid-feedback">
          Odaberite destinaciju.
        </div>
      </div>
      <div class="form-group mb-3">
        <label for="tekst">Tekst:</label>
                <textarea v-model="tekst" class="form-control" id="tekst" required></textarea>
        <div class="invalid-feedback">
          Unesite tekst članka.
        </div>
      </div>
      <div class="form-group mb-3">
        <label for="aktivnosti">Turističke aktivnosti:</label>
        <input type="text" v-model="aktivnosti" class="form-control" id="aktivnosti">
        <small class="form-text text-muted">Unesite aktivnosti razdvojene zarezom.</small>
      </div>
      <button type="submit" class="btn btn-primary">Dodaj</button>
    </form>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      naslov: '',
      destinacijaId: null,
      tekst: '',
      aktivnosti: '',
      destinacije: []
    }
  },
  created() {
    this.fetchDestinacije();
  },
  methods: {
    fetchDestinacije() {
      axios.get('http://localhost:8080/webprojekat/api/destinations')
        .then(response => {
          this.destinacije = response.data;
        })
        .catch(error => {
          console.error('Error fetching destinations:', error);
        });
    },
    addArticle() {
      axios.post('http://localhost:8080/webprojekat/api/articles', {
        naslov: this.naslov,
        destinacijaId: this.destinacijaId,
        tekst: this.tekst,
        aktivnosti: this.aktivnosti,
        vremeKreiranja: new Date().toISOString()
      })
        .then(() => {
          this.$router.push('/articles');
        })
        .catch(error => {
          console.error('Error adding destination:', error);
           console.error('Error updating destination:', error);
          if (error.response && error.response.status === 400) {
          alert('Sva polja moraju biti popunjena');
          }
         
        if (error.response && error.response.status === 401) {
          alert('Niste autorizovani');
        }

        });
    }
  }
}
</script>

<style scoped>
/* Add your custom styles here if needed */
</style>

