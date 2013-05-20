package test

import scala.slick.driver.H2Driver.simple._
import models.{Company, Companies}
import org.specs2.mutable.Specification
import play.api.test.{FakeRequest, FakeApplication}
import play.api.test.Helpers._
import org.junit.{After, Before}
import play.api.db.DB

// Use the implicit threadLocalSession

import Database.threadLocalSession

/**
 * Created with IntelliJ IDEA.
 * User: Orna
 * Date: 12-12-26
 * Time: 下午8:41
 * To change this template use File | Settings | File Templates.
 */
class CompanySpec extends Specification {


  "A Bar" should {

    "be creatable" in {
      Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver") withSession {
        Companies.ddl.create
        Companies.insert(Company(None, "foo"))
        val b = for (b <- Companies) yield b
        b.first.id.get === 1
        Companies.ddl.drop
      }
    }

    "be queryable" in {
      Database.forURL("jdbc:h2:mem:test2", driver = "org.h2.Driver") withSession {
        Companies.ddl.create
        Companies.insert(Company(None, "foo"))
        Companies.insert(Company(None, "boo"))
        val b = for (b <- Companies if b.name === "foo") yield b

        b.first.id.get === 1
        b.first.name === "foo"
        Companies.ddl.drop
      }
    }

    "be updateable" in {
      Database.forURL("jdbc:h2:mem:test3", driver = "org.h2.Driver") withSession {

        Companies.ddl.create
        Companies.insert(Company(None, "foo"))
        val b = for (b <- Companies) yield b
        val b1 = b.first
        b1.id.get === 1
        val nQuery = Companies.filter(b => b.id === 1L).map(_.name)
        nQuery.update("baa")

        val b2 = (for (b <- Companies) yield b).first
        b2.id.get === 1
        b2.name === "baa"
        Companies.ddl.drop
      }
    }

    "be getable" in {
      implicit lazy val database = Database.forURL("jdbc:h2:mem:test3", driver = "org.h2.Driver")

      Database.forURL("jdbc:h2:mem:test3", driver = "org.h2.Driver") withSession {


        Companies.ddl.create
        Companies.insert(Company(None, "foo"))
        val b = for (b <- Companies) yield b
        val b1Opt = Companies.get(1L)
        b1Opt must beSome
        val b1 = b1Opt.get
        b1.id.get === 1
        val nQuery = Companies.filter(b => b.id === 1L).map(_.name)
        nQuery.update("baa")

        val b2 = (for (b <- Companies) yield b).first
        b2.id.get === 1
        b2.name === "baa"
        Companies.ddl.drop
      }
    }

  }
}
