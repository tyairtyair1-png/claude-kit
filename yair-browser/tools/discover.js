// Headless discovery: loads each whitelisted site's home page and lists third-party hosts it requested.
// Usage: node tools/discover.js > discovered.json   (no logins; home pages only)
const { chromium } = require('playwright');
const wl = require('../app/src/main/assets/whitelist.json');
const sites = [...new Set(wl.categories.flatMap(c => c.domains))];
const allowed = h => sites.some(s => h === s || h.endsWith('.' + s));
(async () => {
  const br = await chromium.launch({ args: ['--no-sandbox'] });
  const out = {};
  for (const s of sites) {
    const ctx = await br.newContext({ ignoreHTTPSErrors: false }); const pg = await ctx.newPage();
    const hosts = new Set(); let status = 'ok';
    pg.on('request', r => { try { const h = new URL(r.url()).hostname; if (!allowed(h)) hosts.add(h); } catch {} });
    try { await pg.goto('https://' + s, { timeout: 20000, waitUntil: 'load' }); }
    catch (e) { status = String(e.message).split('\n')[0].slice(0, 80); }
    out[s] = { status, thirdParty: [...hosts].sort() };
    try { await ctx.close(); } catch {}
  }
  await br.close();
  console.log(JSON.stringify(out, null, 1));
})();
