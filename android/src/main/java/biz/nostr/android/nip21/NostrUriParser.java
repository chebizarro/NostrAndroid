package biz.nostr.android.nip21;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Parser for NIP-21 nostr: URIs.
 * Focused on scheme, bech32 identifier, and optional query (no TLV decoding).
 */
public final class NostrUriParser {
    private NostrUriParser() {}

    /**
     * Parse a NIP-21 string like "nostr:npub1...", optionally with query.
     * Rejects private key URIs (nsec1...) for safety.
     * @throws NostrUriException when invalid or using forbidden types
     */
    public static NostrUri parse(final String input) throws NostrUriException {
        if (input == null) throw new NostrUriException("Input is null");
        final String trimmed = input.trim();
        if (trimmed.isEmpty()) throw new NostrUriException("Input is empty");

        final String lower = trimmed.toLowerCase(Locale.US);
        if (!lower.startsWith("nostr:")) {
            throw new NostrUriException("Missing nostr: scheme");
        }

        // Split off query
        final int qIdx = trimmed.indexOf('?');
        final String pre = qIdx >= 0 ? trimmed.substring(0, qIdx) : trimmed;
        final String queryPart = qIdx >= 0 ? trimmed.substring(qIdx + 1) : null;

        // Extract identifier after scheme
        final String id = pre.substring("nostr:".length());
        if (id.isEmpty()) throw new NostrUriException("Missing identifier after nostr:");

        // Reject private key
        if (id.toLowerCase(Locale.US).startsWith("nsec1")) {
            throw new NostrUriException("nsec URIs are not allowed for safety");
        }

        final NostrUri.Kind kind = classify(id);
        if (!NostrBech32Util.isPlausibleBech32(id)) {
            throw new NostrUriException("Identifier is not a valid bech32-looking string");
        }

        final Map<String,String> query = parseQuery(queryPart);
        return new NostrUri(trimmed, kind, id, query);
    }

    /**
     * Quick validation helper (no exception).
     */
    public static boolean isValid(final String input) {
        try {
            parse(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Map<String,String> parseQuery(final String query) throws NostrUriException {
        final Map<String,String> out = new LinkedHashMap<>();
        if (query == null || query.isEmpty()) return out;
        final String[] pairs = query.split("&");
        for (String pair : pairs) {
            if (pair.isEmpty()) continue;
            final int eq = pair.indexOf('=');
            String k;
            String v;
            if (eq < 0) {
                k = decode(pair);
                v = "";
            } else {
                k = decode(pair.substring(0, eq));
                v = decode(pair.substring(eq + 1));
            }
            out.put(k, v);
        }
        return out;
    }

    private static String decode(String s) throws NostrUriException {
        try {
            return URLDecoder.decode(s, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            throw new NostrUriException("Failed to decode query parameter", e);
        }
    }

    private static NostrUri.Kind classify(final String bech32) {
        final String lower = bech32.toLowerCase(Locale.US);
        if (lower.startsWith("npub1")) return NostrUri.Kind.NPUB;
        if (lower.startsWith("nprofile1")) return NostrUri.Kind.NPROFILE;
        if (lower.startsWith("note1")) return NostrUri.Kind.NOTE;
        if (lower.startsWith("nevent1")) return NostrUri.Kind.NEVENT;
        if (lower.startsWith("naddr1")) return NostrUri.Kind.NADDR;
        return NostrUri.Kind.UNKNOWN;
    }

    // Old permissive bech32 validator removed in favor of NostrBech32Util
}
