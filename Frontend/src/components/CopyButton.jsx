import { useState } from 'react'

/** Copies plain text to the clipboard, for pasting into the operational report. */
export default function CopyButton({ getText, label = 'Copy as text' }) {
  const [copied, setCopied] = useState(false)

  async function copy() {
    try {
      await navigator.clipboard.writeText(getText())
      setCopied(true)
      setTimeout(() => setCopied(false), 2000)
    } catch {
      window.alert('Could not access the clipboard.')
    }
  }

  return <button onClick={copy}>{copied ? 'Copied!' : label}</button>
}
