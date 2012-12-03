/**
* Return a page of <caseClassWithReference>.
*
* @param page Page to display
* @param pageSize Number of books per page
* @param orderBy used for sorting
* @param filter Filter applied on the name column
*/
def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[<caseClassWithReference>] = {

val offest = pageSize * page

DB.withConnection { implicit connection =>

  val <m>s = SQL(
    """
      select * from <m>
      <join>
      where <filterCondition>
      order by {orderBy} nulls last
      limit {pageSize} offset {offset}
    """
  ).on(
    'pageSize -> pageSize,
    'offset -> offest,
    'filter -> filter,
    'orderBy -> orderBy
  ).as(<MM>.<sqlParser> *)

  val totalRows = SQL(
    """
      select count(*) from <m>
      <join>
      where  <filterCondition>
    """
  ).on(
    'filter -> filter
  ).as(scalar[Long].single)

  Page(<m>s, page, offest, totalRows)

}

}