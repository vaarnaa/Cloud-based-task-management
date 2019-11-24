const { database } = require('../db');
const { projectBody } = require('../schemas');
const log = require('../log');

const ref_root = '/projects'

const ProjectController = {

  // Get a project
  // GET /projects/{id}
  getSingle(req, res) {
    log.debug("ProjectController.getSingle");

    // TODO: ensure user has access to this project!

    database.ref(`${ref_root}/${req.params.project_id}`).once('value')
      .then(snapshot => {
        const project = snapshot.val()
        log.debug(project);
        res.status(200).json(project);
      })
      .catch(err => {
        const msg = `Failed: ${errorObject.code}`;
        log.error(msg);
        res.status(400).json({ msg });
      });
  },

  // Get all projects user has access to
  getAll(req, res) {
    log.debug("ProjectController.getAll");

    const projects = database.ref().child(ref_root).once('value')
    .then(data => {
      // TODO: filter!
      res.status(200).json({ status: 'ok', data });
    })
    .catch(err => {

    });
  },

  // Create project
  // POST /project/{id}
  createProject(req, res) {
    log.debug("ProjectController.createProject");

    const { error } = projectBody.validate(req.body);
    if (error) {
      return res.status(422).json({ code: 422, message: error });
    }

    const newId = database.ref().child(ref_root).push().key;
    const updates = {};
    const newProject = {
      name: req.body.name,
      description: req.body.description,
      admin: req.auth_user.id,
      created: new Date(),
      deadline: new Date(req.body.deadline),
      badge: '', // url to image
      tasks: [],
      members: [],
      keywords: [],
      attachments: [],
    };
    updates[`${ref_root}/${newId}`] = newProject;

    database.ref().update(updates)
      .then(values => {
        log.debug(`ProjectController.createProject: created: ${newId} with: ${JSON.stringify(updates)}`);
        res.status(201).json({ project_id: newId });
      })
      .catch(err => {
        log.error('ProjectController.createProject: error');
        log.error(err);
        res.status(500).json({ status: 'internal error' });
      });
  },

  // Delete project
  // DELETE /project/{id}
  deleteProject(req, res) {
    log.debug("ProjectController.deleteProject");

    // TODO: delete also other related data!

    const ref = database.ref(`${ref_root}/${req.params.project_id}`);
    ref.remove()
      .then(data => {
        res.status(200).json(data);
      })
      .catch(err => {
        const msg = `Failed: ${err.code}`;
        log.error(msg);
        res.status(400).json({ msg });
      });
  },

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

  // Attach user to project
  // POST /project/{id}/members
  attachUser(req, res) {
    log.debug("ProjectController.attachUser");

    // TODO: ensure user is project's admin

    const ref = database.ref(`${ref_root}/${req.params.project_id}/members`);

    ref.set(req.body.members)
      .then(data => {
        res.status(200).json(data);
      })
      .catch(err => {
        const msg = `Failed: ${err.code}`;
        log.error(msg);
        res.status(400).json({ msg });
      });
  },
};

module.exports = ProjectController;
