package biz.nostr.android.nip55;

import static org.junit.Assert.*;

import android.content.Intent;
import android.net.Uri;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Tests for the IntentBuilder class that creates Intents for NIP-55 flows.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class IntentBuilderRobolectricTest {

    @Test
    public void testGetPublicKeyIntent() {
        String packageName = "com.example.signer";
        String permissions = "somePermissions";

        Intent intent = IntentBuilder.getPublicKeyIntent(packageName, permissions);

        assertEquals("Intent action should be VIEW", Intent.ACTION_VIEW, intent.getAction());
        assertEquals("nostrsigner:", intent.getData().toString());
        assertEquals(packageName, intent.getPackage());
        assertEquals("get_public_key", intent.getStringExtra("type"));
        assertEquals(permissions, intent.getStringExtra("permissions"));
    }

    @Test
    public void testGetPublicKeyIntent_withPermissions() {
        String packageName = "com.example.signer";
        String permissionsJson = "{\"permissions\":[{\"type\":\"sign_event\",\"kind\":22242},{\"type\":\"nip44_decrypt\"}]}";
        
        Intent intent = IntentBuilder.getPublicKeyIntent(packageName, permissionsJson);

        assertEquals(Intent.ACTION_VIEW, intent.getAction());
        assertEquals("nostrsigner:", intent.getData().toString());
        assertEquals(packageName, intent.getPackage());
        assertEquals("get_public_key", intent.getStringExtra("type"));
        assertEquals(permissionsJson, intent.getStringExtra("permissions"));
    }

    @Test
    public void testSignEventIntent() {
        String packageName = "com.example.signer";
        String eventJson = "{\"content\":\"hello\"}";
        String eventId = "event-123";
        String npub = "npubUser";

        Intent intent = IntentBuilder.signEventIntent(packageName, eventJson, eventId, npub);

        assertEquals(Intent.ACTION_VIEW, intent.getAction());
        assertEquals("nostrsigner:" + eventJson, intent.getData().toString());
        assertEquals(packageName, intent.getPackage());
        assertEquals("sign_event", intent.getStringExtra("type"));
        assertEquals(eventId, intent.getStringExtra("id"));
        assertEquals(npub, intent.getStringExtra("current_user"));
    }

    @Test
    public void testNip04EncryptIntent() {
        String packageName = "com.example.signer";
        String plainText = "HelloWorld";
        String id = "encrypt123";
        String npub = "npub123";
        String pubKey = "pubKeyXYZ";

        Intent intent = IntentBuilder.nip04EncryptIntent(packageName, plainText, id, npub, pubKey);

        assertEquals(Intent.ACTION_VIEW, intent.getAction());
        assertEquals("nostrsigner:" + plainText, intent.getData().toString());
        assertEquals(packageName, intent.getPackage());
        assertEquals("nip04_encrypt", intent.getStringExtra("type"));
        assertEquals(id, intent.getStringExtra("id"));
        assertEquals(npub, intent.getStringExtra("current_user"));
        assertEquals(pubKey, intent.getStringExtra("pubKey"));
    }

    @Test
    public void testNip44EncryptIntent() {
        String packageName = "com.example.signer";
        String plainText = "PlainNIP44";
        String id = "encrypt44";
        String npub = "npubUser44";
        String pubKey = "pubKey44";

        Intent intent = IntentBuilder.nip44EncryptIntent(packageName, plainText, id, npub, pubKey);

        assertEquals(Intent.ACTION_VIEW, intent.getAction());
        assertEquals("nostrsigner:" + plainText, intent.getData().toString());
        assertEquals(packageName, intent.getPackage());
        assertEquals("nip44_encrypt", intent.getStringExtra("type"));
        assertEquals(id, intent.getStringExtra("id"));
        assertEquals(npub, intent.getStringExtra("current_user"));
        assertEquals(pubKey, intent.getStringExtra("pubKey"));
    }

    @Test
    public void testNip04DecryptIntent() {
        String packageName = "com.example.signer";
        String encryptedText = "EncryptedData04";
        String id = "decrypt04";
        String npub = "npubDecrypt";
        String pubKey = "senderKey04";

        Intent intent = IntentBuilder.nip04DecryptIntent(packageName, encryptedText, id, npub, pubKey);

        assertEquals(Intent.ACTION_VIEW, intent.getAction());
        assertEquals("nostrsigner:" + encryptedText, intent.getData().toString());
        assertEquals(packageName, intent.getPackage());
        assertEquals("nip04_decrypt", intent.getStringExtra("type"));
        assertEquals(id, intent.getStringExtra("id"));
        assertEquals(npub, intent.getStringExtra("current_user"));
        assertEquals(pubKey, intent.getStringExtra("pubKey"));
    }

    @Test
    public void testNip44DecryptIntent() {
        String packageName = "com.example.signer";
        String encryptedText = "EncryptedData44";
        String id = "decrypt44";
        String npub = "npubUserDecrypt44";
        String pubKey = "senderKey44";

        Intent intent = IntentBuilder.nip44DecryptIntent(packageName, encryptedText, id, npub, pubKey);

        assertEquals(Intent.ACTION_VIEW, intent.getAction());
        assertEquals("nostrsigner:" + encryptedText, intent.getData().toString());
        assertEquals(packageName, intent.getPackage());
        assertEquals("nip44_decrypt", intent.getStringExtra("type"));
        assertEquals(id, intent.getStringExtra("id"));
        assertEquals(npub, intent.getStringExtra("current_user"));
        assertEquals(pubKey, intent.getStringExtra("pubKey"));
    }

    @Test
    public void testDecryptZapEventIntent() {
        String packageName = "com.example.signer";
        String eventJson = "{\"zap\":\"event\"}";
        String id = "zapId";
        String npub = "npubZap";

        Intent intent = IntentBuilder.decryptZapEventIntent(packageName, eventJson, id, npub);

        assertEquals(Intent.ACTION_VIEW, intent.getAction());
        assertEquals("nostrsigner:" + eventJson, intent.getData().toString());
        assertEquals(packageName, intent.getPackage());
        assertEquals("decrypt_zap_event", intent.getStringExtra("type"));
        assertEquals(id, intent.getStringExtra("id"));
        assertEquals(npub, intent.getStringExtra("current_user"));
    }

    @Test
    public void testGetRelaysIntent() {
        String packageName = "com.example.signer";
        String id = "relay123";
        String npub = "npubRelay";

        Intent intent = IntentBuilder.getRelaysIntent(packageName, id, npub);

        assertEquals(Intent.ACTION_VIEW, intent.getAction());
        assertEquals("nostrsigner:", intent.getData().toString());
        assertEquals(packageName, intent.getPackage());
        assertEquals("get_relays", intent.getStringExtra("type"));
        assertEquals(id, intent.getStringExtra("id"));
        assertEquals(npub, intent.getStringExtra("current_user"));
    }

}
