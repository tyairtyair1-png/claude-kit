# Yair Browser (Android)

Allowlist-only browser: WebView + a policy that blocks everything not on `app/src/main/assets/whitelist.json`.

- HTTPS only, standard port, no user-info URLs, no IPs.
- A rule = domain + its subdomains. Google/Microsoft are NOT opened broadly (only the listed hosts).
- Messaging / social / shopping hosts are hard-denied (`Policy.HARD_DENY`) and cannot be approved from the UI.
- Blocked third-party hosts are recorded per site; approve individually in the PIN-protected admin screen (approval = exact host only).
- Add/remove domains from the admin screen (⋮ button). Report of pending hosts can be copied.
- Does not touch device DNS / filtering settings.

Build: `echo sdk.dir=/path/to/android-sdk > local.properties && ./gradlew :app:testDebugUnitTest :app:assembleDebug`
Discovery: `node tools/discover.js` (Playwright, home pages only, no logins) -> see `DISCOVERED-DOMAINS.txt`.

Known limits: WebSocket traffic is not filtered by WebView hooks; only PDFs from allowed hosts open (in-app viewer); login/personal-area flows must be checked on a real device.
