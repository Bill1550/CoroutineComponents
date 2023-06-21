## Coroutine Topics


1. The Basics
   - suspend functions
   - sequences
   - how suspension works
2. More than the basics
   - coroutine builders
   - coroutine context
   - coroutine scopes
2. Structured Concurrency
   - jobs
   - cancellation
   - exception handling
   - scope conventions
3. Dispatchers and Threads
   - Dispatchers
   - Thread management
   - Interoperating with legacy code
   - Android thread model
4. Flows and Channels
   - Flow builders
   - Hot vs Cold
   - StateFlows & SharedFlows
5. Testing Deep Dive
   - Kotlin test library
   - Turbine test library


## Questions
1. Does `synchronized` work with coroutines? What are the alternatives?
2. When I need to use a coroutineScope?
3. How do you test stateFlow/sharedFlow with subscribers (1 or more)?
4. ...