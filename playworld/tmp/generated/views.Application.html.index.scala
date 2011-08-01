
                    package views.Application.html

                    import play.templates._
                    import play.templates.TemplateMagic._
                    import views.html._

                    object index extends BaseScalaTemplate[Html,Format[Html]](HtmlFormat) {

                        def apply/*1.2*/(title:String,users:Seq[models.User]):Html = {
                            try {
                                _display_ {

format.raw/*1.39*/("""

""")+_display_(/*3.2*/main(title)/*3.13*/ {format.raw/*3.15*/("""
    
    """)+_display_(/*5.6*/title)+format.raw/*5.11*/("""
    """)+_display_(/*6.6*/if(users)/*6.15*/ {format.raw/*6.17*/("""
     
    <h2>Here is a list of your current users:Seq:</h2>
     
    <ul>
    """)+_display_(/*11.6*/users/*11.11*/.map/*11.15*/ { user =>format.raw/*11.25*/("""
        <li>""")+_display_(/*12.14*/user/*12.18*/.name)+format.raw/*12.23*/("""</li>
    """)})+format.raw/*13.6*/("""
    </ul>
     
	""")}/*16.4*/else/*16.9*/{format.raw/*16.10*/("""
	     
	    <h2>You don't have any user yet...</h2>
	     
	""")})+format.raw/*20.3*/("""
""")})}
                            } catch {
                                case e:TemplateExecutionError => throw e
                                case e => throw Reporter.toHumanException(e)
                            }
                        }

                    }

                
                /*
                    -- GENERATED --
                    DATE: Mon Aug 01 22:45:57 CST 2011
                    SOURCE: /app/views/Application/index.scala.html
                    HASH: e32a87a11ad81d8e3399b2b6fd0bf5132e60ace4
                    MATRIX: 329->1|473->38|501->41|520->52|540->54|576->65|601->70|632->76|649->85|669->87|777->169|791->174|804->178|833->188|874->202|887->206|913->211|952->222|987->242|999->247|1019->248|1109->310
                    LINES: 10->1|14->1|16->3|16->3|16->3|18->5|18->5|19->6|19->6|19->6|24->11|24->11|24->11|24->11|25->12|25->12|25->12|26->13|29->16|29->16|29->16|33->20
                    -- GENERATED --
                */
            
