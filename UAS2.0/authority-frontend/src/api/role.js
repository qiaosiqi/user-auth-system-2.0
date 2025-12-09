// src/api/role.js

import request from '../api/request' // 假设路径正确

/**
 * 1. 查询所有角色列表
 * GET /api/role/list
 */
export function getRoleList() {
    return request({
        url: '/api/role/list',
        method: 'get'
    })
}

/**
 * 2. 新增或更新角色 (角色 CRUD)
 * POST /api/role/save
 */
export function saveOrUpdateRole(data) {
    return request({
        url: '/api/role/save',
        method: 'post',
        data: data
    })
}

/**
 * 3. 删除角色 (角色 CRUD)
 * DELETE /api/role/delete/{id}
 */
export function deleteRole(roleId) {
    return request({
        url: `/api/role/delete/${roleId}`,
        method: 'delete'
    })
}