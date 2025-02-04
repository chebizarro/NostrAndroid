package biz.nostr.android.nip55;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.database.MatrixCursor;
import android.net.Uri;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ContentProviderController;
import org.robolectric.annotation.Config;

import java.util.List;

import biz.nostr.android.nip55.testprovider.FakeNostrContentProvider;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})
public class SignerRobolectricTest {

    private Context context;
    private static final String TEST_PACKAGE = "com.example.signer";

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
    }

    // Helper method to register a fake provider using the new API.
    private void registerFakeProvider(String authority, String columnName, String value) {
        ProviderInfo info = new ProviderInfo();
        info.authority = authority;
        info.grantUriPermissions = true;

        MatrixCursor cursor = new MatrixCursor(new String[]{columnName});
        cursor.addRow(new Object[]{value});

        ContentProviderController<FakeNostrContentProvider> controller =
                org.robolectric.Robolectric.buildContentProvider(FakeNostrContentProvider.class)
                        .create(info);
        // Set the expected value in our fake provider.
        FakeNostrContentProvider provider = controller.get();
        provider.setTestSignature(value);
    }

    @Test
    public void testGetPublicKey() {
        String authority = TEST_PACKAGE + ".GET_PUBLIC_KEY";
        String expectedNpub = "npubTestValue";

        // Register fake provider under the correct authority.
        registerFakeProvider(authority, "signature", expectedNpub);

        String result = Signer.getPublicKey(context, TEST_PACKAGE);
        assertNotNull("Public key should not be null", result);
        assertEquals("Public key should match", expectedNpub, result);
    }

    @Test
    public void testSignEvent() {
        // For signEvent, the URI is "content://<packageName>.SIGN_EVENT"
        String authority = TEST_PACKAGE + ".SIGN_EVENT";
        // Create a cursor with columns "signature" and "event"
        MatrixCursor cursor = new MatrixCursor(new String[]{"signature", "event"});
        String expectedSignature = "dummySignature";
        String expectedEventJson = "{\"signed\":\"event\"}";
        cursor.addRow(new Object[]{expectedSignature, expectedEventJson});

        // Register our fake provider.
        ProviderInfo info = new ProviderInfo();
        info.authority = authority;
        info.grantUriPermissions = true;
        ContentProviderController<FakeNostrContentProvider> controller =
                org.robolectric.Robolectric.buildContentProvider(FakeNostrContentProvider.class)
                        .create(info);
        FakeNostrContentProvider provider = controller.get();
        // Since our FakeNostrContentProvider is simple, we simulate the expected behavior
        // by setting the "dummySignature" in the provider. (For signEvent, our test provider should
        // return a cursor with two columns; here we assume our Fake provider always returns a fixed value.)
        provider.setTestSignature(expectedSignature);

        // Because our FakeNostrContentProvider always returns one column ("signature"),
        // we simulate signEvent by building our own MatrixCursor and passing it to our test code.
        String eventJson = "{\"content\":\"Hello\"}";
        String eventId = "event123";
        String loggedInUserNpub = "npubUser";

        provider.setSignEventValues("dummySignature", "{\"signed\":\"event\"}");

        String[] result = Signer.signEvent(context, TEST_PACKAGE, "{\"content\":\"Hello\"}", "npubUser");
        assertNotNull("Signed event result should not be null", result);
        assertEquals("dummySignature", result[0]);
        assertEquals("{\"signed\":\"event\"}", result[1]);    }

    @Test
    public void testNip04Encrypt() {
        String authority = TEST_PACKAGE + ".NIP04_ENCRYPT";
        String expectedEncrypted = "encryptedTextNIP04";
        registerFakeProvider(authority, "signature", expectedEncrypted);

        String plainText = "plainText";
        String recipientPubKey = "recipientKey";
        String loggedInUserNpub = "npubUser";

        String result = Signer.nip04Encrypt(context, TEST_PACKAGE, plainText, recipientPubKey, loggedInUserNpub);
        assertNotNull("Encrypted text should not be null", result);
        assertEquals("Encrypted text should match", expectedEncrypted, result);
    }

    @Test
    public void testNip04Decrypt() {
        String authority = TEST_PACKAGE + ".NIP04_DECRYPT";
        String expectedDecrypted = "decryptedTextNIP04";
        registerFakeProvider(authority, "signature", expectedDecrypted);

        String encryptedText = "encryptedText";
        String senderPubKey = "senderKey";
        String loggedInUserNpub = "npubUser";

        String result = Signer.nip04Decrypt(context, TEST_PACKAGE, encryptedText, senderPubKey, loggedInUserNpub);
        assertNotNull("Decrypted text should not be null", result);
        assertEquals("Decrypted text should match", expectedDecrypted, result);
    }

    @Test
    public void testNip44Encrypt() {
        String authority = TEST_PACKAGE + ".NIP44_ENCRYPT";
        String expectedEncrypted = "encryptedTextNIP44";
        registerFakeProvider(authority, "signature", expectedEncrypted);

        String plainText = "plainText";
        String recipientPubKey = "recipientKey";
        String loggedInUserNpub = "npubUser";

        String result = Signer.nip44Encrypt(context, TEST_PACKAGE, plainText, recipientPubKey, loggedInUserNpub);
        assertNotNull("Encrypted text should not be null", result);
        assertEquals("Encrypted text should match", expectedEncrypted, result);
    }

    @Test
    public void testNip44Decrypt() {
        String authority = TEST_PACKAGE + ".NIP44_DECRYPT";
        String expectedDecrypted = "decryptedTextNIP44";
        registerFakeProvider(authority, "signature", expectedDecrypted);

        String encryptedText = "encryptedText";
        String senderPubKey = "senderKey";
        String loggedInUserNpub = "npubUser";

        String result = Signer.nip44Decrypt(context, TEST_PACKAGE, encryptedText, senderPubKey, loggedInUserNpub);
        assertNotNull("Decrypted text should not be null", result);
        assertEquals("Decrypted text should match", expectedDecrypted, result);
    }

    @Test
    public void testDecryptZapEvent() {
        String authority = TEST_PACKAGE + ".DECRYPT_ZAP_EVENT";
        String expectedDecrypted = "decryptedZapEvent";
        registerFakeProvider(authority, "signature", expectedDecrypted);

        String eventJson = "{\"content\":\"zap event\"}";
        String loggedInUserNpub = "npubUser";

        String result = Signer.decryptZapEvent(context, TEST_PACKAGE, eventJson, loggedInUserNpub);
        assertNotNull("Decrypted zap event should not be null", result);
        assertEquals("Decrypted zap event should match", expectedDecrypted, result);
    }

    @Test
    public void testGetRelays() {
        String authority = TEST_PACKAGE + ".GET_RELAYS";
        String expectedRelays = "relayJsonData";
        registerFakeProvider(authority, "signature", expectedRelays);

        String result = Signer.getRelays(context, TEST_PACKAGE, "npubUser");
        assertNotNull("Relay JSON should not be null", result);
        assertEquals("Relay JSON should match", expectedRelays, result);
    }

    @Test
    public void testIsExternalSignerInstalled() {
        // For PackageManager tests, we use the real PackageManager.
        // Since our fake providers do not affect PackageManager queries,
        // we simply assert that the result is not null.
        List<ResolveInfo> resolveInfos = Signer.isExternalSignerInstalled(context, TEST_PACKAGE);
        assertNotNull("resolveInfos should not be null", resolveInfos);
    }

    @Test
    public void testGetInstalledSignerApps() {
        // Test that getInstalledSignerApps returns a non-null list.
        List<AppInfo> apps = Signer.getInstalledSignerApps(context);
        assertNotNull("apps list should not be null", apps);
    }
}
