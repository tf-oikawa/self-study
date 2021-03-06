package fpinscala.datastructures

sealed trait Tree[+A]
case class Leaf[A](value: A) extends Tree[A]
case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]


object Tree {

  def size[A](t: Tree[A]): Int = t match {
    case Branch(l, r) => 1 + size(l) + size(r)
    case _ => 1
  }

  def maximum(t: Tree[Int]): Int = t match {
    case Branch(l, r) => maximum(l) max maximum(r)
    case Leaf(v) => v
  }

  def depth[A](t: Tree[A]): Int = t match {
    case Branch(l, r) => 1 + { depth(l) max depth(r) }
    case Leaf(_) => 0
  }

  def map[A,B](t: Tree[A])(f: A => B): Tree[B] = t match {
    case Leaf(a) => Leaf(f(a))
    case Branch(l, r) => Branch(map(l)(f), map(r)(f))
  }

  def fold[A,B](t: Tree[A])(fa: (A) => B)(fb: (B, B) => B): B = t match {
    case Leaf(a) => fa(a)
    case Branch(l, r) => fb(fold(l)(fa)(fb), fold(r)(fa)(fb))
  }

  def size2[A](t: Tree[A]): Int = fold(t)(_ => 1)(_ + 1 + _)
  def maximum2(t: Tree[Int]): Int = fold(t)(identity)(_ max _)
  def depth2[A](t: Tree[A]): Int = fold(t)(_ => 0)(_ max _)

  def map2[A,B](t: Tree[A])(f: A => B): Tree[B] = fold(t)(a => Leaf(f(a)): Tree[B])(Branch(_, _))
}
