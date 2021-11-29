package cobalt.vortex.reactor.publisher.flux

import cobalt.vortex.function.pipe
import cobalt.vortex.reactor.publisher.FluxExt
import io.kotest.core.spec.style.WordSpec
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

class FluxExtMapTest : WordSpec({
  "map with" `when` {
    "pipe" should {
      val stream = pipe(
        Flux.just(1, 2, 3, 4, 5),
        FluxExt.map { it.inc() }
      )

      StepVerifier.create(stream)
        .expectNext(2)
        .expectNext(3)
        .expectNext(4)
        .expectNext(5)
        .expectNext(6)
        .expectComplete()
        .verify()
    }
  }
})