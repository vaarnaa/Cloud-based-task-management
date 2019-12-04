const Joi = require('@hapi/joi');

// Google Endpoints doesn't provide full-fledged schema validation (https://cloud.google.com/endpoints/docs/openapi/openapi-limitations)
// so should validate on our end

const user = Joi.object({
  id: Joi.string().required(),
});

const taskStatus =
  Joi.string().valid('pending', 'on-going', 'completed');

const projectBody = Joi.object({
  name: Joi.string().required(),
  description: Joi.string().required(),
  deadline: Joi.date().required(),
});

const taskAttributesBody = Joi.object({
  description: Joi.string().required(),
  status: taskStatus.default('pending'),
  deadline: Joi.date().required(),
});

const taskStatusBody = Joi.object({
  taskStatus: taskStatus.required(),
});

const usersBody = Joi.object({
  users: Joi.array().items(user).required(),
});

const membersBody = Joi.object({
  // "it can contain one or more users"
  members: Joi.array().items(user).min(1).required(),
});

module.exports = {
  projectBody,
  taskAttributesBody,
  taskStatusBody,
  usersBody,
  membersBody,
};
