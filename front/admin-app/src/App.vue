<template>
  <div id="app">
    <nav>
            <span v-if="isntLoggedIn">       <router-link to="/login">Prijavi se</router-link> 
</span>
<span v-if="isLoggedIn">       <router-link to="">{{this.ime}}</router-link> <br>
</span>
     <span v-if="isLoggedIn"> <router-link to="/destinations">Destinacije</router-link> | </span>
      <span v-if="isLoggedIn"> <router-link to="/articles">Članci</router-link>  </span>
            <span v-if="isAdmin"> | <router-link to="/members">Korisnici</router-link>  </span>

      <span v-if="isLoggedIn"> | <button @click="logout">Odjavi se</button></span>
    </nav>
    <router-view/>
  </div>
</template>

<script>
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

export default {
  data() {
    return {
      ime: '',
      token: localStorage.getItem('token'),
      admin: localStorage.getItem('isAdmin')
    }
  },
  computed: {
    isLoggedIn() {
      return this.token !== null;
    },
    isntLoggedIn() {
      return this.token == null;
    },
    isAdmin() {
      return this.admin == "true";
    }
  },
  created() {
    this.getUserFullName();
  },
  methods: {
    logout() {
      localStorage.removeItem('token');
      localStorage.removeItem('isAdmin');
      this.token = null; 
      this.admin = null;
      this.ime = null;
      this.$router.push('/login');
    },
    async getUserFullName() {
      try {
        const decodedToken = jwtDecode(this.token);
        const userId = decodedToken.jti; 

        const response = await axios.get(`http://localhost:8080/webprojekat/api/users/${userId}`);
        const user = response.data;
        this.ime = `${user.ime} ${user.prezime}`;
      } catch (error) {
        console.error('Error fetching user details:', error);
        return 'Error fetching user details';
      }
    }
  },
  watch: {
    '$route'() {
      this.token = localStorage.getItem('token');
      this.admin = localStorage.getItem('isAdmin');
      this.getUserFullName();
    
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

nav button {
  font-weight: bold;
  color: #2c3e50;
  background: none;
  border: none;
  cursor: pointer;
  text-decoration: underline;
  padding: 0;
}

nav button:hover {
  color: #42b983;
}
</style>
