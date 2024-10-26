package biz.nostr.android.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import biz.nostr.android.nip55.IntentBuilder;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE_GET_PUBLIC_KEY = 1001;
    private static final int REQUEST_CODE_SIGN_EVENT = 1002;
    private static final String SIGNER_PACKAGE_NAME = "com.example.signerapp";

    private TextView textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textResult = findViewById(R.id.text_result);
        Button buttonCheckSigner = findViewById(R.id.button_check_signer);
        Button buttonGetPublicKey = findViewById(R.id.button_get_public_key);
        Button buttonSignEvent = findViewById(R.id.button_sign_event);

        buttonCheckSigner.setOnClickListener(v -> checkSignerAppInstalled());
        buttonGetPublicKey.setOnClickListener(v -> getPublicKey());
        buttonSignEvent.setOnClickListener(v -> signEvent());
    }

    private void checkSignerAppInstalled() {
        boolean isInstalled = IntentBuilder.isExternalSignerInstalled(this, SIGNER_PACKAGE_NAME);
        if (isInstalled) {
            textResult.setText("Signer app is installed.");
        } else {
            textResult.setText("Signer app is not installed.");
        }
    }

    private void getPublicKey() {
        Intent intent = IntentBuilder.getPublicKeyIntent(SIGNER_PACKAGE_NAME, null);
        try {
            startActivityForResult(intent, REQUEST_CODE_GET_PUBLIC_KEY);
        } catch (Exception e) {
            textResult.setText("Error starting signer app: " + e.getMessage());
        }
    }

    private void signEvent() {
        // For demo purposes, use a sample event JSON
        String eventJson = "{\"content\": \"Hello Nostr\"}";
        String eventId = "event123";
        String npub = "npub1examplepublickey"; // Replace with actual public key or get it dynamically

        Intent intent = IntentBuilder.signEventIntent(SIGNER_PACKAGE_NAME, eventJson, eventId, npub);
        try {
            startActivityForResult(intent, REQUEST_CODE_SIGN_EVENT);
        } catch (Exception e) {
            textResult.setText("Error starting signer app: " + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            textResult.setText("No data returned.");
            return;
        }

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_GET_PUBLIC_KEY:
                    NostrResultParser.GetPublicKeyResult result = NostrResultParser.parseGetPublicKeyResult(data);
                    if (result != null) {
                        textResult.setText("Public Key: " + result.npub + "\nPackage: " + result.packageName);
                    } else {
                        textResult.setText("Failed to parse public key result.");
                    }
                    break;
                case REQUEST_CODE_SIGN_EVENT:
                    NostrResultParser.SignEventResult signResult = NostrResultParser.parseSignEventResult(data);
                    if (signResult != null) {
                        textResult.setText("Signature: " + signResult.signature + "\nEvent ID: " + signResult.id);
                    } else {
                        textResult.setText("Failed to parse sign event result.");
                    }
                    break;
            }
        } else {
            textResult.setText("Operation canceled or failed.");
        }
    }
}