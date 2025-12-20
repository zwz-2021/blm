<template>
  <div class="home-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>系统首页</span>
        </div>
      </template>
      <div class="text item">
        <h1>欢迎来到 BLM Frontend 系统</h1>
        <p>这是一个基于 Vue 3 + TypeScript + Vite 的基础环境。</p>
        <el-button type="primary" @click="$router.push('/login')">去登录页面</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
// 这里可以写逻辑
</script>

<style scoped>
.home-container {
  padding: 20px;
  display: flex;
  justify-content: center;
}
.box-card {
  width: 480px;
}
</style>