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

import java.text.Normalizer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This is the base class for a PRECIS profile. A profile defines a set of rules (width mapping, additional mapping, case mapping, normalization and directionality) and uses one of two string classes, IdentifierClass or FreeformClass, which define the allowed and disallowed characters.
 * <p/>
 * There are three basic use cases you can do with this class:
 * <ul>
 * <li>{@linkplain #prepare(CharSequence) Preparation}: entails only ensuring that the characters in an
 * individual string are allowed by the underlying PRECIS string class.</li>
 * </li>
 * <li>{@linkplain #enforce(CharSequence) Enforcement}: entails applying all of the rules specified for a
 * particular string class or profile thereof to an individual
 * string, for the purpose of determining if the string can be used
 * in a given protocol slot.</li>
 * <li>Comparison: entails applying all of the rules specified for a
 * particular string class or profile thereof to two separate
 * strings, for the purpose of determining if the two strings are
 * equivalent.</li>
 * </ul>
 * <p/>
 *
 * @author Christian Schudt
 * @see <a href="https://tools.ietf.org/html/rfc7564#section-4">4.  String Classes</a>
 * @see <a href="https://tools.ietf.org/html/rfc7564#section-5">5.  Profiles</a>
 */
public abstract class PrecisProfile implements Comparator<CharSequence> {

    /**
     * Maps full- and half-width characters to their decomposition form.
     */
    private static final Map<Character, Character> WIDTH_MAP = new HashMap<>();

    /**
     * Used for the Bidi Rule.
     * EN, ES, CS, ET, ON, BN, or NSM.
     */
    private static final int EN_ES_CS_ET_ON_BN_NSM = 1 << Character.DIRECTIONALITY_EUROPEAN_NUMBER
            | 1 << Character.DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR
            | 1 << Character.DIRECTIONALITY_COMMON_NUMBER_SEPARATOR
            | 1 << Character.DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR
            | 1 << Character.DIRECTIONALITY_OTHER_NEUTRALS
            | 1 << Character.DIRECTIONALITY_BOUNDARY_NEUTRAL
            | 1 << Character.DIRECTIONALITY_NONSPACING_MARK;

    /**
     * Used for the Bidi Rule.
     * L, EN, ES, CS, ET, ON, BN, or NSM.
     */
    private static final int L_EN_ES_CS_ET_ON_BN_NSM = 1 << Character.DIRECTIONALITY_LEFT_TO_RIGHT
            | EN_ES_CS_ET_ON_BN_NSM;

    /**
     * Used for the Bidi Rule.
     * R, AL, AN, EN, ES, CS, ET, ON, BN, or NSM.
     */
    private static final int R_AL_AN_EN_ES_CS_ET_ON_BN_NSM = 1 << Character.DIRECTIONALITY_RIGHT_TO_LEFT
            | 1 << Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC
            | 1 << Character.DIRECTIONALITY_ARABIC_NUMBER
            | EN_ES_CS_ET_ON_BN_NSM;

    /**
     * Used for the Bidi Rule.
     * EN or AN.
     */
    private static final int EN_AN = 1 << Character.DIRECTIONALITY_EUROPEAN_NUMBER
            | 1 << Character.DIRECTIONALITY_ARABIC_NUMBER;

    static final Pattern WHITESPACE = Pattern.compile("\\p{Zs}");

    // Key — Original Character
    // Value — Replacement character
    static {
        // Fullwidth ASCII variants (Latin Symbols, Punctuation, Numbers, and Alphabet)
        for (char c = '\uFF01'; c <= '\uFF5E'; c++) {
            char mapping = (char) (c - '\uFEE0');
            WIDTH_MAP.put(c, mapping);
        }

        // Fullwidth brackets
        WIDTH_MAP.put('\uFF5F', '\u2985'); // FULLWIDTH LEFT WHITE PARENTHESIS
        WIDTH_MAP.put('\uFF60', '\u2986'); // FULLWIDTH RIGHT WHITE PARENTHESIS


        // Halfwidth CJK punctuation
        WIDTH_MAP.put('\uFF61', '\u3002'); // HALFWIDTH IDEOGRAPHIC FULL STOP
        WIDTH_MAP.put('\uFF62', '\u300C'); // HALFWIDTH LEFT CORNER BRACKET
        WIDTH_MAP.put('\uFF63', '\u300D'); // HALFWIDTH RIGHT CORNER BRACKET
        WIDTH_MAP.put('\uFF64', '\u3001'); // HALFWIDTH IDEOGRAPHIC COMMA


        // Halfwidth Katakana variants
        WIDTH_MAP.put('\uFF65', '\u30FB'); // HALFWIDTH KATAKANA MIDDLE DOT
        WIDTH_MAP.put('\uFF66', '\u30F2'); // HALFWIDTH KATAKANA LETTER WO
        WIDTH_MAP.put('\uFF67', '\u30A1'); // HALFWIDTH KATAKANA LETTER SMALL A
        WIDTH_MAP.put('\uFF68', '\u30A3'); // HALFWIDTH KATAKANA LETTER SMALL I
        WIDTH_MAP.put('\uFF69', '\u30A5'); // HALFWIDTH KATAKANA LETTER SMALL U
        WIDTH_MAP.put('\uFF6A', '\u30A7'); // HALFWIDTH KATAKANA LETTER SMALL E
        WIDTH_MAP.put('\uFF6B', '\u30A9'); // HALFWIDTH KATAKANA LETTER SMALL O
        WIDTH_MAP.put('\uFF6C', '\u30E3'); // HALFWIDTH KATAKANA LETTER SMALL YA
        WIDTH_MAP.put('\uFF6D', '\u30E5'); // HALFWIDTH KATAKANA LETTER SMALL YU
        WIDTH_MAP.put('\uFF6E', '\u30E7'); // HALFWIDTH KATAKANA LETTER SMALL YO
        WIDTH_MAP.put('\uFF6F', '\u30C3'); // HALFWIDTH KATAKANA LETTER SMALL TU
        WIDTH_MAP.put('\uFF70', '\u30FC'); // HALFWIDTH KATAKANA-HIRAGANA PROLONGED SOUND MARK
        WIDTH_MAP.put('\uFF71', '\u30A2'); // HALFWIDTH KATAKANA LETTER A
        WIDTH_MAP.put('\uFF72', '\u30A4'); // HALFWIDTH KATAKANA LETTER I
        WIDTH_MAP.put('\uFF73', '\u30A6'); // HALFWIDTH KATAKANA LETTER U
        WIDTH_MAP.put('\uFF74', '\u30A8'); // HALFWIDTH KATAKANA LETTER E
        WIDTH_MAP.put('\uFF75', '\u30AA'); // HALFWIDTH KATAKANA LETTER O
        WIDTH_MAP.put('\uFF76', '\u30AB'); // HALFWIDTH KATAKANA LETTER KA
        WIDTH_MAP.put('\uFF77', '\u30AD'); // HALFWIDTH KATAKANA LETTER KI
        WIDTH_MAP.put('\uFF78', '\u30AF'); // HALFWIDTH KATAKANA LETTER KU
        WIDTH_MAP.put('\uFF79', '\u30B1'); // HALFWIDTH KATAKANA LETTER KE
        WIDTH_MAP.put('\uFF7A', '\u30B3'); // HALFWIDTH KATAKANA LETTER KO
        WIDTH_MAP.put('\uFF7B', '\u30B5'); // HALFWIDTH KATAKANA LETTER SA
        WIDTH_MAP.put('\uFF7C', '\u30B7'); // HALFWIDTH KATAKANA LETTER SI
        WIDTH_MAP.put('\uFF7D', '\u30B9'); // HALFWIDTH KATAKANA LETTER SU
        WIDTH_MAP.put('\uFF7E', '\u30BB'); // HALFWIDTH KATAKANA LETTER SE
        WIDTH_MAP.put('\uFF7F', '\u30BD'); // HALFWIDTH KATAKANA LETTER SO
        WIDTH_MAP.put('\uFF80', '\u30BF'); // HALFWIDTH KATAKANA LETTER TA
        WIDTH_MAP.put('\uFF81', '\u30C1'); // HALFWIDTH KATAKANA LETTER TI
        WIDTH_MAP.put('\uFF82', '\u30C4'); // HALFWIDTH KATAKANA LETTER TU
        WIDTH_MAP.put('\uFF83', '\u30C6'); // HALFWIDTH KATAKANA LETTER TE
        WIDTH_MAP.put('\uFF84', '\u30C8'); // HALFWIDTH KATAKANA LETTER TO
        WIDTH_MAP.put('\uFF85', '\u30CA'); // HALFWIDTH KATAKANA LETTER NA
        WIDTH_MAP.put('\uFF86', '\u30CB'); // HALFWIDTH KATAKANA LETTER NI
        WIDTH_MAP.put('\uFF87', '\u30CC'); // HALFWIDTH KATAKANA LETTER NU
        WIDTH_MAP.put('\uFF88', '\u30CD'); // HALFWIDTH KATAKANA LETTER NE
        WIDTH_MAP.put('\uFF89', '\u30CE'); // HALFWIDTH KATAKANA LETTER NO
        WIDTH_MAP.put('\uFF8A', '\u30CF'); // HALFWIDTH KATAKANA LETTER HA
        WIDTH_MAP.put('\uFF8B', '\u30D2'); // HALFWIDTH KATAKANA LETTER HI
        WIDTH_MAP.put('\uFF8C', '\u30D5'); // HALFWIDTH KATAKANA LETTER HU
        WIDTH_MAP.put('\uFF8D', '\u30D8'); // HALFWIDTH KATAKANA LETTER HE
        WIDTH_MAP.put('\uFF8E', '\u30DB'); // HALFWIDTH KATAKANA LETTER HO
        WIDTH_MAP.put('\uFF8F', '\u30DE'); // HALFWIDTH KATAKANA LETTER MA
        WIDTH_MAP.put('\uFF90', '\u30DF'); // HALFWIDTH KATAKANA LETTER MI
        WIDTH_MAP.put('\uFF91', '\u30E0'); // HALFWIDTH KATAKANA LETTER MU
        WIDTH_MAP.put('\uFF92', '\u30E1'); // HALFWIDTH KATAKANA LETTER ME
        WIDTH_MAP.put('\uFF93', '\u30E2'); // HALFWIDTH KATAKANA LETTER MO
        WIDTH_MAP.put('\uFF94', '\u30E4'); // HALFWIDTH KATAKANA LETTER YA
        WIDTH_MAP.put('\uFF95', '\u30E6'); // HALFWIDTH KATAKANA LETTER YU
        WIDTH_MAP.put('\uFF96', '\u30E8'); // HALFWIDTH KATAKANA LETTER YO
        WIDTH_MAP.put('\uFF97', '\u30E9'); // HALFWIDTH KATAKANA LETTER RA
        WIDTH_MAP.put('\uFF98', '\u30EA'); // HALFWIDTH KATAKANA LETTER RI
        WIDTH_MAP.put('\uFF99', '\u30EB'); // HALFWIDTH KATAKANA LETTER RU
        WIDTH_MAP.put('\uFF9A', '\u30EC'); // HALFWIDTH KATAKANA LETTER RE
        WIDTH_MAP.put('\uFF9B', '\u30ED'); // HALFWIDTH KATAKANA LETTER RO
        WIDTH_MAP.put('\uFF9C', '\u30EF'); // HALFWIDTH KATAKANA LETTER WA
        WIDTH_MAP.put('\uFF9D', '\u30F3'); // HALFWIDTH KATAKANA LETTER N
        WIDTH_MAP.put('\uFF9E', '\u3099'); // HALFWIDTH KATAKANA VOICED SOUND MARK
        WIDTH_MAP.put('\uFF9F', '\u309A'); // HALFWIDTH KATAKANA SEMI-VOICED SOUND MARK


        // Halfwidth Hangul variants
        WIDTH_MAP.put('\uFFA0', '\u3164'); // HALFWIDTH HANGUL FILLER
        // KIYEOK - HIEUH
        for (char c = '\uFFA1'; c <= '\uFFBE'; c++) {
            char mapping = (char) (c - '\uCE70');
            WIDTH_MAP.put(c, mapping);
        }
        // A - E
        for (char c = '\uFFC2'; c <= '\uFFC7'; c++) {
            char mapping = (char) (c - '\uCE73');
            WIDTH_MAP.put(c, mapping);
        }
        // YEO - OE
        for (char c = '\uFFCA'; c <= '\uFFCF'; c++) {
            char mapping = (char) (c - '\uCE75');
            WIDTH_MAP.put(c, mapping);
        }
        // YO to YU
        for (char c = '\uffd2'; c <= '\uFFD7'; c++) {
            char mapping = (char) (c - '\uCE77');
            WIDTH_MAP.put(c, mapping);
        }
        WIDTH_MAP.put('\uFFDA', '\u3161'); // HALFWIDTH HANGUL LETTER EU
        WIDTH_MAP.put('\uFFDB', '\u3162'); // HALFWIDTH HANGUL LETTER YI
        WIDTH_MAP.put('\uFFDC', '\u3163'); // HALFWIDTH HANGUL LETTER I


        // Fullwidth symbol variants
        WIDTH_MAP.put('\uFFE0', '\u00A2'); // FULLWIDTH CENT SIGN
        WIDTH_MAP.put('\uFFE1', '\u00A3'); // FULLWIDTH POUND SIGN
        WIDTH_MAP.put('\uFFE2', '\u00AC'); // FULLWIDTH NOT SIGN
        WIDTH_MAP.put('\uFFE3', '\u00AF'); // FULLWIDTH MACRON
        WIDTH_MAP.put('\uFFE4', '\u00A6'); // FULLWIDTH BROKEN BAR
        WIDTH_MAP.put('\uFFE5', '\u00A5'); // FULLWIDTH YEN SIGN
        WIDTH_MAP.put('\uFFE6', '\u20A9'); // FULLWIDTH WON SIGN


        // Halfwidth symbol variants
        WIDTH_MAP.put('\uFFE8', '\u2502'); // HALFWIDTH FORMS LIGHT VERTICAL
        WIDTH_MAP.put('\uFFE9', '\u2190'); // HALFWIDTH LEFTWARDS ARROW
        WIDTH_MAP.put('\uFFEA', '\u2191'); // HALFWIDTH UPWARDS ARROW
        WIDTH_MAP.put('\uFFEB', '\u2192'); // HALFWIDTH RIGHTWARDS ARROW
        WIDTH_MAP.put('\uFFEC', '\u2193'); // HALFWIDTH DOWNWARDS ARROW
        WIDTH_MAP.put('\uFFED', '\u25A0'); // HALFWIDTH BLACK SQUARE
        WIDTH_MAP.put('\uFFEE', '\u25CB'); // HALFWIDTH WHITE CIRCLE
    }

    private final boolean identifierClass;

    /**
     * @param identifierClass True, if the base class for this profile is the "IdentifierClass"; false if it's the "FreeFormClass".
     */
    protected PrecisProfile(boolean identifierClass) {
        this.identifierClass = identifierClass;
    }

    /**
     * Returns true if the code point is a letter or digit character (as per the PRECIS specification), i.e. in the general category "Ll", "Lu", "Lo", "Nd", "Lm", "Mn" or "Mc".
     *
     * @param cp The code point.
     * @return If the code point is a letter or digit character.
     * @see <a href="https://tools.ietf.org/html/rfc7564#section-9.1">9.1.  LetterDigits (A)</a>
     */
    private static boolean isLetterDigit(final int cp) {
        // Ll, Lu, Lo, Nd, Lm, Mn, Mc
        return ((((1 << Character.LOWERCASE_LETTER) |
                (1 << Character.UPPERCASE_LETTER) |
                (1 << Character.OTHER_LETTER) |
                (1 << Character.DECIMAL_DIGIT_NUMBER) |
                (1 << Character.MODIFIER_LETTER) |
                (1 << Character.NON_SPACING_MARK) |
                (1 << Character.COMBINING_SPACING_MARK)) >> Character.getType(cp)) & 1)
                != 0;
    }

    /**
     * Returns true if the code point is in the exception category.
     *
     * @param cp The code point.
     * @return If the code point is backwards compatible.
     * @see <a href="https://tools.ietf.org/html/rfc7564#section-9.6">9.6.  Exceptions (F)</a>
     */
    private static boolean isExceptionallyValid(final int cp) {
        // PVALID -- Would otherwise have been DISALLOWED
        //
        // 00DF; PVALID     # LATIN SMALL LETTER SHARP S
        // 03C2; PVALID     # GREEK SMALL LETTER FINAL SIGMA
        // 06FD; PVALID     # ARABIC SIGN SINDHI AMPERSAND
        // 06FE; PVALID     # ARABIC SIGN SINDHI POSTPOSITION MEN
        // 0F0B; PVALID     # TIBETAN MARK INTERSYLLABIC TSHEG
        // 3007; PVALID     # IDEOGRAPHIC NUMBER ZERO
        return cp == 0x00DF || cp == 0x03C2 || cp == 0x06FD || cp == 0x06FE || cp == 0x0F0B || cp == 0x3007;
    }

    /**
     * Returns true if the code point is in the exception category.
     *
     * @param cp The code point.
     * @return If the code point is backwards compatible.
     * @see <a href="https://tools.ietf.org/html/rfc7564#section-9.6">9.6.  Exceptions (F)</a>
     */
    private static boolean isExceptionallyDisallowed(final int cp) {
        // 0640; DISALLOWED # ARABIC TATWEEL
        // 07FA; DISALLOWED # NKO LAJANYALAN
        // 302E; DISALLOWED # HANGUL SINGLE DOT TONE MARK
        // 302F; DISALLOWED # HANGUL DOUBLE DOT TONE MARK
        // 3031; DISALLOWED # VERTICAL KANA REPEAT MARK
        // 3032; DISALLOWED # VERTICAL KANA REPEAT WITH VOICED SOUND MARK
        // 3033; DISALLOWED # VERTICAL KANA REPEAT MARK UPPER HALF
        // 3034; DISALLOWED # VERTICAL KANA REPEAT WITH VOICED SOUND MARK UPPER HA
        // 3035; DISALLOWED # VERTICAL KANA REPEAT MARK LOWER HALF
        // 303B; DISALLOWED # VERTICAL IDEOGRAPHIC ITERATION MARK
        return cp == 0x0640 || cp == 0x07FA || cp == 0x302E || cp == 0x302F || cp >= 0x3031 && cp <= 0x3035 || cp == 0x303B;
    }

    /**
     * Returns true if the code point is backwards compatible.
     *
     * @param cp The code point.
     * @return If the code point is backwards compatible.
     * @see <a href="https://tools.ietf.org/html/rfc7564#section-9.7">9.7.  BackwardCompatible (G)</a>
     */
    private static boolean isBackwardsCompatible(final int cp) {
        // Currently this category consists of the empty set, therefore return false.
        return false;
    }

    /**
     * Returns true if the code point is a join control character.
     *
     * @param cp The code point.
     * @return If the code point is a join control character.
     * @see <a href="https://tools.ietf.org/html/rfc7564#section-9.8">9.8.  JoinControl (H)</a>
     */
    private static boolean isJoinControl(final int cp) {
        // U+200C ZERO WIDTH NON-JOINER
        // U+200D ZERO WIDTH JOINER
        return cp == 0x200C || cp == 0x200D;
    }

    /**
     * Returns true if the code point is an old hangul jamo character.
     *
     * @return If the code point is an old hangul jamo character.
     * @see <a href="https://tools.ietf.org/html/rfc7564#section-9.9">9.9.  OldHangulJamo (I)</a>
     * @see <a href="http://www.unicode.org/versions/Unicode8.0.0/ch18.pdf">Unicode Standard, Chapter 18</a>
     */
    private static boolean isOldHangulJamo(final int cp) {
        // Hangul Jamo: U+1100–U+11FF
        // Hangul Jamo Extended-A: U+A960–U+A97F
        // Hangul Jamo Extended-B: U+D7B0–U+D7FF
        return cp >= 0x1100 && cp <= 0x11FF
                || cp >= 0xA960 && cp <= 0xA97F
                || cp >= 0xD7B0 && cp <= 0xD7FF;
    }

    /**
     * Returns true if the code point is unassigned.
     *
     * @return If the code point is unassigned.
     * @see <a href="https://tools.ietf.org/html/rfc7564#section-9.10">9.10.  Unassigned (J)</a>
     */
    static boolean isUnassigned(final int cp) {
        // General_Category(cp) is in {Cn} and
        // Noncharacter_Code_Point(cp) = False
        return !Character.isDefined(cp) && !isNonCharacter(cp);
    }

    /**
     * Returns true if the code point is in the ASCII7 category.
     *
     * @return If the  code point is in the ASCII7 category.
     * @see <a href="https://tools.ietf.org/html/rfc7564#section-9.11">9.11.  ASCII7 (K)</a>
     */
    private static boolean isASCII7(final int cp) {
        // cp is in {0021..007E}
        return cp >= 0x0021 && cp <= 0x007E;
    }

    /**
     * Returns true if the code point is a control character.
     *
     * @return If the  code point is a control character.
     * @see <a href="https://tools.ietf.org/html/rfc7564#section-9.12">9.12.  Controls (L)</a>
     */
    private static boolean isControl(final int cp) {
        return Character.isISOControl(cp);
    }

    /**
     * http://unicode.org/Public/8.0.0/ucd/DerivedCoreProperties.txt
     */
    private static boolean isDefaultIgnorable(final int cp) {
        return cp == 0x00AD
                || cp == 0x034F
                || cp == 0x061C
                || cp >= 0x115F && cp <= 0x1160
                || cp >= 0x17B4 && cp <= 0x17B5
                || cp >= 0x180B && cp <= 0x180E
                || cp >= 0x200B && cp <= 0x200F
                || cp >= 0x202A && cp <= 0x202E
                || cp >= 0x2060 && cp <= 0x206F
                || cp == 0x3164
                || cp >= 0xFE00 && cp <= 0xFE0F
                || cp == 0xFEFF
                || cp == 0xFFA0
                || cp >= 0xFFF0 && cp <= 0xFFF8;
    }

    private static boolean isNonCharacter(final int cp) {
        return cp >= 0xFDD0 && cp <= 0xFDEF
                || cp >= 0xFFFE && cp <= 0xFFFF;
    }

    /**
     * Returns true if the code point is ignorable
     *
     * @return If the  code point is ignorable.
     * @see <a href="https://tools.ietf.org/html/rfc7564#section-9.13">9.13.  PrecisIgnorableProperties (M)</a>
     */
    private static boolean isIgnorable(final int cp) {
        // Default_Ignorable_Code_Point(cp) = True or
        // Noncharacter_Code_Point(cp) = True
        return isDefaultIgnorable(cp) || isNonCharacter(cp);
    }

    /**
     * Returns true if the code point is a space character (as per the PRECIS specification), i.e. in the general category "Zs".
     *
     * @param cp The code point.
     * @return If the code point is a space character.
     * @see <a href="https://tools.ietf.org/html/rfc7564#section-9.14">9.14.  Spaces (N)</a>
     */
    private static boolean isSpace(final int cp) {
        // Zs
        return (((1 << Character.SPACE_SEPARATOR) >> Character.getType(cp)) & 1)
                != 0;
    }

    /**
     * Returns true if the code point is a symbol character, i.e. in the general category "Sm", "Sc", "Sk" or "So".
     *
     * @param cp The code point.
     * @return If the code point is a symbol character.
     * @see <a href="https://tools.ietf.org/html/rfc7564#section-9.15">9.15.  Symbols (O)</a>
     */
    private static boolean isSymbol(final int cp) {
        // Sm, Sc, Sk, So
        return ((((1 << Character.MATH_SYMBOL) |
                (1 << Character.CURRENCY_SYMBOL) |
                (1 << Character.MODIFIER_SYMBOL) |
                (1 << Character.OTHER_SYMBOL)) >> Character.getType(cp)) & 1)
                != 0;
    }

    /**
     * Returns true if the code point is a punctuation character, i.e. in the general category "Pc", "Pd", "Ps", "Pe", "Pi", "Pf" or "Po".
     *
     * @param cp The code point.
     * @return If the code point is a punctuation character.
     * @see <a href="https://tools.ietf.org/html/rfc7564#section-9.16">9.16.  Punctuation (P)</a>
     */
    private static boolean isPunctuation(final int cp) {
        // Pc, Pd, Ps, Pe, Pi, Pf, Po
        return ((((1 << Character.CONNECTOR_PUNCTUATION) |
                (1 << Character.DASH_PUNCTUATION) |
                (1 << Character.START_PUNCTUATION) |
                (1 << Character.END_PUNCTUATION) |
                (1 << Character.INITIAL_QUOTE_PUNCTUATION) |
                (1 << Character.FINAL_QUOTE_PUNCTUATION) |
                (1 << Character.OTHER_PUNCTUATION)) >> Character.getType(cp)) & 1)
                != 0;
    }

    /**
     * Returns true, if the code point has compatibility equivalents as explained in the Unicode Standard.
     *
     * @param cp The code point.
     * @return If the code point is in in the category "HasCompat".
     * @see <a href="https://tools.ietf.org/html/rfc7564#section-9.17">9.17.  HasCompat (Q)</a>
     */
    static boolean hasCompatibilityEquivalent(final int cp) {
        // toNFKC(cp) != cp
        String s = new String(Character.toChars(cp));
        return !Normalizer.normalize(s, Normalizer.Form.NFKC).equals(s);
    }

    /**
     * Returns true if the code point is in the category of letters and digits other than the "traditional" letters and digits, i.e. in the general category "Lt", "Nl", "No" or "Me".
     *
     * @param cp The code point.
     * @return If the code point is in the category of letters and digits other than the "traditional" letters and digits.
     * @see <a href="https://tools.ietf.org/html/rfc7564#section-9.18">9.18.  OtherLetterDigits (R)</a>
     */
    private static boolean isOtherLetterDigit(final int cp) {
        // Lt, Nl, No, Me
        return ((((1 << Character.TITLECASE_LETTER) |
                (1 << Character.LETTER_NUMBER) |
                (1 << Character.OTHER_NUMBER) |
                (1 << Character.ENCLOSING_MARK)) >> Character.getType(cp)) & 1)
                != 0;
    }

    /**
     * Maps full-width and half-width characters to their decomposition mappings.
     *
     * @see <a href="http://unicode.org/charts/PDF/UFF00.pdf">Halfwidth and Fullwidth Forms</a>
     */
    protected static CharSequence widthMap(CharSequence s) {
        StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < s.length(); i++) {
            Character c = WIDTH_MAP.get(s.charAt(i));
            if (c != null) {
                sb.setCharAt(i, c);
            }
        }
        return sb;
    }

    /**
     * Applies the default case folding to a string.
     *
     * @param input The input string.
     * @return The case folded string.
     */
    protected static CharSequence caseFold(final CharSequence input) {
        return input.toString().toUpperCase(Locale.US).toLowerCase(Locale.US);
    }

    /**
     * Checks the Bidi Rule.
     *
     * @param label The label to check.
     * @throws InvalidCodePointException If the label violates the Bidi Rule.
     */
    protected static void checkBidiRule(final CharSequence label) {
        if (label == null) {
            return;
        }
        if (label.length() == 0) {
            return;
        }
        // 1.  The first character must be a character with Bidi property L, R,
        // or AL.  If it has the R or AL property, it is an RTL label; if it
        // has the L property, it is an LTR label.
        int i = 0;
        int cp = Character.codePointAt(label, i);
        i += Character.charCount(cp);
        final byte dir1stChar = Character.getDirectionality(cp);
        final boolean isLTRLabel = dir1stChar == Character.DIRECTIONALITY_LEFT_TO_RIGHT;
        final boolean isRTLLabel = dir1stChar == Character.DIRECTIONALITY_RIGHT_TO_LEFT || dir1stChar == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;

        if (!isLTRLabel && !isRTLLabel) {
            throw new InvalidCodePointException("Bidi Rule 1: The first character must be a character with Bidi property L, R or AL.");
        }

        // In order to check condition 3 and 6, get the Bidi property of the last character, which has not the property NSM.
        byte directionalityLastNonNSMCharacter;
        int length = label.length();
        do {
            cp = Character.codePointBefore(label, length);
            length -= Character.charCount(cp);
            directionalityLastNonNSMCharacter = Character.getDirectionality(cp);
            if (directionalityLastNonNSMCharacter != Character.DIRECTIONALITY_NONSPACING_MARK) {
                break;
            }
        } while (length > 0);

        int directionalityMask = 0;
        while (i < length + 1) {
            cp = Character.codePointAt(label, i);
            i += Character.charCount(cp);
            directionalityMask |= 1 << Character.getDirectionality(cp);
        }

        if (isRTLLabel) {
            // 2.  In an RTL label, only characters with the Bidi properties R, AL,
            // AN, EN, ES, CS, ET, ON, BN, or NSM are allowed.
            if ((directionalityMask & ~R_AL_AN_EN_ES_CS_ET_ON_BN_NSM) != 0) {
                throw new InvalidCodePointException("Bidi Rule 2: In an RTL label, only characters with the Bidi properties R, AL, AN, EN, ES, CS, ET, ON, BN, or NSM are allowed.");
            }

            // 3.  In an RTL label, the end of the label must be a character with
            // Bidi property R, AL, EN, or AN, followed by zero or more
            // characters with Bidi property NSM.
            if (directionalityLastNonNSMCharacter != Character.DIRECTIONALITY_RIGHT_TO_LEFT
                    && directionalityLastNonNSMCharacter != Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC
                    && directionalityLastNonNSMCharacter != Character.DIRECTIONALITY_EUROPEAN_NUMBER
                    && directionalityLastNonNSMCharacter != Character.DIRECTIONALITY_ARABIC_NUMBER) {
                throw new InvalidCodePointException("Bidi Rule 3: In an RTL label, the end of the label must be a character with Bidi property R, AL, EN, or AN.");
            }

            // 4.  In an RTL label, if an EN is present, no AN may be present, and
            // vice versa.
            if ((directionalityMask & EN_AN) == EN_AN) {
                throw new InvalidCodePointException("Bidi Rule 4: In an RTL label, if an EN is present, no AN may be present, and vice versa.");
            }
        } else {
            // 5.  In an LTR label, only characters with the Bidi properties L, EN,
            // ES, CS, ET, ON, BN, or NSM are allowed.
            if ((directionalityMask & ~L_EN_ES_CS_ET_ON_BN_NSM) != 0) {
                throw new InvalidCodePointException("Bidi Rule 5: In an LTR label, only characters with the Bidi properties L, EN, ES, CS, ET, ON, BN, or NSM are allowed.");
            }

            // 6.  In an LTR label, the end of the label must be a character with
            // Bidi property L or EN, followed by zero or more characters with
            // Bidi property NSM.
            if (directionalityLastNonNSMCharacter != Character.DIRECTIONALITY_LEFT_TO_RIGHT
                    && directionalityLastNonNSMCharacter != Character.DIRECTIONALITY_EUROPEAN_NUMBER) {
                throw new InvalidCodePointException("Bidi Rule 6: In an LTR label, the end of the label must be a character with Bidi property L or EN.");
            }
        }
    }

    /**
     * Preparation entails only ensuring that the characters in an
     * individual string are allowed by the underlying PRECIS string
     * class.
     *
     * @param input The input string.
     * @return The prepared string.
     * @throws InvalidCodePointException If the input contains invalid code points (which are disallowed by the underlying Precis String class).
     */
    public String prepare(final CharSequence input) {
        final int length = input.length();
        int offset = 0;
        while (offset < length) {
            final int codePoint = Character.codePointAt(input, offset);

            boolean valid = false;

            // If .cp. .in. Exceptions Then Exceptions(cp);
            // Else If .cp. .in. BackwardCompatible Then BackwardCompatible(cp);
            // Else If .cp. .in. Unassigned Then UNASSIGNED;
            // Else If .cp. .in. ASCII7 Then PVALID;
            // Else If .cp. .in. JoinControl Then CONTEXTJ;
            // Else If .cp. .in. OldHangulJamo Then DISALLOWED;
            // Else If .cp. .in. PrecisIgnorableProperties Then DISALLOWED;
            // Else If .cp. .in. Controls Then DISALLOWED;
            // Else If .cp. .in. HasCompat Then ID_DIS or FREE_PVAL;
            // Else If .cp. .in. LetterDigits Then PVALID;
            // Else If .cp. .in. OtherLetterDigits Then ID_DIS or FREE_PVAL;
            // Else If .cp. .in. Spaces Then ID_DIS or FREE_PVAL;
            // Else If .cp. .in. Symbols Then ID_DIS or FREE_PVAL;
            // Else If .cp. .in. Punctuation Then ID_DIS or FREE_PVAL;
            // Else DISALLOWED;

            if (isExceptionallyValid(codePoint)) {
                valid = true;
            } else if (isExceptionallyDisallowed(codePoint)) {
                valid = false;
            } else if (isBackwardsCompatible(codePoint)) {
                valid = true;
            } else if (isUnassigned(codePoint)) {
                valid = false;
            } else if (isASCII7(codePoint)) {
                valid = true;
            } else if (isJoinControl(codePoint)) {
                valid = false; // TODO
            } else if (isOldHangulJamo(codePoint)) {
                valid = false;
            } else if (isIgnorable(codePoint)) {
                valid = false;
            } else if (isControl(codePoint)) {
                valid = false;
            } else if (hasCompatibilityEquivalent(codePoint)) {
                valid = !identifierClass;
            } else if (isLetterDigit(codePoint)) {
                valid = true;
            } else if (isOtherLetterDigit(codePoint)) {
                valid = !identifierClass;
            } else if (isSpace(codePoint)) {
                valid = !identifierClass;
            } else if (isSymbol(codePoint)) {
                valid = !identifierClass;
            } else if (isPunctuation(codePoint)) {
                valid = !identifierClass;
            }
            if (!valid) {
                throw new InvalidCodePointException("Invalid code point at position " + offset + ": 0x" + Integer.toHexString(codePoint));
            }
            offset += Character.charCount(codePoint);
        }
        return input.toString();
    }

    /**
     * Enforcement entails applying all of the rules specified for a
     * particular string class or profile thereof to an individual
     * string, for the purpose of determining if the string can be used
     * in a given protocol slot.
     * <p/>
     * This base method first applies the profile rules, then the behavioral rules as per RFC 7564 §7.
     *
     * @param input The input string.
     * @return The output string.
     * @throws InvalidCodePointException If the input contains invalid code points (which are disallowed by the underlying Precis String class).
     * @see <a href="https://tools.ietf.org/html/rfc7564#section-7">7.  Order of Operations</a>
     */
    public String enforce(final CharSequence input) {
        // TODO:
        // it is unclear if enforcement
        // a) should first apply the rules, then check the String class as defined in
        // https://tools.ietf.org/html/rfc7564#section-7
        // -- or --
        // b) should first check the String class and then apply the rules as defined in all known profiles.

        // Usually this has no impact, but there's one case, where it has one:
        // U+212B (ANGSTROM SIGN) in Usernames:
        // If first checking the IdentifierClass (preparation) it would be disallowed, because it has a compatibility equivalent.
        // If first applying the rules, it would be normalized with NFC and becomes U+00C5 and then would pass the IdentifierClass check.

        // RFC 7613 introduced a workaround for the preparation by applying width-mapping as part of it, but it seems as if NFC normalization has
        // been overlooked.
        // As per Peter Saint-Andre, the first approach is desirable, so let's stick to it.
        return prepare(applyDirectionalityRule(
                applyNormalizationRule(
                        applyCaseMappingRule(
                                applyAdditionalMappingRule(
                                        applyWidthMappingRule(input))))));
    }

    /**
     * Compares two strings with each other. The default comparison method {@linkplain #enforce(CharSequence) enforces} the rules of a profile to each string and then compares them.
     * However, there are exceptions to this approach, like in the Nickname profile, where comparison uses different rules than enforcement.
     *
     * @param o1 The first string.
     * @param o2 The second string.
     * @return 0 if the strings are equal, otherwise the comparison result.
     * @throws InvalidCodePointException If the input contains invalid code points (which are disallowed by the underlying Precis String class).
     */
    @Override
    public int compare(CharSequence o1, CharSequence o2) {
        return enforce(o1).compareTo(enforce(o2));
    }

    /**
     * The width mapping rule of a profile specifies whether width mapping
     * is performed on the characters of a string, and how the mapping is
     * done.
     *
     * @param input The input string.
     * @return The width-mapped string.
     * @see <a href="https://tools.ietf.org/html/rfc7564#section-5.2.1">5.2.1.  Width Mapping Rule</a>
     */
    protected abstract CharSequence applyWidthMappingRule(CharSequence input);

    /**
     * The additional mapping rule of a profile specifies whether additional
     * mappings are performed on the characters of a string, such as:
     * <p/>
     * Mapping of delimiter characters (such as '@', ':', '/', '+',
     * and '-')
     * <p/>
     * Mapping of special characters (e.g., non-ASCII space characters to
     * ASCII space or control characters to nothing).
     *
     * @param input The input string.
     * @return The mapped string.
     * @see <a href="https://tools.ietf.org/html/rfc7564#section-5.2.2">5.2.2.  Additional Mapping Rule</a>
     */
    protected abstract CharSequence applyAdditionalMappingRule(CharSequence input);

    /**
     * The case mapping rule of a profile specifies whether case mapping
     * (instead of case preservation) is performed on the characters of a
     * string, and how the mapping is applied (e.g., mapping uppercase and
     * titlecase characters to their lowercase equivalents).
     * <p/>
     * If case mapping is desired (instead of case preservation), it is
     * RECOMMENDED to use Unicode Default Case Folding as defined in the
     * Unicode Standard
     *
     * @param input The input string.
     * @return The case mapped string.
     * @see <a href="https://tools.ietf.org/html/rfc7564#section-5.2.3">5.2.3.  Case Mapping Rule</a>
     */
    protected abstract CharSequence applyCaseMappingRule(CharSequence input);

    /**
     * The normalization rule of a profile specifies which Unicode
     * normalization form (D, KD, C, or KC) is to be applied.
     * <p/>
     * In accordance with [RFC5198], normalization form C (NFC) is
     * RECOMMENDED.
     *
     * @param input The input string.
     * @return The normalized string.
     * @see <a href="https://tools.ietf.org/html/rfc7564#section-5.2.4">5.2.4.  Normalization Rule</a>
     */
    protected abstract CharSequence applyNormalizationRule(CharSequence input);

    /**
     * The directionality rule of a profile specifies how to treat strings
     * containing what are often called "right-to-left" (RTL) characters
     * (see Unicode Standard Annex #9 [UAX9]).  RTL characters come from
     * scripts that are normally written from right to left and are
     * considered by Unicode to, themselves, have right-to-left
     * directionality.  Some strings containing RTL characters also contain
     * "left-to-right" (LTR) characters, such as numerals, as well as
     * characters without directional properties.  Consequently, such
     * strings are known as "bidirectional strings".
     *
     * @param input The input string.
     * @return The output string.
     * @see <a href="https://tools.ietf.org/html/rfc7564#section-5.2.5">5.2.5.  Directionality Rule</a>
     */
    protected abstract CharSequence applyDirectionalityRule(CharSequence input);
}
