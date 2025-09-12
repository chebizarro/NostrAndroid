package biz.nostr.android.nip21;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable representation of a NIP-21 nostr: URI.
 */
public final class NostrUri {
    public enum Kind {
        NPUB, NPROFILE, NOTE, NEVENT, NADDR, UNKNOWN
    }

    private final String raw;            // original input string
    private final Kind kind;             // classification by bech32 prefix
    private final String bech32;         // bech32 entity (npub1..., note1..., etc.)
    private final Map<String, String> query; // decoded query map (unmodifiable)

    NostrUri(final String raw, final Kind kind, final String bech32, final Map<String, String> query) {
        this.raw = raw;
        this.kind = kind;
        this.bech32 = bech32;
        this.query = query == null ? Collections.emptyMap() : Collections.unmodifiableMap(new LinkedHashMap<>(query));
    }

    public String getRaw() { return raw; }
    public Kind getKind() { return kind; }
    public String getBech32() { return bech32; }
    public Map<String, String> getQuery() { return query; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NostrUri)) return false;
        NostrUri that = (NostrUri) o;
        return Objects.equals(raw, that.raw)
                && kind == that.kind
                && Objects.equals(bech32, that.bech32)
                && Objects.equals(query, that.query);
    }

    @Override
    public int hashCode() {
        return Objects.hash(raw, kind, bech32, query);
    }

    @Override
    public String toString() {
        return "NostrUri{" +
                "kind=" + kind +
                ", bech32='" + bech32 + '\'' +
                ", query=" + query +
                '}';
    }
}
