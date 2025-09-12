package biz.nostr.android.nip21;

/**
 * Exception type for NIP-21 parsing/building errors.
 */
public final class NostrUriException extends Exception {
    public NostrUriException(String message) {
        super(message);
    }

    public NostrUriException(String message, Throwable cause) {
        super(message, cause);
    }
}
