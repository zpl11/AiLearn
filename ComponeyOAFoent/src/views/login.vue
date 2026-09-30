<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login, type LoginDTO } from '@/api/auth'

const router = useRouter()

// --- 1. 表单状态与引用 ---
const loginFormRef = ref<FormInstance>()
const loading = ref(false)

const loginFormData = reactive<LoginDTO>({
  userName: '',
  password: '',
})

// --- 2. 表单校验规则（防御性拦截） ---
const loginRules: FormRules = {
  userName: [
    { required: true, message: '请输入登录账号', trigger: 'blur' },
    { whitespace: true, message: '账号不能包含全空格', trigger: 'blur' },
    { min: 2, max: 30, message: '账号长度在 2 到 30 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入登录密码', trigger: 'blur' },
    { min: 5, max: 30, message: '密码长度不能少于 5 位', trigger: 'blur' },
  ],
}

// --- 3. 提交登录 ---
const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await login({
        userName: loginFormData.userName.trim(),
        password: loginFormData.password.trim(),
      })

      // 1. 持久化 Token 与鉴权凭证
      localStorage.setItem('TOKEN', res.token)
      localStorage.setItem('USER_INFO', JSON.stringify({
        userId: res.userId,
        userName: res.userName,
        nickName: res.nickName,
      }))
      localStorage.setItem('ROLES', JSON.stringify(res.roles || []))
      localStorage.setItem('PERMISSIONS', JSON.stringify(res.permissions || []))

      ElMessage.success(`欢迎回来，${res.nickName || res.userName}`)

      // 2. 路由平滑跳转
      await router.push('/')
    } catch (error) {
      // 错误信息已被 request.ts 统一拦截提示
    } finally {
      loading.value = false
    }
  })
}

// 重置表单
const handleReset = () => {
  loginFormData.userName = ''
  loginFormData.password = ''
  nextTick(() => {
    loginFormRef.value?.clearValidate()
  })
}
</script>

<template>
  <div class="login-layout">
    <div class="login-container">
      <el-card class="login-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span class="system-title">企业 OA 办公协同系统</span>
            <span class="system-subtitle">System Authentication Platform</span>
          </div>
        </template>

        <el-form
            ref="loginFormRef"
            :model="loginFormData"
            :rules="loginRules"
            label-position="top"
            class="login-form"
            @keyup.enter="handleLogin"
        >
          <el-form-item label="登录账号" prop="userName">
            <el-input
                v-model.trim="loginFormData.userName"
                placeholder="请输入管理员 / 员工账号"
                :prefix-icon="User"
                clearable
                size="large"
            />
          </el-form-item>

          <el-form-item label="登录密码" prop="password">
            <el-input
                v-model.trim="loginFormData.password"
                type="password"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                show-password
                clearable
                size="large"
            />
          </el-form-item>

          <div class="form-actions">
            <el-button
                type="primary"
                size="large"
                :loading="loading"
                class="submit-btn"
                @click="handleLogin"
            >
              登 录
            </el-button>
            <el-button
                size="large"
                class="reset-btn"
                @click="handleReset"
            >
              重置
            </el-button>
          </div>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<style scoped lang="scss">
.login-layout {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
  background-color: #f0f2f5;
  box-sizing: border-box;

  .login-container {
    width: 420px;
    padding: 20px;

    .login-card {
      border-radius: 8px;
      border: 1px solid var(--el-border-color-light);

      .card-header {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 6px;
        padding: 8px 0;

        .system-title {
          font-size: 20px;
          font-weight: 600;
          color: var(--el-text-color-primary);
        }

        .system-subtitle {
          font-size: 12px;
          color: var(--el-text-color-secondary);
        }
      }

      .login-form {
        margin-top: 12px;

        .form-actions {
          display: flex;
          gap: 12px;
          margin-top: 28px;

          .submit-btn {
            flex: 1;
          }

          .reset-btn {
            width: 80px;
          }
        }
      }
    }
  }
}
</style>