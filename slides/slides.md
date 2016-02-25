
class: middle, center
### Once upon a time, in a galaxy far away...

---
class: middle, center
![Shapeless](./logo.png)

## Dark Side Scala Hacking

---
class: middle
# Who am I

- Tin Pavlinic
- github/twitter: @triggerNZ
- Simple Machines
- Commonwealth Bank

---
class: middle, center
# Who am I

![Ventress](./Ventress-small.jpg)

---
class: middle, center

# The Jedi Way

![Jedi](./jedi.png)

---
class: middle

# The Jedi Way

- Do functional programming
- Write general reusable code
- Don't repeat yourself

## BUT

- Be cautious with fancy tricks
- Use advanced language features sparingly
- Write your code in a way that your colleagues can understand it
- Focus on the business problem at hand and fear the general solution, since writing it is risky

---
class: middle, center
# The Sith way

![Vader](./vader-small.jpg)
---

class: middle

# The Sith Way

- Embrace power
- No empathy
- No restrictions

---
class: middle, center

# Type level programming - Dark magic

![Unlimited power](./power.jpg)

---

class: middle

# Type level programming - Dark magic

- Generic code
- Like reflection, but at compile time
- Like macros, but composable
- Keep type safety

---
class: middle, center

# Who is using it ?

![storm troopers](./troopers-small.jpg)

---
class: middle

# Who is using it ?

- JSON libraries (argonaut-shapeless, circe)
- Database interaction libraries (doobie)
- Serialization libraries (scodec)
- Libraries including internal DSL's (coppersmith)

---
class: middle, center

# The tools

![lightsabers](./sabers-small.jpg)

---
class: middle

# The tools

- Type level data structures
  - HLists
  - Coproducts
  - Records
- Typeclasses
- Recursion on types
- Type inference
- Implicit resolution (including dark arcane rules about resolution priority)
- Macros

---
class: middle

# HLists

```scala
sealed trait HList extends Product with Serializable

final case class ::[+H, +T <: HList](head : H, tail : T) extends HList

sealed trait HNil extends HList {
  def ::[H](h : H) = shapeless.::(h, this)
}

case object HNil extends HNil

//Usage
val x : Int :: String :: (String, String, Int) :: HNil =
  1 :: "Hello" :: ("A", "tuple", 1) :: HNil

```

---
class: middle

# Typeclasses

- A trait with one or more type parameters
- Implicit instances of the trait for different types
- Methods using it usually have implicit parameters with the typeclass

---
class: middle

# Typeclasses

```scala
object DarthVader
object Chewbacca

trait MidichorianCount[T] {
  def count(in: T): Long
}

implicit object dvMCount extends MidichorianCount[DarthVader.type] {
  def count(in: DarthVader.type) = 99999
}
implicit object cbMCount extends MidichorianCount[Chewbacca.type] {
  def count(in: Chewbacca.type) = 0
}
def count[T](t: T)(implicit mc: MidichorianCount[T]) = mc.count(t)


scala> count(DarthVader)
res1: Long = 99999
```

---
class: middle

# Typeclasses as type-level functions

```scala
trait Toggle[In]{
  type Out

  def apply(in: In): Out
}

implicit object toggle1 extends Toggle[Int] {
  type Out = String
  def apply(in: Int) = in.toString
}

implicit object toggle2 extends Toggle[String] {
  type Out = Int
  def apply(in: String) = in.toString
}

def toggle[T](t: T)(implicit tgl: Toggle[T]): tgl.Out = tgl(t)
```

---
class: middle

# Recursion on types

```scala
implicit def optionMCount[T](implicit mt: MidichorianCount[T]):
 MidichorianCount[Option[T]] =
   new MidichorianCount[Option[T]] {
     def count(in: Option[T]) = in match {
       case Some(t) => mt.count(t)
       case None => 0
     }
   }
   val x: Option[DarthVader.type] = None

   scala> count(x)
   res5: Long = 0
```

---
class: middle

# Type inference

- There are weird rules
- Interitance hurts
- There are workarounds
- The option code above has inference bugs

---
# Type inference - Aux
class: middle

```scala
def toggleTwice[T](in: T)
  (tgl1: Toggle[T], tgl2: Toggle[t1.Out]): tgl2.Out =
    tgl2(tgl1(t))
```
- DOESNT COMPILE!!!

---
class: middle

# Type inference - Aux

```scala
object Toggle {
  type Aux[In, Out0] = Toggle[In] {type Out = Out0}
}

def toggleTwice[T, O1, O2](in: T)
  (tgl1: Toggle.Aux[T, O1], tgl2: Toggle.Aux[O1, O2]): O2 =
    tgl2(tgl1(t))
```
---

# Implicit resolution
class: middle

- Goes hand in hand with the recursion above
- Most of the time is obvious but sometimes behaves strangely

---
class: middle

# Macros
- Some typeclasses still need macros for computing generically
- Shapeless provides these for you, and they should be composable
using other techniques
- You shouldn't have to write them yourself
- Sometimes you do
- A cool one is `Generic`, which goes from case classes to HLists, and back

---
class: middle, center
LIVE CODING DEMO
================

![darth maul](./maul.jpg)