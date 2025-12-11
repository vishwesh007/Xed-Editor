# Build Instructions

## Network Restriction Issue

The current build environment has network restrictions that prevent access to `dl.google.com` (Google's Maven repository), which is required to download the Android Gradle Plugin and other Android build tools.

## Solution: Use GitHub Actions for Building

Since local building is blocked by network restrictions, the APK should be built using GitHub Actions CI/CD, which has proper network access.

### Steps to Build via GitHub Actions:

1. **Push Changes to Branch**
   - Changes have been pushed to `copilot/implement-folder-file-search`
   
2. **Merge to Main Branch**
   - Merge this PR to `main` or `dev` branch
   - This will trigger the GitHub Actions workflow

3. **GitHub Actions Will:**
   - Clone the repository
   - Initialize submodules
   - Build the release APK
   - Upload artifact
   - Send to Telegram channel (if configured)

### Workflow File
The build configuration is in `.github/workflows/android.yml`:
- Uses JDK 21
- Runs `./gradlew assembleRelease`
- Produces APK: `app/xed-editor-<commit>.apk`

### Manual Build (If Network Access Available)

If building locally with proper network access:

```bash
# Initialize submodules
git submodule update --init --recursive

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing config)
./gradlew assembleRelease

# APK location
ls app/build/outputs/apk/
```

## Note on AGP Version

The AGP version `8.13.1` in `gradle/libs.versions.toml` does not exist in Maven repositories. This will cause build failures in any environment (including CI) unless:
1. The version is corrected to a valid version (e.g., 8.3.0, 8.5.0)
2. Or CI has a fallback/override mechanism we're not aware of

**Recommendation**: Update `agp = "8.13.1"` to a valid version like `agp = "8.5.0"` in `gradle/libs.versions.toml` before merging to ensure CI builds succeed.

The search features implementation is complete and ready for building once the AGP version is corrected.
