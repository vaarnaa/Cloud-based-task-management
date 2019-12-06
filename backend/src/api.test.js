'use strict'

const request = require('supertest')
const util = require('util')
const app = require('./app')
const {database} = require('./db')

afterAll(() => database.goOffline())

const default_headers = {
    'x-endpoint-api-userinfo':
    'eyJpc3N1ZXIiOiJUT0tFTl9JU1NVRVIiLCJpZCI6Ikl3M0JtS2V6cjVoRWw5QUFIM2RkVm1IQWxzOTIiLCJlbWFpbCI6InRlbXBAZXhhbXBsZS5sb2NhbCJ9'
}

describe('Web server', () => {
    it('Front page', async () => {
        const res = await request(app).get('/').send()
        expect(res.statusCode).toEqual(200)
    })
})

describe('ProjectController', () => {
    let res, project_id
    it('Create new project', async () => {
        res = await request(app)
            .post('/project')
            .set(default_headers)
            .send({
                name: 'project_name1',
                description: 'desc',
                deadline: 'Wed, 14 Jun 2020 07:00:00 GMT',
            })
        expect(res.statusCode).toEqual(201)
        expect(res.body).toHaveProperty('project_id')
        project_id = res.project_id
    })
    it('Get project attachments', async () => {
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
                    'ICXa2Dr4LeZTNyN9tv60pNXMjqC3',
                    'QM9HKPoy9KffdUmP4JIWOem6zC93',
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
        expect(res.statusCode).toEqual(204)
    })
    afterEach(() => {
        if (res.body.message) console.log(util.inspect(res.body.message, {depth: 10}))
    })
})
