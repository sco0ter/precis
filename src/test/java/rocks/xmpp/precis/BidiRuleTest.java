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

import org.testng.annotations.Test;

/**
 * @author Christian Schudt
 */
public class BidiRuleTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testBidiRule1() {
        // First character is not L, R or AL, but NSM:
        PrecisProfile.checkBidiRule("\u07AA");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testBidiRule2() {
        // RTL label should not contain L characters
        PrecisProfile.checkBidiRule("\u0786test");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testBidiRule3() {
        // RTL label should not end with L character
        PrecisProfile.checkBidiRule("\u0786\u0793a\u07A6");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testBidiRule4() {
        // RTL label should not contain both EN and AN characters.
        PrecisProfile.checkBidiRule("\u0786123\u0660"); // 0660 = Arabic Zero
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testBidiRule5() {
        PrecisProfile.checkBidiRule("abc\u0786a");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testBidiRule6() {
        // LTR label should end with L or EN.
        PrecisProfile.checkBidiRule("a\u0793");
    }

    @Test
    public void testValidBidiString() {
        PrecisProfile.checkBidiRule("\u0627\u0031\u0628");

        // Failed with RFC 3454, but should work with RFC 5893:
        PrecisProfile.checkBidiRule("\u0786\u07AE\u0782\u07B0\u0795\u07A9\u0793\u07A6\u0783\u07AA");
        PrecisProfile.checkBidiRule("\u05D9\u05B4\u05D5\u05D0\u05B8");
    }
}
