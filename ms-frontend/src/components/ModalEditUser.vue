<script>
import axios from "axios";
import Swal from "sweetalert2";

export default {
  name: "ModalEditUser",
  props: {
    user: {
      type: Object,
      required: true
    }
  },
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
    async edit() {
      try {
        const form = new FormData(document.getElementById("form-edit"));
        const credenptials = {
          username: form.get("username"),
          oldPassword: form.get("oldPassword"),
          newPassword: form.get("newPassword"),
        }
        const token = localStorage.getItem('token');

        await axios.patch("/user/update/password", credenptials, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        await this.toast.fire({
          icon: "success",
          title: "Senha atualizada com sucesso",
          timer: 2000
        });
        this.closeModal();
        window.location.reload();
      } catch (error) {
        await this.toast.fire({
          icon: "error",
          title: "Falha ao atualizar",
          text: error.message
        });
      }
    }
  }
}
</script>

<template>
  <div class="modal fade" id="editModal" tabindex="-1" aria-labelledby="editModalLabel" aria-hidden="true">
    <div class="modal-overlay">
      <div class="modal-dialog">
        <div class="modal-content" style="width: 105%">
          <div class="modal-header">
            <h1 class="modal-title fs-5" id="editModalLabel">Editando senha do usuário - {{user.username}}</h1>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form id="form-edit" class="w-100 d-flex flex-column gap-4">
              <div class="mb-4">
                <label for="username" class="form-label">Username:</label>
                <input type="text" class="form-control" id="username" name="username" required :value="user.username">
              </div>
              <div class="mb-4">
                <label for="oldPassword" class="form-label">Senha Atual:</label>
                <input type="password" class="form-control" id="oldPassword" name="oldPassword" required>
              </div>
              <div class="mb-4">
                <label for="newPassword" class="form-label">Nova Senha:</label>
                <input type="password" class="form-control" id="newPassword" name="newPassword" required>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
            <button type="button" class="btn btn-success" @click="edit">Salvar nova senha</button>
          </div>
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