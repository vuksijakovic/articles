<template>
  <div class="container mt-5">
    <h2>Korisnici</h2>
    <button class="btn btn-primary mb-3" @click="goToAddUser">Dodaj Novog Korisnika</button>
    <table class="table table-bordered">
      <thead>
        <tr>
          <th>Ime i Prezime</th>
          <th>Email</th>
          <th>Tip Korisnika</th>
          <th>Status</th>
          <th>Akcije</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in paginatedUsers" :key="user.id">
          <td>{{ user.ime }} {{ user.prezime }}</td>
          <td>{{ user.email }}</td>
          <td>{{ user.tipKorisnika }}</td>
          <td>{{ user.status  }}</td>
          <td>
            <button class="btn btn-warning" @click="goToEditUser(user.id)">Izmeni</button>
<button class="btn btn-danger" @click="toggleActivation(user.id)" v-if="user.tipKorisnika !== 'admin'">
  {{ user.status === 'aktivan' ? 'Deaktiviraj' : 'Aktiviraj' }}
</button>
          </td>
        </tr>
      </tbody>
    </table>
    <nav aria-label="Page navigation example">
      <ul class="pagination justify-content-center">
        <li class="page-item" :class="{ disabled: currentPage === 1 }">
          <button class="page-link" @click="changePage(currentPage - 1)">Previous</button>
        </li>
        <li class="page-item" v-for="page in totalPages" :key="page" :class="{ active: currentPage === page }">
          <button class="page-link" @click="changePage(page)">{{ page }}</button>
        </li>
        <li class="page-item" :class="{ disabled: currentPage === totalPages }">
          <button class="page-link" @click="changePage(currentPage + 1)">Next</button>
        </li>
      </ul>
    </nav>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      users: [],
      currentPage: 1,
      itemsPerPage: 10
    }
  }, 
  computed: {
    totalPages() {
      return Math.ceil(this.users.length / this.itemsPerPage);
    },
    paginatedUsers() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      return this.users.slice(start, end);
    }
  },
  created() {
    this.fetchUsers();
  },
  methods: {
    fetchUsers() {
      axios.get('http://localhost:8080/webprojekat/api/users')
        .then(response => {
          this.users = response.data;
        })
        .catch(error => {
          console.error('Error fetching users:', error);
        });
    },
    goToAddUser() {
      this.$router.push('/member/add');
    },
    goToEditUser(id) {
      this.$router.push(`/member/edit/${id}`);
    },
    toggleActivation(id) {
      axios.post(`http://localhost:8080/webprojekat/api/users/activate/${id}`)
        .then(() => {
          this.fetchUsers();
        })
        .catch(error => {
          console.error('Error toggling activation:', error);
        });
    },
    changePage(page) {
      if (page > 0 && page <= this.totalPages) {
        this.currentPage = page;
      }
    }
  }
}
</script>

<style scoped>
/* Add your styles here */
</style>
