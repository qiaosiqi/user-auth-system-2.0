// src/views/PermissionManagement.vue

<script setup>
import { ref, onMounted } from 'vue'
import { getPermissionList } from '../api/permission' // 确保导入正确的接口
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

import { deletePermission } from '../api/permission' // 🚨 确保导入删除接口
import PermissionNodeDialog from './PermissionNodeDialog.vue' // 🚨 新增导入

// --- 状态 ---
const loading = ref(false)
const permissionTree = ref([]) // 存储树形结构数据
const expandedKeys = ref([]) // 默认展开的节点 keys

// // 权限新增/编辑弹窗状态 (待创建)
// const dialogVisible = ref(false)
// const isEditMode = ref(false)

// 🚨 权限新增/编辑弹窗状态 (更新)
const dialogVisible = ref(false)
const isEditMode = ref(false)
const currentEditNode = ref({}) // 用于传递给弹窗的数据


// --- 数据转换：将扁平列表转为树形结构 ---
// ⚠️ 确保这个函数与你在 PermissionDialog.vue 中使用的版本一致
const convertListToTree = (list, parentId = 0) => {
  const tree = [];
  list.forEach(item => {
    if (item.parentId === parentId) {
      const children = convertListToTree(list, item.id);
      if (children.length) {
        item.children = children;
      }
      // 默认展开所有一级节点
      if (parentId === 0) {
        expandedKeys.value.push(item.id)
      }
      tree.push(item);
    }
  });
  return tree;
};


// --- 数据获取：加载权限树 ---
const fetchPermissionTree = async () => {
  loading.value = true
  try {
    const res = await getPermissionList() // 调用 /api/permission/list
    const list = res.data || []

    // 🚨 关键：将扁平列表转换为树形结构
    permissionTree.value = convertListToTree(list)
  } catch (error) {
    ElMessage.error('加载权限菜单失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// --- 操作函数实现 ---
const handleAdd = (row) => {
  isEditMode.value = false
  dialogVisible.value = true
  // 如果 row.id 是 0，则新增一级目录；否则新增子级
  currentEditNode.value = {
    id: null,
    parentId: row.id || 0, // 设置父节点ID
    functionName: '', functionCode: '', path: '', component: '', type: 1, sortNum: 0
  }
}

const handleEdit = (row) => {
  isEditMode.value = true
  dialogVisible.value = true
  // 传递当前节点数据用于编辑
  currentEditNode.value = { ...row }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
      `确定要删除权限 [${row.functionName}] 及其所有子权限吗?`,
      '警告',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(async () => {
        try {
          await deletePermission(row.id) // 调用删除 API
          ElMessage.success('权限节点删除成功！')
          fetchPermissionTree() // 刷新列表
        } catch (error) {
          ElMessage.error('删除失败')
        }
      })
      .catch(() => {
        // 用户取消
      })
}

onMounted(() => {
  fetchPermissionTree()
})
</script>

<template>
  <div class="permission-management-container">
    <el-card shadow="never">
      <div class="header-container" style="margin-bottom: 20px;">
        <el-button type="primary" :icon="Plus" @click="handleAdd({id: 0})">新增一级菜单</el-button>
      </div>

      <el-table
          :data="permissionTree"
          v-loading="loading"
          row-key="id"
          :default-expand-all="false"
          :tree-props="{ children: 'children' }"
          style="width: 100%;"
      >
        <el-table-column prop="functionName" label="菜单/权限名称" width="300" />
        <el-table-column prop="functionCode" label="权限标识" width="250" />
        <el-table-column prop="path" label="路由路径" width="180" />
        <el-table-column prop="component" label="组件路径" width="180" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.type === 1" type="success">目录</el-tag>
            <el-tag v-else-if="row.type === 2" type="primary">菜单</el-tag>
            <el-tag v-else type="info">按钮</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
                link
                type="primary"
                size="small"
                v-if="row.type !== 3"
                @click="handleAdd(row)"
            >
              新增子级
            </el-button>
            <el-button link type="warning" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

    </el-card>
  </div>

  <PermissionNodeDialog
      v-model:visible="dialogVisible"
      :is-edit-mode="isEditMode"
      :initial-data="currentEditNode"
      @success="fetchPermissionTree"
  />

</template>