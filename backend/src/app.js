const env = require('./env');
const express = require('express');

const app = express();
app.use(express.json()); // parse JSON payload into `req.body`

// Initialize Database
const firebase = require('./db');

//
// ROUTES + MIDDLEWARE SETUP
//

const ctrls = require('./ctrls');

app.use((req, res, next) => {
    console.log(`DEBUG -- req.body: ${JSON.stringify(req.body)}`);
    console.log(`DEBUG -- req.query: ${JSON.stringify(req.query)}`);
    console.log(`DEBUG -- req.params: ${JSON.stringify(req.params)}`);
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
  console.error(err.stack)
  res.status(500).send('ERROR 500: Something broke!')
})

app.listen(env.port, () => {
  console.log(`[EXPRESS]\tListening on port ${env.port}`)
});

module.exports = app;
