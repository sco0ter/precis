/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 Christian Schudt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package rocks.xmpp.precis;

import org.testng.annotations.Test;

import static rocks.xmpp.precis.PrecisProfiles.XMPP_LOCALPART;

import java.util.Arrays;

/**
 * Tests for {@link PrecisProfiles#XMPP_LOCALPART}.
 *
 * @author Florian Schmaus
 */
public class XmppLocalpartProfileTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testQuoteDisallowed() {
        XMPP_LOCALPART.enforce("\"");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAmpersandDisallowed() {
        XMPP_LOCALPART.enforce("&");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testApostropheDisallowed() {
        XMPP_LOCALPART.enforce("\'");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSolidusDisallowed() {
        XMPP_LOCALPART.enforce("/");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testColonDisallowed() {
        XMPP_LOCALPART.enforce(":");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLessThanSignDisallowed() {
        XMPP_LOCALPART.enforce("<");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGreaterThanSignDisallowed() {
        XMPP_LOCALPART.enforce(">");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCommercialAtDisallowed() {
        XMPP_LOCALPART.enforce("@");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testEmptyStringDisallowed() {
        XMPP_LOCALPART.enforce("");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLongerThan1023Disallowed() {
        char[] chars = new char[1024];
        Arrays.fill(chars, 'a');
        String stringExceedingLengthLimit = new String(chars);
        XMPP_LOCALPART.enforce(stringExceedingLengthLimit);
    }
}
