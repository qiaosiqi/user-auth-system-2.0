import request from './request'
import qs from 'qs' // 用于将对象序列化为 x-www-form-urlencoded 格式

/**
 * 用户登录接口
 * @param {string} username
 * @param {string} password
 * @returns {Promise<any>}
 */
export function userLogin(username, password) {
    // 注意：后端要求使用 x-www-form-urlencoded 格式，我们需要使用 qs 库进行转换
    const data = {
        username,
        password
    }

    return request({
        url: '/api/user/login',
        method: 'post',
        // 强制使用 form-urlencoded 格式的请求头
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        // 将对象转换为 "username=xxx&password=xxx" 字符串
        data: qs.stringify(data)
    })
}

// --- 用户管理接口 ---

/**
 * 获取用户列表 (支持分页和查询)
 * GET /api/user/list?pageNum=1&pageSize=10&username=xxx
 * @param {object} params 查询参数
 */
export function getUserList(params) {
    return request({
        url: '/api/user/list',
        method: 'get',
        // 使用 params 属性将对象转换为查询字符串
        params: params
    })
}

/**
 * 新增或更新用户
 * POST /api/user/save
 * @param {object} data 用户数据 (包含 roleId, username, password 等)
 */
export function saveOrUpdateUser(data) {
    return request({
        url: '/api/user/saveOrUpdate',
        method: 'post',
        data: data
    })
}

/**
 * 删除用户
 * DELETE /api/user/delete/{userId}
 * @param {number} userId 用户ID
 */
export function deleteUser(userId) {
    return request({
        url: `/api/user/delete/${userId}`, // 路径是 /api/user/delete/{userId}
        method: 'delete'
    })
}

/**
 * 获取指定用户当前拥有的角色ID (单个角色)
 * GET /api/user/roles/{userId}
 */
export function getUserRoleId(userId) {
    return request({
        url: `/api/user/roles/${userId}`,
        method: 'get'
    })
}

/**
 * 为用户分配角色
 * POST /api/user/assignRole
 * @param {object} data {userId: 1, roleId: 2}
 */
export function assignUserRole(data) {
    return request({
        url: '/api/user/assignRole',
        method: 'post',
        data: data
    })
}

// /**
//  * 获取所有角色列表 (供 Select 选择使用)
//  * GET /api/role/list
//  */
// export function getRoleList() {
//     return request({
//         url: '/api/role/list',
//         method: 'get'
//     })
// }