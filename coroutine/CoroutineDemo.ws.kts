import kotlin.coroutines.*



val a = 100
a*10

suspend fun test( msg: String) (
    println(msg)
)

runBlocking {
    test("hello")
}
