@file:OptIn(ExperimentalSerializationApi::class)

package com.loneoaktech.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

class ByteArraySerializationTests {

    @Serializable
    data class Sample(
        val label: String,
        val data: ByteArray
    )

    val sample1 = Sample(
        label = "sample1",
        data = "This is some test data with some replication in the test".toByteArray()
    )

    @Test
    fun `encode a byte array as JSON`() {

        val encoded = Json.encodeToString(sample1)

        println("Len=${encoded.length} Encoded: `${encoded}`")

        // not very useful
    }

    @Test
    fun `encode a byte array as CBOR`() {

        println("Data length=${sample1.data.size}")

        val encoded = Cbor.encodeToByteArray(Sample.serializer(), sample1)

        println("len=${encoded.size}")
    }
}