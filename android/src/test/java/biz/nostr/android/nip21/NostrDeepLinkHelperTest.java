package biz.nostr.android.nip21;

import static org.junit.Assert.*;

import android.content.Intent;
import android.net.Uri;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class NostrDeepLinkHelperTest {

    @Test
    public void isNostrDeepLink_true_false() {
        Intent ok = new Intent(Intent.ACTION_VIEW, Uri.parse("nostr:npub1abc1xyz"));
        assertTrue(NostrDeepLinkHelper.isNostrDeepLink(ok));

        Intent wrongScheme = new Intent(Intent.ACTION_VIEW, Uri.parse("http://example.com"));
        assertFalse(NostrDeepLinkHelper.isNostrDeepLink(wrongScheme));

        Intent nullAction = new Intent();
        nullAction.setData(Uri.parse("nostr:npub1abc1xyz"));
        assertFalse(NostrDeepLinkHelper.isNostrDeepLink(nullAction));
    }

    @Test
    public void parseFromIntent_roundTrip() throws Exception {
        Map<String,String> q = new HashMap<>();
        q.put("relay", "wss://relay.example");
        Intent view = NostrDeepLinkHelper.buildViewIntent("note1fntxtkcy9pjwucqwa91xyz", q);
        NostrUri parsed = NostrDeepLinkHelper.parseFromIntent(view);
        assertEquals(NostrUri.Kind.NOTE, parsed.getKind());
        assertEquals("note1fntxtkcy9pjwucqwa91xyz", parsed.getBech32());
        assertEquals("wss://relay.example", parsed.getQuery().get("relay"));
    }
}
