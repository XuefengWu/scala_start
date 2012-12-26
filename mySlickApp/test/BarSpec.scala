package test

import scala.slick.driver.H2Driver.simple._
import models.{Bar, Bars}
import org.specs2.mutable.Specification
import play.api.test.{FakeRequest, FakeApplication}
import play.api.test.Helpers._
import org.junit.{After, Before}

// Use the implicit threadLocalSession

import Database.threadLocalSession

/**
 * Created with IntelliJ IDEA.
 * User: Orna
 * Date: 12-12-26
 * Time: 下午8:41
 * To change this template use File | Settings | File Templates.
 */
class BarSpec extends Specification {


  "A Bar" should {

    "be creatable" in {
      Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver") withSession {
        Bars.ddl.create
        Bars.insert(Bar(None, "foo"))
        val b = for (b <- Bars) yield b
        b.first.id.get === 1
        Bars.ddl.drop
      }
    }

    "be queryable" in {
      Database.forURL("jdbc:h2:mem:test2", driver = "org.h2.Driver") withSession {
        Bars.ddl.create
        Bars.insert(Bar(None, "foo"))
        Bars.insert(Bar(None, "boo"))
        val b = for (b <- Bars if b.name === "foo") yield b

        b.first.id.get === 1
        b.first.name === "foo"
        Bars.ddl.drop
      }
    }

    "be updateable" in {
      Database.forURL("jdbc:h2:mem:test3", driver = "org.h2.Driver") withSession {

        Bars.ddl.create
        Bars.insert(Bar(None, "foo"))
        val b = for (b <- Bars) yield b
        val b1 = b.first
        b1.id.get === 1
        val nQuery = Bars.filter(b => b.id === 1).map(_.name)
        nQuery.update("baa")

        val b2 = (for (b <- Bars) yield b).first
        b2.id.get === 1
        b2.name === "baa"
        Bars.ddl.drop
      }
    }


  }
}
