package rocks.xmpp.precis;


import java.net.IDN;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * A profile for applying the rules for IDN as in RFC 5895. Although IDN doesn't use Precis, it's still very similar so that we can use the base class.
 *
 * @author Christian Schudt
 * @see <a href="https://tools.ietf.org/html/rfc5890">RFC 5890</a>
 * @see <a href="https://tools.ietf.org/html/rfc5895#section-2">RFC 5895</a>
 * @see <a href="https://tools.ietf.org/html/rfc7622#section-3.2">3.2.  Domainpart</a>
 */
final class IDNProfile extends PrecisProfile {

    /**
     * Whenever dots are used as label separators, the following characters MUST be recognized as dots: U+002E (full stop), U+3002 (ideographic full stop), U+FF0E (fullwidth full stop), U+FF61 (halfwidth ideographic full stop).
     */
    private static final String DOTS = "[.\u3002\uFF0E\uFF61]";

    /**
     * Label separators for domain labels, which should be mapped to "." (dot): IDEOGRAPHIC FULL STOP character (U+3002)
     */
    private static final Pattern LABEL_SEPARATOR = Pattern.compile(DOTS);

    IDNProfile() {
        super(false);
    }

    /**
     * Prepares an input string, so that it does not contain invalid labels.
     * It uses the {@link IDN} class for this check.
     *
     * @param input The input string.
     * @return The unicode domain name.
     * @throws IllegalArgumentException If the input contains invalid labels.
     */
    @Override
    public String prepare(CharSequence input) {
        // First ensure that the input contains valid LDH labels. Otherwise it will throw an IllegalArgumentException.
        // Then ensure that domain name is in Unicode, e.g. must not contain A-labels ("xn--")
        return IDN.toUnicode(IDN.toASCII(input.toString(), IDN.USE_STD3_ASCII_RULES), IDN.USE_STD3_ASCII_RULES);
    }

    /**
     * Enforces a domain name by applying the mapping rules of RFC 5895.
     *
     * @param input The input string.
     * @return The enforced string.
     * @see <a href="https://tools.ietf.org/html/rfc5895#section-2">2.  The General Procedure</a>
     */
    @Override
    public String enforce(CharSequence input) {
        // 4. Map IDEOGRAPHIC FULL STOP character (U+3002) to dot.
        return applyAdditionalMappingRule(
                // 3.  All characters are mapped using Unicode Normalization Form C (NFC).
                applyNormalizationRule(
                        // 2. Fullwidth and halfwidth characters (those defined with
                        // Decomposition Types <wide> and <narrow>) are mapped to their
                        // decomposition mappings
                        applyWidthMappingRule(
                                // 1. Uppercase characters are mapped to their lowercase equivalents
                                applyCaseMappingRule(prepare(input))))).toString();
    }

    @Override
    protected CharSequence applyWidthMappingRule(CharSequence charSequence) {
        return widthMap(charSequence);
    }

    @Override
    protected CharSequence applyAdditionalMappingRule(CharSequence charSequence) {
        return LABEL_SEPARATOR.matcher(charSequence).replaceAll(".");
    }

    @Override
    protected CharSequence applyCaseMappingRule(CharSequence charSequence) {
        return charSequence.toString().toLowerCase(Locale.US);
    }

    @Override
    protected CharSequence applyNormalizationRule(CharSequence charSequence) {
        return Normalizer.normalize(charSequence, Normalizer.Form.NFC);
    }

    @Override
    protected CharSequence applyDirectionalityRule(CharSequence charSequence) {
        return charSequence;
    }
}