<script setup>
import { reactive, ref, watch } from 'vue'
import { saveOrUpdateUser } from '../api/user'
import { ElMessage } from 'element-plus'

// --- Props 和 Emits ---
const props = defineProps({
  visible: { type: Boolean, default: false }, // 控制弹窗可见性
  isEdit: { type: Boolean, default: false },   // 判断是新增(false)还是编辑(true)
  initialData: { type: Object, default: () => ({}) } // 编辑时传入的初始数据
})

const emit = defineEmits(['update:visible', 'success'])

// --- 状态与表单 ---
const formRef = ref(null)

// 默认用户数据结构
const defaultFormData = {
  id: null,
  username: '',
  password: '', // 新增时必填，编辑时不填则不修改
  roleId: 1 // 默认为普通用户角色，根据实际情况调整
}
const formData = reactive({ ...defaultFormData })
const loading = ref(false)

// 表单校验规则
const rules = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    {
      required: !props.isEdit, // 新增时密码必填
      message: '请输入密码',
      trigger: 'blur'
    },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }]
})

// 假设的角色列表 (通常从后端获取，这里简化为静态)
const roleOptions = [
  { label: '管理员', value: 0 },
  { label: '普通用户', value: 1 }
]


// --- 监听和重置 ---
watch(() => props.visible, (newVal) => {
  if (newVal) {
    // 弹窗打开时，重置表单并加载初始数据
    if (formRef.value) {
      formRef.value.clearValidate()
    }
    Object.assign(formData, defaultFormData) // 重置

    // 如果是编辑模式，合并初始数据
    if (props.isEdit && props.initialData) {
      Object.assign(formData, props.initialData)
      formData.password = '' // 保证编辑时不填密码则不修改
    }
  } else {
    // 弹窗关闭时，通知父组件更新 visible 状态
    emit('update:visible', false)
  }
})

// --- 提交逻辑 ---
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const dataToSubmit = { ...formData }

        // 如果是编辑模式且密码为空，删除密码字段，避免修改密码
        if (props.isEdit && !dataToSubmit.password) {
          delete dataToSubmit.password
        }

        await saveOrUpdateUser(dataToSubmit)

        ElMessage.success(`${props.isEdit ? '编辑' : '新增'}用户成功！`)
        emit('success') // 通知父组件刷新列表
        emit('update:visible', false) // 关闭弹窗

      } catch (error) {
        // 错误信息已在 request.js 拦截器中处理
        console.error('提交失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<template>
  <el-dialog
      :model-value="visible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="500px"
      @close="emit('update:visible', false)"
  >
    <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="80px"
        v-loading="loading"
    >
      <el-form-item label="用户名" prop="username">
        <el-input v-model="formData.username" :disabled="isEdit && formData.id" />
      </el-form-item>

      <el-form-item label="密码" prop="password">
        <el-input
            v-model="formData.password"
            type="password"
            show-password
            :placeholder="isEdit ? '留空则不修改密码' : '请输入密码'"
        />
      </el-form-item>

      <el-form-item label="用户角色" prop="roleId">
        <el-select v-model="formData.roleId" placeholder="请选择角色">
          <el-option
              v-for="item in roleOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>

    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="emit('update:visible', false)">取消</el-button>
        <el-button type="primary" @click="handleSubmit">
          {{ isEdit ? '保存修改' : '立即创建' }}
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style scoped>
/* 样式 */
</style>