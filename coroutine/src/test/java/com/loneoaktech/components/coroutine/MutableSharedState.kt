package com.loneoaktech.components.coroutine

import org.junit.Test

class MutableSharedStateExamples {

    var sampleVar: Int? = 1

    @Test
    fun accessingMemberVariables() {

        // Produces an error at `sampleVar + 3`
//        if (sampleVar != null) {
//            println( sampleVar + 3)
//
//        }

        sampleVar?.let {
            println( it + 3 )
        }
    }
}
