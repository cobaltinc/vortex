package cobalt.vortex.reactor.publisher

import cobalt.vortex.type.Either
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import reactor.util.retry.Retry
import java.util.function.Supplier

object MonoExt {

  /**
   * Combination
   */

  // zipWith
  fun <A, B> zipWith(other: Mono<B>) = fun(p: Mono<A>) = p.zipWith(other)

  // zipWhen
  fun <A, B> zipWhen(other: (A) -> Mono<B>) = fun(p: Mono<A>) = p.zipWhen(other)

  // concat
  fun <T> Mono<T>.concat(vararg sources: Publisher<T>): Flux<T> = Flux.concat(this, *sources)

  /**
   * Conditional
   */

  // throwIf
  fun <T> throwIf(e: Throwable) = fun(f: (T) -> Boolean) = fun(p: Mono<T>) = p.flatMap { if (f(it)) Mono.error(e) else p }
  fun <T> Mono<T>.throwIf(e: Throwable, f: (T) -> Boolean): Mono<T> = this.flatMap { if (f(it)) Mono.error(e) else this }

  // switchIf
  fun <L, R> switchIf(a: R) = fun(f: (L) -> Boolean) = fun(p: Mono<L>): Mono<Either<L, R>> = p.map { if (f(it)) Either.Right(a) else Either.Left(it) }
  fun <L, R> Mono<L>.switchIf(a: R, f: (L) -> Boolean): Mono<Either<L, R>> = this.map { if (f(it)) Either.Right(a) else Either.Left(it) }

  // switchIfEmpty
  fun <T> switchIfEmpty(a: Mono<T>) = fun(b: Mono<T>): Mono<out T> = b.switchIfEmpty(a)

  // iif
  fun <T, R> iif(a: Mono<R>, b: Mono<R>) = fun(f: (T) -> Boolean) = fun(p: Mono<T>) = p.flatMap { if (f(it)) a else b }
  fun <T, R> iif(a: (T) -> Mono<R>, b: (T) -> Mono<R>) = fun(f: (T) -> Boolean) = fun(p: Mono<T>) = p.flatMap { if (f(it)) a(it) else b(it) }
  fun <T, R> Mono<T>.iif(a: Mono<R>, b: Mono<R>, f: (T) -> Boolean): Mono<R> = this.flatMap { if (f(it)) a else b }
  fun <T, R> Mono<T>.iif(a: (T) -> Mono<R>, b: (T) -> Mono<R>, f: (T) -> Boolean): Mono<R> = this.flatMap { if (f(it)) a(it) else b(it) }

  /**
   * Filtering
   */

  // filter
  fun <T> filter(f: (T) -> Boolean) = fun(p: Mono<T>): Mono<T> = p.filter(f)

  /**
   * Transformation
   */

  // map
  fun <T, R> map(f: (T) -> R) = fun(p: Mono<T>): Mono<R> = p.map(f)

  // mapTo
  fun <T> mapTo(value: T) = fun(p: Mono<T>) = p.map { value }
  fun <T, R> Mono<T>.mapTo(value: R): Mono<R> = this.map { value }

  // flatMap
  fun <T, R> flatMap(f: (T) -> Mono<R>) = fun(p: Mono<T>): Mono<R> = p.flatMap(f)

  // flatMapTo
  fun <T> flatMapTo(value: Supplier<out Mono<T>>) = fun(p: Mono<T>) = p.flatMap { value.get() }
  fun <T, R> Mono<T>.flatMapTo(value: Supplier<out Mono<R>>): Mono<out R> = this.flatMap { value.get() }

  // flatMapMany
  fun <T, R> flatMapMany(f: (T) -> Publisher<R>) = fun(p: Mono<T>): Flux<R> = p.flatMapMany(f)

  // flatMapManyTo
  fun <T, R> Mono<T>.flatMapManyTo(value: Publisher<R>): Flux<out R> = this.flatMapMany { value }

  // flatMapIterable
  fun <T, R> flatMapIterable(f: (T) -> Iterable<R>) = fun(p: Mono<T>): Flux<R> = p.flatMapIterable(f)

  // flatMapArray
  fun <T, R> flatMapArray(f: (T) -> Array<R>) = fun(p: Mono<T>): Flux<R> = p.flatMapIterable { f(it).toList() }
  fun <T, R> Mono<T>.flatMapArray(f: (T) -> Array<R>): Flux<R> = this.flatMapIterable { f(it).toList() }

  // then
  fun <T, R> then(r: Mono<R>) = fun(p: Mono<T>): Mono<R> = p.then(r)

  // thenMany
  fun <T, R> then(r: Flux<R>) = fun(p: Mono<T>): Flux<R> = p.thenMany(r)

  /**
   * Util
   */

  // retry
  fun <T> retry() = fun(p: Mono<T>) = p.retry()
  fun <T> retry(numRetries: Long) = fun(p: Mono<T>) = p.retry(numRetries)

  // retryWhen
  fun <T> retry(retrySpec: Retry) = fun(p: Mono<T>) = p.retryWhen(retrySpec)

  // tap
  fun <T> tap(f: (T) -> Unit) = fun(p: Mono<T>): Mono<T> = p.doOnNext(f)

  // pipe
  inline fun <T1, R> Mono<T1>.pipe(f1: (Mono<T1>) -> R): R = f1(this)
  inline fun <T1, T2, R> Mono<T1>.pipe(f1: (Mono<T1>) -> T2, f2: (T2) -> R): R = f2(f1(this))
  inline fun <T1, T2, T3, R> Mono<T1>.pipe(f1: (Mono<T1>) -> T2, f2: (T2) -> T3, f3: (T3) -> R): R = f3(f2(f1(this)))
  inline fun <T1, T2, T3, T4, R> Mono<T1>.pipe(f1: (Mono<T1>) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> R): R = f4(f3(f2(f1(this))))
  inline fun <T1, T2, T3, T4, T5, R> Mono<T1>.pipe(f1: (Mono<T1>) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> T5, f5: (T5) -> R): R = f5(f4(f3(f2(f1(this)))))
  inline fun <T1, T2, T3, T4, T5, T6, R> Mono<T1>.pipe(f1: (Mono<T1>) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> T5, f5: (T5) -> T6, f6: (T6) -> R): R = f6(f5(f4(f3(f2(f1(this))))))
  inline fun <T1, T2, T3, T4, T5, T6, T7, R> Mono<T1>.pipe(f1: (Mono<T1>) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> T5, f5: (T5) -> T6, f6: (T6) -> T7, f7: (T7) -> R): R = f7(f6(f5(f4(f3(f2(f1(this)))))))
  inline fun <T1, T2, T3, T4, T5, T6, T7, T8, R> Mono<T1>.pipe(f1: (Mono<T1>) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> T5, f5: (T5) -> T6, f6: (T6) -> T7, f7: (T7) -> T8, f8: (T8) -> R): R = f8(f7(f6(f5(f4(f3(f2(f1(this))))))))
  inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> Mono<T1>.pipe(f1: (Mono<T1>) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> T5, f5: (T5) -> T6, f6: (T6) -> T7, f7: (T7) -> T8, f8: (T8) -> T9, f9: (T9) -> R): R = f9(f8(f7(f6(f5(f4(f3(f2(f1(this)))))))))
  inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> Mono<T1>.pipe(f1: (Mono<T1>) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> T5, f5: (T5) -> T6, f6: (T6) -> T7, f7: (T7) -> T8, f8: (T8) -> T9, f9: (T9) -> T10, f10: (T10) -> R): R = f10(f9(f8(f7(f6(f5(f4(f3(f2(f1(this))))))))))
  inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> Mono<T1>.pipe(f1: (Mono<T1>) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> T5, f5: (T5) -> T6, f6: (T6) -> T7, f7: (T7) -> T8, f8: (T8) -> T9, f9: (T9) -> T10, f10: (T10) -> T11, f11: (T11) -> R): R = f11(f10(f9(f8(f7(f6(f5(f4(f3(f2(f1(this)))))))))))
  inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> Mono<T1>.pipe(f1: (Mono<T1>) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> T5, f5: (T5) -> T6, f6: (T6) -> T7, f7: (T7) -> T8, f8: (T8) -> T9, f9: (T9) -> T10, f10: (T10) -> T11, f11: (T11) -> T12, f12: (T12) -> R): R = f12(f11(f10(f9(f8(f7(f6(f5(f4(f3(f2(f1(this))))))))))))

}
