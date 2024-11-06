<script>
import Swal from "sweetalert2";
import {axios} from "../main.js";
import ModalEditUser from "../components/ModalEditUser.vue";
import ModalCreateUser from "../components/ModalCreateUser.vue";
import Navbar from "../components/Navbar.vue";
import dados from "/src/Pages/user-data.json";


export default {
  name: "Users",
  components: {Navbar, ModalEditUser, ModalCreateUser},
  data() {
    return {
      users: dados,
      searchTerm: '',
      toast: Swal.mixin({
        toast: true,
        position: "top-end",
        showConfirmButton: false,
        timer: 3000,
        timerProgressBar: true,
        didOpen: (toast) => {
          toast.onmouseenter = Swal.stopTimer;
          toast.onmouseleave = Swal.resumeTimer;
        }
      }),
      user: {},
      modal: '',
      loading: false
    };
  },
  async mounted() {
    if (!localStorage.getItem("token")) {
      this.$router.push('/auth');
    }
    this.loading = true;
    this.toast.fire({
      icon: "info",
      title: "Aviso",
      text: "Você só poderá atualizar as senhas do usuário que acabou de logar.",
      timer: 3000
    });
    const apiUsers = await this.getUsers();
    if (apiUsers) {
      this.users = [...apiUsers, ...dados];
    } else {
      this.users = [...dados];
    }
  },
  methods: {
    async getUsers() {
      try {
        this.loading = true;

        const token = localStorage.getItem('token');

        const response = await axios.get('/user/get', {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });

        return response.data;
      } catch (error) {
        await this.toast.fire({
          icon: "error",
          title: "Houve um erro com a requisição",
          text: error.message
        });
      } finally {
        this.loading = false;
      }
    },
    formatAddressStreet(address) {
      if (!address) {
        return "-";
      }
      return address.street + ' , ' + address.neighborhood;
    },
    formatAddressCity(address) {
      if (!address) {
        return "-";
      }
      return address.city + ' - ' + address.state;
    },
    handleEditUser(user) {
      this.user = user;
    },
    handleDeleteUser(user) {
      Swal.fire({
        title: "Você tem certeza?",
        html: "Excluindo usuário '" + user.username + "' <br>Esta ação não poderá ser desfeita!",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Sim"
      }).then((result) => {
        if (result.isConfirmed) {
          axios.post('urlapi', {});
        }
      });
    },
  },
  computed: {
    filteredUsers() {
      return this.users.filter(user => {
        const search = this.searchTerm.toLowerCase();
        return (
            user.username.toLowerCase().includes(search) ||
            user.email.toLowerCase().includes(search) ||
            (user.address && user.address.city.toLowerCase().includes(search))
        );
      });
    }
  }
};
</script>
<template>
  <Navbar/>
  <div v-if="loading" class="d-flex justify-content-center align-items-center vh-100">
    <div class="spinner-border" role="status">
      <span class="visually-hidden">Loading...</span>
    </div>
  </div>
  <div v-if="!loading" class="container mt-5">
    <ModalEditUser :user="user"/>
    <div class="mb-3 d-flex align-items-center justify-content-center search-container">
      <img src="../../public/assets/icons/magnifying-glass-solid.svg" alt="Search Icon" style="width: 30px;" class="pe-2">
      <label for="search" class="pe-3">Pesquisar:</label>
      <input
          type="text"
          class="form-control"
          placeholder="Pesquisar por nome, email ou cidade"
          v-model="searchTerm"
      />
    </div>
    <div class="table-container shadow p-5 border">
      <table class="table table-hover">
        <thead>
        <tr>
          <th scope="col">Nome</th>
          <th scope="col">Email</th>
          <th scope="col">CEP</th>
          <th scope="col">Endereço</th>
          <th scope="col">Cidade</th>
          <th scope="col">Ação</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="user in filteredUsers" :key="user.id">
          <td> {{ user.username }}</td>
          <td> {{ user.email }}</td>
          <td> {{ user.address?.zipCode }}</td>
          <td> {{ formatAddressStreet(user.address) }}</td>
          <td> {{ formatAddressCity(user.address) }}</td>
          <td>
            <div class="btn-group">
              <button type="button" class="btn dropdown-toggle action-btn" data-bs-toggle="dropdown"
                      aria-expanded="false">
                Ação
              </button>
              <ul class="dropdown-menu">
                <li><a class="dropdown-item " data-bs-toggle="modal" data-bs-target="#editModal"
                       @click="handleEditUser(user)">Editar Senha</a></li>
                <li v-if="false"><a class="dropdown-item bg-danger text-bg-danger"
                                    @click="handleDeleteUser(user)">Excluir</a>
                </li>
              </ul>
            </div>
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.search-container{
  width: 402px;
}

.table-container {
  max-height: 750px;
  overflow-y: auto;
  border-top-left-radius: 20px;
  border-bottom-left-radius: 20px;
}

.action-btn {
  background-color: rgba(99, 102, 241, 0.22);
  color: #6366f1;
}
</style>
