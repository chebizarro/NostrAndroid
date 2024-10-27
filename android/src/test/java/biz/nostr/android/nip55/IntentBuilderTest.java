package biz.nostr.android.nip55;

import static org.junit.Assert.assertEquals;

import android.content.Intent;

import org.junit.Test;
import org.junit.runner.RunWith;

public class IntentBuilderTest {

    @Test
    public void testGetPublicKeyIntent() {
        String packageName = "com.example.signerapp";

        Intent intent = IntentBuilder.getPublicKeyIntent(packageName, "");

        assertEquals(Intent.ACTION_VIEW, intent.getAction());
        assertEquals("nostrsigner:", intent.getDataString());
        assertEquals(packageName, intent.getPackage());
        assertEquals("get_public_key", intent.getStringExtra("type"));
    }
}
