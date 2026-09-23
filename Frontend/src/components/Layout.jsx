import { NavLink, Outlet } from 'react-router'
import { useAuth } from '../auth/AuthContext.jsx'

const LINKS = {
  AGENT: [
    { to: '/today', label: 'Today' },
    { to: '/history', label: 'My history' },
  ],
  SUPPORT: [
    { to: '/support/daily', label: 'Daily status' },
    { to: '/support/weekly', label: 'Weekly report' },
    { to: '/support/assignments', label: 'Assignments' },
    { to: '/support/users', label: 'Users' },
  ],
}

export default function Layout() {
  const { user, logout } = useAuth()

  return (
    <>
      <header className="topbar">
        <span className="brand">Team Task Tracker</span>
        <nav>
          {LINKS[user.role].map((link) => (
            <NavLink key={link.to} to={link.to}>
              {link.label}
            </NavLink>
          ))}
        </nav>
        <span className="who">
          {user.fullName} <span className="muted">({user.role.toLowerCase()})</span>
          <button className="link" onClick={logout}>
            Log out
          </button>
        </span>
      </header>
      <main>
        <Outlet />
      </main>
    </>
  )
}
