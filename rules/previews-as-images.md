---
name: previews-as-images
description: Owner often has no browser (phone via Remote Control); send previews as images, not links
metadata:
  type: feedback
---

Owner said 2026-10-29: "I have no browser, find a way to show me previews; Drive or email is fine."
**Why:** he follows sessions from his phone through Remote Control; artifact/site links don't help him there.
**How to apply:** send screenshots with SendUserFile (display render) as soon as a piece is ready; Drive/Gmail with
images only if he asks, since base64 uploads are costly. Related: [[matan-launch-state]].

**2026-10-29 later:** owner rejected previews from the dev server ("not processed, no shortcuts"): test names
("בדיקת ביט"), dev N button, preview badge, half-empty phones. Previews must come from a production build on the
clean tromot_preview DB (/root/preview-db.sh, /root/preview.env) or from the live site, and only after a full
art-direction → critique → polish pass. Check every image yourself before sending.
