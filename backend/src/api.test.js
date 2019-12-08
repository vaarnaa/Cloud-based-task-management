'use strict'

//process.env.NODE_ENV = 'development'

const request = require('supertest')
const util = require('util')
const app = require('./app')

// This is not enough to shutdown Firebase (avoiding handle stuck after tests)
//const { database } = require('./db')
//afterAll(() => database.goOffline())

describe('Web server', () => {
    it('Front page', async () => {
        const res = await request(app).get('/').send()
        expect(res.statusCode).toEqual(200)
    })
})

const default_headers = {
    'x-endpoint-api-userinfo':
        'eyJpc3N1ZXIiOiJUT0tFTl9JU1NVRVIiLCJpZCI6Ikl3M0JtS2V6cjVoRWw5QUFIM2RkVm1IQWxzOTIiLCJlbWFpbCI6InRlbXBAZXhhbXBsZS5sb2NhbCJ9'
}

const describe_db = (process.env.GOOGLE_APPLICATION_CREDENTIALS ? describe : describe.skip)

describe_db('ProjectController', () => {
    let res, project_id
    it('Create new project', async () => {
        res = await request(app)
            .post('/project')
            .set(default_headers)
            .send({
                name: 'project_name1',
                description: 'desc',
                type: 'personal',
                deadline: 'Wed, 14 Jun 2020 07:00:00 GMT',
            })
        expect(res.statusCode).toEqual(201)
        expect(res.body).toHaveProperty('project_id')
        project_id = res.body.project_id
    })
    it.skip('Get project attachments', async () => {
        res = await request(app)
            .get(`/project/${project_id}/attachments`)
            .set(default_headers)
            .send()
        expect(res.statusCode).toEqual(200)
        expect(res.body.name).toEqual('project_name1')
    })
    it('Add members', async () => {
        res = await request(app)
            .post(`/project/${project_id}/members/`)
            .set(default_headers)
            .send({
                members: [
                    { id: 'ICXa2Dr4LeZTNyN9tv60pNXMjqC3' },
                    { id: 'QM9HKPoy9KffdUmP4JIWOem6zC93' },
                ]
            })
        expect(res.statusCode).toEqual(200)
    })
    it('Get that project', async () => {
        res = await request(app)
            .get(`/project/${project_id}`)
            .set(default_headers)
            .send()
        expect(res.statusCode).toEqual(200)
        expect(res.body.name).toEqual('project_name1')
    })
    it('Get all projects', async () => {
        res = await request(app)
            .get('/projects/')
            .set(default_headers)
            .send()
        expect(res.statusCode).toEqual(200)
    })
    it('Delete project', async () => {
        res = await request(app)
            .delete(`/project/${project_id}`)
            .set(default_headers)
            .send()
        expect(res.statusCode).toEqual(200)
    })
    afterEach(() => {
        if (res && res.body && res.body.message) {
            console.log(util.inspect(res.body.message, {depth: 10}))
        }
        res = undefined
    })
})

describe_db('TaskController', () => {
    let res, project_id, task_id
    it('Create a project for tasks', async () => {
        res = await request(app)
            .post('/project')
            .set(default_headers)
            .send({
                name: 'project_for_task_testing',
                description: 'desc',
                type: 'personal',
                deadline: 'Wed, 14 Jun 2020 07:00:00 GMT',
            })
        expect(res.statusCode).toEqual(201)
        expect(res.body).toHaveProperty('project_id')
        project_id = res.body.project_id
    })
    it('Create new task', async () => {
        res = await request(app)
            .post(`/project/${project_id}/task`)
            .set(default_headers)
            .send({
                description: 'test task',
                status: 'pending',
                deadline: 'Wed, 14 Jun 2020 07:00:00 GMT',
            })
        expect(res.statusCode).toEqual(201)
        expect(res.body).toHaveProperty('task_id')
        task_id = res.body.task_id
    })
    it('Update task', async () => {
        res = await request(app)
            .put(`/project/${project_id}/task/${task_id}/status`)
            .set(default_headers)
            .send({
                status: 'completed',
            })
        expect(res.statusCode).toEqual(200)
        expect(res.body.status).toEqual('completed')
    })
    it.skip('Assign task', async () => {   // This test gets "FORBIDDEN OPERATION"
        res = await request(app)
            .put(`/project/${project_id}/task/${task_id}/assignments`)
            .set(default_headers)
            .send({
                assignments: [],
            })
        expect(res.statusCode).toEqual(200)
    })
    it.skip('Delete task', async () => {  // Not implemented on app.js
        res = await request(app)
            .delete(`/project/${project_id}/task/${task_id}`)
            .set(default_headers)
            .send()
        expect(res.statusCode).toEqual(200)
    })
    afterEach(() => {
        if (res && res.body && res.body.message) {
            console.log(util.inspect(res.body.message, { depth: 10 }))
        }
        res = undefined
    })

})
