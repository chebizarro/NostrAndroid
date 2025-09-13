# Nostr Android Library

An Android library that provides implementations of Nostr Improvement Proposals (NIPs), specifically designed to be used in plugins for cross-platform toolkits like Capacitor or Flutter, but can also be easily integrated into existing Android projects. Currently, the library implements [NIP-55](https://github.com/nostr-protocol/nips/blob/master/55.md).

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [API Reference](#api-reference)
- [Contributing](#contributing)
- [License](#license)

## Introduction

The Nostr Android Library enables Android applications to interact with the [Nostr](https://nostr.com/) decentralized protocol by providing native implementations of Nostr Improvement Proposals (NIPs). The library is designed to be modular and easily integrated into cross-platform toolkits such as Capacitor or Flutter, as well as into existing Android projects.

Currently, the library implements [NIP-55](https://github.com/nostr-protocol/nips/blob/master/55.md), which defines the protocol for application-level signing of Nostr events and data exchange with Android signer apps.

## Features

- **NIP-55 Implementation**: Provides methods for signing Nostr events as per NIP-55.
- **Easy Integration**: Designed to be easily integrated into Android projects or cross-platform plugins.
- **Modular Design**: Built to accommodate future implementations of additional NIPs.

## Installation

### JitPack (recommended during development)

Add the JitPack repository (choose one style depending on your Gradle layout):

Option A — `settings.gradle` (newer projects)

```gradle
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    maven { url "https://jitpack.io" }
  }
}
```

Note: JitPack will honor the coordinates defined by the project's POM (`groupId`, `artifactId`, `version`).

Option B — root `build.gradle` (older projects)

```gradle
allprojects {
  repositories {
    google()
    mavenCentral()
    maven { url 'https://jitpack.io' }
  }
}
```

Then add the dependency to your Android app/module. This repo publishes the Gradle submodule `:android`, but the published artifactId is set to `nostr-android` via Maven Publish:

```gradle
dependencies {
  // Use a git tag or commit SHA. Example for v2.0.0:
  implementation 'biz.nostr:nostr-android:2.0.0'
}
```

### Maven Central (if published)

```gradle
dependencies {
    implementation 'biz.nostr:nostr-android:1.0.0'
}
```

## Usage

### Library Access

`Signer` and `IntentBuilder` expose static methods.

```java
import biz.nostr.android.nip55.Signer;
import biz.nostr.android.nip55.IntentBuilder;
```

### Using ContentResolver (background) — NIP-55

These calls should be done off the main thread.

```java
String packageName = /* previously selected signer package, e.g., from interactive get_public_key */;
String npub = Signer.getPublicKey(context, packageName); // returns null if not remembered or rejected

String[] signed = Signer.signEvent(context, packageName, eventJson, currentUserNpub);
// signed[0] = result (signature), signed[1] = event (signed event JSON)

String enc = Signer.nip04Encrypt(context, packageName, plainText, recipientHexPubKey, currentUserNpub);
String dec = Signer.nip04Decrypt(context, packageName, encryptedText, senderHexPubKey, currentUserNpub);

String enc44 = Signer.nip44Encrypt(context, packageName, plainText, recipientHexPubKey, currentUserNpub);
String dec44 = Signer.nip44Decrypt(context, packageName, encryptedText, senderHexPubKey, currentUserNpub);

String zapEventJson = Signer.decryptZapEvent(context, packageName, eventJson, currentUserNpub);
String relaysJson = Signer.getRelays(context, packageName, currentUserNpub);
```

### Using Intents (interactive) — NIP-55

```java
// First, discover if any signer is installed
boolean hasSigner = Signer.isExternalSignerInstalled(context);

// Request public key interactively (with optional permissions JSON)
String permissionsJson = "[ {\"type\":\"sign_event\",\"kind\":22242}, {\"type\":\"nip44_decrypt\"} ]";
Intent getPk = IntentBuilder.getPublicKeyIntent("com.example.signer", permissionsJson);
// Optionally add singleTop flags when batching
IntentBuilder.withSingleTopFlags(getPk);
activityResultLauncher.launch(getPk);

// Sign event interactively
Intent sign = IntentBuilder.signEventIntent("com.example.signer", eventJson, eventId, currentUserNpub);
IntentBuilder.withSingleTopFlags(sign);
activityResultLauncher.launch(sign);
```

On result, per NIP-55, read extras: `result`, `id`, and optionally `event` for `sign_event`.

## API Reference

### API Reference (subset)

#### Constructor

Key static methods (see source `android/src/main/java/biz/nostr/android/nip55/Signer.java`):

- `List<ResolveInfo> isExternalSignerInstalled(Context, String)` and `boolean isExternalSignerInstalled(Context)`
- `boolean isSignerPackageAvailable(Context, String)`
- `String getPublicKey(Context, String packageName)` — returns result (npub) or null
- `String[] signEvent(Context, String packageName, String eventJson, String currentUserNpub)` — returns { result, event } or null
- `String nip04Encrypt/Decrypt(...)`, `String nip44Encrypt/Decrypt(...)`, `String decryptZapEvent(...)`, `String getRelays(...)` — return result or null

## Contributing

Contributions are welcome! If you'd like to contribute to this project, please follow these steps:

1. **Fork the Repository**: Click the 'Fork' button at the top right of the repository page.

2. **Clone Your Fork**:

   ```bash
   git clone https://github.com/yourusername/NostrAndroid.git
   cd nostr-android-library
   ```

3. **Create a New Branch**:

   ```bash
   git checkout -b feature/your-feature-name
   ```

4. **Make Your Changes**: Implement your feature or bug fix.

5. **Commit Your Changes**:

   ```bash
   git commit -am 'Add some feature'
   ```

6. **Push to the Branch**:

   ```bash
   git push origin feature/your-feature-name
   ```

7. **Open a Pull Request**: Go to the repository on GitHub and click 'New pull request'.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

**Note**: For any issues or questions, please open an issue on the [GitHub repository](https://github.com/chebizarro/NostrAndroid/issues).

## Additional Information

### Dependencies

- **Android SDK**: minSdk 21+, compileSdk 34
- **Java Version**: Java 8

### Supported Platforms

- **Android**: Supported
- **Cross-Platform Toolkits**: Compatible with Capacitor, Flutter, and others

### Permissions (NIP-55)

Permissions are represented as a JSON array of objects: `[{ "type": string, "kind"?: number }]`.

Examples:

- TypeScript / Capacitor / React Native
```ts
type Permission = { type: string; kind?: number };
const permissions: Permission[] = [
  { type: 'sign_event', kind: 22242 },
  { type: 'nip44_decrypt' },
];
const permissionsJson = JSON.stringify(permissions);
```

- Flutter / Dart
```dart
class Permission {
  final String type; final int? kind; const Permission(this.type, {this.kind});
  Map<String, dynamic> toJson() => { 'type': type, if (kind != null) 'kind': kind };
}
final permissionsJson = jsonEncode([Permission('sign_event', kind: 22242).toJson()]);
```

Pass `permissionsJson` to `IntentBuilder.getPublicKeyIntent(packageName, permissionsJson)`.

### Building the Library

If you need to build the library locally:

1. **Clone the Repository**:

   ```bash
   git clone https://github.com/chebizarro/NostrAndroid.git
   cd nostr-android-library
   ```

2. **Build the Library**:

   ```bash
   ./gradlew build
   ```

3. **Publish to Local Maven Repository (Optional)**:

   ```bash
   ./gradlew publishToMavenLocal
   ```

### Integration with Cross-Platform Plugins

To use the library in cross-platform plugins:

- **Flutter**: Add the dependency to the Android module of your plugin and forward method calls/permissions JSON.
- **Capacitor / React Native**: Serialize permissions and other parameters at the JS/TS boundary and pass strings to Android.

### Troubleshooting

- No signer found: ensure you added the `queries` section in your app manifest per NIP-55 and check `Signer.isExternalSignerInstalled(context)`.
- Wrong results from provider: ensure the signer packageName is validated and constant between calls; store the package from the initial interactive `get_public_key` response.
- Intent payload corrupted: use `IntentBuilder` methods that URI-encode payloads.
- Publishing/consumption issues on JitPack: pin a git tag or SHA. For Maven Central, ensure you’re using the documented coordinates.

### Compatibility

Some older signer implementations may return a legacy column name `signature` instead of the NIP-55-compliant `result`.

This library prefers `result` and will transparently fall back to `signature` when `result` is absent. You can enable a debug log when the legacy fallback is taken:

```java
// Optional: enable to log when legacy 'signature' column is used
biz.nostr.android.nip55.Signer.setLegacyFallbackLogging(true);
```

---

Thank you for using the Nostr Android Library!

## NIP-21: nostr: URI scheme (Android)

This library includes a Java-only implementation for parsing and building `nostr:` URIs (NIP-21). It is dependency-free and framework-agnostic.

### Parse a NIP-21 URI

```java
import biz.nostr.android.nip21.NostrUri;
import biz.nostr.android.nip21.NostrUriParser;

NostrUri u = NostrUriParser.parse("nostr:npub1sn0wdenkukak0d9dfczzeacvhkrgz92ak56egt7vdgzn8pv2wfqqhrjdv9");
// u.getKind() == NostrUri.Kind.NPUB
// u.getBech32() == "npub1sn0wdenkukak0d9dfczzeacvhkrgz92ak56egt7vdgzn8pv2wfqqhrjdv9"
// u.getQuery().isEmpty() == true
```

With a query string:

```java
NostrUri u = NostrUriParser.parse("nostr:note1fntxtkcy9...?...&relay=wss%3A%2F%2Frelay.example");
String relay = u.getQuery().get("relay"); // "wss://relay.example"
```

Safety: `nsec` is rejected by design.

### Build a NIP-21 URI

```java
import biz.nostr.android.nip21.NostrUriBuilder;

String s1 = NostrUriBuilder.build("npub1abcd...");                  // "nostr:npub1abcd..."
Map<String,String> q = new java.util.HashMap<>();
q.put("relay", "wss://ex.com");
String s2 = NostrUriBuilder.build("nevent1xyz...", q); // "nostr:nevent1xyz...?relay=wss%3A%2F%2Fex.com"
```

### Android Deep Link Helper

```java
import biz.nostr.android.nip21.NostrDeepLinkHelper;

boolean isNostr = NostrDeepLinkHelper.isNostrDeepLink(intent);
NostrUri parsed = NostrDeepLinkHelper.parseFromIntent(intent);
Intent view = NostrDeepLinkHelper.buildViewIntent("note1...", java.util.Collections.singletonMap("relay", "wss://relay.example"));
```

### Manifest snippets (host app)

Add a `queries` block to discover nostr handlers:

```xml
<queries>
  <intent>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="nostr" />
  </intent>
  <!-- existing nostrsigner queries may already be present -->
  <intent>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="nostrsigner" />
  </intent>
  
</queries>
```

In your activity to receive deep links:

```xml
<intent-filter>
  <action android:name="android.intent.action.VIEW" />
  <category android:name="android.intent.category.DEFAULT" />
  <category android:name="android.intent.category.BROWSABLE" />
  <data android:scheme="nostr" />
  <!-- optionally host/path filters if you choose to support http(s) indirection -->
</intent-filter>
```

Notes:

- `nsec` is blocked and will throw an exception when parsed or built.
- This module performs minimal bech32 shape validation and classifies by prefix; no TLV decoding is performed here.