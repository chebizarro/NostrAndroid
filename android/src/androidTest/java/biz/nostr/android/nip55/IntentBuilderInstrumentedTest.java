package biz.nostr.android.nip55;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class IntentBuilderInstrumentedTest {

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void testGetPublicKeyIntent() {
        // Perform action that triggers the Intent
        onView(withId(R.id.getPublicKeyButton)).perform(click());

        // Verify that the correct Intent was sent
        intended(allOf(
                hasAction(Intent.ACTION_VIEW),
                hasPackage("com.example.signerapp"),
                hasExtra("type", "get_public_key")
        ));
    }
}
