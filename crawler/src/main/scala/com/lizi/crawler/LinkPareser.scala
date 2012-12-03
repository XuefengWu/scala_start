package com.lizi.crawler

object LinkPareser {

  // I gather the links out of the given content.
  def getLinks(content: String) = {

    // Create a pattern for the link tags. This pattern is using
    // the Verbose tag to allow an easier-to-read regular
    // expression for matching.
    val lnkPattern = """(?xi)
			< a
			(
				\s+
				\w+
				\s*=\s*
				(
					"[^"]*"
					|
					'[^']*'
				)
			)+
			\s*
			>
		"""

    // Gather all occurences of the pattern in the content.
    val links = lnkPattern.r.findAllIn(content).foldLeft(List[String]()) {

      // Define arguments.
      (lns, linkTag) =>

        // Gather the HTTP value. NOTE: Not only the links will
        // have this, but for our purposes, those are the only
        // ones that we are going to care about.
        //"[http:|/][^'\"]+".r.findFirstIn(linkTag) match {
        "href=\"[^'\"]+".r.findFirstIn(linkTag) match {
          // If a match was found, add it to the list.
          case Some(link) => lns :+ link.replace("href=\"", "")

          // If no match was found, return existing list.
          case None => lns

        }

    }

    val imgPattern = """(?xi)
			< img
			(
				\s+
				\w+
				\s*=\s*
				(
					"[^"]*"
					|
					'[^']*'
				)
			)+
			\s*
			/?>
		"""
    val imgs = imgPattern.r.findAllIn(content).map { imgTag =>
      val im = "/[^'\"]+".r.findFirstIn(imgTag).get
      im
      if (im.startsWith("//")) {
        "http:" + im
      } else {
        im
      }
    }

    links ++ imgs
  }

  def getAbstracLinkInSite(ln: String, fromUrl: String, base: String) =
    if (!ln.contains("http")) {
      if (ln.startsWith("/")) {
        base + ln
      } else {
        if (base == fromUrl) {
          base + "/" + ln
        } else {
          fromUrl.dropRight(fromUrl.length - fromUrl.lastIndexOf("/") - 1) + ln
        }
      }

    } else {
      ln
    }

}

object LinkPareserTest extends App {
  val cnt = scala.io.Source.fromFile("/site/www.tutorialspoint.com/developers_best_practices/index.htm").mkString
  LinkPareser.getLinks(cnt).foreach(println _)
}