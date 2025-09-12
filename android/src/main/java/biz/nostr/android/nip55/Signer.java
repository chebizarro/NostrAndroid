package biz.nostr.android.nip55;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Signer {
    private static volatile boolean LEGACY_FALLBACK_LOG = false;

    public static void setLegacyFallbackLogging(boolean enabled) {
        LEGACY_FALLBACK_LOG = enabled;
    }
    public static List<ResolveInfo> isExternalSignerInstalled(Context context, String packageName) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("nostrsigner:"));
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> all = packageManager.queryIntentActivities(intent, 0);
        if (packageName == null || packageName.isEmpty()) return all;
        List<ResolveInfo> filtered = new ArrayList<>();
        for (ResolveInfo ri : all) {
            if (ri.activityInfo != null && packageName.equals(ri.activityInfo.packageName)) {
                filtered.add(ri);
            }
        }
        return filtered;
    }

    public static boolean isExternalSignerInstalled(Context context) {
        if (context == null) return false;
        return isExternalSignerInstalled(context, null).size() > 0;
    }

    public static boolean isSignerPackageAvailable(Context context, String packageName) {
        if (context == null || packageName == null || packageName.isEmpty()) return false;
        List<ResolveInfo> infos = isExternalSignerInstalled(context, packageName);
        for (ResolveInfo ri : infos) {
            if (ri.activityInfo != null && packageName.equals(ri.activityInfo.packageName)) return true;
        }
        return false;
    }

    public static List<AppInfo> getInstalledSignerApps(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("nostrsigner:"));
        PackageManager packageManager = context.getPackageManager();

        List<ResolveInfo> resolveInfos = isExternalSignerInstalled(context, null);
        List<AppInfo> appsArray = new ArrayList<>();
        for (ResolveInfo resolveInfo : resolveInfos) {
            CharSequence appName = resolveInfo.loadLabel(packageManager);
            String packageName = resolveInfo.activityInfo.packageName;
            Drawable iconDrawable = resolveInfo.loadIcon(packageManager);
            AppInfo appInfo = new AppInfo(appName, packageName, iconDrawable);
            appsArray.add(appInfo);
        }
        return appsArray;
    }

    public static String getPublicKey(Context context, String packageName) {
        if (context == null || packageName == null || packageName.isEmpty()) return null;
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://" + packageName + ".GET_PUBLIC_KEY");
        String[] projection = new String[] { "login" };
        Cursor result = contentResolver.query(uri, projection, null, null, null);
        if (result == null) {
            return null;
        }
        try {
            // If signer indicates permanent rejection, do not proceed
            if (result.getColumnIndex("rejected") > -1) return null;

            String npub = null;
            if (result.moveToFirst()) {
                int index = result.getColumnIndex("result");
                if (index < 0) {
                    // Legacy fallback
                    index = result.getColumnIndex("signature");
                    if (LEGACY_FALLBACK_LOG && index >= 0) Log.d("NIP55", "Using legacy column 'signature' for getPublicKey");
                }
                if (index >= 0) {
                    npub = result.getString(index);
                }
            }
            return npub;
        } finally {
            result.close();
        }
    }

    public static String[] signEvent(Context context, String packageName, String eventJson, String loggedInUserNpub) {
        if (context == null || packageName == null || packageName.isEmpty() || eventJson == null || loggedInUserNpub == null) return null;
        Uri uri = Uri.parse("content://" + packageName + ".SIGN_EVENT");
        String[] projection = new String[] { eventJson, "", loggedInUserNpub };
        ContentResolver contentResolver = context.getContentResolver();
        Cursor result = contentResolver.query(uri, projection, null, null, null);
        if (result == null) {
            return null;
        }
        try {
            if (result.getColumnIndex("rejected") > -1) return null;

            String[] signedEvent = null;
            if (result.moveToFirst()) {
                int sigIdx = result.getColumnIndex("result");
                if (sigIdx < 0) {
                    // Legacy fallback
                    sigIdx = result.getColumnIndex("signature");
                    if (LEGACY_FALLBACK_LOG && sigIdx >= 0) Log.d("NIP55", "Using legacy column 'signature' for signEvent");
                }
                int eventIndex = result.getColumnIndex("event");

                if (sigIdx >= 0 && eventIndex >= 0) {
                    String signature = result.getString(sigIdx);
                    String signedEventJson = unescapeJson(result.getString(eventIndex));
                    signedEvent = new String[]{signature, signedEventJson};
                }
            }
            return signedEvent;
        } finally {
            result.close();
        }
    }

    public static String nip04Encrypt(Context context, String packageName, String plainText, String recipientPubKey, String loggedInUserNpub) {
        if (context == null || packageName == null || packageName.isEmpty() || plainText == null || recipientPubKey == null || loggedInUserNpub == null) return null;
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://" + packageName + ".NIP04_ENCRYPT");
        String[] projection = new String[] { plainText, recipientPubKey, loggedInUserNpub };
        Cursor result = contentResolver.query(uri, projection, null, null, null);

        if (result == null) {
            return null;
        }

        try {
            if (result.getColumnIndex("rejected") > -1) return null;

            String encryptedText = null;
            if (result.moveToFirst()) {
                int index = result.getColumnIndex("result");
                if (index < 0) {
                    // Legacy fallback
                    index = result.getColumnIndex("signature");
                    if (LEGACY_FALLBACK_LOG && index >= 0) Log.d("NIP55", "Using legacy column 'signature' for nip04Encrypt");
                }
                if (index >= 0) {
                    encryptedText = result.getString(index);
                }
            }
            return encryptedText;
        } finally {
            result.close();
        }
    }

    public static String nip04Decrypt(Context context, String packageName, String encryptedText, String senderPubKey, String loggedInUserNpub) {
        if (context == null || packageName == null || packageName.isEmpty() || encryptedText == null || senderPubKey == null || loggedInUserNpub == null) return null;
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://" + packageName + ".NIP04_DECRYPT");
        String[] projection = new String[] { encryptedText, senderPubKey, loggedInUserNpub };
        Cursor result = contentResolver.query(uri, projection, null, null, null);

        if (result == null) {
            return null;
        }

        try {
            if (result.getColumnIndex("rejected") > -1) return null;

            String decryptedText = null;
            if (result.moveToFirst()) {
                int index = result.getColumnIndex("result");
                if (index < 0) {
                    // Legacy fallback
                    index = result.getColumnIndex("signature");
                    if (LEGACY_FALLBACK_LOG && index >= 0) Log.d("NIP55", "Using legacy column 'signature' for nip04Decrypt");
                }
                if (index >= 0) {
                    decryptedText = result.getString(index);
                }
            }
            return decryptedText;
        } finally {
            result.close();
        }
    }

    public static String nip44Encrypt(Context context, String packageName, String plainText, String recipientPubKey, String loggedInUserNpub) {
        if (context == null || packageName == null || packageName.isEmpty() || plainText == null || recipientPubKey == null || loggedInUserNpub == null) return null;
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://" + packageName + ".NIP44_ENCRYPT");
        String[] projection = new String[] { plainText, recipientPubKey, loggedInUserNpub };
        Cursor result = contentResolver.query(uri, projection, null, null, null);

        if (result == null) {
            return null;
        }

        try {
            if (result.getColumnIndex("rejected") > -1) return null;

            String encryptedText = null;
            if (result.moveToFirst()) {
                int index = result.getColumnIndex("result");
                if (index < 0) {
                    // Legacy fallback
                    index = result.getColumnIndex("signature");
                }
                if (index >= 0) {
                    encryptedText = result.getString(index);
                }
            }
            return encryptedText;
        } finally {
            result.close();
        }
    }

    public static String nip44Decrypt(Context context, String packageName, String encryptedText, String senderPubKey, String loggedInUserNpub) {
        if (context == null || packageName == null || packageName.isEmpty() || encryptedText == null || senderPubKey == null || loggedInUserNpub == null) return null;
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://" + packageName + ".NIP44_DECRYPT");
        String[] projection = new String[] { encryptedText, senderPubKey, loggedInUserNpub };
        Cursor result = contentResolver.query(uri, projection, null, null, null);

        if (result == null) {
            return null;
        }

        try {
            if (result.getColumnIndex("rejected") > -1) return null;

            String decryptedText = null;
            if (result.moveToFirst()) {
                int index = result.getColumnIndex("result");
                if (index < 0) {
                    // Legacy fallback
                    index = result.getColumnIndex("signature");
                }
                if (index >= 0) {
                    decryptedText = result.getString(index);
                }
            }
            return decryptedText;
        } finally {
            result.close();
        }
    }

    public static String decryptZapEvent(Context context, String packageName, String eventJson, String loggedInUserNpub) {
        if (context == null || packageName == null || packageName.isEmpty() || eventJson == null || loggedInUserNpub == null) return null;
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://" + packageName + ".DECRYPT_ZAP_EVENT");
        String[] projection = new String[] { eventJson, "", loggedInUserNpub };
        Cursor result = contentResolver.query(uri, projection, null, null, null);

        if (result == null) {
            return null;
        }

        try {
            if (result.getColumnIndex("rejected") > -1) return null;

            String decryptedEventJson = null;
            if (result.moveToFirst()) {
                int index = result.getColumnIndex("result");
                if (index < 0) {
                    // Legacy fallback
                    index = result.getColumnIndex("signature");
                    if (LEGACY_FALLBACK_LOG && index >= 0) Log.d("NIP55", "Using legacy column 'signature' for decryptZapEvent");
                }
                if (index >= 0) {
                    decryptedEventJson = unescapeJson(result.getString(index));
                }
            }
            return decryptedEventJson;
        } finally {
            result.close();
        }
    }

    public static String getRelays(Context context, String packageName, String loggedInUserNpub) {
        if (context == null || packageName == null || packageName.isEmpty() || loggedInUserNpub == null) return null;
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://" + packageName + ".GET_RELAYS");
        String[] projection = new String[] { loggedInUserNpub };
        Cursor result = contentResolver.query(uri, projection, null, null, null);

        if (result == null) {
            return null;
        }

        try {
            if (result.getColumnIndex("rejected") > -1) return null;

            String relayJson = null;
            if (result.moveToFirst()) {
                int index = result.getColumnIndex("result");
                if (index < 0) {
                    // Legacy fallback
                    index = result.getColumnIndex("signature");
                    if (LEGACY_FALLBACK_LOG && index >= 0) Log.d("NIP55", "Using legacy column 'signature' for getRelays");
                }
                if (index >= 0) {
                    relayJson = result.getString(index);
                }
            }
            return relayJson;
        } finally {
            result.close();
        }
    }

}
