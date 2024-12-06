<template>
  <div class="edit-user-form container">
    <h2>Izmeni Korisnika</h2>
    <form @submit.prevent="updateUser" class="needs-validation" novalidate>
      <div class="form-group">
        <label for="ime">Ime:</label>
        <input type="text" v-model="ime" id="ime" class="form-control" required>
        <div class="invalid-feedback">Unesite ime korisnika.</div>
      </div>
      <div class="form-group">
        <label for="prezime">Prezime:</label>
        <input type="text" v-model="prezime" id="prezime" class="form-control" required>
        <div class="invalid-feedback">Unesite prezime korisnika.</div>
      </div>
      <div class="form-group">
        <label for="email">Email:</label>
        <input type="email" v-model="email" id="email" class="form-control" required>
        <div class="invalid-feedback">Unesite ispravan email.</div>
      </div>
      <div class="form-group">
        <label for="tipKorisnika">Tip korisnika:</label>
        <select v-model="tipKorisnika" id="tipKorisnika" class="form-control" required>
          <option value="admin">Administrator</option>
          <option value="uredjivac">Uređivač sadržaja</option>
        </select>
        <div class="invalid-feedback">Odaberite tip korisnika.</div>
      </div>
      <button type="submit" class="btn btn-primary">Izmeni</button>
    </form>
  </div>
</template>


<script>
import axios from 'axios';

export default {
  props: ['id'],
  data() {
    return {
      ime: '',
      prezime: '',
      email: '',
      tipKorisnika: ''
    }
  },
  created() {
    this.fetchUser();
  },
  methods: {
    fetchUser() {
      axios.get(`http://localhost:8080/webprojekat/api/users/${this.$route.params.id}`)
        .then(response => {
          const user = response.data;
          this.ime = user.ime;
          this.prezime = user.prezime;
          this.email = user.email;
          this.tipKorisnika = user.tipKorisnika;
        })
        .catch(error => {
          console.error('Error fetching user:', error);
        });
    },
    updateUser() {
      const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailPattern.test(this.email)) {
        alert("Neispravan email format");
        return;
      } 
      axios.put(`http://localhost:8080/webprojekat/api/users/${this.$route.params.id}`, {
        ime: this.ime,
        prezime: this.prezime,
        email: this.email,
        tipKorisnika: this.tipKorisnika
      })
        .then(() => {
          this.$router.push('/members');
        })
        .catch(error => {
          console.error('Error adding user:', error);
          if (error.response && error.response.status === 400) {
          alert('Sva polja moraju biti popunjena');
          }
if (error.response && error.response.status === 409) {
          alert('Postoji već korisnik sa tim mejlom');
        }
        if (error.response && error.response.status === 401) {
          alert('Niste autorizovani');
        }

        });
    }
  }
};
</script>

<style scoped>
/* Add your styles here */
</style>
