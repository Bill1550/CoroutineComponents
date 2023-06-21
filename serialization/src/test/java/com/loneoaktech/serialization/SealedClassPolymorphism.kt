package com.loneoaktech.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class SealedClassPolymorphism {


    @Serializable
    sealed interface Things

    @Serializable
    @SerialName("A")
    data class  ThingA( val int: Int ) : Things

    @Serializable
    @SerialName("B")
    data class ThingB( val str: String) : Things

    @Serializable
    @SerialName("C")
    data class ThingC( val double: Double): Things

    val severalDifferentThings = listOf( ThingA(1), ThingA(2), ThingB("one)"), ThingC(3.0))

    val severalSimilarThings = listOf(ThingA(1), ThingA(2), ThingA(3) )


    @Test
    fun testListSerialization() {

        val encoded = Json.encodeToString(severalSimilarThings)

        println("----> Homogeneous list: $encoded")
    }

    @Test
    fun testPolymorphicSerialization() {

        val encoded = Json.encodeToString(severalDifferentThings)

        println("----> Heterogeneous list: $encoded")
    }


}