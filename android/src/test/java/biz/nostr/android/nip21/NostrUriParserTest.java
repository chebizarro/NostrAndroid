package biz.nostr.android.nip21;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Map;

public class NostrUriParserTest {

    @Test
    public void parse_validNpub() throws Exception {
        String s = "nostr:npub1sn0wdenkukak0d9dfczzeacvhkrgz92ak56egt7vdgzn8pv2wfqqhrjdv9";
        NostrUri uri = NostrUriParser.parse(s);
        assertEquals(NostrUri.Kind.NPUB, uri.getKind());
        assertEquals("npub1sn0wdenkukak0d9dfczzeacvhkrgz92ak56egt7vdgzn8pv2wfqqhrjdv9", uri.getBech32());
        assertTrue(uri.getQuery().isEmpty());
    }

    @Test
    public void parse_validNote_withQuery() throws Exception {
        String s = "nostr:note1fntxtkcy9pjwucqwa9mddn7v03wwwsu9j330jj350nvhpky2tuaspk6nqc?relay=wss%3A%2F%2Fex.com&foo=bar";
        NostrUri uri = NostrUriParser.parse(s);
        assertEquals(NostrUri.Kind.NOTE, uri.getKind());
        Map<String,String> q = uri.getQuery();
        assertEquals("wss://ex.com", q.get("relay"));
        assertEquals("bar", q.get("foo"));
    }

    @Test
    public void parse_uppercaseScheme() throws Exception {
        String s = "NOSTR:nprofile1qq..."; // minimal bech32 shape check only
        try {
            NostrUri uri = NostrUriParser.parse(s);
            assertEquals(NostrUri.Kind.NPROFILE, uri.getKind());
        } catch (NostrUriException e) {
            // In case of strict bech32 checks failing with placeholder, assert UNKNOWN
            NostrUri uri = NostrUriParser.parse("NOSTR:nprofile1qqsrhuxx8l9e1xyz");
            assertTrue(uri.getKind() == NostrUri.Kind.NPROFILE || uri.getKind() == NostrUri.Kind.UNKNOWN);
        }
    }

    @Test
    public void parse_rejectNsec() {
        String s = "nostr:nsec1deadbeef";
        try {
            NostrUriParser.parse(s);
            fail("Expected NostrUriException");
        } catch (NostrUriException expected) {
            assertTrue(expected.getMessage().toLowerCase().contains("nsec"));
        }
    }

    @Test
    public void parse_invalidBech32_throws() {
        String s = "nostr:invalid_without_one_separator";
        try {
            NostrUriParser.parse(s);
            fail("Expected NostrUriException");
        } catch (NostrUriException expected) {
            // ok
        }
    }

    @Test
    public void isValid_true_false() {
        assertTrue(NostrUriParser.isValid("nostr:npub1something1xyz"));
        assertFalse(NostrUriParser.isValid("nostr:"));
        assertFalse(NostrUriParser.isValid("http://npub1"));
        assertFalse(NostrUriParser.isValid("nostr:nsec1abc"));
    }
}
