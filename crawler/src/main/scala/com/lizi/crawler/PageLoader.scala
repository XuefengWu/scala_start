package com.lizi.crawler

// Bonus Problem: Make the size follow the links on a given page,
// and load them as well. For example, a sizer for google.com
// would compute the size for Google and all of the pages it
// links to.

import scala.io._
import scala.actors._
import scala.actors.Actor._

// I am singleton object that contains access methods for URLs.
object PageLoader {

  // I get the content at the given URL.
  def getContent(murl: String) = {

      import dispatch._
	  val svc = url(murl)
	  val country = Http(svc OK as.String) 
	  country.apply
 
  }

  // I get teh size of the given URL (the number of characters).
  def getPageSize(url: String) = {

    // Get the page content of the target page.
    var content = getContent(url)

    // Start with the initial page size and then add the size of
    // each linked page to the total.
    getLinks(content).foldLeft(content.size) {

      // Define arguments.
      (pageSize, link) =>

        // Add the size of the linked page content.
        (pageSize + getContent(link).size)

    }

  }

  // I gather the links out of the given content.
  def getLinks(content: String) = {

    // Create a pattern for the link tags. This pattern is using
    // the Verbose tag to allow an easier-to-read regular
    // expression for matching.
    var pattern = """(?xi)
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
    pattern.r.findAllIn(content).foldLeft(List[String]()) {

      // Define arguments.
      (links, linkTag) =>

        // Gather the HTTP value. NOTE: Not only the links will
        // have this, but for our purposes, those are the only
        // ones that we are going to care about.
        //"[http:|/][^'\"]+".r.findFirstIn(linkTag) match {
        "href=\"[^'\"]+".r.findFirstIn(linkTag) match {	
          // If a match was found, add it to the list.
          case Some(link) => links :+ link.replace("href=\"", "")

          // If no match was found, return existing list.
          case None => links

        }

    }

  }

}

// ---------------------------------------------------------- //
// ---------------------------------------------------------- //

object PageLoaderClient extends App {
  // I am the list of URLs that we will be experimenting with.
  val urls = List(
    "http://www.amazon.com",
    "http://www.twitter.com",
    "http://www.google.com",
    "http://www.cnn.com")

  // ---------------------------------------------------------- //
  // ---------------------------------------------------------- //

  // I get the page size in a sequential order. That is, I can't
  // get the data from one URL until the previous URL has finished
  // being calculated.
  def getPageSizeSequentially() = {

    // Loop over each url.
    for (url <- urls) {

      // Print the size of the URL.
      println(
        "Size for " + url + ": " +
          PageLoader.getPageSize(url))

    }

  }

  // I get the page size in a concurrent order. That is, I can gather
  // page sizes for each URL in parallel.
  def getPageSizeConcurrently() = {

    // Get a reference to the current Actor (I think that each
    // program runs in its own Actor or Threat). The "self" reference
    // is a reference to the current actor.
    val caller = self

    // Loop over each url to get its size.
    for (url <- urls) {

      // NOTE: The actor() method is a factor method of the current
      // thread that creates an actor that executes the given
      // closure.... I think.

      // Create an actor that gets the page size.
      actor {
        caller ! (url, PageLoader.getPageSize(url))
      }

    }

    // Now that we have launched a bunch of threads, we need to wait
    // for them to respond. To do this, we are going to use the
    // receive method. This is a *blocking* method. That is, it is
    // going to halt processing of the primary thread until one of
    // the actors returns.
    1.to(urls.size).foreach { i =>

      // The receive() method taks a partial function.
      receive {

        case (url, size) => {
          println("Size for " + url + ": " + size)
        }

      }

    }
  }

  def getLinks(url:String) = {
    PageLoader.getLinks(PageLoader.getContent(url))
  }
  // ---------------------------------------------------------- //
  // ---------------------------------------------------------- //

  // Execute sequential access.
  println("Sequential run:")
  //getPageSizeSequentially()

  println("---------------")

  // Execute concurrent access.
  println("Concurrent run:")
  //getPageSizeConcurrently()
  
  val url = "http://www.cnn.com"
  getLinks(url).foreach(println _)
  
  println("---------------")
  println("Finished:")
  
}