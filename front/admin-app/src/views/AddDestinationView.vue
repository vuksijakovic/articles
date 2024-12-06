<template>
  <div class="container add-destination mt-5">
    <h2 class="mb-4">Dodaj Novu Destinaciju</h2>
    <form @submit.prevent="addDestination" class="needs-validation" novalidate>
      <div class="form-group mb-3">
        <label for="ime">Ime:</label>
        <input type="text" v-model="ime" class="form-control" id="ime" required>
        <div class="invalid-feedback">
          Unesite ime destinacije.
        </div>
      </div>
      <div class="form-group mb-3">
        <label for="opis">Opis:</label>
        <textarea v-model="opis" class="form-control" id="opis" required></textarea>
        <div class="invalid-feedback">
          Unesite opis destinacije.
        </div>
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
      ime: '',
      opis: ''
    }
  },
  methods: {
    addDestination() {
      axios.post('http://localhost:8080/webprojekat/api/destinations', {
        ime: this.ime,
        opis: this.opis
      })
        .then(() => {
          this.$router.push('/destinations');
        })
        .catch(error => {
          console.error('Error adding destination:', error);
           console.error('Error updating destination:', error);
          if (error.response && error.response.status === 400) {
          alert('Sva polja moraju biti popunjnna');
          }
          if (error.response && error.response.status === 409) {
          alert('Postoji već destinacija sa tim imenom');
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
