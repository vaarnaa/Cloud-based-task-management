'use strict'
const { database } = require('../db')
const { projectBody, membersBody } = require('../schemas')
const {
    getProjectAdmin, getProjectMembers,
    PROJECT_ROOT, setModifiedToNow,
} = require('../refs')
const log = require('../log')

const ref_root = PROJECT_ROOT

const projectNotFoundResponse = () => ({
    code: 404,
    message: 'Project not found'
})

const ProjectController = {
    // GET /projects/{id}
    async getSingle(req, res) {
        log.debug('ProjectController.getSingle')
        // TODO: ensure user has access to this project!
        try {
            const snapshot = await database.ref(`${ref_root}/${req.params.project_id}`).once('value')
            const project = snapshot.val()
            log.debug(project)
            return res.status(200).json(project)
        } catch (e) {
            const msg = `Failed: ${e.code}`
            log.error(msg)
            return res.status(400).json({code: 400, message: msg})
        }
    },
    // Get all projects user has access to
    async getAll(req, res) {
        log.debug('ProjectController.getAll')
        const projects = await database.ref().child(ref_root).once('value')
        // TODO: filter by user has access to this project!
        return res.status(200).json({ status: 'ok', projects })
    },
    // Create project
    // POST /project/{id}
    async createProject(req, res) {
        log.debug('ProjectController.createProject')

        const { error, value } = projectBody.validate(req.body)
        if (error) {
            return res.status(422).json({ code: 422, message: error })
        }

        const newId = database.ref().child(ref_root).push().key
        const updates = {}
        const newProject = {
            name: value.name,
            description: value.description,
            admin: req.auth_user.id,
            type: value.type,
            created: new Date(),
            modified: new Date(),
            deadline: value.deadline && new Date(value.deadline) || null,
            badge: value.badge || null, // url to image
            tasks: { },
            members: [],
            keywords: value.keywords || [],
            attachments: { },
            events: { },
        }
        updates[`${ref_root}/${newId}`] = newProject

        const values = await database.ref().update(updates)

        log.debug(`ProjectController.createProject: created: ${newId} with: ${JSON.stringify(values)}`)
        res.status(201).json({ project_id: newId })
    },

    // Delete project
    // DELETE /project/{id}
    async deleteProject(req, res) {
        log.debug('ProjectController.deleteProject')

        const admin = await getProjectAdmin(req.params.project_id)

        if (!admin) {
            return res.status(404).json(projectNotFoundResponse())
        }

        if (req.auth_user.id != admin) {
            return res.status(403).json({ code: 403, message: 'Forbidden operation'})
        }

        // TODO: delete also other related data!
        const data = await database.ref(`${ref_root}/${req.params.project_id}`).remove()
        res.status(200).json(data)
    },

    /*
  // Get list of attachments
  // GET /project/{id}/attachments
  getAttachments(req, res) {
    log.debug("ProjectController.getAttachments");

    // TODO: ensure user has access to this project!

    database.ref(`${ref_root}/${req.params.project_id}/attachments`).once('value')
      .then(snapshot => {
        const project = snapshot.val()
        log.debug(project);
        res.status(200).json(project);
      })
      .catch(err => {
        const msg = `Failed: ${err.code}`;
        log.error(msg);
        res.status(400).json({ msg });
      });
  },
  */

    // Attach users to project
    // PUT /project/{id}/members
    async attachUsers(req, res) {
        log.debug('ProjectController.attachUsers')

        const { error } = membersBody.validate(req.body)
        if (error) {
            return res.status(422).json({ code: 422, message: error })
        }

        const admin = await getProjectAdmin(req.params.project_id)
        const members = await getProjectMembers(req.params.project_id)

        if (!admin) {
            return res.status(404).json(projectNotFoundResponse())
        }

        if (admin !== req.auth_user.id && !members.find(member => member === req.auth_user.id)) {
            return res.status(403).json({ code: 403, message: 'Forbidden operation' })
        }

        const data = await database.ref(
            `${ref_root}/${req.params.project_id}/members`
        ).set(req.body.members.map(member => member.id))

        await setModifiedToNow(req.params.project_id)

        res.status(200).json(data)
    },
}

module.exports = ProjectController
