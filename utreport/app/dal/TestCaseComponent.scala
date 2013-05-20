package dal

import models._
import scala.slick.lifted.Query

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession

trait TestCaseComponent {
  def find(id: Long): Query[TestCases.type, TestCase]
  
  def findall(reportId:Long): Query[TestCases.type,TestCase] 

  def list(reportId:Long,page: Int, pageSize: Int, orderBy: Int): Query[TestCases.type,TestCase]

}

class TestCaseComponentImpl extends TestCaseComponent {

  def find(id: Long): Query[TestCases.type, TestCase] = {
    TestCases.find(id)
  }
 
  def findall(reportId:Long): Query[TestCases.type,TestCase] = {
    TestCases.findAll(reportId)
  }

  def list(reportId:Long,page: Int, pageSize: Int, orderBy: Int): Query[TestCases.type,TestCase] = {
    TestCases.list(reportId,page, pageSize, orderBy)
  }

 
}
