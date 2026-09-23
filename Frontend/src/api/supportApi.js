import client from './client.js'

// Users
export const getUsers = (role) => client.get('/users', { params: { role } }).then((r) => r.data)

export const createUser = (user) => client.post('/users', user).then((r) => r.data)

export const updateUser = (id, changes) => client.patch(`/users/${id}`, changes).then((r) => r.data)

// Official weekly assignments
export const getAssignments = (weekStart) =>
  client.get('/assignments', { params: { weekStart } }).then((r) => r.data)

export const createAssignment = (agentId, taskType, weekStart) =>
  client.post('/assignments', { agentId, taskType, weekStart }).then((r) => r.data)

export const deleteAssignment = (id) => client.delete(`/assignments/${id}`)

export const copyWeek = (from, to) =>
  client.post('/assignments/copy', null, { params: { from, to } }).then((r) => r.data)

// Reports
export const getDailyReport = (date) =>
  client.get('/reports/daily', { params: { date } }).then((r) => r.data)

export const getWeeklyReport = (weekStart) =>
  client.get('/reports/weekly', { params: { weekStart } }).then((r) => r.data)
