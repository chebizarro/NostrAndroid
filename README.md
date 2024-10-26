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

Currently, the library implements [NIP-55](https://github.com/nostr-protocol/nips/blob/master/55.md), which defines the protocol for application-level signing of Nostr events.

## Features

- **NIP-55 Implementation**: Provides methods for signing Nostr events as per NIP-55.
- **Easy Integration**: Designed to be easily integrated into Android projects or cross-platform plugins.
- **Modular Design**: Built to accommodate future implementations of additional NIPs.

## Installation

### Gradle

Add the library to your project's `build.gradle` dependencies:

```gradle
dependencies {
    implementation 'biz.nostr:nip55:1.0.0'
}
```

### JitPack (If using JitPack)

If the library is hosted on JitPack, add the JitPack repository to your root `build.gradle`:

```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Then add the dependency:

```gradle
dependencies {
    implementation 'com.github.chebizarro:nostr-android-library:1.0.0'
}
```

## Usage

### Initializing the Library

```java
import biz.nostr.nip55.Signer;

Signer signer = new Signer(context);
```

### Getting the Public Key

```java
try {
    String publicKey = signer.getPublicKey();
    // Use the public key as needed
} catch (Exception e) {
    // Handle exception
}
```

### Signing an Event

```java
String eventJson = "{ \"content\": \"Hello, Nostr!\" ... }";
try {
    String signedEvent = signer.signEvent(eventJson);
    // Use the signed event as needed
} catch (Exception e) {
    // Handle exception
}
```

## API Reference

### NostrPlugin

#### Constructor

```java
Signer()
```

Creates a new instance of the `NostrPlugin` class.

#### Methods

- `String getPublicKey()`

  Retrieves the user's public key.

  **Returns:**

    - The public key as a string.

  **Throws:**

    - `Exception` if the public key cannot be retrieved.

- `String signEvent(String eventJson)`

  Signs a Nostr event represented as a JSON string.

  **Parameters:**

    - `eventJson`: A JSON string representing the event to be signed.

  **Returns:**

    - The signed event as a JSON string.

  **Throws:**

    - `Exception` if the event cannot be signed.

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

- **Android SDK**: API Level 24 or higher
- **Java Version**: Java 8 or higher

### Supported Platforms

- **Android**: Supported
- **Cross-Platform Toolkits**: Compatible with Capacitor, Flutter, and others

### Permissions

Ensure that any required permissions are declared in your application's `AndroidManifest.xml` file.

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

- **Flutter**: Add the library as a dependency in your Flutter plugin's `build.gradle` file.
- **Capacitor**: Include the library in your Capacitor plugin's Android module.

---

Thank you for using the Nostr Android Library!