package util
 
object PageParseUtil {
  
  def getLinks(url: String, content: String) = {
	 
    def isHtml(url: String) = !(url.endsWith("gif") || url.endsWith("png") || url.endsWith("jpg"))
    
    def getUrlBase(url: String) = {
      val httpLength = 8
      if (url.lastIndexOf("/") < httpLength) {
        url
      } else {
        url.take(url.lastIndexOf("/"))
      }
    }
    def getLinkFullPath(url: String, link: String) = {

      if (link.startsWith("/")) {
        getUrlBase(url) + link
      } else if (link.startsWith("http")) {
        link
      } else {
        getUrlBase(url) + "/" + link
      }
    }

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
    def links = lnkPattern.r.findAllIn(content).foldLeft(List[String]()) {

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
    def imgs = imgPattern.r.findAllIn(content).map { imgTag =>
      val im = "/[^'\"]+".r.findFirstIn(imgTag).getOrElse("")
      if (im.startsWith("//")) {
        "http:" + im
      } else {
        im
      }
    }
    if(isHtml(url)){
    	(links ++ imgs).map(getLinkFullPath(url, _))
    } else {
      Nil
    }
    
  }
 
}