import { createContext, useContext, useState } from 'react'
import client, { TOKEN_KEY, USER_KEY } from '../api/client.js'

const AuthContext = createContext(null)

function readStoredUser() {
  try {
    const stored = localStorage.getItem(USER_KEY)
    return stored && localStorage.getItem(TOKEN_KEY) ? JSON.parse(stored) : null
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readStoredUser)

  async function login(username, password) {
    const { data } = await client.post('/auth/login', { username, password })
    localStorage.setItem(TOKEN_KEY, data.token)
    localStorage.setItem(USER_KEY, JSON.stringify(data.user))
    setUser(data.user)
    return data.user
  }

  function logout() {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
    setUser(null)
  }

  return <AuthContext.Provider value={{ user, login, logout }}>{children}</AuthContext.Provider>
}

export function useAuth() {
  return useContext(AuthContext)
}
