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

import java.text.Normalizer;

/**
 * @author Christian Schudt
 * @see <a href="https://tools.ietf.org/html/rfc7613#section-4.2">4.2.  OpaqueString Profile</a>
 */
final class OpaqueStringProfile extends PrecisProfile {

    OpaqueStringProfile() {
        super(false);
    }

    @Override
    public final String enforce(final CharSequence input) {
        final String enforced = super.enforce(input);
        // A password MUST NOT be zero bytes in length. This rule is to be
        // enforced after any normalization and mapping of code points.
        if (enforced.isEmpty()) {
            throw new IllegalArgumentException("String must not be empty after applying the rules.");
        }
        return enforced;
    }

    @Override
    protected final CharSequence applyWidthMappingRule(final CharSequence input) {
        // 1.  Width-Mapping Rule: Fullwidth and halfwidth characters MUST NOT
        // be mapped to their decomposition mappings
        return input;
    }

    @Override
    protected final CharSequence applyAdditionalMappingRule(final CharSequence input) {
        // 2.  Additional Mapping Rule: Any instances of non-ASCII space MUST be
        // mapped to ASCII space (U+0020); a non-ASCII space is any Unicode
        // code point having a Unicode general category of "Zs" (with the
        // exception of U+0020).
        return WHITESPACE.matcher(input).replaceAll(" ");
    }

    @Override
    protected final CharSequence applyCaseMappingRule(final CharSequence input) {
        // 3.  Case-Mapping Rule: Uppercase and titlecase characters MUST NOT be
        // mapped to their lowercase equivalents.
        return input;
    }

    @Override
    protected final CharSequence applyNormalizationRule(final CharSequence input) {
        // 4.  Normalization Rule: Unicode Normalization Form C (NFC) MUST be
        // applied to all characters.
        return Normalizer.normalize(input, Normalizer.Form.NFC);
    }

    @Override
    protected final CharSequence applyDirectionalityRule(final CharSequence input) {
        // 5.  Directionality Rule: There is no directionality rule.
        return input;
    }
}
