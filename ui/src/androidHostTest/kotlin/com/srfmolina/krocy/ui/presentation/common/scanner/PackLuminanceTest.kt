package com.srfmolina.krocy.ui.presentation.common.scanner

import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class PackLuminanceTest {

    @Test
    fun `rowStride equal to width round-trips the bytes unchanged`() {
        val width = 4
        val height = 3
        val bytes = ByteArray(width * height) { (it + 1).toByte() }
        val buffer = ByteBuffer.wrap(bytes)

        val result = packLuminance(buffer, rowStride = width, width = width, height = height)

        assertContentEquals(bytes, result)
    }

    @Test
    fun `rowStride greater than width drops exactly the padding`() {
        val width = 4
        val height = 3
        val rowStride = 6
        // Distinctive per-row values: row y has image bytes (y*10 + 1..width) then padding bytes.
        val source = ByteArray(rowStride * height)
        for (y in 0 until height) {
            for (x in 0 until rowStride) {
                source[y * rowStride + x] = if (x < width) {
                    (y * 10 + x + 1).toByte()
                } else {
                    // Padding: a value that would be visibly wrong if it leaked into the output.
                    (0x7F).toByte()
                }
            }
        }
        val buffer = ByteBuffer.wrap(source)

        val result = packLuminance(buffer, rowStride = rowStride, width = width, height = height)

        val expected = ByteArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                expected[y * width + x] = (y * 10 + x + 1).toByte()
            }
        }
        assertContentEquals(expected, result)
    }

    @Test
    fun `result size is always width times height`() {
        val width = 5
        val height = 7

        val packed = packLuminance(
            ByteBuffer.wrap(ByteArray(width * height)),
            rowStride = width,
            width = width,
            height = height
        )
        val padded = packLuminance(
            ByteBuffer.wrap(ByteArray((width + 3) * height)),
            rowStride = width + 3,
            width = width,
            height = height
        )

        assertEquals(width * height, packed.size)
        assertEquals(width * height, padded.size)
    }
}
