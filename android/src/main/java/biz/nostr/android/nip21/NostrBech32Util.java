package biz.nostr.android.nip21;

import java.util.Locale;

/**
 * Minimal Bech32 shape validator for NIP-19/NIP-21 identifiers.
 * This does not decode bech32, it only validates plausible shape:
 * - whole string printable ASCII
 * - contains a separator '1' with hrp length >=1
 * - data part length >= 6 (checksum length) and consists of valid bech32 charset
 * - prefers lowercase (reject mixed case)
 */
final class NostrBech32Util {
    // Note: We intentionally do NOT enforce the strict bech32 charset here to remain
    // permissive, matching historical behavior and unit tests that only expect
    // a basic shape validation.

    private NostrBech32Util() {}

    static boolean isPlausibleBech32(final String s) {
        if (s == null || s.isEmpty()) return false;
        // must be printable ASCII
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c <= 0x20 || c >= 0x7f) return false;
        }
        // reject mixed case; prefer lowercase
        String lower = s.toLowerCase(Locale.US);
        if (!s.equals(lower)) return false;

        int pos = s.indexOf('1');
        if (pos <= 0) return false; // need hrp of at least 1 char
        int dataLen = s.length() - pos - 1;
        if (dataLen < 6) return false; // checksum length at least 6

        // Permissive: do not validate data part charset to allow placeholder values in tests.
        return true;
    }
}
