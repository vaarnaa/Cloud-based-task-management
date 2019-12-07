'use strict'
const Joi = require('@hapi/joi')

// Google Endpoints doesn't provide full-fledged schema validation
// https://cloud.google.com/endpoints/docs/openapi/openapi-limitations
// so should validate on our end


const user = Joi.object({
    id: Joi.string().required(),
})


// const userId = Joi.string().required()

const taskStatus =
  Joi.string().valid('pending', 'on-going', 'completed')

const projectType =
  Joi.string().valid('personal', 'group')

const projectBody = Joi.object({
    name: Joi.string().required(),
    type: projectType.required(),
    description: Joi.string().required(),
    deadline: Joi.date().optional(),
    badge: Joi.string().optional(),
    keywords: Joi.array().items(Joi.string()).max(3).optional()
})

const taskAttributesBody = Joi.object({
    description: Joi.string().required(),
    status: taskStatus.default('pending'),
    deadline: Joi.date().required(),
})

const taskStatusBody = Joi.object({
    taskStatus: taskStatus.required(),
})

const membersBody = Joi.object({
    // "it can contain one or more users"
    members: Joi.array().items(user).min(1).required(),
})

const assignTaskBody = Joi.object({
    assignments: Joi.array().items(user).required(),
})

module.exports = {
    projectBody,
    taskAttributesBody,
    taskStatusBody,
    assignTaskBody,
    membersBody,
}
