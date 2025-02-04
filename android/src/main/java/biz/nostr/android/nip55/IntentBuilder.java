package biz.nostr.android.nip55;

import android.content.Intent;
import android.net.Uri;

public class IntentBuilder {

    public static Intent getPublicKeyIntent(String packageName, String permissions) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("nostrsigner:"));
        intent.setPackage(packageName);
        intent.putExtra("type", "get_public_key");
        if (permissions != null) {
            intent.putExtra("permissions", permissions);
        }
        return intent;
    }

    public static Intent signEventIntent(String packageName, String eventJson, String eventId, String npub) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("nostrsigner:" + eventJson));
        intent.setPackage(packageName);
        intent.putExtra("type", "sign_event");
        intent.putExtra("id", eventId);
        intent.putExtra("current_user", npub);
        return intent;
    }

    public static Intent nip04EncryptIntent(String packageName, String plainText, String id, String npub, String pubKey) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("nostrsigner:" + plainText));
		intent.setPackage(packageName);
		intent.putExtra("type", "nip04_encrypt");
		intent.putExtra("id", id);
		intent.putExtra("current_user", npub);
		intent.putExtra("pubKey", pubKey);
		return intent;
    }

    public static Intent nip44EncryptIntent(String packageName, String plainText, String id, String npub, String pubKey) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("nostrsigner:" + plainText));
		intent.setPackage(packageName);
		intent.putExtra("type", "nip44_encrypt");
		intent.putExtra("id", id);
		intent.putExtra("current_user", npub);
		intent.putExtra("pubKey", pubKey);
		return intent;
    }

    public static Intent nip04DecryptIntent(String packageName, String encryptedText, String id, String npub, String pubKey) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("nostrsigner:" + encryptedText));
		intent.setPackage(packageName);
		intent.putExtra("type", "nip04_decrypt");
		intent.putExtra("id", id);
		intent.putExtra("current_user", npub);
		intent.putExtra("pubKey", pubKey);
		return intent;
    }

    public static Intent nip44DecryptIntent(String signerPackageName, String encryptedText, String id, String npub, String pubKey) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("nostrsigner:" + encryptedText));
		intent.setPackage(signerPackageName);
		intent.putExtra("type", "nip44_decrypt");
		intent.putExtra("id", id);
		intent.putExtra("current_user", npub);
		intent.putExtra("pubKey", pubKey);
        return intent;
    }

    public static Intent decryptZapEventIntent(String signerPackageName, String eventJson, String id, String npub) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("nostrsigner:" + eventJson));
		intent.setPackage(signerPackageName);
		intent.putExtra("type", "decrypt_zap_event");
		intent.putExtra("id", id);
		intent.putExtra("current_user", npub);
		return intent;
    }

    public static Intent getRelaysIntent(String signerPackageName, String id, String npub) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("nostrsigner:"));
        intent.setPackage(signerPackageName);
        intent.putExtra("type", "get_relays");
        intent.putExtra("id", id);
        intent.putExtra("current_user", npub);
        return intent;
    }

}
