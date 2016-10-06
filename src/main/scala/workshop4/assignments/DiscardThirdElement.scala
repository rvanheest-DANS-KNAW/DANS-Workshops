package workshop4.assignments

import rx.lang.scala.Observable

import scala.concurrent.duration.DurationInt
import scala.io.StdIn
import scala.language.postfixOps

trait DiscardThirdElement {

  def discardThirdBuffer[T](obs: Observable[T]): Observable[T] = ???

  def discardThirdWindow[T](obs: Observable[T]): Observable[T] = ???
}

object DiscardThirdElementWithBuffer extends App with DiscardThirdElement {

  println("discard every third element using buffer")

  discardThirdBuffer(Observable.from(1 to 20))
    .subscribe(println(_), e => e.printStackTrace(), () => println("done"))
}

object DiscardThirdElementWithBufferAndInterval extends App with DiscardThirdElement {

  println("discard every third element using buffer")

  discardThirdBuffer(Observable.interval(1 second).take(20))
    .subscribe(println(_), e => e.printStackTrace(), () => { println("done"); System.exit(0) })

  StdIn.readLine()
}

object DiscardThirdElementWithWindow extends App with DiscardThirdElement {

  println("discard every third element using window")

  discardThirdWindow(Observable.from(1 to 20))
    .subscribe(println(_), e => e.printStackTrace(), () => println("done"))
}

object DiscardThirdElementWithWindowAndInterval extends App with DiscardThirdElement {

  println("discard every third element using window")

  discardThirdWindow(Observable.interval(1 second).take(20))
    .subscribe(println(_), e => e.printStackTrace(), () => { println("done"); System.exit(0) })

  StdIn.readLine()
}
