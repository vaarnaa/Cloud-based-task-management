const env = require('./env');
const express = require('express');
const log = require('./log');
const base64url = require('base64url');

const app = express();
app.use(express.json()); // parse JSON payload into `req.body`

// Initialize Database
const { admin } = require('./db');

//
// ROUTES + MIDDLEWARE SETUP
//

const ctrls = require('./ctrls');

// Debug middleware
if (env.debug) {
    app.use((req, res, next) => {
        log.debug(`------------------`);
        log.debug(`-- req.url ${req.originalUrl}`);
        log.debug(`-- req.body: ${JSON.stringify(req.body)}`);
        log.debug(`-- req.query: ${JSON.stringify(req.query)}`);
        log.debug(`-- req.params: ${JSON.stringify(req.params)}`);
        return next();
    })
}

// Parse EndPoints header for the authenticated user
app.use((req, res, next) => {
  const encodedInfo = req.get('X-Endpoint-API-UserInfo');
  if (encodedInfo) {
    const decodedInfo = base64url.decode(encodedInfo);
    req.auth_user = JSON.parse(base64url.decode(encodedInfo));
  }

  next();
})

app.get('/', (req, res) => {
  res.status(200).json({
    msg: `version: ${env.version}`
    });
});

// Project related
app.get('/projects/', ctrls.ProjectController.getAll);
app.post('/project/', ctrls.ProjectController.createProject);
app.get('/project/:project_id', ctrls.ProjectController.getSingle);
app.delete('/project/:project_id', ctrls.ProjectController.deleteProject);

// User/member related
app.post('/project/:project_id/members', ctrls.ProjectController.attachUser);

// Task related
// app.get('/project/:project_id/tasks', ctrls.TaskController.getAllTasks);
// app.post('/project/:project_id/task', ctrls.TaskController.createTask);
// app.put('/project/:project_id/task/:task_id', ctrls.TaskController.updateTask);

// Attachment related
app.get('/project/:project_id/attachments', ctrls.ProjectController.getAttachments);
// app.post('/project/:project_id/attachment', ctrls.ProjectController.submitAttachment);




// Generic

app.use(function (req, res, next)
{
  res.status(404).send("ERROR 404: Sorry can't find that!")
})

app.use(function (err, req, res, next)
{
  log.error(err.stack)
  res.status(500).send('ERROR 500: Something broke!')
})

app.listen(env.port, () => {
  log.info(`Express listening on port ${env.port}`)
});

module.exports = app;
