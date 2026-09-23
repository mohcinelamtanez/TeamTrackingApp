// Dates travel as 'YYYY-MM-DD' strings and are always handled in local time.

export function toIsoDate(date) {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

export function parseIsoDate(iso) {
  const [y, m, d] = iso.split('-').map(Number)
  return new Date(y, m - 1, d)
}

export function today() {
  return toIsoDate(new Date())
}

export function addDays(iso, days) {
  const date = parseIsoDate(iso)
  date.setDate(date.getDate() + days)
  return toIsoDate(date)
}

/** The Monday of the week containing the given date. */
export function weekStartOf(iso) {
  const date = parseIsoDate(iso)
  const offset = (date.getDay() + 6) % 7
  return addDays(iso, -offset)
}

export function weekDays(weekStart) {
  return Array.from({ length: 7 }, (_, i) => addDays(weekStart, i))
}

export function formatDay(iso) {
  return parseIsoDate(iso).toLocaleDateString(undefined, { weekday: 'short', day: 'numeric', month: 'short' })
}

export function formatLongDay(iso) {
  return parseIsoDate(iso).toLocaleDateString(undefined, {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  })
}

export function formatTime(instant) {
  return new Date(instant).toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' })
}
