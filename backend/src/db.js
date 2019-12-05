const firebase = require('firebase')
const env = require('./env')
const log = require('./log')

if (firebase.apps.length === 0)
{
    const app = firebase.initializeApp(env.firebase)
    log.info(`Firebase initialized, app: ${app.name}`)
}

module.exports = {
    database: firebase.database(),
}
