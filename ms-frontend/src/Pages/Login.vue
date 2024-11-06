<script>
import ContainerBox from "../components/ContainerBox.vue";
import Navbar from "../components/Navbar.vue";
import {axios} from "../main.js";
import Swal from "sweetalert2";

export default {
  name: "Login",
  components: {Navbar, ContainerBox},
  data() {
    return {
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
    }
  },
  methods: {
    async login() {
      try {
        const form = new FormData(document.getElementById("form-login"));
        const credenptials = {
          email: form.get("email"),
          password: form.get("password"),
        }

        const response = await axios.post("/user/auth", credenptials);
        let value = response.data.token;
        localStorage.setItem('token', value);
        this.toast.fire({
          icon: "success",
          title: "Login realizado com sucesso",
          timer: 2000
        });
        await this.canAccess()
      } catch (error) {
        await this.toast.fire({
          icon: "error",
          title: "Falha ao autenticar",
          text: "Dados inválidos."
        });
      }
    },
    async canAccess() {
      if (localStorage.getItem("token")) {
        this.$router.push('/users');
      }
      await this.toast.fire({
        icon: "error",
        title: "Usuário não autenticado",
        text: "Tente novamente mais tarde.",
        timer: 2500
      });
      this.$router.push('/users');
    }
  }
}
</script>
<template>
  <div class="container">
    <ContainerBox>
      <img src="../../public/assets/imgs/logotype.png" alt="Logotype Enterprise" style="width: 400px">
      <div>
        <h2 class="text-center">Entre em sua conta!</h2>
        <p class="text-center">Boas-vindas! Digite suas informações para logar.</p>
      </div>
      <form id="form-login" class="w-100 d-flex flex-column gap-4" method="post" action="/">
        <div class="">
          <label for="email" class="form-label">Email:</label>
          <input type="email" class="form-control" id="email" name="email" autofocus required>
        </div>
        <div>
          <label for="password" class="form-label">Senha:</label>
          <input type="password" class="form-control" id="password" name="password" required>
          <div class="link">
            <router-link class="link" to="/register">Não possui cadastro? Registre-se agora.</router-link>
          </div>
        </div>
        <div>
          <button type="submit" @click.prevent="login" class="btn login-btn text-white w-100">Entrar</button>
        </div>
      </form>
    </ContainerBox>
  </div>
</template>

<style scoped>
.container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.link {
  color: #6366f1;
  padding-top: 20px;
}

.login-btn {
  background-color: #6366f1;
}
</style>