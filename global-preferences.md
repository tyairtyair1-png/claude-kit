# Global preferences of Yair (all projects on this machine)

Single source: this file in the `claude-kit` repo (local clone `C:/Users/tyair/claude-kit`). `~/.claude/CLAUDE.md` imports it.
Edit it here and push; there is no other copy.

## Token economy
- Strict diffs: never output full files when modifying code; show only changed lines, functions or blocks.
- Minimalism: very brief explanations; no conversational filler, verbose planning or long recaps.
- Restricted file access: do not scan data folders, large JSON/CSV files or virtual environments (`.venv`, `node_modules`) unless explicitly asked.
- No duplicate checks: what already passed is not re-run; agents run only fast checks on what they changed, the release check runs the rest once.

## Communication
- Talk to the user in simple, clear Hebrew without jargon. Apps/UI for the user: Hebrew, RTL, readable numbers.
- All replies and status updates in Hebrew. Never reverse Hebrew text or re-encode files to "fix" terminal display.
  `claude` in PowerShell opens mintty (Bidi=2); `claude-plain` runs in PowerShell itself; full RTL only in the browser
  (claude.ai/code via Remote Control). Do not send `\e[?2501h` to Claude's TUI (it clips lines).
- Progress updates: short (1-3 lines), frequent, each starting with "🟣 **עדכון:**".
- The user is not a developer: simple words, no jargon, explain results not mechanics.

## Projects (each has its own CLAUDE.md + memory; never mix their rules)
- TradePilot: `C:\Users\tyair\TradePilot` (trading tool, paper only).
- MarathonX: `C:\Users\tyair\marathonx-redesign` (course site marathonx.co.il; live server rules in its memory).
- Trampoline: `C:\Users\tyair\trampoline` (inflatables rental site, Google Apps Script via clasp).
- WeGive (formerly Matan / מתן, donation platform, domain wegive.co.il): repo `C:\Users\tyair\platform`; working copies in
  WSL Ubuntu `/root/platform`, `/root/matan-p4`. Live server: Oracle Always Free (Jerusalem), updates itself from
  GitHub `main` commits signed by the release gate. **Whenever a task touches it, from any folder:** first read
  `<repo>/CLAUDE.md` (binding workflow), `docs/DEPLOY.md` / `docs/COST-PLAN.md` for anything about hosting or costs, and
  the `.claude/skills/*/SKILL.md` the task needs. The release gate is the release check (the separate reviewer step was
  removed by the owner on 15.11.2026). Shipping through the gate is pre-approved; changes to real money flows, data
  deletion or brand need his OK.
- General/other work: started from `C:\WINDOWS\system32`.

## Working style
- Act; make routine technical decisions; ask only when a material fact is missing.
- Check the project's own docs and decisions before advising on costs, hosting or architecture; never default to a paid service.
- Verify with real runs/tests before claiming done; report failures plainly. Dry-check scripts before running long jobs.
- Prefer plain code, free/open-source tools; state costs before any paid service.
- Browser checks (Playwright/screenshots/Chrome) cost tokens: batch them and run them once per group of changes.
- Keep the laptop free: heavy test runs go to CI (GitHub Actions) where the project supports it.
- Model choice is mine, automatically (the owner prefers quality to saving tokens, 15.11.2026): Opus for design,
  creative work, hard debugging and final reviews; Fable freely where it clearly improves the result (he has plenty);
  Sonnet for mechanical work (merges, renames, test fixes); Haiku or my own grep for lookups.
- Never handle the user's passwords in chat; build local prompts instead.
- Outward-facing or irreversible actions (deploys outside an approved flow, real-money actions, sending messages)
  need explicit user approval.

## Shared skills (all computers, all projects)
- Skills in this repo's `skills/` and in `~/OneDrive/Claude/skills/<name>/` (the extra ones Yair adopted). At the start of a
  session, copy any that are missing into `~/.claude/skills/`. Design work uses `emil-design-eng` and `impeccable`
  (plus `animate`, `design-taste-frontend`, `hallmark`, `review-animations`, `redesign-existing-projects`, `no-ai-slop`,
  `logo-design`, `pixel2motion`, `logo-animation`; `threejs` for real 3D). `anatomy` draws interactive isometric figures; it is slow by design; never publish to its community
  gallery unless he asks.
