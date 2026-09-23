import { addDays, formatDay, today, weekStartOf } from '../utils/dates.js'

/** Moves between weeks; `value` is always a Monday. */
export default function WeekPicker({ value, onChange }) {
  const current = weekStartOf(today())

  return (
    <div className="week-picker">
      <button onClick={() => onChange(addDays(value, -7))}>&larr; Previous</button>
      <strong>
        {formatDay(value)} &ndash; {formatDay(addDays(value, 6))}
      </strong>
      <button onClick={() => onChange(addDays(value, 7))}>Next &rarr;</button>
      {value !== current && (
        <button className="link" onClick={() => onChange(current)}>
          This week
        </button>
      )}
    </div>
  )
}
