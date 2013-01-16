package crud2.models

import language.experimental.macros

/**
 * Created with IntelliJ IDEA.
 * User: Orna
 * Date: 13-1-15
 * Time: 下午9:00
 * To change this template use File | Settings | File Templates.
 */
trait CRUD2[T] {
  def get(id: Long)(implicit database: scala.slick.session.Database) = macro crud2.macros.Models.getImpl[Option[T]]
}
