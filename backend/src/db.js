const firebase = require('firebase');
const env = require('./env');

if (firebase.apps.length === 0)
{
    const app = firebase.initializeApp(env.firebase);
    console.log(`[FIREBASE]\tinitialized, app: ${app.name}`);
}

module.exports = firebase;
