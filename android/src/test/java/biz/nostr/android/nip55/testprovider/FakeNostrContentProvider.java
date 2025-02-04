package biz.nostr.android.nip55.testprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

/**
 * A fake content provider that always returns a MatrixCursor with
 * a “signature” column set to some test value.
 */
public class FakeNostrContentProvider extends ContentProvider {

    private static final String TAG = "FakeNostrProvider";

    // We can store a “testSignature” for returning in queries
    private String testSignature = null;
    private String signEventSignature;
    private String signEventContent;

    @Override
    public boolean onCreate() {
        Log.d(TAG, "FakeNostrContentProvider onCreate called");
        // Return true if provider was successfully loaded.
        return true;
    }

    /**
     * Helper to set the signature we want to return in queries.
     */
    public void setTestSignature(String signature) {
        this.testSignature = signature;
    }

    public void setSignEventValues(String signature, String event) {
        this.signEventSignature = signature;
        this.signEventContent = event;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        if (uri.toString().endsWith(".SIGN_EVENT")) {
            MatrixCursor cursor = new MatrixCursor(new String[]{"signature", "event"});
            cursor.addRow(new Object[]{ signEventSignature, signEventContent });
            return cursor;
        } else if (uri.toString().endsWith(".GET_PUBLIC_KEY")) {
            MatrixCursor cursor = new MatrixCursor(new String[]{"signature"});
            cursor.addRow(new Object[]{ testSignature });
            return cursor;
        } else {
            // Return a single row containing a “signature” column if we have one
            MatrixCursor cursor = new MatrixCursor(new String[]{"signature"});
            if (testSignature != null) {
                cursor.addRow(new Object[]{testSignature});
            }
            return cursor;
        }
    }

    // For completeness, but not strictly needed if only testing queries
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
