# Coroutines Tutorial: Articles App

A standalone, hands-on exercise for learning Kotlin coroutines by implementing six loading
strategies for the same small "Articles" feed, each building on the previous one:

1. **`Task1BlockVsSuspend`** — implement a blocking and a suspending version of the same loading
   function, and see the difference in UI responsiveness.
2. **`Task2SequentialVsAsynchronous`** — load all articles concurrently with `async`/`awaitAll`
   instead of one after another.
3. **`Task3StructuralConcurrency`** — compare a properly scoped, cancellable loading coroutine
   against one that leaks into `GlobalScope` and can't be cancelled.
4. **`Task4Progress`** — report articles as they load, one by one, using a `Flow`.
5. **`Task5AsyncProgress`** — combine concurrency and progress reporting with `channelFlow`.
6. **`Task6Exceptions`** — handle a flaky network call, and use a `SupervisorJob` so a failed
   loading attempt doesn't break the ones after it.

Run the `Articles` run configuration (or `desktop-client`'s `jvmRun` Gradle task) to launch the app.
On startup it automatically starts the backend server; once it's ready, the article browser appears
in the same window, with one loading mode per task above.

The hands-on exercises live in `desktop-client/src/jvmMain/kotlin/org/example/articles/tasks`. This
branch (`main`) contains the unsolved tasks; switch to the `solutions` branch to see one way of
implementing them. Each task also has a matching test in
`desktop-client/src/jvmTest/kotlin/org/example/articles/tasks`, using virtual time so you can check
your solution's behavior and timing without waiting for the simulated network delays.
