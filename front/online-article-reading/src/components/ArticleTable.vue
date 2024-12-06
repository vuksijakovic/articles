<template>
  <div>
    <b-table striped hover :items="pagedClanci" :fields="fields">
      <template v-slot:cell(destinacijaIme)="row">
        <span>{{ row.item.destinacijaIme }}</span>
      </template>
      <template v-slot:cell(vremeKreiranja)="row">
        <span>{{ formatDate(row.item.vremeKreiranja) }}</span>
      </template>
    </b-table>
    <b-pagination v-model="currentPage" :total-rows="clanci.length" :per-page="perPage" @change="onPageChange"></b-pagination>
    <template v-if="clanci.length === 0">
      <div class="text-center mt-4">Učitavanje...</div>
    </template>
  </div>
</template>

<script>
import { BTable, BPagination } from 'bootstrap-vue';

export default {
  components: {
    BTable,
    BPagination
  },
  props: {
    clanci: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      currentPage: 1,
      perPage: 10,
      fields: ['naslov', 'tekst', 'destinacijaIme', 'vremeKreiranja']
    };
  },
  computed: {
    pagedClanci() {
      const start = (this.currentPage - 1) * this.perPage;
      const end = start + this.perPage;
      return this.clanci.slice(start, end);
    }
  },
  methods: {
    onPageChange(page) {
      this.currentPage = page;
    },
    formatDate(date) {
      const options = { year: 'numeric', month: 'long', day: 'numeric' };
      return new Date(date).toLocaleDateString('sr-RS', options);
    }
  }
};
</script>

<style>
/* Stilovi po potrebi */
</style>
