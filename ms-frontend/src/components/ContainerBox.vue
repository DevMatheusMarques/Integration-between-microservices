<script>
export default {
  name: "ContainerBox"
}
</script>

<template>
  <main class="container-fluid border border-secondary-subtle rounded-3 d-flex align-items-center justify-content-center flex-column gap-4 shadow p-5 main-container">
    <slot />
  </main>
</template>

<style scoped>
.main-container {
  width: 489px;
  height: 580px;
}
</style>