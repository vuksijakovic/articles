<template>
  <div class="container mt-5">
    <h2>Članci</h2>
    <button class="btn btn-primary mb-3" @click="goToAddArticle">Dodaj Novi Članak</button>
    <table class="table table-bordered">
      <thead>
        <tr>
          <th>Naslov</th>
          <th>Autor</th>
          <th>Datum Kreiranja</th>
          <th>Akcije</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="article in paginatedArticles" :key="article.id">
<td><a :href="`http://localhost:8081/article/${article.id}`" target="_blank">{{ article.naslov }}</a></td>
          <td>{{ article.autorIme }}</td>
          <td>{{ formatDate(article.vremeKreiranja) }}</td>
          <td>
            <button class="btn btn-warning" @click="goToEditArticle(article.id)">Izmeni</button>
            <button class="btn btn-danger" @click="deleteArticle(article.id)">Obriši</button>
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
      articles: [],
      currentPage: 1,
      itemsPerPage: 10
    }
  }, 
  computed: {
    totalPages() {
      return Math.ceil(this.articles.length / this.itemsPerPage);
    },
    paginatedArticles() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      return this.articles.slice(start, end);
    }
  },
  created() {
    this.fetchArticles();
  },
  methods: {
    async fetchArticles() {
      try {
        const response = await axios.get('http://localhost:8080/webprojekat/api/articles');
        const articles = response.data.sort((a, b) => new Date(b.vremeKreiranja) - new Date(a.vremeKreiranja));
        const articlesWithAuthors = await Promise.all(articles.map(async article => {
          const autorIme = await this.fetchName(article.autorId);
          return { ...article, autorIme };
        }));
        this.articles = articlesWithAuthors;
      } catch (error) {
        console.error('Error fetching articles:', error);
      }
    },
    formatDate(dateString) {
      const options = { year: 'numeric', month: 'long', day: 'numeric' };
      return new Date(dateString).toLocaleDateString(undefined, options);
    },
    goToAddArticle() {
      this.$router.push('/article/add');
    },
    goToEditArticle(id) {
      this.$router.push(`/article/edit/${id}`);
    },
    deleteArticle(id) {
      axios.delete(`http://localhost:8080/webprojekat/api/articles/${id}`)
        .then(() => {
          this.fetchArticles();
        })
        .catch(error => {
          console.error('Error deleting article:', error);
        });
    },
    async fetchName(id) {
      try {
        const response = await axios.get(`http://localhost:8080/webprojekat/api/users/${id}`);
        return response.data.ime + " " + response.data.prezime;
      } catch (error) {
        console.error('Error fetching user name:', error);
        return 'Nepoznat Autor';
      }
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
