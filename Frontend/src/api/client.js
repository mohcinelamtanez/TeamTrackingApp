import axios from 'axios'

export const TOKEN_KEY = 'tasktracker.token'
export const USER_KEY = 'tasktracker.user'

const client = axios.create({ baseURL: '/api' })

client.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// An expired or revoked token sends the user back to the login page.
client.interceptors.response.use(
  (response) => response,
  (error) => {
    const isLogin = error.config?.url === '/auth/login'
    if (error.response?.status === 401 && !isLogin) {
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
      window.location.assign('/login')
    }
    return Promise.reject(error)
  },
)

export function errorMessage(error) {
  return error.response?.data?.message || 'Something went wrong. Please try again.'
}

export default client
