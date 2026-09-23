import { useCallback, useEffect, useState } from 'react'
import { copyWeek, createAssignment, deleteAssignment, getAssignments, getUsers } from '../../api/supportApi.js'
import { errorMessage } from '../../api/client.js'
import WeekPicker from '../../components/WeekPicker.jsx'
import TaskBadge from '../../components/TaskBadge.jsx'
import { addDays, today, weekStartOf } from '../../utils/dates.js'
import { TASK_TYPES } from '../../utils/taskTypes.js'

/** Official weekly assignments. Any number of agents can be put on any task type. */
export default function AssignmentsPage() {
  const [weekStart, setWeekStart] = useState(weekStartOf(today()))
  const [assignments, setAssignments] = useState([])
  const [agents, setAgents] = useState([])
  const [error, setError] = useState('')
  const [info, setInfo] = useState('')

  const load = useCallback(() => {
    getAssignments(weekStart)
      .then(setAssignments)
      .catch((e) => setError(errorMessage(e)))
  }, [weekStart])

  useEffect(() => {
    setError('')
    setInfo('')
    load()
  }, [load])

  useEffect(() => {
    getUsers('AGENT')
      .then((users) => setAgents(users.filter((u) => u.active)))
      .catch((e) => setError(errorMessage(e)))
  }, [])

  async function run(action) {
    setError('')
    setInfo('')
    try {
      await action()
      load()
    } catch (e) {
      setError(errorMessage(e))
    }
  }

  function remove(assignment) {
    if (window.confirm(`Remove ${assignment.agentName} from this task for this week?`)) {
      run(() => deleteAssignment(assignment.id))
    }
  }

  function copyPreviousWeek() {
    run(async () => {
      const created = await copyWeek(addDays(weekStart, -7), weekStart)
      setInfo(`${created.length} assignment(s) copied from the previous week.`)
    })
  }

  const assignedIds = new Set(assignments.map((a) => a.agentId))
  const unassigned = agents.filter((a) => !assignedIds.has(a.id))

  return (
    <>
      <div className="page-header">
        <h1>Weekly assignments</h1>
        <button onClick={copyPreviousWeek}>Copy previous week</button>
      </div>
      <WeekPicker value={weekStart} onChange={setWeekStart} />
      {error && <p className="error">{error}</p>}
      {info && <p className="info">{info}</p>}

      <div className="columns">
        {TASK_TYPES.map((type) => {
          const rows = assignments.filter((a) => a.taskType === type)
          const available = agents.filter((agent) => !rows.some((r) => r.agentId === agent.id))
          return (
            <section key={type} className="card column">
              <h2>
                <TaskBadge type={type} /> <span className="muted">{rows.length} agent(s)</span>
              </h2>
              <ul className="chips">
                {rows.map((a) => (
                  <li key={a.id}>
                    {a.agentName}
                    <button className="link danger" title="Remove" onClick={() => remove(a)}>
                      &times;
                    </button>
                  </li>
                ))}
              </ul>
              {available.length > 0 && (
                <select
                  value=""
                  onChange={(e) => e.target.value && run(() => createAssignment(Number(e.target.value), type, weekStart))}
                >
                  <option value="">+ Add agent…</option>
                  {available.map((agent) => (
                    <option key={agent.id} value={agent.id}>
                      {agent.fullName}
                    </option>
                  ))}
                </select>
              )}
            </section>
          )
        })}
      </div>

      <section className="card">
        <h2>Not assigned this week</h2>
        {unassigned.length === 0 ? (
          <p className="muted">Every active agent has at least one assignment.</p>
        ) : (
          <p>{unassigned.map((a) => a.fullName).join(', ')}</p>
        )}
      </section>
    </>
  )
}
