<script setup lang="ts">
import {RouterView} from 'vue-router'
</script>

<template>
  <RouterView/>
</template>

<style lang="scss">
//有一个能够改变盒子模型的属性，是 display: inline-block inline 等这些 css 属性
// box-sizing : content-box 实际宽度 = width + padding +border 设置了宽度 100px 加上 padding 20px 整个盒子就会被撑大到 120px 容易破坏布局
// 边框盒模型： box-sizing: border-box 现代前端行业标准， 书记宽度=width 设定的 100px 就包含了 padding 和 border 内部内容就会自动缩进。
*,
*::before,
*::after {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

html,
body,
#app {
  width: 100%;
  height: 100%;
}
</style>
