package fpinscala.datastructures

import scala.annotation.tailrec

sealed trait List[+A] // `List` data type, parameterized on a type, `A`
case object Nil extends List[Nothing] // A `List` data constructor representing the empty list
/* Another data constructor, representing nonempty lists. Note that `tail` is another `List[A]`,
which may be `Nil` or another `Cons`.
 */
case class Cons[+A](head: A, tail: List[A]) extends List[A]

object List { // `List` companion object. Contains functions for creating and working with lists.
  def sum(ints: List[Int]): Int = ints match { // A function that uses pattern matching to add up a list of integers
    case Nil => 0 // The sum of the empty list is 0.
    case Cons(x,xs) => x + sum(xs) // The sum of a list starting with `x` is `x` plus the sum of the rest of the list.
  }

  def product(ds: List[Double]): Double = ds match {
    case Nil => 1.0
    case Cons(0.0, _) => 0.0
    case Cons(x,xs) => x * product(xs)
  }

  def apply[A](as: A*): List[A] = // Variadic function syntax
    if (as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))

  val x = List(1,2,3,4,5) match {
    case Cons(x, Cons(2, Cons(4, _))) => x
    case Nil => 42
    case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x + y
    case Cons(h, t) => h + sum(t)
    case _ => 101
  }

  def append[A](a1: List[A], a2: List[A]): List[A] =
    a1 match {
      case Nil => a2
      case Cons(h,t) => Cons(h, append(t, a2))
    }

  def foldRight[A,B](as: List[A], z: B)(f: (A, B) => B): B = // Utility functions
    as match {
      case Nil => z
      case Cons(x, xs) => f(x, foldRight(xs, z)(f))
    }

  def sum2(ns: List[Int]) =
    foldRight(ns, 0)((x,y) => x + y)

  def product2(ns: List[Double]) =
    foldRight(ns, 1.0)(_ * _) // `_ * _` is more concise notation for `(x,y) => x * y`; see sidebar


  def tail[A](l: List[A]): List[A] = l match {
    case Nil => ??? //TODO: error
    case Cons(_, xs) => xs
  }

  def setHead[A](l: List[A], h: A): List[A] = l match {
    case Nil => ??? //TODO: error
    case Cons(_, xs) => Cons(h, xs)
  }

  def drop[A](l: List[A], n: Int): List[A] = if (n <= 0) l else {
    l match {
      case Nil => ??? //TODO: error
      case Cons(_, xs) => drop(xs, n-1)
    }
  }

  def dropWhile[A](l: List[A], f: A => Boolean): List[A] = l match {
      case Cons(x, xs) if f(x) => dropWhile(xs, f)
      case _ => l
    }

  def init[A](l: List[A]): List[A] = l match {
    case Cons(x, Nil) => Nil
    case Cons(x, xs) => Cons(x, init(xs))
    case _ => ??? //TODO: error
  }

  def length[A](l: List[A]): Int = foldRight(l, 0)((a, n) => n + 1)

  @tailrec
  def foldLeft[A,B](l: List[A], z: B)(f: (B, A) => B): B = l match {
    case Nil => z
    case Cons(x, xs) => foldLeft(xs, f(z, x))(f)
  }

  def sum3(ns: List[Int]) = foldLeft(ns, 0)((x,y) => x + y)
  def product3(ns: List[Double]) = foldLeft(ns, 1.0)((x,y) => x * y)
  def length2[A](l: List[A]): Int = foldLeft(l, 0)((n, a) => n + 1)

  def reverse[A](l: List[A]): List[A] = foldRight(l, Nil: List[A])((a, nl) => Cons(a, nl))

  def foldRight2[A,B](as: List[A], z: B)(f: (A, B) => B): B =
    foldLeft(as, identity[B](_))((g, a) => f.curried(a) andThen g) apply z

  def append2[A](a1: List[A], a2: List[A]): List[A] = foldRight2(a1, a2)((a, xs) => Cons(a, xs))

  def flatten[A](l: List[List[A]]): List[A] = foldRight2(l, Nil: List[A])((a,b) => append2(a, b))

  def map[A,B](l: List[A])(f: A => B): List[B] = foldRight2(l, Nil: List[B])((a, xs) => Cons(f(a), xs))
  def flatMap[A,B](as: List[A])(f: A => List[B]): List[B] = foldRight2(as, Nil: List[B])((a, xs) => append2(f(a), xs))

  def filter[A](as: List[A])(f: A => Boolean): List[A] = flatMap(as) {
    case a if f(a) => List(a)
    case _ => Nil
  }

  def zipWith[A, B, C](la: List[A], lb: List[B])(f: (A, B) => C)(a0: A, b0: B): List[C] = (la, lb) match {
    case (Cons(a, ta), Cons(b, tb)) => Cons(f(a, b), zipWith(ta, tb)(f)(a0, b0))
    case (Cons(a, ta), Nil) => Cons(f(a, b0), zipWith(ta, Nil: List[B])(f)(a0, b0))
    case (Nil, Cons(b, tb)) => Cons(f(a0, b), zipWith(Nil: List[A], tb)(f)(a0, b0))
    case _ => Nil
  }

  @tailrec
  def hasSubsequence[A](sup: scala.List[A], sub: scala.List[A]): Boolean = if (sup.isEmpty) false else {
    sup.startsWith(sub) || hasSubsequence(sup.drop(1), sub)
  }
}
