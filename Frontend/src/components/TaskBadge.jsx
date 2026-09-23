import { TASK_LABELS } from '../utils/taskTypes.js'

export default function TaskBadge({ type }) {
  return <span className={`badge badge-${type.toLowerCase()}`}>{TASK_LABELS[type]}</span>
}
