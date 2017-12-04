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

/**
 * This class provides common PRECIS profiles, mainly specified by <a href="https://tools.ietf.org/html/rfc8265">Preparation, Enforcement, and Comparison of Internationalized Strings
 * Representing Usernames and Passwords</a> (RFC 7613). Each profile offers methods for preparation, enforcement and comparison of Unicode strings.
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
 * Usually you can just use <code>String.equals()</code> on two enforced strings to determine if they are equivalent,
 * e.g.:
 * <pre>
 * {@code
 * PrecisProfile profile = PrecisProfiles.USERNAME_CASE_MAPPED;
 * if (profile.enforce("foobar").equals(profile.enforce("FooBar"))) {
 *     // username already exists.
 * }
 * }
 * </pre>
 * However, using {@link PrecisProfile#compare(CharSequence, CharSequence)} is preferable, because a profile may use different rules during comparison than during enforcement (as the Nickname profile, RFC 7700):
 * <pre>
 * {@code
 * if (profile.compare("foobar", "FooBar") == 0) {
 *     // username already exists.
 * }
 * }
 * </pre>
 * Also note that {@link PrecisProfile} implements {@link java.util.Comparator}.
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

    private PrecisProfiles() {
    }
}
