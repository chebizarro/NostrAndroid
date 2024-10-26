package biz.nostr.android.nip55;

import org.junit.Test;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class IntentBuilderTest {

    @Test
    public void testGetPublicKeyIntent() {
        String packageName = "com.example.signerapp";
        String permissions = "read,write";

        Intent intent = IntentBuilder.getPublicKeyIntent(packageName, permissions);

        assertEquals(Intent.ACTION_VIEW, intent.getAction());
        assertEquals("nostrsigner:", intent.getDataString());
        assertEquals(packageName, intent.getPackage());
        assertEquals("get_public_key", intent.getStringExtra("type"));
        assertEquals(permissions, intent.getStringExtra("permissions"));
    }
}

@RunWith(MockitoJUnitRunner.class)
public class ContentProviderTest {

    @Mock
    ContentResolver mockContentResolver;

    @Mock
    Cursor mockCursor;

    @Test
    public void testQueryContentProvider() {
        // Setup mock behavior
        when(mockContentResolver.query(any(Uri.class), any(), any(), any(), any()))
                .thenReturn(mockCursor);

    }
}
