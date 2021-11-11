package rocks.xmpp.precis;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests for {@link PrecisProfiles#IDN}.
 *
 * @author Christian Schudt
 */
public class IDNProfileTest {

    @Test
    public void testDots() {
        String domainName = PrecisProfiles.IDN.enforce("a\u3002b\uFF0Ec\uFF61d");
        Assert.assertEquals(domainName, "a.b.c.d");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNonLDHAsciiCharacters() {
        PrecisProfiles.IDN.enforce("_test");
    }

    @Test
    public void testUnicodeDomain() {
        // Example from: https://tools.ietf.org/html/rfc5122#section-2.7.3
        String domainName = PrecisProfiles.IDN.enforce("\u010Dechy.example");
        Assert.assertEquals(domainName, "\u010Dechy.example");
    }

    @Test
    public void testAsciiDomain() {
        String domainName = PrecisProfiles.IDN.enforce("xn--dmin-moa0i");
        Assert.assertEquals(domainName, "dömäin");

        domainName = PrecisProfiles.IDN.enforce("xn--xample-2of.com");
        Assert.assertEquals(domainName, "еxample.com");
    }

    @Test
    public void testCaseMapping() {
        String domainName = PrecisProfiles.IDN.enforce("DOMAIN");
        Assert.assertEquals(domainName, "domain");
    }
}
