import org.scalatra._
import com.mongodb._
import com.mongodb.casbah.Imports._
import scala.xml._ 
import org.scalatra._ 
import org.scalatra.scalate._
import java.util.UUID
 
class WebApp extends ScalatraServlet  with UrlSupport  with ScalateSupport{
	val mongo = MongoConnection()
	val coll = mongo("blog")("msgs")
	val questions = mongo("exam")("questions")
  object Template {

    def style() = 
      """
      pre { border: 1px solid black; padding: 10px; } 
      body { font-family: Helvetica, sans-serif; } 
      h1 { color: #8b2323 }
      """

    def page(title:String, content:Seq[Node]) = {
      <html>
        <head>
          <title>{ title }</title>
	  <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
          <style>{ Template.style }</style>
        </head>
        <body>
          <h1>{ title }</h1>
          { content }
          <hr/>
          <a href={url("/date/2009/12/26")}>date example</a>
          <a href={url("/form")}>form example</a>
          <a href={url("/upload")}>upload</a>
          <a href={url("/")}>hello world</a>
          <a href={url("/flash-map/form")}>flash scope</a>
          <a href={url("/login")}>login</a>
          <a href={url("/logout")}>logout</a>
          <a href={url("/filter-example")}>filter example</a>
          <a href={url("/cookies-example")}>cookies example</a>
          <a href={url("/chat")}>chat demo</a>
	  <a href={url("/scalate")}>scalate</a>
	  <a href={url("/exam")}>exam</a>	 
        </body>
      </html>
    }
  }
  before {
    contentType = "text/html"
  }
  
	post("/exam") {
	  val newObj = MongoDBObject("subject" -> params("subject"),
                           "o1" -> params("o1"),
                           "o2" -> params("o2"),
                           "o3" -> params("o3"),
                           "o4" -> params("o4"),				     
			   "answer" ->  params("answer"),
			   "id" -> UUID.randomUUID().toString())
		questions += newObj
		redirect("/exam")
	}

  get("/exam"){
     renderTemplate("exam.ssp",("title" -> "Hello Scalatra"),("questions" -> questions))    
  }

  post("/answer"){
    //find answer by id and check with selected value
    val q = MongoDBObject("id" -> params("id"))
    val result = questions.findOne(q) match {
	case Some(v) => v.getOrElse("answer","???")
        case None => "N"
    }
    result
  }
  
  get("/") {
    Template.page("Scalatra: Hello World",
    <h2>Hello world!</h2>
    <p>Referer: { (request referer) map { Text(_) } getOrElse { <i>none</i> }}</p>
    <pre>Route: /</pre>
    )
  }
  
 get("/date/:year/:month/:day") {
    Template.page("Scalatra: Date Example", 
    <ul>
      <li>Year: {params("year")}</li>
      <li>Month: {params("month")}</li>
      <li>Day: {params("day")}</li>
    </ul>
    <pre>Route: /date/:year/:month/:day</pre>
    )
  }
	get("/hello/:name") {
		<html><head><title>Hello!</title></head><body><h1>Hello {params("name")}!</h1></body></html>
	}
	get("/msgs") {
		<body>
		<form method="POST" action="/msgs">
		Author: <input type="text" name="author"/>
		 
		Message: <input type="text" name="msg"/>
		 
		<input type="submit"/>
		</form>
		
		<ul>
		{for (l <- coll) yield <li>          
		 From: {l.getOrElse("author", "???")} -                           
		 {l.getOrElse("msg", "???")}</li>}                       
		 </ul>		 
		</body>
	}
	post("/msgs") {
		val builder = MongoDBObject.newBuilder
		builder += "author" -> params("author")
		builder += "msg" -> params("msg")
		 
		coll += builder.result.asDBObject
		redirect("/msgs")
	}
	get("/scalate") {
		val content = "this is some fake content for the web page.yade yade"
		  renderTemplate("index.scaml",("content"-> content))
	}
  
  get("/chat") {
    renderTemplate("chat.ssp")
  }
   
	protected def contextPath = request.getContextPath
}
