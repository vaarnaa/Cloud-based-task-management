const firebase = require('firebase');
const admin = require("firebase-admin");

const env = require('./env');
const log = require('./log');

if (firebase.apps.length === 0)
{
    const app = firebase.initializeApp(env.firebase);
    log.info(`Firebase initialized, app: ${app.name}`);

    const serviceAccount = require(env.pathToServiceAccountKey);
    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount),
      databaseURL: "https://mcc-fall-2019-g09.firebaseio.com"
    });

}

module.exports = {
    // admin has functions to verify auth tokens
    admin: admin,

    // firebase itself has functions to sign in and access DB
    auth: firebase.auth(),
    database: firebase.database(),
};
