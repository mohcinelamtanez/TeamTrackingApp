import { useCallback, useEffect, useState } from 'react'
import { deleteHelp, getMyHelp, getMyWeek, markTodayDone, recordHelp, undoToday } from '../../api/agentApi.js'
import { errorMessage } from '../../api/client.js'
import TaskBadge from '../../components/TaskBadge.jsx'
import HelpTable from '../../components/HelpTable.jsx'
import { formatLongDay, formatTime, today, weekStartOf } from '../../utils/dates.js'
import { TASK_LABELS, TASK_TYPES } from '../../utils/taskTypes.js'

export default function TodayPage() {
  const date = today()
  const [assignments, setAssignments] = useState(null)
  const [help, setHelp] = useState([])
  const [helpType, setHelpType] = useState('')
  const [helpNote, setHelpNote] = useState('')
  const [error, setError] = useState('')

  const load = useCallback(() => {
    Promise.all([getMyWeek(weekStartOf(date)), getMyHelp(date, date)])
      .then(([week, todaysHelp]) => {
        setAssignments(week)
        setHelp(todaysHelp)
      })
      .catch((e) => setError(errorMessage(e)))
  }, [date])

  useEffect(() => {
    load()
  }, [load])

  async function run(action) {
    setError('')
    try {
      await action()
      load()
    } catch (e) {
      setError(errorMessage(e))
    }
  }

  async function submitHelp(event) {
    event.preventDefault()
    await run(() => recordHelp(helpType, helpNote))
    setHelpType('')
    setHelpNote('')
  }

  if (!assignments) {
    return error ? <p className="error">{error}</p> : <p className="muted">Loading…</p>
  }

  // Help is only for task types the agent is not officially assigned to this week.
  const assignedTypes = assignments.map((a) => a.taskType)
  const helpTypes = TASK_TYPES.filter((t) => !assignedTypes.includes(t) && !help.some((h) => h.taskType === t))

  return (
    <>
      <h1>{formatLongDay(date)}</h1>
      {error && <p className="error">{error}</p>}

      <section className="card">
        <h2>My official tasks this week</h2>
        <p className="muted">Assigned by Support. Mark each one as done when today&apos;s work is finished.</p>
        {assignments.length === 0 && <p>You have no official assignment this week.</p>}
        <ul className="task-list">
          {assignments.map((a) => {
            const doneToday = a.completions.find((c) => c.date === date)
            return (
              <li key={a.id}>
                <TaskBadge type={a.taskType} />
                {doneToday ? (
                  <>
                    <span className="done">Done at {formatTime(doneToday.completedAt)}</span>
                    <button className="link" onClick={() => run(() => undoToday(a.id))}>
                      Undo
                    </button>
                  </>
                ) : (
                  <>
                    <span className="pending">Not done yet</span>
                    <button className="primary" onClick={() => run(() => markTodayDone(a.id))}>
                      Mark today as done
                    </button>
                  </>
                )}
              </li>
            )
          })}
        </ul>
      </section>

      <section className="card">
        <h2>Additional help (optional)</h2>
        <p className="muted">
          Did you also help with another task today? Recording it does not change your official assignment.
        </p>
        {helpTypes.length > 0 && (
          <form className="inline-form" onSubmit={submitHelp}>
            <select value={helpType} onChange={(e) => setHelpType(e.target.value)} required>
              <option value="">I helped with…</option>
              {helpTypes.map((t) => (
                <option key={t} value={t}>
                  {TASK_LABELS[t]}
                </option>
              ))}
            </select>
            <input
              placeholder="Note (optional)"
              maxLength={255}
              value={helpNote}
              onChange={(e) => setHelpNote(e.target.value)}
            />
            <button className="primary">Record help</button>
          </form>
        )}
        <HelpTable help={help} onDelete={(h) => run(() => deleteHelp(h.id))} />
      </section>
    </>
  )
}
