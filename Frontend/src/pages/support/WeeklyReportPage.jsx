import { useEffect, useState } from 'react'
import { getWeeklyReport } from '../../api/supportApi.js'
import { errorMessage } from '../../api/client.js'
import WeekPicker from '../../components/WeekPicker.jsx'
import WeekGrid from '../../components/WeekGrid.jsx'
import HelpTable from '../../components/HelpTable.jsx'
import CopyButton from '../../components/CopyButton.jsx'
import { today, weekStartOf } from '../../utils/dates.js'
import { weeklyReportText } from '../../utils/reportText.js'

export default function WeeklyReportPage() {
  const [weekStart, setWeekStart] = useState(weekStartOf(today()))
  const [report, setReport] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    setError('')
    getWeeklyReport(weekStart)
      .then(setReport)
      .catch((e) => setError(errorMessage(e)))
  }, [weekStart])

  return (
    <>
      <div className="page-header">
        <h1>Weekly report</h1>
        {report && <CopyButton getText={() => weeklyReportText(report)} />}
      </div>
      <WeekPicker value={weekStart} onChange={setWeekStart} />
      {error && <p className="error">{error}</p>}

      {report && (
        <>
          <section className="card">
            <h2>Official assignments and completion</h2>
            <WeekGrid weekStart={weekStart} assignments={report.assignments} showAgent />
          </section>

          <section className="card">
            <h2>Additional help</h2>
            <HelpTable help={report.help} showAgent showDate />
          </section>
        </>
      )}
    </>
  )
}
