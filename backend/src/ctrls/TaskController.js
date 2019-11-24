const { database } = require('../db');
const { taskAttributesBody } = require('../schemas');
const log = require('../log');

const TaskController = {

  // Create a new task in project
  // POST /project/{id}/task
  createTask(req, res) {
    const { error } = taskAttributesBody.validate(req.body);
    if (error) {
      return res.status(422).json({ code: 422, message: error });
    }
  },

  // Update a task in project (status, assignment)
  // PUT /project/{id}/task/{id}
  updateTask(req, res) {

  },

  // Get list of tasks
  // GET /project/{id}/tasks
  getAllTasks(req, res) {
    // const projects = firebase.database().ref().child('projects');
    // res.status(200).json({ status: 'ok', data: projects });
  },

};

module.exports = TaskController;
