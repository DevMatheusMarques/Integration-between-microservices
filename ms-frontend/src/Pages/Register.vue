<script>
import ContainerBox from "../components/ContainerBox.vue";
import Navbar from "../components/Navbar.vue";
import Swal from "sweetalert2";
import {axios} from "../main.js";

export default {
  name: "Register",
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
    async store() {
      try {
        const form = new FormData(document.getElementById("form-store"));
        const newUser = {
          username: form.get("username"),
          password: form.get("password"),
          email: form.get("email"),
          cep: form.get("cep")
        }

        const response = await axios.post("/user/register", newUser);
        if (response.status !== 201) {
          throw new Error('Houve um erro ao cadastrar um novo usuário. Entre em contato com o adm do sistema.')
        }
        await this.toast.fire({
          icon: "success",
          title: "Usuário cadastrado com sucesso",
          timer: 2000
        });
        this.$router.push('/auth');
      } catch (error) {
        await this.toast.fire({
          icon: "error",
          title: "Falha ao cadastrar",
          text: "Erro ao cadastrar usuário. Por favor tente novamente."
        });
        this.$router.push('/register');
      }
    }
  }
}
</script>

<template>
  <div class="container">
    <ContainerBox>
      <img src="../../public/assets/imgs/logotype.png" alt="Logotype Enterprise" style="width: 400px">
      <div>
        <h2 class="text-center">Cadastre-se!</h2>
        <p class="text-center">Informe seus dados para concluir o cadastro.</p>
      </div>
      <form id="form-store" class="w-100 d-flex flex-column gap-4" method="post" action="/">
        <div>
          <label for="username" class="form-label">Nome:</label>
          <input type="text" class="form-control" id="username" name="username" autofocus required>
        </div>
        <div>
          <label for="email" class="form-label">Email:</label>
          <input type="email" class="form-control" id="email" name="email" required>
        </div>
        <div>
          <label for="password" class="form-label">Senha:</label>
          <input type="password" class="form-control" id="password" name="password" required>
        </div>
        <div>
          <label for="cep" class="form-label">CEP:</label>
          <input type="text" class="form-control" id="cep" name="cep" required>
          <div class="link">
            <router-link class="link" to="/auth">Já possui cadastro ? Fazer login.</router-link>
          </div>
        </div>
        <div>
          <button @click="store" type="button" class="btn register-btn text-white w-100">Registrar</button>
        </div>
      </form>
    </ContainerBox>
  </div>
</template>
<style scoped>
.main-container {
  width: 489px;
  height: 800px;
}

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

.register-btn {
  background-color: #6366f1;
}
</style>