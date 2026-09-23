import client from './client.js'

// Official assignments (read-only for agents) and daily completion
export const getMyWeek = (weekStart) =>
  client.get('/me/assignments', { params: { weekStart } }).then((r) => r.data)

export const markTodayDone = (assignmentId) =>
  client.put(`/me/assignments/${assignmentId}/completions/today`).then((r) => r.data)

export const undoToday = (assignmentId) =>
  client.delete(`/me/assignments/${assignmentId}/completions/today`)

// Additional help (separate from official assignments)
export const getMyHelp = (from, to) =>
  client.get('/me/help', { params: { from, to } }).then((r) => r.data)

export const recordHelp = (taskType, note) =>
  client.post('/me/help', { taskType, note }).then((r) => r.data)

export const deleteHelp = (id) => client.delete(`/me/help/${id}`)
