'use strict'
const log = require('./log')
const { database } = require('./db')

const PROJECT_ROOT = '/projects'

const resolveRef = async (ref) => {
    const snapshot = await database.ref(ref).once('value')
    const val = snapshot.val()
    log.debug(`Resolved value for ref ${ref}: ${val}`)
    return val
}

const getProjectAdmin = (projectId) => resolveRef(`${PROJECT_ROOT}/${projectId}/admin`)
const getProjectMembers = (projectId) => resolveRef(`${PROJECT_ROOT}/${projectId}/members`)
const isGroupProject = async (projectId) => {
    const val = await resolveRef(`${PROJECT_ROOT}/${projectId}/type`)
    return val === 'group'
}

const getProject = (projectId) => resolveRef(`${PROJECT_ROOT}/${projectId}`)

const belongsToProject = async (userId, projectId) => {
    const { admin, members } = await getProject(projectId)
    return admin === userId || members.find(member => member === userId)
}

const notBelongsToProject = async (userId, projectId) => !(await belongsToProject(userId, projectId))

module.exports = {
    resolveRef,
    getProjectMembers,
    getProjectAdmin,
    isGroupProject,
    belongsToProject,
    notBelongsToProject,
    getProject,
    PROJECT_ROOT,
}
