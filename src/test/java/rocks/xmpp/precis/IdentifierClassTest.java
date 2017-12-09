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

import org.testng.Assert;
import org.testng.annotations.Test;

import static rocks.xmpp.precis.PrecisProfiles.USERNAME_CASE_MAPPED;

/**
 * @author Christian Schudt
 */
public class IdentifierClassTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldNotAllowNonCharacter() {
        USERNAME_CASE_MAPPED.prepare("\uFDD0");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldNotAllowOldHangulJamoCharacters() {
        USERNAME_CASE_MAPPED.prepare("\uA960");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldNotAllowIgnorableCharacters() {
        USERNAME_CASE_MAPPED.prepare("\u034F");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldNotAllowControlCharacters() {
        USERNAME_CASE_MAPPED.prepare("\u061C");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldNotAllowSymbols() {
        USERNAME_CASE_MAPPED.prepare("\u265A"); // BLACK CHESS KING
    }

    @Test
    public void testHasCompat() {
        Assert.assertTrue(PrecisProfile.hasCompatibilityEquivalent(0x2163)); // ROMAN NUMERAL FOUR
    }

    @Test
    public void shouldBeExceptionallyValid() {
        USERNAME_CASE_MAPPED.prepare("\u03C2\u00DF");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldBeExceptionallyDisallowed() {
        USERNAME_CASE_MAPPED.prepare("\u3032");
    }

    /**
     * Tests this rule for "Unassigned":
     * <p/>
     * General_Category(cp) is in {Cn} and
     * Noncharacter_Code_Point(cp) = False
     */
    @Test
    public void testUnassigned() {
        // Unassigned code points
        Assert.assertTrue(PrecisProfile.isUnassigned(0x2065));
        Assert.assertTrue(PrecisProfile.isUnassigned(0x05FF));

        // Non-characters
        Assert.assertFalse(PrecisProfile.isUnassigned(0xFFFF));
        Assert.assertFalse(PrecisProfile.isUnassigned(0xFDD0));
    }
}
