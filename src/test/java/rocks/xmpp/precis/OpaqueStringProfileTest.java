/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 Christian Schudt
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

import static rocks.xmpp.precis.PrecisProfiles.OPAQUE_STRING;

/**
 * @author Christian Schudt
 */
public class OpaqueStringProfileTest {

    @Test
    public void testAllowedStrings() {
        // ASCII space is allowed
        Assert.assertEquals(OPAQUE_STRING.enforce("correct horse battery staple"), "correct horse battery staple");
        Assert.assertEquals(OPAQUE_STRING.enforce("Correct Horse Battery Staple"), "Correct Horse Battery Staple");
        Assert.assertEquals(OPAQUE_STRING.enforce("πßå"), "πßå");
        Assert.assertEquals(OPAQUE_STRING.enforce("Jack of \u2666s"), "Jack of \u2666s");
        Assert.assertEquals(OPAQUE_STRING.enforce("foo\u1680bar"), "foo bar");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testZeroLength() {
        OPAQUE_STRING.enforce("");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testControlCharacters() {
        OPAQUE_STRING.enforce("my cat is a \u0009by");
    }
}
