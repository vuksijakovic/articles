<template>
  <div class="container destinations mt-5">
    <h2 class="my-4">Destinacije</h2>
    <button class="btn btn-primary mb-3" @click="$router.push('/destinations/add')">Dodaj Novu Destinaciju</button>
    <div class="table-responsive">
      <table class="table table-bordered table-hover">
        <thead class="thead-light">
          <tr>
            <th>Ime</th>
            <th>Opis</th>
            <th>Akcije</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="destination in paginatedDestinations" :key="destination.id">
            <td>
              <router-link :to="{ name: 'articleDestination', params: { id: destination.id } }">
                {{ destination.ime }}
              </router-link>
            </td>
            <td>{{ destination.opis }}</td>
            <td>
              <button class="btn btn-warning btn-sm" @click="$router.push({ name: 'editDestination', params: { id: destination.id } })">Izmeni</button>
              <button class="btn btn-danger btn-sm" @click="deleteDestination(destination.id)">Obriši</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
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
      destinations: [],
      currentPage: 1,
      itemsPerPage: 10
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.destinations.length / this.itemsPerPage);
    },
    paginatedDestinations() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      return this.destinations.slice(start, end);
    }
  },
  created() {
    this.fetchDestinations();
  },
  methods: {
    fetchDestinations() {
      axios.get('http://localhost:8080/webprojekat/api/destinations')
        .then(response => {
          this.destinations = response.data;
        })
        .catch(error => {
          console.error('Error fetching destinations:', error);
        });
    },
    deleteDestination(id) {
      axios.delete(`http://localhost:8080/webprojekat/api/destinations/${id}`)
        .then(() => {
          this.fetchDestinations();
        })
        .catch(error => {
          alert('Destinacija se može obrisati samo ako nema nijedan članak vezan za tu destinaciju');
          console.error('Error deleting destination:', error);
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
.table-responsive {
  margin-top: 20px;
}
</style>
