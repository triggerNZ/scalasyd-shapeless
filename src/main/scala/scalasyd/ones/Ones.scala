package scalasyd.popcount

import shapeless._

trait PopCount[T] {
  def popCount(in: T): Long
}

object PopCount {
  def apply[T](t: T)(implicit pc : PopCount[T]): Long = pc.popCount(t)

  implicit val intPC: PopCount[Int] = new PopCount[Int]{
    def popCount(in: Int): Long = {
      (0 until 32).map(pos => (in >> pos) & 0x00000001).sum
    }
  }

  implicit def seqPc[T](implicit tPc: PopCount[T]): PopCount[Seq[T]] = new PopCount[Seq[T]]{
    def popCount(in: Seq[T]): Long = {
      in.map(el => tPc.popCount(el)).sum
    }
  }

  implicit def caseClassPc[T <: Product, HL <: HList]
    (implicit gen: Generic.Aux[T, HL], hlPc: PopCount[HL]): PopCount[T] = new PopCount[T] {
    def popCount(in: T): Long = {
      hlPc.popCount(gen.to(in))
    }
  }

  implicit val hnilPc = new PopCount[HNil] {
    def popCount(in: HNil): Long = 0
  }

  implicit def hconsPc[H, T <: HList](implicit hPc: PopCount[H], tPc: PopCount[T]) = new PopCount[H :: T] {
    def popCount(in: H :: T): Long = {
      val head = in.head
      val tail = in.tail
      hPc.popCount(head) + tPc.popCount(tail)
    }
  }
}
