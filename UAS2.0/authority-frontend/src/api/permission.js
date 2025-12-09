import request from '../api/request' // 假设你的 request 实例路径正确

/**
 * 1. 获取所有权限列表
 * GET /api/permission/list
 */
export function getPermissionList() {
    return request({
        url: '/api/permission/list',
        method: 'get'
    })
}

/**
 * 2. 获取指定角色已拥有的权限 ID 列表
 * GET /api/role/getFunctionsByRole/{roleId}
 */
export function getRoleFunctions(roleId) {
    return request({
        url: `/api/role/getFunctionsByRole/${roleId}`,
        method: 'get'
    })
}

/**
 * 3. 保存权限分配结果
 * POST /api/role/assignFunction
 */
export function assignRolePermissions(data) {
    // data 格式: {roleId: 1, functionIds: [1, 2, 3]}
    return request({
        url: '/api/role/assignFunction',
        method: 'post',
        data: data
    })
}


/**
 * 新增或编辑权限节点
 * POST /api/permission/saveOrUpdate
 */
export function saveOrUpdatePermission(data) {
    return request({
        url: '/api/permission/saveOrUpdate',
        method: 'post',
        data: data
    })
}

/**
 * 删除权限及其子权限
 * DELETE /api/permission/delete/{id}
 */
export function deletePermission(permissionId) {
    return request({
        url: `/api/permission/delete/${permissionId}`,
        method: 'delete'
    })
}