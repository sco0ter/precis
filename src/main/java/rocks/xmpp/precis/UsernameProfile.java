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

import java.text.Bidi;
import java.text.Normalizer;

/**
 * The base class for user name profiles.
 *
 * @author Christian Schudt
 * @see <a href="https://tools.ietf.org/html/rfc7613#section-3">3.  Usernames</a>
 */
final class UsernameProfile extends PrecisProfile {

    private final boolean caseMapped;

    UsernameProfile(boolean caseMapped) {
        super(true);
        this.caseMapped = caseMapped;
    }

    @Override
    public final String prepare(final CharSequence input) {
        // An entity that prepares a string according to this profile MUST first
        // map fullwidth and halfwidth characters to their decomposition
        // mappings (see Unicode Standard Annex #11 [UAX11]).
        final CharSequence mapped = widthMap(input);

        // ensure that the string consists only of Unicode code points that conform to the PRECIS IdentifierClass.
        return super.prepare(mapped);
    }

    @Override
    public final String enforce(final CharSequence input) {
        final String enforced = super.enforce(input);
        // A username MUST NOT be zero bytes in length.  This rule is to be
        // enforced after any normalization and mapping of code points.
        if (enforced.isEmpty()) {
            throw new InvalidCodePointException("A username must not be empty.");
        }

        return enforced;
    }

    @Override
    protected final CharSequence applyWidthMappingRule(final CharSequence input) {
        // 1. Width-Mapping Rule: Applied as part of preparation (see above).
        return input;
    }

    @Override
    protected final CharSequence applyAdditionalMappingRule(final CharSequence input) {
        // 2.  Additional Mapping Rule: There is no additional mapping rule.
        return input;
    }

    @Override
    protected final CharSequence applyCaseMappingRule(final CharSequence input) {
        // 3.  Case-Mapping Rule: Uppercase and titlecase characters MUST NOT be
        // mapped to their lowercase equivalents;

        // 3.  Case-Mapping Rule: Uppercase and titlecase characters MUST be
        // mapped to their lowercase equivalents, preferably using Unicode
        // Default Case Folding as defined in the Unicode Standard
        return caseMapped ? caseFold(input) : input;
    }

    @Override
    protected final CharSequence applyNormalizationRule(final CharSequence input) {
        // 4.  Normalization Rule: Unicode Normalization Form C (NFC) MUST be applied to all characters.
        return Normalizer.normalize(input, Normalizer.Form.NFC);
    }

    @Override
    protected final CharSequence applyDirectionalityRule(final CharSequence input) {
        // 5.  Directionality Rule: Applications MUST apply the "Bidi Rule"
        // defined in [RFC5893] to strings that contain right-to-left
        // characters (i.e., each of the six conditions of the Bidi Rule
        // must be satisfied).
        if (Bidi.requiresBidi(input.toString().toCharArray(), 0, input.length())) {
            checkBidiRule(input);
        }
        return input;
    }
}
