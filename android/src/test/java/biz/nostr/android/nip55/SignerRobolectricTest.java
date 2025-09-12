package biz.nostr.android.nip55;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ContentProviderController;
import org.robolectric.shadows.ShadowPackageManager;

@RunWith(RobolectricTestRunner.class)
public class SignerRobolectricTest {

    private TestCursorProvider attachProvider(String authority) {
        ProviderInfo info = new ProviderInfo();
        info.authority = authority;
        ContentProviderController<TestCursorProvider> controller = Robolectric.buildContentProvider(TestCursorProvider.class).create(info);
        return controller.get();
    }

    @Test
    public void getPublicKey_success() {
        Context ctx = ApplicationProvider.getApplicationContext();
        String authority = "com.example.signer.GET_PUBLIC_KEY";
        TestCursorProvider provider = attachProvider(authority);
        provider.setResponse(authority, new String[]{"result"}, new Object[]{"npub1xyz"});

        String pubkey = Signer.getPublicKey(ctx, "com.example.signer");
        assertEquals("npub1xyz", pubkey);
    }

    @Test
    public void getPublicKey_rejected_returnsNull() {
        Context ctx = ApplicationProvider.getApplicationContext();
        String authority = "com.example.signer.GET_PUBLIC_KEY";
        TestCursorProvider provider = attachProvider(authority);
        provider.setResponse(authority, new String[]{"rejected"}, new Object[]{1});

        String pubkey = Signer.getPublicKey(ctx, "com.example.signer");
        assertNull(pubkey);
    }

    @Test
    public void signEvent_success_returnsSignatureAndEvent() {
        Context ctx = ApplicationProvider.getApplicationContext();
        String authority = "com.example.signer.SIGN_EVENT";
        TestCursorProvider provider = attachProvider(authority);
        provider.setResponse(authority, new String[]{"result","event"}, new Object[]{"sig123","{\\\"id\\\":\\\"abc\\\"}"});

        String[] res = Signer.signEvent(ctx, "com.example.signer", "{\"id\":\"abc\"}", "npub1me");
        assertNotNull(res);
        assertEquals("sig123", res[0]);
        assertEquals("{\"id\":\"abc\"}", res[1]);
    }

    @Test
    public void nip04Encrypt_success_returnsResult() {
        Context ctx = ApplicationProvider.getApplicationContext();
        String authority = "com.example.signer.NIP04_ENCRYPT";
        TestCursorProvider provider = attachProvider(authority);
        provider.setResponse(authority, new String[]{"result"}, new Object[]{"enc-abc"});

        String res = Signer.nip04Encrypt(ctx, "com.example.signer", "hello", "hexpub", "npub1me");
        assertEquals("enc-abc", res);
    }

    @Test
    public void nip04Decrypt_success_returnsResult() {
        Context ctx = ApplicationProvider.getApplicationContext();
        String authority = "com.example.signer.NIP04_DECRYPT";
        TestCursorProvider provider = attachProvider(authority);
        provider.setResponse(authority, new String[]{"result"}, new Object[]{"plain-abc"});

        String res = Signer.nip04Decrypt(ctx, "com.example.signer", "enc-abc", "hexpub", "npub1me");
        assertEquals("plain-abc", res);
    }

    @Test
    public void nip44EncryptDecrypt_success_returnsResult() {
        Context ctx = ApplicationProvider.getApplicationContext();
        String encAuth = "com.example.signer.NIP44_ENCRYPT";
        TestCursorProvider encProvider = attachProvider(encAuth);
        encProvider.setResponse(encAuth, new String[]{"result"}, new Object[]{"enc44"});

        String enc = Signer.nip44Encrypt(ctx, "com.example.signer", "hello", "hexpub", "npub1me");
        assertEquals("enc44", enc);

        String decAuth = "com.example.signer.NIP44_DECRYPT";
        TestCursorProvider decProvider = attachProvider(decAuth);
        decProvider.setResponse(decAuth, new String[]{"result"}, new Object[]{"plain44"});

        String dec = Signer.nip44Decrypt(ctx, "com.example.signer", "enc44", "hexpub", "npub1me");
        assertEquals("plain44", dec);
    }

    @Test
    public void decryptZapEvent_success_returnsResult() {
        Context ctx = ApplicationProvider.getApplicationContext();
        String authority = "com.example.signer.DECRYPT_ZAP_EVENT";
        TestCursorProvider provider = attachProvider(authority);
        provider.setResponse(authority, new String[]{"result"}, new Object[]{"{\\\"kind\\\":9735}"});

        String res = Signer.decryptZapEvent(ctx, "com.example.signer", "{}", "npub1me");
        assertEquals("{\"kind\":9735}", res);
    }

    @Test
    public void getRelays_success_returnsResult() {
        Context ctx = ApplicationProvider.getApplicationContext();
        String authority = "com.example.signer.GET_RELAYS";
        TestCursorProvider provider = attachProvider(authority);
        provider.setResponse(authority, new String[]{"result"}, new Object[]{"[\"wss://relay.example\"]"});

        String res = Signer.getRelays(ctx, "com.example.signer", "npub1me");
        assertEquals("[\"wss://relay.example\"]", res);
    }

    @Test
    public void isExternalSignerInstalled_true_whenHandlerPresent() {
        Context ctx = ApplicationProvider.getApplicationContext();
        ShadowPackageManager spm = Shadows.shadowOf(ctx.getPackageManager());
        Intent view = new Intent(Intent.ACTION_VIEW, Uri.parse("nostrsigner:"));
        ResolveInfo ri = new ResolveInfo();
        ri.activityInfo = new ActivityInfo();
        ri.activityInfo.packageName = "com.example.signer";
        spm.addResolveInfoForIntent(view, ri);

        assertTrue(Signer.isExternalSignerInstalled(ctx));
        assertTrue(Signer.isSignerPackageAvailable(ctx, "com.example.signer"));
        assertFalse(Signer.isSignerPackageAvailable(ctx, "com.other"));
    }
}
