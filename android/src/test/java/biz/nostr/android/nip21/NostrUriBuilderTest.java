package biz.nostr.android.nip21;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class NostrUriBuilderTest {

    @Test
    public void build_basic() throws Exception {
        String out = NostrUriBuilder.build("npub1sn0wdenkukak0d9dfc1xyz");
        assertEquals("nostr:npub1sn0wdenkukak0d9dfc1xyz", out);
    }

    @Test
    public void build_withQuery() throws Exception {
        Map<String,String> q = new HashMap<>();
        q.put("relay", "wss://ex.com");
        q.put("foo", "bar baz");
        String out = NostrUriBuilder.build("nevent1qqstna2y1xyz", q);
        assertTrue(out.startsWith("nostr:nevent1qqstna2y1xyz?"));
        assertTrue(out.contains("relay=wss%3A%2F%2Fex.com"));
        assertTrue(out.contains("foo=bar+baz") || out.contains("foo=bar%20baz"));
    }

    @Test
    public void build_rejectNsec() {
        try {
            NostrUriBuilder.build("nsec1deadbeef");
            fail("Expected NostrUriException");
        } catch (NostrUriException expected) {
            // ok
        }
    }
}
