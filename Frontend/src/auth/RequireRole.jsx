import { Navigate } from 'react-router'
import { useAuth } from './AuthContext.jsx'

export function homePathFor(user) {
  return user.role === 'SUPPORT' ? '/support/daily' : '/today'
}

/** Only renders its children for a logged-in user with the given role. The backend enforces the same rule. */
export default function RequireRole({ role, children }) {
  const { user } = useAuth()
  if (!user) {
    return <Navigate to="/login" replace />
  }
  if (user.role !== role) {
    return <Navigate to={homePathFor(user)} replace />
  }
  return children
}
