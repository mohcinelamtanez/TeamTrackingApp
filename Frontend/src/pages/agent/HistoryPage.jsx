import { useEffect, useState } from 'react'
import { getMyHelp, getMyWeek } from '../../api/agentApi.js'
import { errorMessage } from '../../api/client.js'
import WeekPicker from '../../components/WeekPicker.jsx'
import WeekGrid from '../../components/WeekGrid.jsx'
import HelpTable from '../../components/HelpTable.jsx'
import { addDays, today, weekStartOf } from '../../utils/dates.js'

export default function HistoryPage() {
  const [weekStart, setWeekStart] = useState(weekStartOf(today()))
  const [assignments, setAssignments] = useState([])
  const [help, setHelp] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    setError('')
    Promise.all([getMyWeek(weekStart), getMyHelp(weekStart, addDays(weekStart, 6))])
      .then(([week, weekHelp]) => {
        setAssignments(week)
        setHelp(weekHelp)
      })
      .catch((e) => setError(errorMessage(e)))
  }, [weekStart])

  return (
    <>
      <h1>My history</h1>
      <WeekPicker value={weekStart} onChange={setWeekStart} />
      {error && <p className="error">{error}</p>}

      <section className="card">
        <h2>Official tasks</h2>
        <WeekGrid weekStart={weekStart} assignments={assignments} />
      </section>

      <section className="card">
        <h2>Additional help</h2>
        <HelpTable help={help} showDate />
      </section>
    </>
  )
}
