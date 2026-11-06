# Global preferences of Yair (all projects on this machine)

## Token economy
- Strict diffs: never output full files when modifying code; show only changed lines, functions or blocks.
- Minimalism: very brief explanations; no conversational filler, verbose planning or long recaps.
- Restricted file access: do not scan data folders, large JSON/CSV files or virtual environments (`.venv`, `node_modules`) unless explicitly asked.

## Communication
- Talk to the user in simple, clear Hebrew without jargon. Apps/UI for the user: Hebrew, RTL, readable numbers.
- All replies and status updates in Hebrew (user asked 2026-10-21). Never reverse Hebrew text or re-encode files to
  "fix" terminal display. Typing `claude` in PowerShell runs a profile function that opens mintty (Bidi=2) in the current
  folder: Hebrew words in the right order but left-aligned; `claude-plain` runs in the PowerShell window itself; full RTL layout only in the browser (claude.ai/code via Remote Control). Do not send `\e[?2501h`
  (RTL autodetect) to Claude's TUI: tested 21.10.2026, it clips lines.
- Progress updates: short (1-3 lines), frequent, each starting with "🟣 **עדכון:**".
- The user is not a developer: simple words, no jargon, explain results not mechanics.

## Projects (each has its own CLAUDE.md + memory; never mix their rules)
- TradePilot: `C:\Users\tyair\TradePilot` (trading tool, paper only).
- MarathonX: `C:\Users\tyair\marathonx-redesign` (course site marathonx.co.il; live server rules in its memory).
- Trampoline: `C:\Users\tyair\trampoline` (inflatables rental site, Google Apps Script via clasp).
- Matan (מתן, donation platform): repo `C:\Users\tyair\platform`; working copies in WSL Ubuntu `/root/platform`
  (ships), `/root/matan-p4`. Live server updates itself from GitHub `main`. **Whenever a task touches Matan, from
  any folder:** before any work read `<repo>/CLAUDE.md` (binding workflow at its top), `docs/REVIEWS.md`, and the
  `.claude/skills/*/SKILL.md` the task needs; run the reviewers from `.claude/agents/` (as general-purpose agents
  with that file as instructions when the agent type is not loaded). Nothing ships without both reviews; the gate
  and the git pre-push hook enforce it. Opening Claude in the repo (`cd C:\Users\tyair\platform; claude`) loads all of
  this automatically. **2026-10-27: the owner paused both reviews and the slow gate steps** (`docs/REVIEWS_PAUSED`
  in the repo); don't run the reviewers until he turns them back on (delete that file and ship).
- General/other work: started from `C:\WINDOWS\system32`.

## Working style
- Act; make routine technical decisions; ask only when a material fact is missing.
- Verify with real runs/tests before claiming done; report failures plainly.
- Prefer plain code, free/open-source tools; state costs before any paid service.
- Browser checks (Playwright/screenshots/Chrome) cost tokens: batch them — finish a group of changes, then run all
  browser checks once in one sequence; don't re-run them after every small edit (user asked 2026-10-25, all sessions).
- Model choice is mine, automatically, in every session and project (user asked 2026-11-04: he can't judge which
  model to use and doesn't want tokens burned). Strong model (Opus) only for decisions, architecture, money/security
  logic and final checks; implementation rounds (UI, CSS, copy, tests, multi-file edits) go to one agent with
  `model: "sonnet"`; search/lookups to `model: "haiku"` or my own grep. If the session itself runs on Opus for routine
  work, suggest `/model sonnet` to him in one line.
- Never handle the user's passwords in chat; build local prompts instead.
- Outward-facing or irreversible actions (deploys, real-money trading, sending messages) need explicit user approval.

## Shared skills (all computers, all projects)
- Source of truth for skills + these preferences, for any computer or cloud session: repo github.com/tyairtyair1-png/claude-kit (local clone C:/Users/tyair/claude-kit; push updates there). Skills Yair adopted also live in `~/OneDrive/Claude/skills/<name>/`. At the start of a session, if one is missing from
  `~/.claude/skills/`, copy it there (no download needed). Current list: design skills `animate`, `design-taste-frontend`, `emil-design-eng`, `hallmark`, `impeccable`, `improve-animations`, `review-animations`, `redesign-existing-projects`; and `anatomy` (interactive isometric technical
  figures; slow by design, a simple one can take up to an hour; never publish to its community gallery unless he asks).
