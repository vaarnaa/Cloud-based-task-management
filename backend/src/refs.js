'use strict'
const log = require('./log')
const { database } = require('./db')

const PROJECT_ROOT = '/projects'
const USER_ROOT    = '/users'

const resolveRef = async (ref) => {
    const snapshot = await database.ref(ref).once('value')
    const val = snapshot.val()
    log.debug(`Resolved value for ref ${ref}: ${JSON.stringify(val)}`)
    return val
}

const getProjectAdmin = async (projectId) => {
    const res = await resolveRef(`${PROJECT_ROOT}/${projectId}/admin`)
    log.debug(`Fetched project ${projectId} admin: ${JSON.stringify(res)}`)
    return res
}

const getProjectMembers = async (projectId) => {
    const res = await resolveRef(`${PROJECT_ROOT}/${projectId}/members`)
    return (res && Object.keys(res)) || []
}
const isGroupProject = async (projectId) => {
    const val = await resolveRef(`${PROJECT_ROOT}/${projectId}/type`)
    return val === 'group'
}

const getProject = (projectId) => resolveRef(`${PROJECT_ROOT}/${projectId}`)

const belongsToProject = async (userId, projectId) => {
    const { admin, members } = await getProject(projectId) || { }
    return admin === userId || ((members && Object.keys(members)) || []).find(member => member === userId)
}

const notBelongsToProject = async (userId, projectId) => !(await belongsToProject(userId, projectId))

// Set /projects/<id>/modified to current time
// TODO: move elsewhere
const setModifiedToNow = async (projectId) => {
    const ref = `${PROJECT_ROOT}/${projectId}/modified`
    await database.ref().update({ [ref]: new Date() })
}

const taskExists = async (projectId, taskId) => {
    const ref = `${PROJECT_ROOT}/${projectId}/tasks/${taskId}`
    const task = await resolveRef(ref)
    return task != null
}

module.exports = {
    resolveRef,
    getProjectMembers,
    getProjectAdmin,
    isGroupProject,
    belongsToProject,
    notBelongsToProject,
    taskExists,
    getProject,
    USER_ROOT,
    setModifiedToNow,
    PROJECT_ROOT,
}
