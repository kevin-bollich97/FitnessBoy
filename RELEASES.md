# Release Setup

This project can publish a signed release APK to GitHub Releases whenever you push a tag like `v0.1.0`.

## 1. Create a signing keystore

Run this once locally:

```bash
keytool -genkeypair \
  -v \
  -keystore release-keystore.jks \
  -alias fitnessboy \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

## 2. Add GitHub secrets

Create these repository secrets:

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

Create the base64 value like this:

```bash
base64 -w 0 release-keystore.jks
```

On macOS use:

```bash
base64 -i release-keystore.jks
```

## 3. Create a release build

Push a version tag:

```bash
git tag v0.1.0
git push origin v0.1.0
```

The GitHub Action will:

- build a signed release APK
- create or update the GitHub Release for the tag
- attach the APK and a SHA256 checksum file

## 4. Install on Android

Open the GitHub Release on your phone, download the APK, and allow installation from your browser if Android asks for permission.
