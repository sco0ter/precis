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
 * The implementation of the PRECIS: Nickname Profile, RFC 7700.
 *
 * @author Christian Schudt
 * @see <a href="https://tools.ietf.org/html/rfc7700">Preparation, Enforcement, and Comparison of Internationalized Strings Representing Nicknames</a>
 */
final class NicknameProfile extends PrecisProfile {

    NicknameProfile() {
        super(false);
    }

    @Override
    public final String enforce(final CharSequence input) {
        // An entity that performs enforcement according to this profile MUST
        // prepare a string as described in Section 2.2 and MUST also apply the
        // following rules specified in Section 2.1 in the order shown:

        // 1.  Additional Mapping Rule
        // 2.  Normalization Rule
        // 3.  Directionality Rule

        final String enforced = applyDirectionalityRule(applyNormalizationRule(applyAdditionalMappingRule(prepare(input)))).toString();

        // After all of the foregoing rules have been enforced, the entity MUST
        // ensure that the nickname is not zero bytes in length (this is done
        // after enforcing the rules to prevent applications from mistakenly
        // omitting a nickname entirely, because when internationalized
        // characters are accepted a non-empty sequence of characters can result
        // in a zero-length nickname after canonicalization).
        if (enforced.isEmpty()) {
            throw new IllegalArgumentException("Nickname must not be empty after applying the rules.");
        }
        return enforced;
    }

    /**
     * Compares two nicknames with each other.
     *
     * @param o1 The first string.
     * @param o2 The second string.
     * @return 0 of both strings are equal with regard to this profile, otherwise the comparison result.
     * @see <a href="https://tools.ietf.org/html/rfc7700#section-2.4">2.4.  Comparison</a>
     */
    @Override
    public final int compare(CharSequence o1, CharSequence o2) {
        return applyRulesForComparison(o1).compareTo(applyRulesForComparison(o2));
    }

    private String applyRulesForComparison(CharSequence input) {
        // An entity that performs comparison of two strings according to this
        // profile MUST prepare each string as specified in Section 2.2 and MUST
        // apply the following rules specified in Section 2.1 in the order
        // shown:
        // 1.  Additional Mapping Rule
        // 2.  Case Mapping Rule
        // 3.  Normalization Rule
        // 4.  Directionality Rule
        return applyDirectionalityRule(
                applyNormalizationRule(
                        applyCaseMappingRule(
                                applyAdditionalMappingRule(
                                        prepare(input))))).toString();
    }

    @Override
    protected final CharSequence applyWidthMappingRule(final CharSequence input) {
        // 1.  Width Mapping Rule: There is no width-mapping rule
        return input;
    }

    @Override
    protected final CharSequence applyAdditionalMappingRule(final CharSequence input) {
        // 2.  Additional Mapping Rule: The additional mapping rule consists of
        // the following sub-rules.

        // 1.  Any instances of non-ASCII space MUST be mapped to ASCII
        // space (U+0020); a non-ASCII space is any Unicode code point
        // having a general category of "Zs", naturally with the
        // exception of U+0020.
        final String mapped = WHITESPACE.matcher(input).replaceAll(" ");

        // 2.  Any instances of the ASCII space character at the beginning
        // or end of a nickname MUST be removed (e.g., "stpeter " is
        // mapped to "stpeter").
        final String trimmed = mapped.trim();

        // 3.  Interior sequences of more than one ASCII space character
        // MUST be mapped to a single ASCII space character (e.g.,
        // "St  Peter" is mapped to "St Peter").
        return trimmed.replaceAll("[ ]+", " ");
    }

    @Override
    protected final CharSequence applyCaseMappingRule(final CharSequence input) {

        // 3.  Case Mapping Rule: Uppercase and titlecase characters MUST be
        // mapped to their lowercase equivalents using Unicode Default Case
        // Folding as defined in the Unicode Standard [Unicode] (at the time
        // of this writing, the algorithm is specified in Chapter 3 of
        // [Unicode7.0]).  In applications that prohibit conflicting
        // nicknames, this rule helps to reduce the possibility of confusion
        // by ensuring that nicknames differing only by case (e.g.,
        // "stpeter" vs. "StPeter") would not be presented to a human user
        // at the same time.
        return caseFold(input);
    }

    @Override
    protected final CharSequence applyNormalizationRule(final CharSequence input) {

        // 4.  Normalization Rule: The string MUST be normalized using Unicode
        // Normalization Form KC (NFKC).  Because NFKC is more "aggressive"
        // in finding matches than other normalization forms (in the
        // terminology of Unicode, it performs both canonical and
        // compatibility decomposition before recomposing code points), this
        // rule helps to reduce the possibility of confusion by increasing
        // the number of characters that would match (e.g., U+2163 ROMAN
        // NUMERAL FOUR would match the combination of U+0049 LATIN CAPITAL
        // LETTER I and U+0056 LATIN CAPITAL LETTER V).
        return Normalizer.normalize(input, Normalizer.Form.NFKC);
    }

    @Override
    protected final CharSequence applyDirectionalityRule(final CharSequence input) {
        // 5.  Directionality Rule: There is no directionality rule.
        return input;
    }
}
