@file:OptIn(ExperimentalSerializationApi::class)

package com.loneoaktech.serialization

import junit.framework.TestCase.assertEquals
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.text.Charsets.UTF_8

class GZipTests {

    fun gzip(content: String): ByteArray {
        val bos = ByteArrayOutputStream()
        GZIPOutputStream(bos).bufferedWriter(UTF_8).use { it.write(content) }
        return bos.toByteArray()
    }

    fun ungzip(content: ByteArray): String =
        GZIPInputStream(content.inputStream()).bufferedReader(UTF_8).use { it.readText() }


    @Test
    fun `compress some orders`() {


        Samples.cases.forEach {
            val comp = gzip(it.data)
            val ratio =
                (((it.data.length - comp.size).toDouble() / it.data.length) * 1000).roundToInt()
                    .toFloat() / 10
            println(
                "${it.label.padEnd(12, ' ')}  ${
                    it.data.length.toString().padStart(7)
                } ${comp.size.toString().padStart(8)}   $ratio%"
            )
        }
    }

    @Test
    fun `test decompression`() {
        Samples.cases.forEach {
            val comp = gzip(it.data)

            val uncomp = ungzip(comp)

            assertEquals(it.data, uncomp)
        }
    }

    @Test
    fun `try serializing gzip output with cbor`() {

        @Serializable
        data class Holder( val comp: ByteArray)

        Samples.cases.forEach {

            val comp = gzip(it.data)
            val serialized = Cbor.encodeToByteArray(Holder.serializer(), Holder(comp))

            println("${it.label} ${it.data.length}  ${comp.size}  ${serialized.size}")
        }

    }

    @Test
    fun `measure some packed strings`() {
        Samples.cases.forEach {
            val ba = it.data.toByteArray() // UTF-8

            println("${it.label}  ${it.data.length}  ${ba.size}")
        }
    }
}