package cobalt.vortex.reactor.publisher.mono

import cobalt.vortex.function.pipe
import cobalt.vortex.function.then
import cobalt.vortex.reactor.publisher.MonoExt
import io.kotest.core.spec.style.WordSpec
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class MonoExtMapTest : WordSpec({
  "map with" `when` {
    "pipe" should {
      val stream = pipe(
        Mono.just(10),
        MonoExt.map { it.toString() }
      )

      StepVerifier.create(stream)
        .expectNext("10")
        .expectComplete()
        .verify()
    }

    "function composit" should {
      val inc = MonoExt.map<Int, Int> { it.inc() }
      val intToString = MonoExt.map<Int, String> { it.toString() }
      val incAndToString = inc then intToString

      val stream = pipe(
        Mono.just(10),
        incAndToString
      )

      StepVerifier.create(stream)
        .expectNext("11")
        .expectComplete()
        .verify()
    }
  }
})