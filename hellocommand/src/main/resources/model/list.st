/**
* Return a page of (book,author).
*
* @param page Page to display
* @param pageSize Number of books per page
* @param orderBy Computer property used for sorting
* @param filter Filter applied on the name column
*/
def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[(Book, Option[User])] = {

val offest = pageSize * page

DB.withConnection { implicit connection =>

  val books = SQL(
    """
      select * from book
      left join user on book.author_id = user.id
      where book.title like {filter}
      order by {orderBy} nulls last
      limit {pageSize} offset {offset}
    """
  ).on(
    'pageSize -> pageSize,
    'offset -> offest,
    'filter -> filter,
    'orderBy -> orderBy
  ).as(Book.withReference *)

  val totalRows = SQL(
    """
      select count(*) from book
      left join user on book.author_id = user.id
      where  book.title like {filter}
    """
  ).on(
    'filter -> filter
  ).as(scalar[Long].single)

  Page(books, page, offest, totalRows)

}

}