<script setup>
import { reactive, ref, watch } from 'vue'
import { getPermissionList, getRoleFunctions, assignRolePermissions } from '../api/permission' // 🚨 假设的 API 接口
import { ElMessage } from 'element-plus'

// --- Props 和 Emits ---
const props = defineProps({
  visible: { type: Boolean, default: false },
  roleId: { type: [Number, String], default: null }, // 当前要分配权限的角色ID
  roleName: { type: String, default: '' } // 角色名称（仅显示）
})

const emit = defineEmits(['update:visible', 'success'])

// --- 状态 ---
const loading = ref(false)
const treeRef = ref(null)             // 权限树组件的引用
const permissionList = ref([])        // 所有的权限数据 (扁平化或树形结构)
const defaultCheckedKeys = ref([])    // 当前角色已拥有的权限ID列表

// --- Tree 组件配置 ---
const defaultProps = {
  children: 'children', // 假设后端返回的数据有 children 字段
  label: 'functionName', // 权限名称
  id: 'id' // 权限 ID
}

// --- 数据获取与加载逻辑 ---

// 1. 获取所有权限列表 (权限树的基础数据)
const fetchAllPermissions = async () => {
  // 假设 /api/permission/list 返回扁平数据，这里需要转换成树形结构
  // **注意:** 如果后端直接返回树形结构，则不需要手动转换
  try {
    const res = await getPermissionList()
    permissionList.value = convertListToTree(res.data) // 🚨 假设存在一个转换函数
  } catch (error) {
    ElMessage.error('加载权限列表失败')
  }
}

// 2. 获取当前角色已有的权限
const fetchRolePermissions = async (id) => {
  if (!id) return
  try {
    const res = await getRoleFunctions(id) // 调用 /api/role/getFunctionsByRole/{roleId}
    defaultCheckedKeys.value = res.data || [] // 设置默认勾选的权限ID列表
  } catch (error) {
    ElMessage.error('加载角色权限失败')
  }
}

// 3. 核心转换函数 (如果后端返回扁平数据，需要此函数)
const convertListToTree = (list, parentId = 0) => {
  const tree = [];
  list.forEach(item => {
    if (item.parentId === parentId) {
      const children = convertListToTree(list, item.id);
      if (children.length) {
        item.children = children;
      }
      tree.push(item);
    }
  });
  return tree;
};


// --- 监听和初始化 ---
watch(() => props.visible, (newVal) => {
  if (newVal && props.roleId) {
    loading.value = true
    Promise.all([
      fetchAllPermissions(),
      fetchRolePermissions(props.roleId)
    ]).finally(() => {
      loading.value = false
    })
  } else if (!newVal) {
    // 关闭时重置状态
    defaultCheckedKeys.value = []
    treeRef.value?.setCheckedKeys([], false)
    emit('update:visible', false)
  }
})


// --- 提交逻辑 ---
const handleSubmit = async () => {
  if (!props.roleId) return ElMessage.warning('角色ID丢失')

  // 1. 获取当前所有勾选和半勾选的节点ID
  const halfCheckedKeys = treeRef.value.getHalfCheckedKeys()
  const checkedKeys = treeRef.value.getCheckedKeys()

  // 2. 合并所有需要提交的权限ID (通常提交所有全选和半选的ID)
  const functionIds = [...new Set([...checkedKeys, ...halfCheckedKeys])].map(Number)

  loading.value = true
  try {
    // 调用 /api/role/assignFunction 接口
    const params = {
      roleId: props.roleId,
      functionIds: functionIds
    }
    await assignRolePermissions(params)

    ElMessage.success(`角色 ${props.roleName} 权限分配成功！`)
    emit('success')
    emit('update:visible', false)

  } catch (error) {
    console.error('权限分配失败:', error)
    ElMessage.error('权限分配失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <el-dialog
      :model-value="visible"
      :title="`为角色 [${roleName}] 分配权限`"
      width="500px"
      @close="emit('update:visible', false)"
  >
    <div v-loading="loading" style="min-height: 200px;">
      <el-tree
          ref="treeRef"
          :data="permissionList"
          show-checkbox
          node-key="id"
          :default-checked-keys="defaultCheckedKeys"
          :props="defaultProps"
          empty-text="权限列表加载中或为空"
          default-expand-all
      />
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="emit('update:visible', false)">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          保存分配
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>