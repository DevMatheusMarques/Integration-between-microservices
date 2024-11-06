<script>
import Swal from "sweetalert2";
import {axios} from "../main.js";

export default {
  name: "ModalCreateUser",
  emits: ["close"],
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
    closeModal() {
      this.$emit("close");
    },
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
        this.closeModal();
        window.location.reload();
      } catch (error) {
        await this.toast.fire({
          icon: "error",
          title: "Erro ao cadastrar usuário. Por favor tente novamente.",
          text: error.message
        });
      }
    },
  },
};
</script>

<template>
  <div class="modal fade show d-block" tabindex="-1" aria-hidden="true">
    <div class="modal-overlay">
      <div class="modal-content w-25">
        <div class="modal-header">
          <h1 class="modal-title fs-5 ">Cadastrar Usuário</h1>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close" @click="closeModal"></button>
        </div>
        <div class="modal-body">
          <form id="form-store" class="w-100 d-flex flex-column gap-4">
            <div>
              <label for="username" class="form-label">Nome:</label>
              <input type="text" class="form-control" id="username" name="username" required>
            </div>
            <div>
              <label for="email" class="form-label">Email:</label>
              <input type="email" class="form-control" id="email" name="email" required>
            </div>
            <div>
              <label for="password" class="form-label">Senha:</label>
              <input type="password" class="form-control" id="password" name="password" required>
            </div>
            <div class="pb-4">
              <label for="cep" class="form-label">CEP:</label>
              <input type="tel" class="form-control" id="cep" name="cep" required>
            </div>
          </form>
        </div>
        <div class="modal-footer d-flex gap-2">
          <button type="button" class="btn btn-secondary " @click="closeModal">Fechar</button>
          <button @click="store" type="button" class="btn btn-success w-20">Registrar</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
</style>
