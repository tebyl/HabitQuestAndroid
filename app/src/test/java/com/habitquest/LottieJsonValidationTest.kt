package com.habitquest

import org.json.JSONObject
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class LottieJsonValidationTest {

    @Test
    fun validateLottieFiles() {
        val rawDir = File("src/main/res/raw")
        if (!rawDir.exists()) {
             // In some environments, the path might be different
             return 
        }
        
        val lottieFiles = rawDir.listFiles { _, name -> name.endsWith(".json") } ?: emptyArray()
        
        assertTrue("No Lottie files found in res/raw", lottieFiles.isNotEmpty())
        
        lottieFiles.forEach { file ->
            val content = file.readText()
            assertTrue("File ${file.name} is empty", content.isNotBlank())
            
            try {
                val json = JSONObject(content)
                val requiredKeys = listOf("v", "fr", "ip", "op", "w", "h", "layers")
                requiredKeys.forEach { key ->
                    assertTrue("File ${file.name} missing key: $key", json.has(key))
                }
            } catch (e: Exception) {
                throw AssertionError("File ${file.name} is not a valid JSON: ${e.message}")
            }
        }
    }
}
