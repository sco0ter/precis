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

import java.text.Bidi;
import java.text.Normalizer;

/**
 * The base class for user name profiles.
 *
 * @author Christian Schudt
 * @see <a href="https://tools.ietf.org/html/rfc8265#section-3">3.  Usernames</a>
 */
final class UsernameProfile extends PrecisProfile {

    private static final long serialVersionUID = 848281423907881855L;

    private final boolean caseMapped;

    UsernameProfile(boolean caseMapped) {
        super(true);
        this.caseMapped = caseMapped;
    }

    @Override
    public final String prepare(final CharSequence input) {
        // 1. Apply the width mapping rule specified in Section 3.4.1.  It is
        // necessary to apply the rule at this point because otherwise the
        // PRECIS "HasCompat" category specified in Section 9.17 of
        // [RFC8264] would forbid fullwidth and halfwidth code points.
        final CharSequence mapped = applyWidthMappingRule(input);

        // 2. Ensure that the string consists only of Unicode code points that conform to the PRECIS IdentifierClass.
        return super.prepare(mapped);
    }

    @Override
    public final String enforce(final CharSequence input) {
        // 1.  Case Mapping Rule
        // 2.  Normalization Rule
        // 3.  Directionality Rule
        final String enforced = applyDirectionalityRule(applyNormalizationRule(applyCaseMappingRule(prepare(input)))).toString();
        // A username MUST NOT be zero bytes in length. This rule is to be
        // enforced after any normalization and mapping of code points.
        if (enforced.isEmpty()) {
            throw new IllegalArgumentException("A username must not be empty.");
        }

        return enforced;
    }

    @Override
    protected final CharSequence applyWidthMappingRule(final CharSequence input) {
        // 1. Width Mapping Rule: Map fullwidth and halfwidth code points to
        // their decomposition mappings (see Unicode Standard Annex #11 [UAX11]).
        return widthMap(input);
    }

    @Override
    protected final CharSequence applyAdditionalMappingRule(final CharSequence input) {
        // 2.  Additional Mapping Rule: There is no additional mapping rule.
        return input;
    }

    @Override
    protected final CharSequence applyCaseMappingRule(final CharSequence input) {
        // 3. Case Mapping Rule: There is no case mapping rule.

        // 3.  Case Mapping Rule: Map uppercase and titlecase code points to
        // their lowercase equivalents, preferably using the Unicode
        // toLowerCase() operation as defined in the Unicode Standard [Unicode]
        return caseMapped ? caseMap(input) : input;
    }

    @Override
    protected final CharSequence applyNormalizationRule(final CharSequence input) {
        // 4.  Normalization Rule: Apply Unicode Normalization Form C (NFC) to all strings.
        return Normalizer.normalize(input, Normalizer.Form.NFC);
    }

    @Override
    protected final CharSequence applyDirectionalityRule(final CharSequence input) {
        // 5. Directionality Rule: Apply the "Bidi Rule" defined in [RFC5893]
        // to strings that contain right-to-left code points (i.e., each of
        // the six conditions of the Bidi Rule must be satisfied); for
        // strings that do not contain right-to-left code points, there is
        // no special processing for directionality.
        if (Bidi.requiresBidi(input.toString().toCharArray(), 0, input.length())) {
            checkBidiRule(input);
        }
        return input;
    }
}
