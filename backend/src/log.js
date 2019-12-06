'use strict'
const env = require('./env')
const colors = require('colors')

colors.setTheme({
    debug: 'grey',
    warn: 'yellow',
    info: 'white',
    error: 'red'
})

// colors.disable();

const logger = {

    log: (level, msg) => {
        const time = new Date().toJSON()

        if (typeof msg === 'object' && msg !== null)
            msg = JSON.stringify(msg)

        let final_msg = `[${time}] [${level}] ${msg}`

        if (level == 'INFO')
            final_msg = final_msg.info
        else if (level == 'DEBUG')
            final_msg = final_msg.debug
        else if (level == 'WARN')
            final_msg = final_msg.warn
        else if (level == 'ERROR')
            final_msg = final_msg.error

        console.log(final_msg)
    },

    info: (msg) => {
        if (env.debug)
            logger.log('INFO', msg)
    },

    debug: (msg) => {
        if (env.debug)
            logger.log('DEBUG', msg)
    },

    warn: (msg) => {
        logger.log('ERROR', msg)
    },

    error: (msg) => {
        logger.log('ERROR', msg)
    },
}

module.exports = logger
