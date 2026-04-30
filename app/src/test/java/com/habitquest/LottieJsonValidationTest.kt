package com.habitquest

import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.File

class LottieJsonValidationTest {

    private val rawDir = File("src/main/res/raw")

    private val lottieFiles = listOf(
        "pet_seed_idle.json",
        "pet_baby_idle.json",
        "pet_explorer_idle.json",
        "pet_guardian_idle.json",
        "pet_essence_idle.json"
    )

    // Required top-level string keys in a Lottie JSON.
    private val requiredKeys = listOf("\"v\"", "\"fr\"", "\"ip\"", "\"op\"", "\"w\"", "\"h\"", "\"layers\"")

    @Test
    fun all_lottie_jsons_exist_and_parse() {
        lottieFiles.forEach { filename ->
            val file = File(rawDir, filename)

            assertTrue("File not found: $filename — check app/src/main/res/raw/", file.exists())
            assertTrue("File is empty: $filename", file.length() > 0)

            val text = file.readText().trim()
            assertTrue("$filename does not start with '{' — likely malformed JSON", text.startsWith("{"))
            assertTrue("$filename does not end with '}' — likely truncated", text.endsWith("}"))

            requiredKeys.forEach { key ->
                assertTrue(
                    "$filename: missing required key $key",
                    text.contains(key)
                )
            }

            // Extract fr and op to verify sensible animation metadata.
            val fr = extractIntField(text, "fr")
            val op = extractIntField(text, "op")
            val ip = extractIntField(text, "ip")

            if (fr != null && op != null && ip != null) {
                assertTrue("$filename: frame rate must be > 0 (got $fr)", fr > 0)
                assertTrue("$filename: op ($op) must be > ip ($ip)", op > ip)
                val durationSec = (op - ip).toFloat() / fr
                println("[OK] $filename  fr=$fr  ip=$ip  op=$op  duration=%.2fs".format(durationSec))
            } else {
                fail("$filename: could not extract fr/ip/op values from JSON")
            }

            // Verify layers array is non-empty (has at least one '{' inside the layers value).
            val layersIdx = text.indexOf("\"layers\"")
            assertTrue("$filename: 'layers' key not found", layersIdx >= 0)
            val arrayStart = text.indexOf('[', layersIdx)
            val arrayEnd   = text.indexOf(']', arrayStart)
            assertTrue("$filename: layers array not closed", arrayEnd > arrayStart)
            val layersContent = text.substring(arrayStart + 1, arrayEnd).trim()
            assertTrue("$filename: layers array is empty — no animation data", layersContent.isNotEmpty())
        }
    }

    /** Extracts the integer value of a JSON field like `"fr": 30`. */
    private fun extractIntField(json: String, field: String): Int? {
        val pattern = Regex("\"$field\"\\s*:\\s*(\\d+)")
        return pattern.find(json)?.groupValues?.get(1)?.toIntOrNull()
    }
}
