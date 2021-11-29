package cobalt.vortex.function

import reactor.util.function.*

infix fun <A, B, C> ((A) -> B).then(other: (B) -> C): (A) -> C = { other.invoke(this.invoke(it)) }

fun <R> pipe(a: R): R = a
inline fun <T1, R> pipe(a: T1, f1: (T1) -> R): R = f1(a)
inline fun <T1, T2, R> pipe(a: T1, f1: (T1) -> T2, f2: (T2) -> R): R = f2(f1(a))
inline fun <T1, T2, T3, R> pipe(a: T1, f1: (T1) -> T2, f2: (T2) -> T3, f3: (T3) -> R): R = f3(f2(f1(a)))
inline fun <T1, T2, T3, T4, R> pipe(a: T1, f1: (T1) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> R): R = f4(f3(f2(f1(a))))
inline fun <T1, T2, T3, T4, T5, R> pipe(a: T1, f1: (T1) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> T5, f5: (T5) -> R): R = f5(f4(f3(f2(f1(a)))))
inline fun <T1, T2, T3, T4, T5, T6, R> pipe(a: T1, f1: (T1) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> T5, f5: (T5) -> T6, f6: (T6) -> R): R = f6(f5(f4(f3(f2(f1(a))))))
inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> pipe(a: T1, f1: (T1) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> T5, f5: (T5) -> T6, f6: (T6) -> T7, f7: (T7) -> T8, f8: (T8) -> T9, f9: (T9) -> R): R = f9(f8(f7(f6(f5(f4(f3(f2(f1(a)))))))))
inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> pipe(a: T1, f1: (T1) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> T5, f5: (T5) -> T6, f6: (T6) -> T7, f7: (T7) -> T8, f8: (T8) -> T9, f9: (T9) -> T10, f10: (T10) -> R): R = f10(f9(f8(f7(f6(f5(f4(f3(f2(f1(a))))))))))
inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> pipe(a: T1, f1: (T1) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> T5, f5: (T5) -> T6, f6: (T6) -> T7, f7: (T7) -> T8, f8: (T8) -> T9, f9: (T9) -> T10, f10: (T10) -> T11, f11: (T11) -> R): R = f11(f10(f9(f8(f7(f6(f5(f4(f3(f2(f1(a)))))))))))
inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> pipe(a: T1, f1: (T1) -> T2, f2: (T2) -> T3, f3: (T3) -> T4, f4: (T4) -> T5, f5: (T5) -> T6, f6: (T6) -> T7, f7: (T7) -> T8, f8: (T8) -> T9, f9: (T9) -> T10, f10: (T10) -> T11, f11: (T11) -> T12, f12: (T12) -> R): R = f12(f11(f10(f9(f8(f7(f6(f5(f4(f3(f2(f1(a))))))))))))

operator fun <T> Tuple2<T, *>.component1(): T = t1
operator fun <T> Tuple2<*, T>.component2(): T = t2
operator fun <T> Tuple3<*, *, T>.component3(): T = t3
operator fun <T> Tuple4<*, *, *, T>.component4(): T = t4
operator fun <T> Tuple5<*, *, *, *, T>.component5(): T = t5
operator fun <T> Tuple6<*, *, *, *, *, T>.component6(): T = t6
operator fun <T> Tuple7<*, *, *, *, *, *, T>.component7(): T = t7
operator fun <T> Tuple8<*, *, *, *, *, *, *, T>.component8(): T = t8

operator fun <T> Pair<T, *>.component1(): T = first
operator fun <T> Pair<*, T>.component2(): T = second

operator fun <T> Triple<T, *, *>.component1(): T = first
operator fun <T> Triple<*, T, *>.component2(): T = second
operator fun <T> Triple<*, *, T>.component3(): T = third