package scalasyd.ones

import shapeless._

trait Ones[T] {
  def ones(t: T): Int
}

object Ones {
  def apply[T](t: T)(implicit o: Ones[T]): Int = o.ones(t)

  implicit val onesChar = new Ones[Char] {
    def ones(n: Char): Int = {
      (0 until 16).map {bit => (n >> bit & 0x0001)}.sum
    }
  }

  implicit val onesInt = new Ones[Int] {
    def ones(n: Int): Int = {
      (0 until 32).map {bit => (n >> bit & 0x00000001)}.sum
    }
  }

  implicit val onesLong = new Ones[Long] {
    def ones(n: Long): Int = {
      (0 until 64).map {bit => (n >> bit & 0x0000000000000001)}.sum.toInt
    }
  }

  implicit def onesList[T](implicit onesT: Ones[T]) = new Ones[List[T]] {
    def ones(s: List[T]) = s.map(t => onesT.ones(t)).sum
  }

  implicit def onesString[T] = new Ones[String] {
    def ones(s: String) = s.map(t => onesChar.ones(t)).sum
  }

  //TODO: Work out how to generically handle all sequences.

  implicit def onesProduct[P <: Product, HL <: HList](implicit genP: Generic.Aux[P, HL], genHl: Ones[HL]) = new Ones[P] {
    def ones(p: P) = genHl.ones(genP.to(p))
  }

  implicit val onesHNil = new Ones[HNil] {
    def ones(hn: HNil) = 0
  }

  implicit def onesHcons[H, T <: HList](implicit onesH: Ones[H], onesT: Ones[T]) = new Ones[H :: T] {
    def ones(hl: H :: T) = onesH.ones(hl.head) + onesT.ones(hl.tail)
  }
}

object Demo {
  case class Person(name: String, height: Int,  age: Int)

  Ones('A')
  Ones(1)
  Ones(5000L)
  Ones(List(1,2,3))
  Ones("HAPPY")
  Ones((1,2, "Hello"))
  Ones(Person("Tin", 180, 16))
}
