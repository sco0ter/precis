package rocks.xmpp.precis;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

final class XmppLocalpartProfile extends UsernameProfile {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * From <a href="https://tools.ietf.org/html/rfc7622#section-3.3.1">RFC 7622 §
     * 3.3.1</a>.
     */
    // @formatter:off
    private static final char[] LOCALPART_FURTHER_EXCLUDED_CHARACTERS = new char[] {
        '"',  // U+0022 (QUOTATION MARK) , i.e., "
        '&',  // U+0026 (AMPERSAND), i.e., &
        '\'', // U+0027 (APOSTROPHE), i.e., '
        '/',  // U+002F (SOLIDUS), i.e., /
        ':',  // U+003A (COLON), i.e., :
        '<',  // U+003C (LESS-THAN SIGN), i.e., <
        '>',  // U+003E (GREATER-THAN SIGN), i.e., >
        '@',  // U+0040 (COMMERCIAL AT), i.e., @
    };
    // @formatter:on

    static {
        // Ensure that the char array is sorted as we use Arrays.binarySearch() on it.
        Arrays.sort(LOCALPART_FURTHER_EXCLUDED_CHARACTERS);
    }

    XmppLocalpartProfile() {
        super(true);
    }

    @Override
    public String enforce(final CharSequence input) {
        int inputLength = input.length();
        for (int i = 0; i < inputLength; i++) {
            char c = input.charAt(i);
            int excludedCharPos = Arrays.binarySearch(LOCALPART_FURTHER_EXCLUDED_CHARACTERS, c);
            if (excludedCharPos >= 0) {
                throw new InvalidCodePointException("XMPP localparts must not contain '"
                        + LOCALPART_FURTHER_EXCLUDED_CHARACTERS[excludedCharPos] + "'. Found in '" + input
                        + "' at position " + excludedCharPos + ". See RFC 7622 § 3.3.1");
            }
        }

        String res = super.enforce(input);
        assertNotLongerThan1023BytesOrEmpty(res);
        return res;
    }

    private static void assertNotLongerThan1023BytesOrEmpty(String string) {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > 1023) {
            throw new IllegalArgumentException("Given string after enforcment and encoded in UTF-8 is longer then 1023 bytes");
        } else if (bytes.length == 0) {
            throw new IllegalArgumentException("Given string after enforcment and encoded in UTF-8 is empty");
        }
    }
}
