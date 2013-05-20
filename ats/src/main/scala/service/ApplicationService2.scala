package service

object ApplicationService2 extends CSDMService {
  
  def apply(action: String, req: String)(implicit fw: java.io.FileWriter) = {
    SoapClient.invoke(base + "services/csdm/ApplicationService.csdm/ApplicationServiceHttpSoap12Endpoint/", "xmlns:ser=\"http://service.LocalWS.csdm.carestreamhealth.com\"", action, req).getOrElse("")
  }

   
  def openObjects(instances: Seq[String],app: String = "")(implicit fw: java.io.FileWriter) = {
    val param = """
      <ser:openObjects>
         <!--Optional:-->
         <ser:objectInternalIDs>
        <![CDATA[
         <?xml version="1.0" encoding="UTF-8"?>
            <tns:appOpenObjectsRequest xmlns:tns="http://www.carestreamhealth.com/CSI/CSDM/1/Schema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.carestreamhealth.com/CSI/CSDM/1/Schema ApplicationOpenObjectsRequest.xsd ">
             <tns:application>%s</tns:application>
              <tns:instanceIDList>
                %s
              </tns:instanceIDList>
            </tns:appOpenObjectsRequest>
         ]]>
         </ser:objectInternalIDs>
      </ser:openObjects>
      """ format (app, instances.map("<tns:instanceID>"+_+"</tns:instanceID>").mkString("\n"))
    apply("openObjects", param)
  }
 
}