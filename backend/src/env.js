'use strict'
const admin = require('firebase-admin')

const common = {
    version: '0.0.1',
    port: process.env.PORT || 8080,
    debug: false,
    firebase: {
        credential: admin.credential.applicationDefault(),
        databaseURL: 'https://mcc-fall-2019-g09.firebaseio.com',
    },
}

const options = {
    development: {
        ...common,
        debug: true,
    },
    test: {
        ...common,
        port: undefined,
    },
    production: {
        ...common,
    }
}

const env = process.env.NODE_ENV || 'development'
module.exports = options[env]
