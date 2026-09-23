import { useCallback, useEffect, useState } from 'react'
import { createUser, getUsers, updateUser } from '../../api/supportApi.js'
import { errorMessage } from '../../api/client.js'
import { useAuth } from '../../auth/AuthContext.jsx'

const EMPTY_FORM = { fullName: '', username: '', password: '', role: 'AGENT' }

export default function UsersPage() {
  const { user: currentUser } = useAuth()
  const [users, setUsers] = useState([])
  const [form, setForm] = useState(EMPTY_FORM)
  const [error, setError] = useState('')
  const [info, setInfo] = useState('')

  const load = useCallback(() => {
    getUsers()
      .then(setUsers)
      .catch((e) => setError(errorMessage(e)))
  }, [])

  useEffect(() => {
    load()
  }, [load])

  async function run(action, successMessage) {
    setError('')
    setInfo('')
    try {
      await action()
      if (successMessage) setInfo(successMessage)
      load()
      return true
    } catch (e) {
      setError(errorMessage(e))
      return false
    }
  }

  async function handleCreate(event) {
    event.preventDefault()
    if (await run(() => createUser(form), `User ${form.username} created.`)) {
      setForm(EMPTY_FORM)
    }
  }

  function resetPassword(user) {
    const password = window.prompt(`New password for ${user.fullName} (at least 6 characters):`)
    if (password) {
      run(() => updateUser(user.id, { password }), `Password changed for ${user.fullName}.`)
    }
  }

  const field = (name) => ({ value: form[name], onChange: (e) => setForm({ ...form, [name]: e.target.value }) })

  return (
    <>
      <h1>Users</h1>
      {error && <p className="error">{error}</p>}
      {info && <p className="info">{info}</p>}

      <section className="card">
        <h2>Add a user</h2>
        <form className="inline-form" onSubmit={handleCreate}>
          <input placeholder="Full name" required maxLength={100} {...field('fullName')} />
          <input placeholder="Username" required maxLength={50} {...field('username')} />
          <input placeholder="Password" type="password" required minLength={6} {...field('password')} />
          <select {...field('role')}>
            <option value="AGENT">Agent</option>
            <option value="SUPPORT">Support</option>
          </select>
          <button className="primary">Add</button>
        </form>
      </section>

      <section className="card">
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Name</th>
                <th>Username</th>
                <th>Role</th>
                <th>Status</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id} className={u.active ? undefined : 'inactive'}>
                  <td>{u.fullName}</td>
                  <td>{u.username}</td>
                  <td>{u.role.toLowerCase()}</td>
                  <td>{u.active ? 'Active' : 'Inactive'}</td>
                  <td className="actions">
                    <button className="link" onClick={() => resetPassword(u)}>
                      Reset password
                    </button>
                    {u.id !== currentUser.id && (
                      <button className="link" onClick={() => run(() => updateUser(u.id, { active: !u.active }))}>
                        {u.active ? 'Deactivate' : 'Activate'}
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </>
  )
}
