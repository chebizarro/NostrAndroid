package biz.nostr.android.nip21;

import android.content.Intent;
import android.net.Uri;


import java.util.Map;

/**
 * Helper to integrate NIP-21 with Android deep links (no UI).
 */
public final class NostrDeepLinkHelper {
    private NostrDeepLinkHelper() {}

    /** Returns true if the intent is an ACTION_VIEW with scheme nostr:. */
    public static boolean isNostrDeepLink(Intent intent) {
        if (intent == null) return false;
        if (!Intent.ACTION_VIEW.equals(intent.getAction())) return false;
        Uri data = intent.getData();
        if (data == null) return false;
        String scheme = data.getScheme();
        return scheme != null && scheme.equalsIgnoreCase("nostr");
    }

    /** Parse a deep link intent into a NostrUri. */
    public static NostrUri parseFromIntent(Intent intent) throws NostrUriException {
        if (!isNostrDeepLink(intent)) throw new NostrUriException("Not a NIP-21 deep link");
        Uri data = intent.getData();
        // Preserve raw as close as possible
        String raw = data.toString();
        return NostrUriParser.parse(raw);
    }

    /** Build an ACTION_VIEW intent for a given bech32+query. */
    public static Intent buildViewIntent(final String bech32, final Map<String,String> query) throws NostrUriException {
        final String uri = NostrUriBuilder.build(bech32, query);
        return new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
    }
}
