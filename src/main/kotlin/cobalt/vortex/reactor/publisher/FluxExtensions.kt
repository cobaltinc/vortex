package cobalt.vortex.reactor.publisher

import cobalt.vortex.type.Either
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.util.function.Supplier

object FluxExt {

  /**
   * Combination
   */

  // zipWith
  fun <A, B> zipWith(other: Publisher<B>) = fun(p: Flux<A>) = p.zipWith(other)

  // zipWithIterable
  fun <A, B> zipWithIterable(other: Iterable<B>) = fun(p: Flux<A>) = p.zipWithIterable(other)

  // concat
  fun <T> concat(vararg sources: Publisher<T>) = fun(p: Publisher<T>) = Flux.concat(p, *sources)
  fun <T> Flux<T>.concat(vararg sources: Publisher<T>): Flux<T> = Flux.concat(this, *sources)

  /**
   * Conditional
   */

  // throwIf
  fun <T> throwIf(e: Throwable) = fun(f: (T) -> Boolean) = fun(p: Flux<T>) = p.flatMap { if (f(it)) Flux.error(e) else p }
  fun <T> Flux<T>.throwIf(e: Throwable, f: (T) -> Boolean): Flux<T> = this.flatMap { if (f(it)) Flux.error(e) else this }

  // switchIf
  fun <L, R> switchIf(a: R) = fun(f: (L) -> Boolean) = fun(p: Flux<L>): Flux<Either<L, R>> = p.map { if (f(it)) Either.Right(a) else Either.Left(it) }
  fun <L, R> Flux<L>.switchIf(a: R, f: (L) -> Boolean): Flux<Either<L, R>> = this.map { if (f(it)) Either.Right(a) else Either.Left(it) }

  // switchIfEmpty
  fun <T> switchIfEmpty(a: Publisher<T>) = fun(b: Flux<T>): Flux<out T> = b.switchIfEmpty(a)

  // iif
  fun <T, R> iif(a: Flux<R>, b: Flux<R>) = fun(f: (T) -> Boolean) = fun(p: Flux<T>) = p.flatMap { if (f(it)) a else b }
  fun <T, R> Flux<T>.iif(a: Publisher<R>, b: Publisher<R>, f: (T) -> Boolean): Flux<R> = this.flatMap { if (f(it)) a else b }

  /**
   * Filtering
   */

  // filter
  fun <T> filter(f: (T) -> Boolean) = fun(p: Flux<T>): Flux<T> = p.filter(f)

  // any
  fun <T> any(f: (T) -> Boolean) = fun(p: Flux<T>): Mono<Boolean> = p.filter(f).count().map { it > 0 }
  fun <T> Flux<T>.any(f: (T) -> Boolean): Mono<Boolean> = this.filter(f).count().map { it > 0 }

  // all
  fun <T> all(f: (T) -> Boolean) = fun(p: Flux<T>): Mono<Boolean> = Mono.zip(p.count(), p.filter(f).count()).map { it.t1 == it.t2 }
  fun <T> Flux<T>.all(f: (T) -> Boolean): Mono<Boolean> = Mono.zip(this.count(), this.filter(f).count()).map { it.t1 == it.t2 }

  // distinct
  fun <T> distinct() = fun(p: Flux<T>): Flux<T> = p.distinct()
  fun <T, R> distinct(keySelector: (T) -> R) = fun(p: Flux<T>): Flux<T> = p.distinct(keySelector)

  // findFirst
  fun <T> findFirst(value: T) = fun(p: Flux<T>): Mono<T> = p.filter { it == value }.singleOrEmpty()
  fun <T> findFirst(f: (T) -> Boolean) = fun(p: Flux<T>): Mono<T> = p.filter(f).singleOrEmpty()
  fun <T> Flux<T>.findFirst(f: (T) -> Boolean): Mono<T> = this.filter(f).singleOrEmpty()

  // last
  fun <T> last() = fun(p: Flux<T>): Mono<T> = p.last()
  fun <T> last(defaultValue: T) = fun(p: Flux<T>): Mono<T> = p.last(defaultValue)

  // skip
  fun <T> skip(skipped: Long) = fun(p: Flux<T>): Flux<T> = p.skip(skipped)

  /**
   * Transformation
   */

  // map
  fun <T, R> map(f: (T) -> R) = fun(p: Flux<T>): Flux<R> = p.map(f)

  // single
  fun <T> single() = fun(p: Flux<T>): Mono<T> = p.single()
  fun <T> single(defaultValue: T) = fun(p: Flux<T>): Mono<T> = p.single(defaultValue)

  // singleOrEmpty
  fun <T> singleOrEmpty() = fun(p: Flux<T>): Mono<T> = p.singleOrEmpty()

  // mapTo
  fun <T> mapTo(value: T) = fun(p: Flux<T>) = p.map { value }
  fun <T, R> Flux<T>.mapTo(value: R): Flux<R> = this.map { value }

  // flatMap
  fun <T, R> flatMap(f: (T) -> Mono<R>) = fun(p: Flux<T>): Flux<R> = p.flatMap(f)

  // flatMapTo
  fun <T> flatMapTo(value: Publisher<T>) = fun(p: Flux<T>) = p.flatMap { value }
  fun <T, R> Flux<T>.flatMapTo(value: Publisher<R>): Flux<out R> = this.flatMap { value }

  // flatMapIterable
  fun <T, R> flatMapIterable(f: (T) -> Iterable<R>) = fun(p: Flux<T>): Flux<R> = p.flatMapIterable(f)

  // flatMapArray
  fun <T, R> flatMapArray(f: (T) -> Array<R>) = fun(p: Flux<T>): Flux<R> = p.flatMapIterable { f(it).toList() }

  // then
  fun <T, R> then(r: Mono<R>) = fun(p: Flux<T>): Mono<R> = p.then(r)

  // thenMany
  fun <T, R> then(r: Flux<R>) = fun(p: Flux<T>): Flux<R> = p.thenMany(r)

  // count
  fun <T> count() = fun(p: Flux<T>): Mono<Long> = p.count()

  // collectList
  fun <T> collectList(p: Flux<T>): Mono<List<T>> = p.collectList()

  // reduce
  fun <T> reduce(initialValue: T, f: (T, T) -> T) = fun(p: Flux<T>): Mono<T> = p.reduce(initialValue, f)
  fun <T> reduce(f: (T, T) -> T) = fun(p: Flux<T>): Mono<T> = p.reduce(f)

  // groupBy
  fun <T, K> groupBy(f: (T) -> K) = fun(p: Flux<T>) = p.groupBy(f)

  // partition
  fun <T> partition(f: (T) -> Boolean) = fun(p: Flux<T>) = Flux.zip(p.filter(f), p.filter{ !f(it) })

  /**
   * Util
   */

  // retry
  fun <T> retry() = fun(p: Flux<T>) = p.retry()
  fun <T> retry(numRetries: Long) = fun(p: Flux<T>) = p.retry(numRetries)

  // retryWhen
  fun <T> retry(retrySpec: Retry) = fun(p: Flux<T>) = p.retryWhen(retrySpec)

  // tap
  fun <T> tap(f: (T) -> Unit) = fun(p: Flux<T>): Flux<T> = p.doOnNext(f)

  // pipe
  inline fun <T1, R> Flux<T1>.pipe(f1: (Flux<T1>) -> R): R = f1(this)
  inline fun <T1, T2, R> Flux<T1>.pipe(f1: (Flux<T1>) -> T2, f2: (T2) -> R): R = f2(f1(this))
  inline fun <T1, T2, T3, R> Flux<T1>.pipe(f1: (Flux<T1>) -> T2, f2: (T2) -> T3, f3: (T3) -> R): R = f3(f2(f1(this)))
  inline fun <T1, T2, T3, T4, R> Flux<T1>.pipe(f1: (Flux<T1>) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> R): R = f4(f3(f2(f1(this))))
  inline fun <T1, T2, T3, T4, T5, R> Flux<T1>.pipe(f1: (Flux<T1>) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> T5, f5: (T5) -> R): R = f5(f4(f3(f2(f1(this)))))
  inline fun <T1, T2, T3, T4, T5, T6, R> Flux<T1>.pipe(f1: (Flux<T1>) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> T5, f5: (T5) -> T6, f6: (T6) -> R): R = f6(f5(f4(f3(f2(f1(this))))))
  inline fun <T1, T2, T3, T4, T5, T6, T7, R> Flux<T1>.pipe(f1: (Flux<T1>) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> T5, f5: (T5) -> T6, f6: (T6) -> T7, f7: (T7) -> R): R = f7(f6(f5(f4(f3(f2(f1(this)))))))
  inline fun <T1, T2, T3, T4, T5, T6, T7, T8, R> Flux<T1>.pipe(f1: (Flux<T1>) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> T5, f5: (T5) -> T6, f6: (T6) -> T7, f7: (T7) -> T8, f8: (T8) -> R): R = f8(f7(f6(f5(f4(f3(f2(f1(this))))))))
  inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> Flux<T1>.pipe(f1: (Flux<T1>) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> T5, f5: (T5) -> T6, f6: (T6) -> T7, f7: (T7) -> T8, f8: (T8) -> T9, f9: (T9) -> R): R = f9(f8(f7(f6(f5(f4(f3(f2(f1(this)))))))))
  inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> Flux<T1>.pipe(f1: (Flux<T1>) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> T5, f5: (T5) -> T6, f6: (T6) -> T7, f7: (T7) -> T8, f8: (T8) -> T9, f9: (T9) -> T10, f10: (T10) -> R): R = f10(f9(f8(f7(f6(f5(f4(f3(f2(f1(this))))))))))
  inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> Flux<T1>.pipe(f1: (Flux<T1>) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> T5, f5: (T5) -> T6, f6: (T6) -> T7, f7: (T7) -> T8, f8: (T8) -> T9, f9: (T9) -> T10, f10: (T10) -> T11, f11: (T11) -> R): R = f11(f10(f9(f8(f7(f6(f5(f4(f3(f2(f1(this)))))))))))
  inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> Flux<T1>.pipe(f1: (Flux<T1>) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> T5, f5: (T5) -> T6, f6: (T6) -> T7, f7: (T7) -> T8, f8: (T8) -> T9, f9: (T9) -> T10, f10: (T10) -> T11, f11: (T11) -> T12, f12: (T12) -> R): R = f12(f11(f10(f9(f8(f7(f6(f5(f4(f3(f2(f1(this))))))))))))

}
