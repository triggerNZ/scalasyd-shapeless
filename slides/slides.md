
class: middle, center
### A long time ago in a galaxy far, far away....

---
class: middle, center
![Shapeless](./logo.png)

## Dark Side Scala Hacking

---
class: middle
# Who am I

- Tin Pavlinic
- github/twitter: @triggerNZ
- Keen Scala hacker
- Keen shapeless user
- Small-time shapeless contributor
---

class: middle, center

# Who am I

![Maul](./darthmaul-small.jpg)

### Probably not...

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
- Repeat yourself, rather than embracing the type level darkness

---
class: middle, center
# The Sith way

![Vader](./vader-small.jpg)
---

class: middle

# The Sith Way

- Embrace power
- No empathy
- Use the full language
- The end goal of generic code and not repeating yourself justifies the means

---
class: middle, center

# Dark magic

![Unlimited power](./power.jpg)

---

class: middle

# Dark magic
## Type level programming

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
- Libraries including internal DSLs

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
- Type classes
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

# Type classes

- A trait with one or more type parameters
- Implicit instances of the trait for different types
- Methods using it usually have implicit parameters with the type class

---
class: middle

# Type classes

```scala
object DarthVader
object Chewbacca

trait MidichlorianCount[T] {
  def count(in: T): Long
}

implicit object dvMCount extends MidichlorianCount[DarthVader.type] {
  def count(in: DarthVader.type) = 99999
}
implicit object cbMCount extends MidichlorianCount[Chewbacca.type] {
  def count(in: Chewbacca.type) = 0
}
def count[T](t: T)(implicit mc: MidichlorianCount[T]) = mc.count(t)


scala> count(DarthVader)
res1: Long = 99999
```

---
class: middle

# Type classes
## As type-level functions

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
implicit def optionMCount[T](implicit mt: MidichlorianCount[T]):
 MidichlorianCount[Option[T]] =
   new MidichlorianCount[Option[T]] {
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
- Inheritance hurts
- There are workarounds
- The option code above has inference bugs

---

class: middle
# Aux

```scala
def toggleTwice[T](in: T)
  (implicit tgl1: Toggle[T], tgl2: Toggle[tgl1.Out]): tgl2.Out =
    tgl2(tgl1(t))
```
- DOESNT COMPILE!!!

---
class: middle

# Aux

```scala
object Toggle {
  type Aux[In, Out0] = Toggle[In] {type Out = Out0}
}

def toggleTwice[T, O1, O2](in: T)
  (implicit tgl1: Toggle.Aux[T, O1], tgl2: Toggle.Aux[O1, O2]): O2 =
    tgl2(tgl1(t))
```
---
class: middle
# Implicit resolution.


- Goes hand in hand with the recursion above
- Most of the time is obvious but sometimes behaves strangely
- Order matters. Use trait inheritance to prioritise. Subtraits have higher
priority than supertraits.

---
class: middle
# Implicit resolution.

```scala
trait LowPriority {
  def genericToggle[T]: Toggle[T] = ???
}

object HigherPriority extends LowPriority {
  def intToggle: Toggle[Int] = ???
}
```
---
class: middle

# Macros
- Some type classes still need macros for computing generically
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
