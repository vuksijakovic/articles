<template>
  <div class="add-user-form container">
    <h2>Dodaj Novog Korisnika</h2>
    <form @submit.prevent="addUser" class="needs-validation" novalidate>
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
      <div class="form-group">
        <label for="lozinka">Lozinka:</label>
        <input type="password" v-model="lozinka" id="lozinka" class="form-control" required>
        <div class="invalid-feedback">Unesite lozinku.</div>
      </div>
      <div class="form-group">
        <label for="potvrdaLozinke">Potvrda lozinke:</label>
        <input type="password" v-model="potvrdaLozinke" id="potvrdaLozinke" class="form-control" required>
        <div class="invalid-feedback">Potvrdite lozinku.</div>
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
      prezime: '',
      email: '',
      tipKorisnika: 'uredjivac',
      lozinka: '',
      potvrdaLozinke: ''
    };
  },
  methods: {
    addUser() {
      const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailPattern.test(this.email)) {
        alert("Neispravan email format");
        return;
      } 
      if(this.lozinka!=this.potvrdaLozinke){
        alert("Lozinke se ne poklapaju");
        return;
      }
      axios.post('http://localhost:8080/webprojekat/api/users', {
        ime: this.ime,
        prezime: this.prezime,
        email: this.email,
        tipKorisnika: this.tipKorisnika,
        lozinka: this.lozinka,
        status: "aktivan"
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
</style>
