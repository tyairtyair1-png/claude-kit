package il.yair.browser

import org.junit.Assert.assertEquals
import org.junit.Test

class PolicyTest {
    private val p = Policy(
        listOf(Rule("gov.il", true), Rule("drive.google.com", true), Rule("gmail.com", true)),
        listOf(Rule("accounts.google.com", false)), emptySet())

    @Test fun httpsOnly() {
        assertEquals(Decision.ALLOW, p.decide("https://www.gov.il/he"))
        assertEquals(Decision.BLOCK_SCHEME, p.decide("http://www.gov.il"))
        assertEquals(Decision.BLOCK_SCHEME, p.decide("intent://x"))
    }
    @Test fun subdomainBoundary() {
        assertEquals(Decision.ALLOW, p.decide("https://my.gov.il/x"))
        assertEquals(Decision.BLOCK_HOST, p.decide("https://evilgov.il/"))
        assertEquals(Decision.BLOCK_HOST, p.decide("https://gov.il.evil.com/"))
        assertEquals(Decision.BLOCK_HOST, p.decide("https://good@evil.com@gov.il/"))
    }
    @Test fun noBlanketGoogle() {
        assertEquals(Decision.ALLOW, p.decide("https://drive.google.com/"))
        assertEquals(Decision.BLOCK_HOST, p.decide("https://www.google.com/"))
        assertEquals(Decision.BLOCK_HOST, p.decide("https://mail.google.com/"))
    }
    @Test fun exactApprovalDoesNotCoverSubdomains() {
        assertEquals(Decision.ALLOW, p.decide("https://accounts.google.com/"))
        assertEquals(Decision.BLOCK_HOST, p.decide("https://x.accounts.google.com/"))
    }
    @Test fun ipsPortsAndHardDeny() {
        assertEquals(Decision.BLOCK_HOST, p.decide("https://1.2.3.4/"))
        assertEquals(Decision.BLOCK_HOST, p.decide("https://www.gov.il:8443/"))
        assertEquals(Decision.HARD_DENY, p.decide("https://web.whatsapp.com/"))
        val q = Policy(listOf(Rule("facebook.com", true)), emptyList(), emptySet())
        assertEquals(Decision.HARD_DENY, q.decide("https://facebook.com/"))
    }
    @Test fun removedBase() {
        val q = Policy(listOf(Rule("gov.il", true)), emptyList(), setOf("gov.il"))
        assertEquals(Decision.BLOCK_HOST, q.decide("https://gov.il/"))
    }
    @Test fun exactInfraHost() {
        val q = Policy(listOf(Rule("www.google.com", false)), emptyList(), emptySet())
        assertEquals(Decision.ALLOW, q.decide("https://www.google.com/search?q=x"))
        assertEquals(Decision.BLOCK_HOST, q.decide("https://mail.google.com/"))
        assertEquals(Decision.BLOCK_HOST, q.decide("https://a.www.google.com/"))
    }
}
