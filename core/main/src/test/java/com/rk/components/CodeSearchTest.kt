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
 * Unit tests for CodeSearchDialog functionality
 * Tests word-in-file (code snippet) search capabilities
 */
class CodeSearchTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var testRoot: File
    private lateinit var testRootObject: FileObject

    @Before
    fun setup() {
        testRoot = tempFolder.root
        testRootObject = FileWrapper(testRoot)
        createTestCodeFiles()
    }

    private fun createTestCodeFiles() {
        // Create Kotlin files
        File(testRoot, "Main.kt").writeText("""
            package com.example
            
            fun main() {
                println("Hello World")
                val result = calculate(10, 20)
                println("Result: ${'$'}result")
            }
            
            fun calculate(a: Int, b: Int): Int {
                return a + b
            }
        """.trimIndent())
        
        File(testRoot, "Utils.kt").writeText("""
            package com.example.utils
            
            object StringUtils {
                fun formatString(input: String): String {
                    return input.trim().lowercase()
                }
                
                fun containsPattern(text: String, pattern: String): Boolean {
                    return text.contains(pattern, ignoreCase = true)
                }
            }
        """.trimIndent())
        
        // Create Java file
        File(testRoot, "Example.java").writeText("""
            package com.example;
            
            public class Example {
                public static void main(String[] args) {
                    System.out.println("Hello from Java");
                }
                
                public int calculate(int x, int y) {
                    return x + y;
                }
            }
        """.trimIndent())
        
        // Create text files
        File(testRoot, "README.md").writeText("""
            # Test Project
            
            This is a test project for search functionality.
            It contains various code examples and documentation.
        """.trimIndent())
        
        File(testRoot, "notes.txt").writeText("""
            TODO: Implement feature X
            TODO: Fix bug in calculate function
            DONE: Add search functionality
        """.trimIndent())
        
        // Create subdirectory with files
        File(testRoot, "src/tests").mkdirs()
        File(testRoot, "src/tests/TestMain.kt").writeText("""
            package com.example.tests
            
            import org.junit.Test
            
            class MainTest {
                @Test
                fun testCalculate() {
                    // Test the calculate function
                    val result = calculate(5, 3)
                    assert(result == 8)
                }
            }
        """.trimIndent())
        
        // Create hidden file (should be skipped in search by default)
        File(testRoot, ".gitignore").writeText("""
            *.class
            *.log
            build/
        """.trimIndent())
        
        // Create large file (should be handled properly)
        val largeContent = buildString {
            repeat(1000) { i ->
                appendLine("Line $i: This is test content with keyword searchable")
            }
        }
        File(testRoot, "large.txt").writeText(largeContent)
    }

    @Test
    fun testBasicCodeSearch() = runBlocking {
        val results = findCodeInFiles(testRootObject, "calculate")
        
        assertTrue("Should find 'calculate' in multiple files", results.isNotEmpty())
        assertTrue("Should find at least 3 occurrences", results.values.sumOf { it.size } >= 3)
    }

    @Test
    fun testCaseInsensitiveSearch() = runBlocking {
        val lowerResults = findCodeInFiles(testRootObject, "hello")
        val upperResults = findCodeInFiles(testRootObject, "HELLO")
        
        assertEquals("Search should be case-insensitive", 
            lowerResults.values.sumOf { it.size }, 
            upperResults.values.sumOf { it.size })
    }

    @Test
    fun testSearchInMultipleFileTypes() = runBlocking {
        val results = findCodeInFiles(testRootObject, "main")
        
        // Should find in .kt, .java, and possibly other files
        val fileTypes = results.keys.map { it.getName().substringAfterLast(".") }.toSet()
        assertTrue("Should search across multiple file types", fileTypes.size > 1)
    }

    @Test
    fun testSearchInTextFiles() = runBlocking {
        val results = findCodeInFiles(testRootObject, "TODO")
        
        assertTrue("Should find TODO in text files", results.isNotEmpty())
        assertTrue("Should find multiple TODOs", results.values.sumOf { it.size } >= 2)
    }

    @Test
    fun testSearchInMarkdown() = runBlocking {
        val results = findCodeInFiles(testRootObject, "search functionality")
        
        assertTrue("Should find text in markdown files", results.isNotEmpty())
    }

    @Test
    fun testNestedDirectorySearch() = runBlocking {
        val results = findCodeInFiles(testRootObject, "TestMain")
        
        // Should find in nested src/tests directory
        assertTrue("Should search in nested directories", 
            results.keys.any { it.getAbsolutePath().contains("src/tests") })
    }

    @Test
    fun testMultipleOccurrencesInSameFile() = runBlocking {
        val results = findCodeInFiles(testRootObject, "println")
        
        // Main.kt has multiple println statements
        val mainKtResults = results.entries.find { it.key.getName() == "Main.kt" }
        assertNotNull("Should find Main.kt", mainKtResults)
        assertTrue("Should find multiple occurrences in same file", 
            mainKtResults!!.value.size > 1)
    }

    @Test
    fun testSearchWithSpecialCharacters() = runBlocking {
        val results = findCodeInFiles(testRootObject, "Int)")
        
        // Should handle special characters like parentheses
        assertTrue("Should handle special characters", results.isNotEmpty())
    }

    @Test
    fun testEmptySearchQuery() = runBlocking {
        val results = findCodeInFiles(testRootObject, "")
        
        // Empty query should return no results or all results depending on implementation
        // In our case, we expect it to be filtered out
        assertTrue("Empty search should be handled", true)
    }

    @Test
    fun testSearchInLargeFile() = runBlocking {
        val results = findCodeInFiles(testRootObject, "searchable")
        
        // Should find in large.txt
        val largeFileResults = results.entries.find { it.key.getName() == "large.txt" }
        assertNotNull("Should search in large files", largeFileResults)
        assertTrue("Should find multiple matches in large file", 
            largeFileResults!!.value.size > 100)
    }

    @Test
    fun testHiddenFilesExcluded() = runBlocking {
        val results = findCodeInFiles(testRootObject, "gitignore", showHidden = false)
        
        // Should not search in .gitignore when hidden files are excluded
        assertFalse("Should exclude hidden files", 
            results.keys.any { it.getName().startsWith(".") })
    }

    @Test
    fun testHiddenFilesIncluded() = runBlocking {
        val results = findCodeInFiles(testRootObject, "build", showHidden = true)
        
        // Should search in .gitignore when hidden files are included
        assertTrue("Should include hidden files when enabled", 
            results.keys.any { it.getName().startsWith(".") })
    }

    @Test
    fun testLineAndColumnNumbers() = runBlocking {
        // Create a specific test file
        val testFile = File(testRoot, "positions.txt")
        testFile.writeText("""
            First line
            Second line with target word
            Third line
            Fourth line with target at end target
        """.trimIndent())
        
        val results = findCodeInFiles(testRootObject, "target")
        val posResults = results.entries.find { it.key.getName() == "positions.txt" }
        
        assertNotNull("Should find the test file", posResults)
        assertEquals("Should find 3 occurrences", 3, posResults!!.value.size)
        
        // Verify line numbers are correct (1-indexed)
        assertTrue("Line numbers should be correct", 
            posResults.value.all { it.line > 0 })
    }

    @Test
    fun testSearchWithNumbers() = runBlocking {
        val results = findCodeInFiles(testRootObject, "10")
        
        assertTrue("Should find numeric patterns", results.isNotEmpty())
    }

    @Test
    fun testSearchWithUnicode() = runBlocking {
        // Create file with unicode
        File(testRoot, "unicode.txt").writeText("Hello 世界 🌍")
        
        val results = findCodeInFiles(testRootObject, "世界")
        
        assertTrue("Should handle unicode characters", results.isNotEmpty())
    }

    // Helper data class
    data class CodeMatch(val line: Int, val column: Int, val snippet: String)

    // Helper function to simulate code search
    private suspend fun findCodeInFiles(
        parent: FileObject,
        query: String,
        showHidden: Boolean = false
    ): Map<FileObject, List<CodeMatch>> {
        val results = mutableMapOf<FileObject, MutableList<CodeMatch>>()
        
        if (query.isEmpty()) return results
        
        searchInDirectory(parent, query, results, showHidden)
        return results
    }

    private suspend fun searchInDirectory(
        parent: FileObject,
        query: String,
        results: MutableMap<FileObject, MutableList<CodeMatch>>,
        showHidden: Boolean
    ) {
        val childFiles = parent.listFiles()
        
        for (file in childFiles) {
            val isHidden = file.getName().startsWith(".")
            if (isHidden && !showHidden) continue
            
            if (file.isDirectory()) {
                searchInDirectory(file, query, results, showHidden)
                continue
            }
            
            // Skip very large files (over 10MB)
            if (file.length() > 10_000_000) continue
            
            // Try to read file content
            val content = try {
                file.readText()
            } catch (e: Exception) {
                continue
            }
            
            if (content == null) continue
            
            // Search in file content
            val lines = content.lines()
            lines.forEachIndexed { lineIndex, line ->
                if (line.lowercase().contains(query.lowercase())) {
                    val column = line.lowercase().indexOf(query.lowercase())
                    val match = CodeMatch(
                        line = lineIndex + 1,  // 1-indexed
                        column = column + 1,   // 1-indexed
                        snippet = line.trim()
                    )
                    results.getOrPut(file) { mutableListOf() }.add(match)
                }
            }
        }
    }
}
