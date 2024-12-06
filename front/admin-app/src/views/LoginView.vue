<template>
  <div class="container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="card">
          <div class="card-header text-center">
            <h2>Prijavi se</h2>
          </div>
          <div class="card-body">
            <form @submit.prevent="login">
              <div class="form-group">
                <label for="email">Email:</label>
                <input
                  type="email"
                  class="form-control"
                  id="email"
                  v-model="email"
                  required
                />
              </div>
              <div class="form-group">
                <label for="password">Lozinka: </label>
                <input
                  type="password"
                  class="form-control"
                  id="password"
                  v-model="password"
                  required
                />
              </div>
              <button type="submit" class="btn btn-primary btn-block">
                Login
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

export default {
  data() {
    return {
      email: '',
      password: ''
    };
  },
  methods: {
    async login() {
      try {
        const response = await axios.post('http://localhost:8080/webprojekat/api/auth/login', {
          email: this.email,
          lozinka: this.password
        });
const token = response.data;
        const decodedToken = jwtDecode(token);
        const isAdmin = decodedToken.isAdmin;
        console.log(decodedToken);
        console.log(isAdmin);
        console.log(token);
        localStorage.setItem('token', response.data);
        localStorage.setItem('isAdmin', isAdmin);
        this.$router.push('/destinations');
      } catch (error) {
        console.error('Error logging in:', error);
        alert('Pogrešni podaci ili korisnik neaktivan');
      } 
    }
  }
};
</script>

<style scoped>
/* Dodajte stilove po želji */
</style>
