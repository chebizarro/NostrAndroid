package biz.nostr.android.nip55;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Minimal test ContentProvider that allows mapping an authority to a single-row MatrixCursor.
 * Used only in Robolectric tests.
 */
public class TestCursorProvider extends ContentProvider {
    private final Map<String, MatrixCursor> responses = new HashMap<>();

    @Override
    public boolean onCreate() {
        return true;
    }

    public void setResponse(String authority, String[] columns, Object[] row) {
        MatrixCursor mc = new MatrixCursor(columns);
        if (row != null) mc.addRow(row);
        responses.put(authority, mc);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        MatrixCursor mc = responses.get(uri.getAuthority());
        return mc == null ? null : mc;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }
}
