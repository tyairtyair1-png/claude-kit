# Yair's Claude kit

The skills and working preferences Yair uses with Claude Code, for any computer or cloud session.
Nothing here depends on a specific machine.

## Install (once per computer or session)
```sh
git clone https://github.com/tyairtyair1-png/claude-kit.git ~/claude-kit
mkdir -p ~/.claude/skills && cp -r ~/claude-kit/skills/* ~/.claude/skills/
```
Then add one line to `~/.claude/CLAUDE.md` (create it if missing):
```
@~/claude-kit/global-preferences.md
```
To update later: `cd ~/claude-kit && git pull`, then copy the skills again.

## Inside
- `global-preferences.md`: how Yair works with Claude. Hebrew, short "🟣 עדכון" updates, non-developer, screenshots,
  automatic model choice (Opus for decisions/specs, Sonnet for building, haiku for search), token economy, safety.
- `rules/`: his general writing and work rules (natural Hebrew copywriting, Hebrew in every line, send previews as
  images, borrow competitors' ideas never their words). Read them before writing Hebrew copy.
- `skills/`: his skills that carry an open licence (MIT / Apache 2.0, each keeps its LICENSE):
  - Design: impeccable, frontend-design, hallmark.
  - Figures: anatomy (interactive isometric explainers; MIT, github.com/wheresryan22/anatomy @1482e84; never publish to
    its gallery unless Yair asks).
  - Working (obra/superpowers, MIT): systematic-debugging, verification-before-completion, test-driven-development,
    requesting-code-review; webapp-testing (Anthropic, Apache 2.0).
  - Trading (paper only; ml4t/skills Apache 2.0, tradermonty MIT): position-sizer, pre-trade-discipline-gate,
    backtest-overfitting, lookahead-bias, point-in-time, regime-awareness, survivorship-bias, transaction-costs.
- Not included here (no clear licence to republish; Yair has them on his own machines): animate, emil-design-eng,
  design-taste-frontend (github.com/Leonxlnx/taste-skill), redesign-existing-projects, improve-animations,
  review-animations, audit-context-building, differential-review, sharp-edges, variant-analysis,
  post-patch-validation, semgrep, wp-plugin-development, wp-project-triage.
- `anatomy-example-spec.md`: the Matan donation-machine spec, an example of how to brief Anatomy well.

Each project keeps its own rules in its repo's CLAUDE.md and memory. Never mix projects' rules.
