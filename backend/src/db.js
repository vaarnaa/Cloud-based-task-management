'use strict'
const admin = require('firebase-admin')
const env = require('./env')
const log = require('./log')

if (admin.apps.length === 0)
{
    const app = admin.initializeApp(env.firebase)
    log.info(`Firebase initialized, app: ${app.name}`)
}

module.exports = {
    database: admin.database(),
}
