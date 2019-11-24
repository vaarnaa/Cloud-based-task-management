'use strict';

const common = {
    version: '0.0.1',
    port: process.env.PORT || 8080,

    // To use firebase db, fill in following firebase settings:
    // 1. goto https://console.firebase.google.com/project/mcc-fall-2019-g09/settings
    // 2. General -> Your Apps -> select "mcc-backend-app" web app
    // 3. Firebase SDK config -> select "config"
    // 4. Copy the contents below
    // firebase: {},
    firebase: {
        apiKey: "AIzaSyAwshnihuiKH3-31zXcGlXNbsU8d6BJ3zI",
        authDomain: "mcc-fall-2019-g09.firebaseapp.com",
        databaseURL: "https://mcc-fall-2019-g09.firebaseio.com",
        projectId: "mcc-fall-2019-g09",
        storageBucket: "mcc-fall-2019-g09.appspot.com",
        messagingSenderId: "955102176183",
        appId: "1:955102176183:web:63e19d224d44ed53e6fe32"
    },
};

const options = {
    development: {
        ...common,
        debug: true,
    },
    production: {
        ...common,
        debug: false,
    }
};

const env = process.env.NODE_ENV || 'development';
module.exports = options[env];
