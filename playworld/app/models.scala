package models

import com.twitter.querulous.evaluator.QueryEvaluator

trait DB {
  val queryEvaluator = QueryEvaluator("localhost:3306/pinche", "root", "g00dluck")
}
case class User(id: Int, name: String)
case class Line(id: Int, title: String)
 
object User extends DB{
  val users = queryEvaluator.select("SELECT * FROM user WHERE id IN (?) OR name = ?", List(1, 2, 3), "Jacques") { row =>
    new User(row.getInt("id"), row.getString("name"))
  }
}
object Line extends DB{
  val lines = queryEvaluator.select("select * from line") { row =>
    new Line(row.getInt("id"), row.getString("title"))
  }
} 


