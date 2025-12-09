// src/views/PermissionNodeDialog.vue (简化版，核心实现)

<script setup>
import { ref, watch } from 'vue'
import { saveOrUpdatePermission } from '../api/permission'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: { type: Boolean, default: false },
  isEditMode: { type: Boolean, default: false },
  // 传入当前编辑节点数据或新增节点的父节点信息
  initialData: {
    type: Object,
    default: () => ({ id: null, parentId: 0, functionName: '', functionCode: '', path: '', component: '', type: 1, sortNum: 0 })
  }
})

const emit = defineEmits(['update:visible', 'success'])

// ... (省略状态和 watch 逻辑，与 RoleDialog 类似，用于初始化 form 和 title)

// 表单数据和校验规则 (必须包含所有 SysPermission 字段)
const permissionForm = ref({ ...props.initialData })
const formRef = ref(null)
const formRules = {
  functionName: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  functionCode: [{ required: true, message: '请输入权限标识', trigger: 'blur' }],
  type: [{ required: true, message: '请选择权限类型', trigger: 'change' }]
}

// 权限类型选项
const typeOptions = [
  { value: 1, label: '目录' },
  { value: 2, label: '菜单' },
  { value: 3, label: '按钮' }
]

const handleSubmit = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      // ... 调用 saveOrUpdatePermission 并处理成功/失败
      try {
        await saveOrUpdatePermission(permissionForm.value)
        ElMessage.success('保存成功')
        emit('success')
        emit('update:visible', false)
      } catch (e) {
        ElMessage.error('保存失败')
      }
    }
  })
}
</script>

<template>
  <el-dialog
      :model-value="visible"
      :title="isEditMode ? '编辑权限' : '新增权限'"
      width="500px"
      @close="emit('update:visible', false)"
  >
    <el-form ref="formRef" :model="permissionForm" :rules="formRules" label-width="100px">
      <el-form-item label="权限类型" prop="type">
        <el-select v-model="permissionForm.type" placeholder="请选择类型">
          <el-option
              v-for="item in typeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="名称" prop="functionName">
        <el-input v-model="permissionForm.functionName" />
      </el-form-item>
      <el-form-item label="标识" prop="functionCode">
        <el-input v-model="permissionForm.functionCode" placeholder="如：sys:user:add" />
      </el-form-item>
      <template v-if="permissionForm.type !== 3">
        <el-form-item label="路由路径" prop="path">
          <el-input v-model="permissionForm.path" />
        </el-form-item>
        <el-form-item label="组件路径" prop="component">
          <el-input v-model="permissionForm.component" />
        </el-form-item>
      </template>
      <el-form-item label="排序号" prop="sortNum">
        <el-input-number v-model="permissionForm.sortNum" :min="0" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="emit('update:visible', false)">取消</el-button>
      <el-button type="primary" @click="handleSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>