'use strict';

const express = require('express');
const bodyParser = require('body-parser');
const app = express();
app.use(bodyParser.json())

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`Listening on port ${PORT}`)
});

app.post('/', (req, res) => {
  const { body } = req;


  res.status(200).json(body);
});

app.get('/project', (req, res) => {

});

app.delete('/project/:projectId', (req, res) => {
  console.log(req.params.projectId);
});

app.post('/project/:projectId/members', (req, res) => {

});

module.exports = app;
