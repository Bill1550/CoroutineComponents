@file:OptIn(ExperimentalSerializationApi::class)

package com.loneoaktech.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.junit.Test

class ComplexSealedClassTest {

    interface PersistableStuff {
        val item: String
    }

    sealed interface States {

        val name: String

        class FirstState(
            override val name: String = "first"
        ) : States

        class SecondState(
            override val item: String,
            override val name: String = "second"
        ) : States, PersistableStuff

        class ThirdState(
            override val item: String,
            override val name: String = "third"
        ) : States, PersistableStuff
    }

    abstract class  PersistableStuffSerializer<T:PersistableStuff>(
        val factory: (String, PersistableStuff)->T
    ) : KSerializer<T> {

        @Serializable
        private data class StuffSurrogate(
            override val item: String
        ) : PersistableStuff {
            constructor( other: PersistableStuff) : this(
                item = other.item
            )

        }

        @Serializable
        private data class StuffHolder(
            val name: String,
            val stuff: StuffSurrogate
        )

        override val descriptor: SerialDescriptor
            get() = SerialDescriptor("PersistableStuff", StuffHolder.serializer().descriptor)

        override fun deserialize(decoder: Decoder): T {
            val holder = decoder.decodeSerializableValue(StuffHolder.serializer())

            return factory(holder.name, holder.stuff)
        }

        override fun serialize(encoder: Encoder, value: T) {
            encoder.encodeSerializableValue( StuffHolder.serializer(),
                StuffHolder( "TBD", StuffSurrogate(value) ))
        }
    }

    object StateSuffSerializer : PersistableStuffSerializer<PersistableStuff>(
        factory = { name, stuff ->
            when(name) {
                "second" -> States.SecondState( item = stuff.item)
                "third" -> States.ThirdState(item = stuff.item)
                else -> throw IllegalArgumentException("Unrecognized type: $name")
            }

        }
    )


    @Test
    fun smokeTest() {
        println("Hello testing")
    }

    @Test
    fun `check that when spans all`() {

        fun isFirstState( state: States) =
            when(state) {
                is States.FirstState -> true
                is States.SecondState -> false
                is States.ThirdState -> false
            }


        assert( isFirstState( States.FirstState() ))
    }
}