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

import java.text.Normalizer;
import java.util.function.Function;

/**
 * The implementation of the PRECIS: Nickname Profile, RFC 8266.
 *
 * @author Christian Schudt
 * @see <a href="https://tools.ietf.org/html/rfc8266">Preparation, Enforcement, and Comparison of Internationalized Strings Representing Nicknames</a>
 */
final class NicknameProfile extends PrecisProfile {

    private static final long serialVersionUID = 8561449693996385797L;

    NicknameProfile() {
        super(false);
    }

    @Override
    public final String enforce(final CharSequence input) {
        // An entity that performs enforcement according to this profile MUST
        // prepare an input string as described in Section 2.2 and MUST also
        // apply the following rules specified in Section 2.1 in the order
        // shown:

        // 1.  Additional Mapping Rule
        // 2.  Normalization Rule
        final Function<CharSequence, String> rules = in -> prepare(applyNormalizationRule(applyAdditionalMappingRule(in)));
        final String enforced = stabilize(input, rules);

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
     * Comparison uses different rules than enforcement.
     * <p>
     * E.g. "Foo Bar" and "foo bar" would yield the same result. Both comparable string are equal.
     *
     * @param input The input string.
     * @return The comparable string.
     * @see <a href="https://tools.ietf.org/html/rfc8266#section-2.4">2.4.  Comparison</a>
     */
    @Override
    public final String toComparableString(final CharSequence input) {
        // An entity that performs comparison of two strings according to this
        // profile MUST prepare each string as specified in Section 2.2 and MUST
        // apply the following rules specified in Section 2.1 in the order
        // shown:
        // 1.  Additional Mapping Rule
        // 2.  Case Mapping Rule
        // 3.  Normalization Rule
        return stabilize(input, super::enforce);
    }

    @Override
    protected final CharSequence applyWidthMappingRule(final CharSequence input) {
        // 1.  Width Mapping Rule: There is no width mapping rule
        return input;
    }

    @Override
    protected final CharSequence applyAdditionalMappingRule(final CharSequence input) {
        // 2.  Additional Mapping Rule: The additional mapping rule consists of
        // the following sub-rules.

        // a.  Map any instances of non-ASCII space to SPACE (U+0020); a
        // non-ASCII space is any Unicode code point having a general
        // category of "Zs", naturally with the exception of SPACE
        // (U+0020).  (The inclusion of only ASCII space prevents
        // confusion with various non-ASCII space code points, many of
        // which are difficult to reproduce across different input
        // methods.)
        final String mapped = WHITESPACE.matcher(input).replaceAll(" ");

        // b. Remove any instances of the ASCII space character at the
        // beginning or end of a nickname (e.g., "stpeter " is mapped to
        // "stpeter").
        final String trimmed = mapped.trim();

        // c. Map interior sequences of more than one ASCII space character
        // to a single ASCII space character (e.g., "St  Peter" is
        // mapped to "St Peter").
        return trimmed.replaceAll("[ ]+", " ");
    }

    @Override
    protected final CharSequence applyCaseMappingRule(final CharSequence input) {
        // 3.  Case Mapping Rule: Apply the Unicode toLowerCase() operation, as
        // defined in the Unicode Standard
        return caseMap(input);
    }

    @Override
    protected final CharSequence applyNormalizationRule(final CharSequence input) {
        // 4.  Normalization Rule: Apply Unicode Normalization Form KC.
        return Normalizer.normalize(input, Normalizer.Form.NFKC);
    }

    @Override
    protected final CharSequence applyDirectionalityRule(final CharSequence input) {
        // 5.  Directionality Rule: There is no directionality rule.
        return input;
    }
}
