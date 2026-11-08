# Google Play launch: Yair Browser (monthly subscription)

Build: `YAIR_KS=<keystore> YAIR_KS_PASS=<pass> ./gradlew :app:bundlePlayRelease`
-> `app/build/outputs/bundle/playRelease/app-play-release.aab` (applicationId `il.yair.browser`, versionCode 3, target API 36)

Flavors: `sideload` = free APK, no billing. `play` = needs active subscription (paywall).

## Play Console steps (owner)
1. Developer account (one-time fee), create app "Yair Browser", language Hebrew, paid via subscription.
2. Monetize > Subscriptions > create product id **`yair_monthly`** (exact), base plan monthly auto-renewing, set price (and an optional free trial offer). Activate it.
3. Upload the AAB first to Internal testing; add licence testers (Settings > License testing) and verify: paywall shows price, purchase, cancel, restore.
4. Opt in to Play App Signing. The keystore used here is the upload key: keep it safe.
5. Store listing: icon 512 (`branding/icon512.png`), feature graphic 1024x500, 2+ phone screenshots, short and full description (see below).
6. App content: privacy policy URL (draft in `PRIVACY-POLICY.md`, host it on a public URL), Data safety (see below), content rating questionnaire, target audience (not children), ads: none.
7. Subscription disclosure: price, monthly renewal and cancellation are shown on the paywall in-app and must also be in the listing description.

## Data safety answers
No data collected or shared by the app. Domain approvals, PIN hash and discovered-domain list stay on the device. Purchases are handled by Google Play.

## Not verified here
Billing flow cannot be tested without a Play Console product; only compilation and manifest (BILLING permission, target 36) were checked.
Entitlement is checked client-side with a 3-day offline grace; there is no server-side verification.
Target API requirement (36 from 2026-08-31) is per Google Play help; confirm in Console.

## Suggested listing text (Hebrew)
Short: דפדפן בטוח שפותח רק אתרים מאושרים.
Full: Yair Browser הוא דפדפן לאנדרואיד שפותח רק רשימת אתרים מאושרת: בנקים, שירותי ממשלה, דואר, שידוכים, תורה והלכה. HTTPS בלבד, בלי רשתות חברתיות, תקשורת וקניות. ניהול הרשימה בקוד PIN. מנוי חודשי, מתחדש אוטומטית, ביטול בכל עת ב-Google Play.
