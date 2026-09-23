import { Navigate, Route, Routes } from 'react-router'
import { useAuth } from './auth/AuthContext.jsx'
import RequireRole, { homePathFor } from './auth/RequireRole.jsx'
import Layout from './components/Layout.jsx'
import LoginPage from './pages/LoginPage.jsx'
import TodayPage from './pages/agent/TodayPage.jsx'
import HistoryPage from './pages/agent/HistoryPage.jsx'
import DailyStatusPage from './pages/support/DailyStatusPage.jsx'
import WeeklyReportPage from './pages/support/WeeklyReportPage.jsx'
import AssignmentsPage from './pages/support/AssignmentsPage.jsx'
import UsersPage from './pages/support/UsersPage.jsx'

export default function App() {
  const { user } = useAuth()

  return (
    <Routes>
      <Route path="/login" element={user ? <Navigate to={homePathFor(user)} replace /> : <LoginPage />} />

      <Route element={<RequireRole role="AGENT"><Layout /></RequireRole>}>
        <Route path="/today" element={<TodayPage />} />
        <Route path="/history" element={<HistoryPage />} />
      </Route>

      <Route element={<RequireRole role="SUPPORT"><Layout /></RequireRole>}>
        <Route path="/support/daily" element={<DailyStatusPage />} />
        <Route path="/support/weekly" element={<WeeklyReportPage />} />
        <Route path="/support/assignments" element={<AssignmentsPage />} />
        <Route path="/support/users" element={<UsersPage />} />
      </Route>

      <Route path="*" element={<Navigate to={user ? homePathFor(user) : '/login'} replace />} />
    </Routes>
  )
}
