---
name: hebrew-every-line
description: "Every user-visible line in Hebrew, including one-line replies after background notifications and lines before tool calls"
metadata:
  node_type: memory
  type: feedback
  originSessionId: 85fb3bb5-ada2-4cf9-a98d-8675de967315
  modified: 2026-10-27T09:41:44.831Z
---

Every line shown to the user must be Hebrew: short status lines, one-liners after a background-task notification, and the sentence before a tool call. English slipped in exactly there on 2026-10-27 and the user corrected it twice ("אני מבין עברית בלבד", "עברית - גם האחרונה וגם תמיד").

**Why:** the user reads only Hebrew; an English line is an unreadable line.
**How to apply:** before sending any text (even "waiting for X"), check it is Hebrew. Commands/code stay as-is, with a Hebrew sentence explaining them.
