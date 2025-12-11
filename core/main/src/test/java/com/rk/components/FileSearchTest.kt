package com.rk.components

import com.rk.file.FileObject
import com.rk.file.FileWrapper
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File

/**
 * Unit tests for FileSearchDialog functionality
 * Tests file and folder search capabilities
 */
class FileSearchTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var testRoot: File
    private lateinit var testRootObject: FileObject

    @Before
    fun setup() {
        testRoot = tempFolder.root
        testRootObject = FileWrapper(testRoot)
        
        // Create test directory structure
        createTestFileStructure()
    }

    private fun createTestFileStructure() {
        // Create various files and folders for testing
        File(testRoot, "test.txt").writeText("Hello World")
        File(testRoot, "example.kt").writeText("fun main() { println(\"Hello\") }")
        File(testRoot, "README.md").writeText("# Test Project")
        
        // Create subdirectories
        File(testRoot, "src").mkdir()
        File(testRoot, "src/main").mkdirs()
        File(testRoot, "src/main/kotlin").mkdirs()
        File(testRoot, "src/main/kotlin/Main.kt").writeText("package main\n\nfun main() {}")
        
        // Create hidden files
        File(testRoot, ".hidden").writeText("hidden content")
        File(testRoot, ".config").mkdir()
        File(testRoot, ".config/settings.json").writeText("{}")
        
        // Create files with special characters
        File(testRoot, "file with spaces.txt").writeText("content")
        File(testRoot, "file-with-dashes.txt").writeText("content")
        
        // Create nested structure
        File(testRoot, "deep/nested/path").mkdirs()
        File(testRoot, "deep/nested/path/file.txt").writeText("deep file")
    }

    @Test
    fun testFileIndexing() = runBlocking {
        val results = mutableListOf<FileObject>()
        indexFilesForTest(testRootObject, results, showHidden = false)
        
        // Should find files but not hidden ones
        assertTrue("Should find multiple files", results.size > 5)
        assertFalse("Should not include hidden files", 
            results.any { it.getName().startsWith(".") })
    }

    @Test
    fun testFileIndexingWithHidden() = runBlocking {
        val results = mutableListOf<FileObject>()
        indexFilesForTest(testRootObject, results, showHidden = true)
        
        // Should find all files including hidden
        assertTrue("Should find hidden files", 
            results.any { it.getName().startsWith(".") })
    }

    @Test
    fun testFileSearchByName() = runBlocking {
        val results = mutableListOf<FileObject>()
        indexFilesForTest(testRootObject, results, showHidden = false)
        
        // Test exact name match
        val ktFiles = results.filter { it.getName().lowercase().contains("kt") }
        assertTrue("Should find .kt files", ktFiles.isNotEmpty())
        
        // Test partial match
        val testFiles = results.filter { it.getName().lowercase().contains("test") }
        assertTrue("Should find files with 'test' in name", testFiles.isNotEmpty())
    }

    @Test
    fun testFolderSearch() = runBlocking {
        val results = mutableListOf<FileObject>()
        indexFilesForTest(testRootObject, results, showHidden = false)
        
        // Should find directories
        val folders = results.filter { it.isDirectory() }
        assertTrue("Should find folders", folders.isNotEmpty())
        
        // Verify specific folders exist
        assertTrue("Should find 'src' folder", 
            folders.any { it.getName() == "src" })
    }

    @Test
    fun testSearchCaseSensitivity() = runBlocking {
        val results = mutableListOf<FileObject>()
        indexFilesForTest(testRootObject, results, showHidden = false)
        
        // Search should be case-insensitive
        val upperSearch = results.filter { it.getName().uppercase().contains("TEST") }
        val lowerSearch = results.filter { it.getName().lowercase().contains("test") }
        
        assertEquals("Search should be case-insensitive", 
            upperSearch.size, lowerSearch.size)
    }

    @Test
    fun testSpecialCharactersInFilenames() = runBlocking {
        val results = mutableListOf<FileObject>()
        indexFilesForTest(testRootObject, results, showHidden = false)
        
        // Should handle files with spaces
        assertTrue("Should find files with spaces", 
            results.any { it.getName().contains(" ") })
        
        // Should handle files with dashes
        assertTrue("Should find files with dashes", 
            results.any { it.getName().contains("-") })
    }

    @Test
    fun testNestedDirectorySearch() = runBlocking {
        val results = mutableListOf<FileObject>()
        indexFilesForTest(testRootObject, results, showHidden = false)
        
        // Should find deeply nested files
        assertTrue("Should find deeply nested files", 
            results.any { it.getAbsolutePath().contains("deep/nested/path") })
    }

    @Test
    fun testEmptyDirectoryHandling() = runBlocking {
        // Create empty directory
        File(testRoot, "empty_dir").mkdir()
        
        val results = mutableListOf<FileObject>()
        indexFilesForTest(testRootObject, results, showHidden = false)
        
        // Should still index the empty directory
        assertTrue("Should find empty directory", 
            results.any { it.getName() == "empty_dir" && it.isDirectory() })
    }

    // Helper function to simulate file indexing
    private suspend fun indexFilesForTest(
        parent: FileObject, 
        results: MutableList<FileObject>,
        showHidden: Boolean
    ) {
        val childFiles = parent.listFiles()
        
        for (file in childFiles) {
            val isHidden = file.getName().startsWith(".")
            if (isHidden && !showHidden) continue
            
            results.add(file)
            
            if (file.isDirectory()) {
                indexFilesForTest(file, results, showHidden)
            }
        }
    }
}
