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

import java.util.function.Function;

import static rocks.xmpp.precis.PrecisProfiles.USERNAME_CASE_MAPPED;

/**
 * Tests for {@link PrecisProfiles#USERNAME_CASE_MAPPED}.
 *
 * @author Christian Schudt
 */
public class UsernameCaseMappedProfileTest {

    @Test
    public void testAllowedStrings() {

        Assert.assertEquals(USERNAME_CASE_MAPPED.enforce("juliet@example.com"), "juliet@example.com");
        Assert.assertEquals(USERNAME_CASE_MAPPED.enforce("fussball"), "fussball");

        Assert.assertEquals(USERNAME_CASE_MAPPED.enforce("fu\u00DFball"), "fu\u00DFball");
        Assert.assertEquals(USERNAME_CASE_MAPPED.enforce("\u03C0"), "\u03C0");
        Assert.assertEquals(USERNAME_CASE_MAPPED.enforce("\u03A3"), "\u03C3");
        Assert.assertEquals(USERNAME_CASE_MAPPED.enforce("\u03C3"), "\u03C3");
        Assert.assertEquals(USERNAME_CASE_MAPPED.enforce("\u03C2"), "\u03C2");
        Assert.assertEquals(USERNAME_CASE_MAPPED.enforce("I"), "i");

        Assert.assertEquals(USERNAME_CASE_MAPPED.enforce("\u03B0"), "\u03B0");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSpaceCharacters() {
        USERNAME_CASE_MAPPED.enforce("foo bar");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRomanFour() {
        USERNAME_CASE_MAPPED.enforce("henry\u2163");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInfinity() {
        USERNAME_CASE_MAPPED.enforce("\u221E");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testBlackChessKing() {
        USERNAME_CASE_MAPPED.enforce("\u265A");
    }

    @Test
    public void testLetterDigits() {
        USERNAME_CASE_MAPPED.enforce("~");
        USERNAME_CASE_MAPPED.enforce("a");
    }

    @Test
    public void testPrintableCharacters() {
        USERNAME_CASE_MAPPED.enforce("!");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSpaceCharacters1() {
        USERNAME_CASE_MAPPED.prepare(" ");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSpaceCharacters2() {
        USERNAME_CASE_MAPPED.prepare("\t");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSpaceCharacters3() {
        USERNAME_CASE_MAPPED.prepare("\n");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSymbolCharacters1() {
        USERNAME_CASE_MAPPED.prepare("\u2600");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSymbolCharacters2() {
        USERNAME_CASE_MAPPED.prepare("\u26d6");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSymbolCharacters3() {
        USERNAME_CASE_MAPPED.prepare("\u26FF");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCompatibilityEquivalent() {
        USERNAME_CASE_MAPPED.prepare("\uFB00");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testOtherLetterDigits1() {
        USERNAME_CASE_MAPPED.prepare("\u01C5"); // Lt CAPITAL LETTER D WITH SMALL LETTER Z WITH CARON
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testOtherLetterDigits2() {
        USERNAME_CASE_MAPPED.prepare("\u16EE"); // Nl RUNIC ARLAUG SYMBOL
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testOtherLetterDigits3() {
        USERNAME_CASE_MAPPED.prepare("\u00B2"); // No SUPERSCRIPT TWO
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testOtherLetterDigits4() {
        USERNAME_CASE_MAPPED.prepare("\u0488"); // Me COMBINING CYRILLIC HUNDRED THOUSANDS SIGN
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testEmptyUsername() {
        USERNAME_CASE_MAPPED.enforce("");
    }

    @Test
    public void testCompositeCharactersAndCombiningSequence() {
        CharSequence ang = USERNAME_CASE_MAPPED.enforce("\u212B"); // angstrom sign
        CharSequence a = USERNAME_CASE_MAPPED.enforce("A\u030A"); // A + ring
        CharSequence b = USERNAME_CASE_MAPPED.enforce("\u00C5");       // A with ring
        Assert.assertEquals(a, b);
        Assert.assertEquals(a, ang);

        CharSequence c = USERNAME_CASE_MAPPED.enforce("c\u0327"); // c + cedille
        CharSequence d = USERNAME_CASE_MAPPED.enforce("\u00E7");       // c cedille
        Assert.assertEquals(c, d);

        CharSequence e = USERNAME_CASE_MAPPED.enforce("R\u030C");
        CharSequence f = USERNAME_CASE_MAPPED.enforce("\u0158");
        Assert.assertEquals(e, f);
    }

    @Test
    public void testConfusableCharacters() {
        CharSequence a = USERNAME_CASE_MAPPED.enforce("A"); // LATIN CAPITAL LETTER A
        CharSequence b = USERNAME_CASE_MAPPED.enforce("\u0410"); // CYRILLIC CAPITAL LETTER A
        Assert.assertNotEquals(a, b);
    }

    @Test
    public void testWidthMapping() {
        CharSequence a = USERNAME_CASE_MAPPED.enforce("\uFF21\uFF22");
        CharSequence b = USERNAME_CASE_MAPPED.enforce("ab");
        Assert.assertEquals(a, b);
    }

    @Test(enabled = false)
    public void testPerformance() {
        int n = 1000000;
        long c = 0;
        for (int i = 0; i < n; i++) {
            long nano = System.nanoTime();
            USERNAME_CASE_MAPPED.enforce("äääääääääääääääääääääääääääääääääääääääääääääää");
            c += System.nanoTime() - nano;
        }
        System.out.println(c / n);
    }

    @Test
    public void testIdempotencyEnforcement() {
        testIdempotency(USERNAME_CASE_MAPPED::enforce);
    }

    @SuppressWarnings("EmptyCatch")
    static void testIdempotency(Function<CharSequence, String> rules) {
        for (int cp = Character.MIN_CODE_POINT; cp < Character.MAX_CODE_POINT; cp++) {
            String input = new String(Character.toChars(cp));
            try {
                String applied = rules.apply(input);
                String applied2 = rules.apply(applied);
                Assert.assertEquals(applied, applied2);
            } catch (IllegalArgumentException ignore) {
            }
        }
    }
}
