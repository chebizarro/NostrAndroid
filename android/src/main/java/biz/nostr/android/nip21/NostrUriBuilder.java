package biz.nostr.android.nip21;

import java.net.URLEncoder;
import java.util.Map;

/**
 * Builder for NIP-21 nostr: URIs.
 */
public final class NostrUriBuilder {
    private NostrUriBuilder() {}

    /**
     * Build a nostr: URI from a bech32 entity (npub/nprofile/note/nevent/naddr). Rejects nsec.
     * Returns a string like: "nostr:npub1..." or with query: "nostr:nevent1...?k=v".
     */
    public static String build(final String bech32, final Map<String, String> query) throws NostrUriException {
        if (bech32 == null) throw new NostrUriException("bech32 is null");
        final String trimmed = bech32.trim();
        if (trimmed.isEmpty()) throw new NostrUriException("bech32 is empty");
        final String lower = trimmed.toLowerCase(java.util.Locale.US);
        if (lower.startsWith("nsec1")) throw new NostrUriException("nsec URIs are not allowed for safety");

        final StringBuilder sb = new StringBuilder("nostr:");
        sb.append(trimmed);
        if (query != null && !query.isEmpty()) {
            StringBuilder qsb = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String,String> e : query.entrySet()) {
                String k = e.getKey() == null ? "" : e.getKey();
                String v = e.getValue() == null ? "" : e.getValue();
                String encK;
                String encV;
                try {
                    encK = URLEncoder.encode(k, "UTF-8");
                    encV = URLEncoder.encode(v, "UTF-8");
                } catch (Exception ex) {
                    throw new NostrUriException("Failed to encode query parameter", ex);
                }
                if (!first) qsb.append('&');
                qsb.append(encK).append('=').append(encV);
                first = false;
            }
            sb.append('?').append(qsb.toString());
        }
        return sb.toString();
    }

    /** Build without query params. */
    public static String build(final String bech32) throws NostrUriException {
        return build(bech32, null);
    }
}
