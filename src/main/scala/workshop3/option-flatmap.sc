/*
  Consider the following class hierarchy and function (we leave out the implementations of the
  methods for now). Here the methods 'getBar' and 'getBaz' can potentially return a null and
  consequentially 'compute' can return a null too.
 */
object stage0 {

  class Foo {
    def getBar: Bar = ???
  }
  class Bar {
    def getBaz: Baz = ???
  }
  class Baz {
    def getText: String = ???
  }

  def compute(foo: Foo): String = {
    val bar = foo.getBar
    if (bar != null) {
      val baz = bar.getBaz
      if (baz != null) {
        baz.getText
      }
      else null
    }
    else null
  }
}

/*
  Just as before we can rewrite this such that 'getBar' and 'getBaz' return an Option instead.
 */
object stage1 {

  class Foo {
    def getBar: Option[Bar] = ???
  }
  class Bar {
    def getBaz: Option[Baz] = ???
  }
  class Baz {
    def getText: String = ???
  }

  /*
    To use these new methods in 'compute' we will first start by defining some helper functions for
    a better understanding of what is going on. First we define 'computeBaz' which given a Baz
    returns the String from 'getText'.
   */
  def computeBaz(baz: Baz): String = {
    baz.getText
  }

  /*
    Next, to compute the String from a Bar, we can use the 'computeBaz' we just defined. However,
    the Baz we need as input for this function is wrapped in an Option (see 'Bar.getBaz'). We can use
    'map' on this Option and in that way get the String (also wrapped in an Option, of course).
   */
  def computeBar(bar: Bar): Option[String] = {
    bar.getBaz.map(baz => computeBaz(baz))
  }

  /*
    If we want to get the String from a Foo, we can try to apply the same trick as we did in
    'computeBar'. What we end up with, however, is not what we wanted: Option[Option[String]. Now we
    got two Options nested before we have the String. Both Options denote the fact that the value
    inside could potentially be a null. Of course we can do with only one Option that denotes this and
    therefore we can flatten the Option[Option[String]] into an Option[String] by using 'flatMap'
    instead of 'map'. This operator first maps a value inside an Option to another Option (this
    results in an Option[Option[T]]) and then flattens it to end up with an Option[T].
   */
  def computeFooWRONG(foo: Foo): Option[Option[String]] = {
    foo.getBar.map(bar => computeBar(bar))
  }
  def computeFoo(foo: Foo): Option[String] = {
    foo.getBar.flatMap(bar => computeBar(bar))
  }

  /*
    If we now substitute all the peaces together, we get a series of nested higher order functions.
    Verify for yourself that this is correct!
    This code becomes very hard to read quickly, especially with multiple flatMaps, as shown below!
   */
  def compute(foo: Foo): Option[String] = {
    foo.getBar
      .flatMap(bar => bar.getBaz
        .map(baz => baz.getText))
  }
  def compute(fooOpt: Option[Foo]): Option[String] = {
    fooOpt
      .flatMap(foo => foo.getBar
        .flatMap(bar => bar.getBaz
          .map(baz => baz.getText)))
  }

  /*
    One way to make this a little more readable is to chain the operators rather than nest them.
    Note that with this you cannot access the 'foo' or 'bar' anymore inside the '.map(baz => ...)'.
    It is therefore dependent upon the situation whether you chain or nest the operators!
   */
  def computeChained(fooOpt: Option[Foo]): Option[String] = {
    fooOpt
      .flatMap(foo => foo.getBar)
      .flatMap(bar => bar.getBaz)
      .map(baz => baz.getText)
  }

  /*
    A second (and definitely better) way to make this more readable is to rewrite the expression
    to a for-comprehension. You read these lines as:
      foo <- fooOpt         "foo drawn from fooOpt"
      bar <- foo.getBar     "bar drawn from foo.getBar"
      etc.
   */
  def computeWithForComprehension(fooOpt: Option[Foo]): Option[String] = {
    for {
      foo <- fooOpt
      bar <- foo.getBar
      baz <- bar.getBaz
    } yield baz.getText
  }

  /*
    In all three implementations it holds that if 'fooOpt' is null, the computation will terminate
    immediately by returning a None (Option.empty). The same holds when 'bar' or 'baz' is null:
    all following operations will be discarded and a None will be returned.
   */
}