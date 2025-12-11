# Search Features Testing Documentation

## Overview
This document describes the comprehensive testing of the search features in Xed-Editor:
1. **File/Folder Search** - Search for files and folders in a project
2. **Code Search** - Search for words/code snippets within files

## Features Already Implemented

### 1. FileSearchDialog (File/Folder Search)
**Location**: `core/main/src/main/java/com/rk/components/FileSearchDialog.kt`

**Functionality**:
- Recursively indexes all files and folders in a project
- Real-time filtering as user types
- Case-insensitive search
- Shows/hides hidden files based on settings
- Displays file/folder icons and paths
- Opens files or navigates to folders when selected

**Access**: 
- Command: "Search files in project" (Ctrl+P or Command Palette)
- Command ID: `global.search_file_folder`

### 2. CodeSearchDialog (Word-in-File Search)
**Location**: `core/main/src/main/java/com/rk/components/CodeSearchDialog.kt`

**Functionality**:
- Searches for text/code snippets across all files in project
- Recursive directory traversal
- Case-insensitive search
- Skips binary files (images, audio, video, archives, executables)
- Skips files over 10MB
- Shows syntax-highlighted code snippets with matches
- Displays line and column numbers
- Shows/hides hidden files based on settings
- Handles already-opened files (searches in-memory content)
- Jumps to exact location when match is clicked

**Access**:
- Command: "Search code in project" (Command Palette)
- Command ID: `global.search_code`

## Manual Testing Guide

### Test Setup
1. Create a test project with the following structure:
   ```
   test-project/
   ├── README.md
   ├── src/
   │   ├── main/
   │   │   ├── kotlin/
   │   │   │   ├── Main.kt
   │   │   │   └── Utils.kt
   │   │   └── java/
   │   │       └── Example.java
   │   └── test/
   │       └── TestMain.kt
   ├── docs/
   │   └── guide.md
   ├── .gitignore
   ├── .config/
   │   └── settings.json
   └── build/
       └── output.txt
   ```

2. Add various file contents with searchable text
3. Open the project in Xed-Editor

### File Search Tests

#### TC-FS-001: Basic File Search
**Steps**:
1. Open Command Palette (swipe down or menu)
2. Select "Search files in project"
3. Type "Main"

**Expected**: Should find Main.kt, TestMain.kt, and main folder

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-FS-002: Folder Search
**Steps**:
1. Open File Search
2. Type "src"

**Expected**: Should find src folder and show folder icon

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-FS-003: Case Insensitive Search
**Steps**:
1. Open File Search
2. Type "MAIN" (uppercase)

**Expected**: Should find same results as "main" (lowercase)

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-FS-004: Partial Name Match
**Steps**:
1. Open File Search
2. Type "kt"

**Expected**: Should find all .kt files

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-FS-005: Hidden Files (Disabled)
**Steps**:
1. Ensure "Show hidden files in search" is OFF in settings
2. Open File Search
3. Type "git"

**Expected**: Should NOT show .gitignore or .config folder

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-FS-006: Hidden Files (Enabled)
**Steps**:
1. Enable "Show hidden files in search" in settings
2. Open File Search
3. Type "git"

**Expected**: Should show .gitignore file

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-FS-007: Nested Directory Search
**Steps**:
1. Open File Search
2. Type "kotlin"

**Expected**: Should find kotlin folder even though it's nested in src/main/

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-FS-008: File Selection and Opening
**Steps**:
1. Open File Search
2. Type "Utils"
3. Tap on Utils.kt

**Expected**: Should close search dialog and open Utils.kt in editor

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-FS-009: Folder Selection and Navigation
**Steps**:
1. Open File Search
2. Type "test"
3. Tap on test folder

**Expected**: Should close search and navigate to test folder in file tree

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-FS-010: Empty Search Results
**Steps**:
1. Open File Search
2. Type "nonexistentfile12345"

**Expected**: Should show "No results" message

**Status**: ✅ PASS / ❌ FAIL

---

### Code Search Tests

#### TC-CS-001: Basic Code Search
**Steps**:
1. Open Command Palette
2. Select "Search code in project"
3. Type "fun main"

**Expected**: Should find all function definitions with "fun main"

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-CS-002: Multi-File Search
**Steps**:
1. Open Code Search
2. Type "println"

**Expected**: Should show results from multiple files (Main.kt, Utils.kt, etc.)

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-CS-003: Case Insensitive Code Search
**Steps**:
1. Open Code Search
2. Type "PRINTLN"

**Expected**: Should find same results as "println"

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-CS-004: Syntax Highlighting in Results
**Steps**:
1. Open Code Search
2. Type "function"
3. Observe the code snippets

**Expected**: Code snippets should have syntax highlighting based on file type

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-CS-005: Line Numbers Display
**Steps**:
1. Open Code Search
2. Type any common word
3. Check result items

**Expected**: Each result should show line number and column number

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-CS-006: Jump to Code Location
**Steps**:
1. Open Code Search
2. Search for "println"
3. Tap on a result

**Expected**: 
- Should close search dialog
- Open the file in editor
- Jump to exact line and column
- Highlight the matched text

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-CS-007: Multiple Occurrences in Same File
**Steps**:
1. Create a file with multiple "TODO" comments
2. Open Code Search
3. Type "TODO"

**Expected**: Should list all occurrences separately with different line numbers

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-CS-008: Binary File Exclusion
**Steps**:
1. Add image files (.png, .jpg) to project
2. Open Code Search
3. Type any text

**Expected**: Should not search in image files (no errors, no results from images)

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-CS-009: Large File Handling
**Steps**:
1. Create a file larger than 10MB
2. Open Code Search
3. Search for text

**Expected**: Should skip the large file (no results from it, no lag)

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-CS-010: Hidden Files (Disabled)
**Steps**:
1. Ensure "Show hidden files in search" is OFF
2. Add text to .gitignore
3. Open Code Search
4. Search for that text

**Expected**: Should NOT find results in .gitignore

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-CS-011: Hidden Files (Enabled)
**Steps**:
1. Enable "Show hidden files in search"
2. Add unique text to .gitignore
3. Open Code Search
4. Search for that text

**Expected**: Should find results in .gitignore

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-CS-012: Search in Opened Files
**Steps**:
1. Open Main.kt in editor
2. Modify it (add "TESTMARKER" text) but don't save
3. Open Code Search
4. Search for "TESTMARKER"

**Expected**: Should find the unsaved changes (searches in-memory editor content)

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-CS-013: Empty Search Query
**Steps**:
1. Open Code Search
2. Leave search field empty

**Expected**: Should show "Enter a search query to find in files" message

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-CS-014: Special Characters Search
**Steps**:
1. Open Code Search
2. Search for "(" or ")" or other special characters

**Expected**: Should find function calls and other code with those characters

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-CS-015: Unicode Character Search
**Steps**:
1. Add file with Unicode text (e.g., "Hello 世界")
2. Open Code Search
3. Search for "世界"

**Expected**: Should find the Unicode text correctly

**Status**: ✅ PASS / ❌ FAIL

---

### Performance Tests

#### TC-PERF-001: Large Project Indexing
**Steps**:
1. Open a project with 1000+ files
2. Open File Search
3. Measure time to complete indexing

**Expected**: Should complete within reasonable time (< 30 seconds)

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-PERF-002: Real-time Search Filtering
**Steps**:
1. Open File Search with indexed results
2. Type characters quickly
3. Observe UI responsiveness

**Expected**: UI should remain responsive, no lag while typing

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-PERF-003: Code Search in Large Project
**Steps**:
1. Open project with many files
2. Search for common word
3. Observe search speed and results display

**Expected**: Should complete within reasonable time (< 60 seconds for 1000+ files)

**Status**: ✅ PASS / ❌ FAIL

---

### Edge Cases and Error Handling

#### TC-EDGE-001: Empty Project
**Steps**:
1. Create empty project folder
2. Open File Search

**Expected**: Should show "No results" gracefully

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-EDGE-002: Permission Denied Files
**Steps**:
1. Create files with restricted permissions
2. Open File Search / Code Search

**Expected**: Should handle gracefully without crashes

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-EDGE-003: Symbolic Links
**Steps**:
1. Create symbolic links in project
2. Open File Search

**Expected**: Should handle symbolic links appropriately

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-EDGE-004: Special Characters in Filenames
**Steps**:
1. Create files with spaces, dashes, underscores
2. Open File Search
3. Search for these files

**Expected**: Should find and display files with special characters correctly

**Status**: ✅ PASS / ❌ FAIL

---

#### TC-EDGE-005: Very Long Filenames
**Steps**:
1. Create file with very long name (200+ chars)
2. Open File Search
3. Search for it

**Expected**: Should handle and display long filenames appropriately

**Status**: ✅ PASS / ❌ FAIL

---

## Automated Tests

### Unit Tests Created

1. **FileSearchTest.kt** - Tests file and folder search functionality
   - Location: `core/main/src/test/java/com/rk/components/FileSearchTest.kt`
   - Coverage:
     - File indexing
     - Hidden file handling
     - Name-based search
     - Folder detection
     - Case sensitivity
     - Special characters
     - Nested directories
     - Empty directories

2. **CodeSearchTest.kt** - Tests word-in-file search functionality
   - Location: `core/main/src/test/java/com/rk/components/CodeSearchTest.kt`
   - Coverage:
     - Basic code search
     - Case insensitivity
     - Multi-file search
     - Multiple file types
     - Text files and markdown
     - Nested directories
     - Multiple occurrences
     - Special characters
     - Large files
     - Hidden files
     - Unicode characters
     - Line/column numbers

### Running Tests

```bash
# Run all search tests
./gradlew :core:main:testDebugUnitTest

# Run specific test
./gradlew :core:main:testDebugUnitTest --tests com.rk.components.FileSearchTest
./gradlew :core:main:testDebugUnitTest --tests com.rk.components.CodeSearchTest
```

## Configuration

### Settings
The following settings affect search behavior:

1. **Show hidden files in search** (`Settings.show_hidden_files_search`)
   - Location: Settings > App Settings
   - Default: OFF
   - When enabled: Shows files and folders starting with "." in search results

## Known Limitations

1. **Binary File Detection**: Uses both file extension and character analysis
2. **Large File Limit**: Files over 10MB are skipped in code search
3. **Search Performance**: Depends on project size and device performance
4. **Syntax Highlighting**: Only works after opening at least one file in the editor

## Recommendations

1. Test on various Android versions (API 26+)
2. Test on devices with different performance levels
3. Test with projects of different sizes (small, medium, large, very large)
4. Test with various file encodings
5. Monitor memory usage during large project searches
6. Verify no memory leaks after repeated searches

## Test Results Summary

| Category | Total Tests | Passed | Failed | Not Tested |
|----------|------------|--------|--------|------------|
| File Search | 10 | - | - | - |
| Code Search | 15 | - | - | - |
| Performance | 3 | - | - | - |
| Edge Cases | 5 | - | - | - |
| **Total** | **33** | **-** | **-** | **-** |

---

**Last Updated**: 2025-12-11
**Tester**: -
**Version**: -
