const { database } = require('../db');
const { taskAttributesBody } = require('../schemas');
const { getProjectAdmin, getProjectMembers, PROJECT_ROOT } = require('../refs');
const log = require('../log');

const TaskController = {

  // Create a new task in project
  // POST /project/{id}/task
  async createTask(req, res) {
    const { error, value } = taskAttributesBody.validate(req.body);
    if (error) {
      return res.status(422).json({ code: 422, message: error });
    }

    const rootRef = `${PROJECT_ROOT}/${req.params.project_id}/tasks`;
    const taskId = database.ref().child(rootRef).push().key;
    const updates = { [`${rootRef}/${taskId}`]: value };

    const values = await database.ref().update(updates);

    log.debug(`TaskController.createTask: created: ${taskId} with: ${JSON.stringify(updates)}`);
    res.status(201).json({ task_id: taskId });
  },

  async assignTask(req, res) {
    const { error } = taskAttributesBody.validate(req.body);
    if (error) {

    }
  },

  // Update a task in project (status, assignment)
  // PUT /project/{id}/task/{id}
  updateTask(req, res) {

  },

/*
  // Get list of tasks
  // GET /project/{id}/tasks
  getAllTasks(req, res) {
    // const projects = firebase.database().ref().child('projects');
    // res.status(200).json({ status: 'ok', data: projects });
  },
*/
};

module.exports = TaskController;
