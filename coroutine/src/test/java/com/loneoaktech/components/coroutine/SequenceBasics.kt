package com.loneoaktech.components.coroutine

import org.junit.Test
import kotlin.system.measureTimeMillis

class SequenceBasics {

    val reps = 100

    @Test
    fun `list example`() {

        measureTimeMillis {
            val lst = List(reps) { it }
            val listResult = lst.map { it * 2 }.reduce { acc, i -> acc + i }
            println("result=$listResult")
        }.also { println("list time=$it") }

    }

    @Test
    fun `sequence example`()  {
        measureTimeMillis {
            val seq = sequence {
                repeat(reps){
                    yield(it)
                }
            }
            val seqResult = seq.map { it * 2 }.reduce { acc, i -> acc + i }
            println("result=$seqResult")
        }.also { println("sequence time =$it")}
    }

    val smallReps = 4

    @Test
    fun `list example traced`() {

        val lst = List(smallReps) {
            println("generating $it")
            it
        }

        val listResult = lst.map {
            println("mapping $it")
            it * 2
        }.reduce { acc, i ->
            println("summing $i")
            acc + i
        }
        println("result=$listResult")

    }

    @Test
    fun `sequence example traced`()  {
        val seq = sequence {
            repeat(smallReps){
                println("generating $it")
                yield(it)
            }
        }
        val seqResult = seq.map {
            println("mapping $it")
            it * 2
        }.reduce { acc, i ->
            println("summing $i")
            acc + i
        }
        println("result=$seqResult")
    }

    @Test
    fun more() {
        val lst = listOf(1,2,3,4)
        val result = lst.asSequence().map {
            println("computing $it")
            it * it}.takeWhile { it < 4 }.toList()

        println(result)
    }

}