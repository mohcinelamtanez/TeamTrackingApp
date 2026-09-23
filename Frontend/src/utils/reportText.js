import { TASK_LABELS, TASK_TYPES } from './taskTypes.js'
import { formatDay, formatLongDay, formatTime } from './dates.js'

// Plain-text versions of the reports, ready to paste into the operational report.

function helpLines(help, withDate) {
  if (help.length === 0) {
    return ['- None']
  }
  return help.map((h) => {
    const when = withDate ? `${formatDay(h.date)} ${formatTime(h.createdAt)}` : formatTime(h.createdAt)
    const note = h.note ? ` (${h.note})` : ''
    return `- ${h.agentName} helped with ${TASK_LABELS[h.taskType]} at ${when}${note}`
  })
}

export function dailyReportText(report) {
  const done = report.assignments.filter((a) => a.completions.length > 0).length
  const lines = [`Daily report - ${formatLongDay(report.from)}`, '']
  lines.push(`Official tasks (${done}/${report.assignments.length} completed)`)
  for (const type of TASK_TYPES) {
    const rows = report.assignments.filter((a) => a.taskType === type)
    const people = rows.length
      ? rows.map((a) => `${a.agentName} ${a.completions.length ? 'done' : 'NOT done'}`).join(', ')
      : 'nobody assigned'
    lines.push(`${TASK_LABELS[type]}: ${people}`)
  }
  lines.push('', 'Additional help', ...helpLines(report.help, false))
  return lines.join('\n')
}

export function weeklyReportText(report) {
  const lines = [`Weekly report - ${formatDay(report.from)} to ${formatDay(report.to)}`, '', 'Official assignments']
  for (const type of TASK_TYPES) {
    const rows = report.assignments.filter((a) => a.taskType === type)
    const people = rows.length
      ? rows.map((a) => `${a.agentName} (${a.completions.length} day(s) completed)`).join(', ')
      : 'nobody assigned'
    lines.push(`${TASK_LABELS[type]}: ${people}`)
  }
  lines.push('', `Additional help (${report.help.length})`, ...helpLines(report.help, true))
  return lines.join('\n')
}
