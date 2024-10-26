package biz.nostr.android.nip55;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ContentProviderInstrumentedTest {

    @Rule
    public ProviderTestRule providerTestRule = new ProviderTestRule.Builder(MockContentProvider.class, "com.example.provider").build();

    @Test
    public void testQuery() {
        ContentResolver resolver = providerTestRule.getResolver();
        Cursor cursor = resolver.query(/* URI and other parameters */);

        assertNotNull(cursor);
        // Assert cursor data
    }
}
