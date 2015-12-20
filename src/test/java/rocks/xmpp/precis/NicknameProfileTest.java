/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Christian Schudt
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

import org.testng.Assert;
import org.testng.annotations.Test;

import static rocks.xmpp.precis.PrecisProfiles.NICKNAME;

/**
 * @author Christian Schudt
 */
public class NicknameProfileTest {

    @Test
    public void shouldReplaceNonAsciiSpaces() {
        Assert.assertEquals(NICKNAME.enforce("a\u00A0a\u1680a\u2000a\u2001a\u2002a\u2003a\u2004a\u2005a\u2006a\u2007a\u2008a\u2009a\u200Aa\u202Fa\u205Fa\u3000a"), "a a a a a a a a a a a a a a a a a");
    }

    @Test
    public void shouldTrim() {
        Assert.assertEquals(NICKNAME.enforce("stpeter "), "stpeter");
    }

    @Test
    public void shouldMapToSingleSpace() {
        Assert.assertEquals(NICKNAME.enforce("st    peter"), "st peter");
    }

    @Test
    public void shouldNormalizeNFKC() {
        Assert.assertEquals(NICKNAME.enforce("\u2163"), "IV");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldNotBeEmpty() {
        NICKNAME.enforce("");
    }

    @Test(enabled = false)
    public void testExamples() {
        Assert.assertEquals(NICKNAME.enforce("Foo"), "foo");
        Assert.assertEquals(NICKNAME.enforce("foo"), "foo");
        Assert.assertEquals(NICKNAME.enforce("Foo Bar"), "foo bar");
        Assert.assertEquals(NICKNAME.enforce("foo bar"), "foo bar");
        Assert.assertEquals(NICKNAME.enforce("\u03A3"), "\u03C3");
        Assert.assertEquals(NICKNAME.enforce("\u03C3"), "\u03C3");
        Assert.assertEquals(NICKNAME.enforce("\u03C2"), "\u03C3");
        Assert.assertEquals(NICKNAME.enforce("\u265A"), "\u265A");
        Assert.assertEquals(NICKNAME.enforce("Richard \u2163"), "richard iv");
    }
}
