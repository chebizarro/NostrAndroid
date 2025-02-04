package biz.nostr.android.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.List;

import biz.nostr.android.nip55.AppInfo;
import biz.nostr.android.nip55.IntentBuilder;
import biz.nostr.android.nip55.Signer;

public class MainActivity extends Activity {

    private static final int REQUEST_GET_PUBLIC_KEY = 1001;
    private static final int REQUEST_SIGN_EVENT = 1002;
    private static final int REQUEST_ENCRYPT = 1003;
    private static final int REQUEST_DECRYPT = 1004;
    private static final int REQUEST_DECRYPT_ZAP = 1005;
    private static final int REQUEST_GET_RELAYS = 1006;

    // We'll display results in this text field
    private TextView textResult;

    // The container for dynamically created buttons
    private LinearLayout layoutSignerApps;

    // The field for the package name
    private EditText editSelectedPackage;

    private EditText editEventContent;
    private EditText editRecipientPubkey;
    private EditText editMessageToEncrypt;

    private String currentPublicKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textResult = findViewById(R.id.text_result);
        layoutSignerApps = findViewById(R.id.layout_signer_apps);
        editSelectedPackage = findViewById(R.id.edit_selected_package);
        editEventContent = findViewById(R.id.edit_event_content);
        editRecipientPubkey = findViewById(R.id.edit_recipient_pubkey);
        editMessageToEncrypt = findViewById(R.id.edit_message_to_encrypt);

        Button buttonListSigners = findViewById(R.id.button_list_signers);
        buttonListSigners.setOnClickListener(v -> listSignerApps());

        Button buttonCheckSigner = findViewById(R.id.button_check_signer);
        buttonCheckSigner.setOnClickListener(v -> checkSignerAppInstalled());

        Button buttonGetPublicKey = findViewById(R.id.button_get_public_key);
        buttonGetPublicKey.setOnClickListener(v -> getPublicKey());

        Button buttonSignEvent = findViewById(R.id.button_sign_event);
        buttonSignEvent.setOnClickListener(v -> signEvent());

        // Encryption / Decryption
        Button buttonEncrypt = findViewById(R.id.button_encrypt);
        buttonEncrypt.setOnClickListener(v -> encryptMessage());

        Button buttonDecrypt = findViewById(R.id.button_decrypt);
        buttonDecrypt.setOnClickListener(v -> decryptMessage());

        Button buttonDecryptZap = findViewById(R.id.button_decrypt_zap);
        buttonDecryptZap.setOnClickListener(v -> decryptZapEvent());

        Button buttonGetRelays = findViewById(R.id.button_get_relays);
        buttonGetRelays.setOnClickListener(v -> getRelays());
    }

    /**
     * Dynamically create buttons for each installed signer app, with icon and name.
     * On click, set the package name in editSelectedPackage.
     */
    private void listSignerApps() {
        layoutSignerApps.removeAllViews();

        List<AppInfo> apps = Signer.getInstalledSignerApps(this);
        if (apps.isEmpty()) {
            textResult.setText("No signer apps installed.");
            return;
        }

        for (AppInfo info : apps) {
            Button appButton = new Button(this);
            appButton.setText(info.name); // set the label
            // Optionally, set the icon on the left side of the button:
            Drawable icon = info.icon;
            if (icon != null) {
                icon.setBounds(0, 0, 60, 60); // or some other dimension
                appButton.setCompoundDrawables(icon, null, null, null);
            }

            // On click, set the package name field
            appButton.setOnClickListener(v -> {
                editSelectedPackage.setText(info.packageName);
                textResult.setText("Selected package: " + info.packageName);
            });

            layoutSignerApps.addView(appButton);
        }

        textResult.setText("Listed " + apps.size() + " signer apps. Tap to select one.");
    }

    private void checkSignerAppInstalled() {
        String pkgName = editSelectedPackage.getText().toString().trim();
        if (pkgName.isEmpty()) {
            textResult.setText("No package name selected.");
            return;
        }
        List<ResolveInfo> resolveInfos = Signer.isExternalSignerInstalled(this, pkgName);
        textResult.setText(resolveInfos.isEmpty()
                ? "Signer app is not installed."
                : "Signer app is installed.");
    }

    private void getPublicKey() {
        String pkgName = editSelectedPackage.getText().toString().trim();
        if (pkgName.isEmpty()) {
            textResult.setText("No package name selected.");
            return;
        }
        // Pass a JSON string for permissions
        String permissionsJson = "{\"permissions\":[{\"type\":\"sign_event\",\"kind\":22242},{\"type\":\"nip44_decrypt\"}]}";
        Intent intent = IntentBuilder.getPublicKeyIntent(pkgName, null);
        try {
            startActivityForResult(intent, REQUEST_GET_PUBLIC_KEY);
            textResult.setText("Launched getPublicKey intent...");
        } catch (Exception e) {
            textResult.setText("Error starting signer app: " + e.getMessage());
        }
    }

    private void signEvent() {
        String pkgName = editSelectedPackage.getText().toString().trim();
        if (pkgName.isEmpty()) {
            textResult.setText("No package name selected.");
            return;
        }
        String content = editEventContent.getText().toString();
        String eventJson = "{\"content\":\"" + content + "\"}";
        String eventId = "event123";
        String npub = currentPublicKey.isEmpty() ? "npub1placeholder" : currentPublicKey;

        Intent intent = IntentBuilder.signEventIntent(pkgName, eventJson, eventId, npub);
        try {
            startActivityForResult(intent, REQUEST_SIGN_EVENT);
            textResult.setText("Launched signEvent intent...");
        } catch (Exception e) {
            textResult.setText("Error starting signer app: " + e.getMessage());
        }
    }

    private void encryptMessage() {
        String pkgName = editSelectedPackage.getText().toString().trim();
        String plainText = editMessageToEncrypt.getText().toString().trim();
        String pubKey = editRecipientPubkey.getText().toString().trim();
        if (pkgName.isEmpty() || plainText.isEmpty() || pubKey.isEmpty()) {
            textResult.setText("Missing fields (pkgName, plainText, pubKey).");
            return;
        }
        // We'll do a nip04Encrypt fallback
        Intent intent = IntentBuilder.nip04EncryptIntent(pkgName, plainText, "encrypt123", currentPublicKey, pubKey);
        try {
            startActivityForResult(intent, REQUEST_ENCRYPT);
            textResult.setText("Launched nip04Encrypt intent...");
        } catch (Exception e) {
            textResult.setText("Error: " + e.getMessage());
        }
    }

    private void decryptMessage() {
        String pkgName = editSelectedPackage.getText().toString().trim();
        String encryptedText = editMessageToEncrypt.getText().toString().trim();
        String pubKey = editRecipientPubkey.getText().toString().trim();
        if (pkgName.isEmpty() || encryptedText.isEmpty() || pubKey.isEmpty()) {
            textResult.setText("Missing fields (pkgName, encryptedText, pubKey).");
            return;
        }
        // nip04Decrypt fallback
        Intent intent = IntentBuilder.nip04DecryptIntent(pkgName, encryptedText, "decrypt123", currentPublicKey, pubKey);
        try {
            startActivityForResult(intent, REQUEST_DECRYPT);
            textResult.setText("Launched nip04Decrypt intent...");
        } catch (Exception e) {
            textResult.setText("Error: " + e.getMessage());
        }
    }

    private void decryptZapEvent() {
        String pkgName = editSelectedPackage.getText().toString().trim();
        if (pkgName.isEmpty()) {
            textResult.setText("No package name selected.");
            return;
        }
        String eventJson = "{\"content\":\"Zap event content\"}";

        Intent intent = IntentBuilder.decryptZapEventIntent(pkgName, eventJson, "zap123", currentPublicKey);
        try {
            startActivityForResult(intent, REQUEST_DECRYPT_ZAP);
            textResult.setText("Launched decryptZapEvent intent...");
        } catch (Exception e) {
            textResult.setText("Error: " + e.getMessage());
        }
    }

    private void getRelays() {
        String pkgName = editSelectedPackage.getText().toString().trim();
        if (pkgName.isEmpty()) {
            textResult.setText("No package name selected.");
            return;
        }
        Intent intent = IntentBuilder.getRelaysIntent(pkgName, "relay123", currentPublicKey);
        try {
            startActivityForResult(intent, REQUEST_GET_RELAYS);
            textResult.setText("Launched getRelays intent...");
        } catch (Exception e) {
            textResult.setText("Error: " + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            textResult.setText("No data returned.");
            return;
        }
        if (resultCode != RESULT_OK) {
            textResult.setText("Operation canceled or failed.");
            return;
        }
        // A real app would parse the extras from data.
        // We'll just show everything:
        Bundle extras = data.getExtras();
        if (extras != null) {
            StringBuilder sb = new StringBuilder("Request Code: ").append(requestCode).append("\n");
            for (String key : extras.keySet()) {
                Object val = extras.get(key);
                sb.append(key).append(" = ").append(val).append("\n");
                if ("npub".equals(key) && val instanceof String) {
                    currentPublicKey = (String) val;
                }
            }
            textResult.setText(sb.toString());
        } else {
            textResult.setText("No extras returned from activity.");
        }
    }
}
