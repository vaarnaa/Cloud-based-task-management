const log = require('./log');
const { database } = require('./db');

const PROJECT_ROOT = '/projects';

const resolveRef = async (ref) => {
  const snapshot = await database.ref(ref).once('value');
  const val = snapshot.val();
  log.debug(`Resolved value for ref ${ref}: ${val}`);
  return val;
};

const getProjectAdmin = (projectId) => resolveRef(`${PROJECT_ROOT}/${projectId}/admin`);
const getProjectMembers = (projectId) => resolveRef(`${PROJECT_ROOT}/${projectId}/members`);

module.exports = {
  resolveRef,
  getProjectMembers,
  getProjectAdmin,
}
