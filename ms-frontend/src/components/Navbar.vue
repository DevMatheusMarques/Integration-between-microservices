<script>
import ModalCreateUser from "./ModalCreateUser.vue";

export default {
  name: "Navbar",
  components: { ModalCreateUser },
  data() {
    return {
      isHovered: true,
      isModalVisible: false,
    };
  },
  methods: {
    logout() {
      localStorage.removeItem('token');
      this.$router.push('/auth');
    },
    toggleModal() {
      this.isModalVisible = !this.isModalVisible;
    },
  },
};
</script>

<template>
  <nav class="navbar navbar-expand-lg">
    <div class="container-fluid">
      <div class="collapse navbar-collapse" id="navbarNavAltMarkup">
        <div class="navbar-nav d-flex align-items-center justify-content-center">
          <div class="pe-5">
            <img src="../../public/assets/imgs/logotype-white.png" alt="Enterprise Logotype" style="width: 200px">
          </div>
          <button
              class="nav-link text-white"
              @click.prevent="toggleModal"
          >
            Cadastrar Usuário
          </button>
          <router-link
              class="nav-link text-white"
              :class="[$route.name === 'users' ? 'active' : '']"
              aria-current="page"
              to="/users"
          >
            Listar Usuários
          </router-link>
        </div>
      </div>
    </div>
    <button v-if="isHovered" class="btn text-white me-5" @click="logout" style="width: 100px">
      <img src="../../public/assets/icons/box-arrow-white-right.png" alt="Exit Icon" class="icon-exit">Sair
    </button>
    <ModalCreateUser v-if="isModalVisible" @close="toggleModal"/>
  </nav>
</template>

<style scoped>
.icon-exit {
  width: 20px;
  height: 20px;
  padding-right: 5px;
  margin-top: -2px;
}

.navbar {
  background-color: #6366f1;
}
</style>
