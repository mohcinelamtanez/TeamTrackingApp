import TaskBadge from './TaskBadge.jsx'
import { formatDay, formatTime, today, weekDays } from '../utils/dates.js'

/** Official assignments (rows) by day (columns), with a tick where the day was completed. */
export default function WeekGrid({ weekStart, assignments, showAgent }) {
  const days = weekDays(weekStart)
  const now = today()

  if (assignments.length === 0) {
    return <p className="muted">No official assignments for this week.</p>
  }

  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            {showAgent && <th>Agent</th>}
            <th>Task</th>
            {days.map((day) => (
              <th key={day} className={day === now ? 'today' : undefined}>
                {formatDay(day)}
              </th>
            ))}
            <th>Days done</th>
          </tr>
        </thead>
        <tbody>
          {assignments.map((a) => {
            const byDate = Object.fromEntries(a.completions.map((c) => [c.date, c]))
            return (
              <tr key={a.id}>
                {showAgent && <td>{a.agentName}</td>}
                <td>
                  <TaskBadge type={a.taskType} />
                </td>
                {days.map((day) => (
                  <td key={day} className={`cell ${day === now ? 'today' : ''}`}>
                    {byDate[day] ? (
                      <span className="done" title={`Completed at ${formatTime(byDate[day].completedAt)}`}>
                        ✓
                      </span>
                    ) : (
                      <span className="muted">&middot;</span>
                    )}
                  </td>
                ))}
                <td>{a.completions.length}</td>
              </tr>
            )
          })}
        </tbody>
      </table>
    </div>
  )
}
