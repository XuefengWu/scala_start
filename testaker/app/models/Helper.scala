package models

/**
 * Created by IntelliJ IDEA.
 * User: 19002850
 * Date: 12-4-11
 * Time: 下午1:41
 * To change this template use File | Settings | File Templates.
 */

/**
 * Helper for pagination.
 */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

package util {

object TimeStampColumn {
  import anorm._
  import java.sql.Timestamp

    implicit def rowToTimestamp: anorm.Column[Timestamp] = Column.nonNull { (value, meta) =>
      val MetaDataItem(qualified, nullable, clazz) = meta
      value match {
        case timestamp: Timestamp => Right(timestamp)
        case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass + " java.sql.Timestamp for column " + qualified))
      }
    }

}
}
