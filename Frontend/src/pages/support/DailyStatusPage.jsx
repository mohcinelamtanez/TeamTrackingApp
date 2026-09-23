import { useEffect, useState } from 'react'
import { getDailyReport } from '../../api/supportApi.js'
import { errorMessage } from '../../api/client.js'
import TaskBadge from '../../components/TaskBadge.jsx'
import HelpTable from '../../components/HelpTable.jsx'
import CopyButton from '../../components/CopyButton.jsx'
import { formatLongDay, formatTime, today } from '../../utils/dates.js'
import { dailyReportText } from '../../utils/reportText.js'

export default function DailyStatusPage() {
  const [date, setDate] = useState(today())
  const [report, setReport] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    setError('')
    getDailyReport(date)
      .then(setReport)
      .catch((e) => setError(errorMessage(e)))
  }, [date])

  const done = report ? report.assignments.filter((a) => a.completions.length > 0).length : 0

  return (
    <>
      <div className="page-header">
        <h1>Daily status</h1>
        <input type="date" value={date} onChange={(e) => e.target.value && setDate(e.target.value)} />
        {report && <CopyButton getText={() => dailyReportText(report)} />}
      </div>
      {error && <p className="error">{error}</p>}

      {report && (
        <>
          <section className="card">
            <h2>
              Official tasks &ndash; {formatLongDay(date)}{' '}
              <span className="muted">
                ({done}/{report.assignments.length} completed)
              </span>
            </h2>
            {report.assignments.length === 0 ? (
              <p className="muted">No official assignments for this week.</p>
            ) : (
              <div className="table-wrap">
                <table>
                  <thead>
                    <tr>
                      <th>Task</th>
                      <th>Agent</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {report.assignments.map((a) => {
                      const completion = a.completions[0]
                      return (
                        <tr key={a.id}>
                          <td>
                            <TaskBadge type={a.taskType} />
                          </td>
                          <td>{a.agentName}</td>
                          <td>
                            {completion ? (
                              <span className="done">Done at {formatTime(completion.completedAt)}</span>
                            ) : (
                              <span className="pending">Not done</span>
                            )}
                          </td>
                        </tr>
                      )
                    })}
                  </tbody>
                </table>
              </div>
            )}
          </section>

          <section className="card">
            <h2>Additional help</h2>
            <HelpTable help={report.help} showAgent />
          </section>
        </>
      )}
    </>
  )
}
