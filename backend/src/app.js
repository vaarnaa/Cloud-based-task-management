const env = require('./env');
const express = require('express');
const log = require('./log');

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
    req.body.auth_user = JSON.parse(new Buffer(encodedInfo, 'base64'));
  }

  next();
})

app.get('/', (req, res) => {
  res.status(200).json({
    msg: `version: ${env.version}`
    });
});

// app.post('/', (req, res) => {
//   const { body } = req;
//   res.status(200).json(body);
// });

// app.get('/project', (req, res) => {

// });

// app.delete('/project/:projectId', (req, res) => {
//   console.log(req.params.projectId);
// });

// app.post('/project/:projectId/members', (req, res) => {

// });

app.get('/users/', ctrls.UserController.getAll);
app.put('/users/', ctrls.UserController.createUser);
// app.post('/users/', ctrls.UserController.asd2);
// app.delete('/users/', ctrls.UserController.asd);

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
