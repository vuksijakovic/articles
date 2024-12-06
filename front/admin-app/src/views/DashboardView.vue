<template>
  <div class="dashboard">
    <h2>Dashboard</h2>
    <p>Welcome to the dashboard! You are {{ isAdmin ? 'an admin' : 'a user' }}.</p>
  </div>
</template>

<script>
export default {
  computed: {
    isAdmin() {
      return localStorage.getItem('isAdmin') === 'true';
    }
  },
  
};
</script>

<style scoped>
/* Dodajte stilove po želji */
</style>
