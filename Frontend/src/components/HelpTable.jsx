import TaskBadge from './TaskBadge.jsx'
import { formatDay, formatTime } from '../utils/dates.js'

/** Additional help records. Kept visually separate from official assignments. */
export default function HelpTable({ help, showAgent, showDate, onDelete }) {
  if (help.length === 0) {
    return <p className="muted">No additional help recorded.</p>
  }

  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            {showDate && <th>Date</th>}
            {showAgent && <th>Agent</th>}
            <th>Helped with</th>
            <th>Recorded at</th>
            <th>Note</th>
            {onDelete && <th />}
          </tr>
        </thead>
        <tbody>
          {help.map((h) => (
            <tr key={h.id}>
              {showDate && <td>{formatDay(h.date)}</td>}
              {showAgent && <td>{h.agentName}</td>}
              <td>
                <TaskBadge type={h.taskType} />
              </td>
              <td>{formatTime(h.createdAt)}</td>
              <td>{h.note || <span className="muted">&ndash;</span>}</td>
              {onDelete && (
                <td>
                  <button className="link danger" onClick={() => onDelete(h)}>
                    Remove
                  </button>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
