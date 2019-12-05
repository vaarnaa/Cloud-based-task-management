const forbidden = () =>         ({ code: 403, message: 'Forbidden operation' })
const projectNotFound = () =>   ({ code: 404, message: 'Not found' })
const invalidInput = (error) => ({ code: 422, message: error })

module.exports = {
    forbidden,
    invalidInput,
    projectNotFound,
}
