package cobalt.vortex.type

sealed class Either<A, B> {
  class Left<A, B>(val left: A): Either<A, B>()
  class Right<A, B>(val right: B): Either<A, B>()
}

typealias Result<V> = Either<Exception, V>
fun <V> Success(v: V): Result<V> = Either.Right(v)
fun <V> Failure(e: Exception): Result<V> = Either.Left(e)
