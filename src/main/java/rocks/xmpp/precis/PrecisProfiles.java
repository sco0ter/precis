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

/**
 * This class provides common PRECIS profiles, mainly specified by <a href="https://tools.ietf.org/html/rfc8265">Preparation, Enforcement, and Comparison of Internationalized Strings
 * Representing Usernames and Passwords</a> (RFC 8265). Each profile offers methods for preparation, enforcement and comparison of Unicode strings.
 * <h2>Preparation</h2>
 * Preparation ensures, that a string contains only valid characters, but usually does not apply any mapping rules.
 * <pre>
 * {@code
 * PrecisProfiles.USERNAME_CASE_MAPPED.prepare("UpperCaseUsername");
 * }
 * </pre>
 * If the passed string contains any invalid characters, an {@link InvalidCodePointException} is thrown:
 * <pre>
 * {@code
 * PrecisProfiles.USERNAME_CASE_MAPPED.prepare("Username\u265A"); // Contains symbol, throws exception.
 * }
 * </pre>
 * <h2>Enforcement</h2>
 * Enforcement applies specific rules (e.g. case mapping) to the string for the purpose of determining if the string can be used
 * in a given protocol slot.
 * <pre>
 * {@code
 * String enforced = PrecisProfiles.USERNAME_CASE_MAPPED.enforce("UpperCaseUsername"); // uppercaseusername
 * }
 * </pre>
 * <h2>Comparison</h2>
 * You can just use {@link PrecisProfile#toComparableString(CharSequence)} to check, if two strings compare to each other,
 * e.g.:
 * <pre>
 * {@code
 * PrecisProfile profile = PrecisProfiles.USERNAME_CASE_MAPPED;
 * if (profile.toComparableString("foobar").equals(profile.toComparableString("FooBar"))) {
 *     // username already exists.
 * }
 * }
 * </pre>
 * Or you can use {@link PrecisProfile} as a {@link java.util.Comparator}:
 * <pre>
 * {@code
 * if (profile.compare("foobar", "FooBar") == 0) {
 *     // username already exists.
 * }
 * }
 * </pre>
 * Note that a profile may use different rules during comparison than during enforcement (as the Nickname profile, RFC 8266).
 *
 * @author Christian Schudt
 * @see PrecisProfile
 */
public final class PrecisProfiles {

    /**
     * The "UsernameCaseMapped Profile" specified in "Preparation, Enforcement, and Comparison of Internationalized Strings
     * Representing Usernames and Passwords", <a href="https://tools.ietf.org/html/rfc8265">RFC 8265</a>.
     *
     * @see <a href="https://tools.ietf.org/html/rfc8265#section-3.3">3.3.  UsernameCaseMapped Profile</a>
     */
    public final static PrecisProfile USERNAME_CASE_MAPPED = new UsernameProfile(true);

    /**
     * The "UsernameCasePreserved Profile" specified in "Preparation, Enforcement, and Comparison of Internationalized Strings
     * Representing Usernames and Passwords", <a href="https://tools.ietf.org/html/rfc8265">RFC 8265</a>.
     *
     * @see <a href="https://tools.ietf.org/html/rfc8265#section-3.4">3.4.  UsernameCasePreserved Profile</a>
     */
    public static final PrecisProfile USERNAME_CASE_PRESERVED = new UsernameProfile(false);

    /**
     * The "OpaqueString Profile" specified in "Preparation, Enforcement, and Comparison of Internationalized Strings
     * Representing Usernames and Passwords", <a href="https://tools.ietf.org/html/rfc8265">RFC 8265</a>.
     *
     * @see <a href="https://tools.ietf.org/html/rfc8265#section-4.2">4.2.  OpaqueString Profile</a>
     */
    public static final PrecisProfile OPAQUE_STRING = new OpaqueStringProfile();

    /**
     * The "Nickname Profile" specified in "Preparation, Enforcement, and Comparison
     * of Internationalized Strings Representing Nicknames", <a href="https://tools.ietf.org/html/rfc8266">RFC 8266</a>.
     *
     * @see <a href="https://tools.ietf.org/html/rfc8266">Preparation, Enforcement, and Comparison of Internationalized Strings
     * Representing Nicknames</a>
     */
    public static final PrecisProfile NICKNAME = new NicknameProfile();

    /**
     * A profile for preparing and enforcing international domain names.
     * While not an official PRECIS profile, this profiles applies the mapping rules described in <a href="https://tools.ietf.org/html/rfc5895#section-2">RFC 5895 2.  The General Procedure</a>
     * to a domain name.
     *
     * @see <a href="https://tools.ietf.org/html/rfc5895#section-2">RFC 5895 2.  The General Procedure</a>
     */
    public static final PrecisProfile IDN = new IDNProfile();

    /**
     * A profile used to prepare and enforce localparts of XMPP addresses (JIDs), specified in
     * <a href="https://tools.ietf.org/html/rfc7622">RFC 7622</a>
     *
     * @see <a href="https://tools.ietf.org/html/rfc7622#section-3.3">RFC 7622 3.3.  Localpart</a>
     */
    public static final PrecisProfile XMPP_LOCALPART = new XmppLocalpartProfile();

    private PrecisProfiles() {
    }
}
