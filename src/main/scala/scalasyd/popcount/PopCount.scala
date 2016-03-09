package scalasyd.popcount

import shapeless._

trait PopCount[T] {
  def count(t: T): Long
}

object PopCount {
  implicit val intPc = new PopCount[Int] {
    def count(t: Int): Long = {
      (0 until 32).map(offset => (t >> offset) & 1).sum
    }
  }

  implicit val charPc = new PopCount[Char] {
    def count(t: Char): Long = {
      (0 until 16).map(offset => (t >> offset) & 1).sum
    }
  }

  implicit def seqPc[T, S <% Seq[T]](implicit tpc: PopCount[T]) = new PopCount[S] {
    def count(s: S): Long = {
      s.map(item => tpc.count(item)).sum
    }
  }

  implicit def caseClassPc[CC <: Product, HL <: HList]
    (implicit gen: Generic.Aux[CC, HL],
      hlPc: PopCount[HL]) = new PopCount[CC] {
    def count(cc: CC): Long = {
      val hl: HL = gen.to(cc)
      hlPc.count(hl)
    }
  }

  implicit val hnilPc: PopCount[HNil] = new PopCount[HNil] {
    def count(hn: HNil) = 0
  }

  implicit def hconsPc[H, T <: HList](implicit hPc: PopCount[H], tpc: PopCount[T]):
    PopCount[H :: T] = new PopCount[H :: T] {
    def count(hl: H :: T) = hPc.count(hl.head) + tpc.count(hl.tail)
  }

  def apply[T](t: T)(implicit tPc: PopCount[T]) = tPc.count(t)
}
