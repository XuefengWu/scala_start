package service

object SimpleInstanceService  extends CSDMService {

    def apply(acion:String,req:String)(implicit fw:java.io.FileWriter) = {
    SoapClient.invoke(base + "services/SimpleInstanceService.SimpleInstanceServiceHttpSoap12Endpoint/","xmlns:ser=\"http://services.RemoteWS.pas.carestreamhealth.com\"",acion,req).getOrElse("")
  }
  
  def getSimpleInstance(instanceInternalID:String)(implicit fw:java.io.FileWriter) = {
    val input = """
      <ser:getSimpleInstance>
         <!--Optional:-->
         <ser:instanceInternalID>%s</ser:instanceInternalID>
      </ser:getSimpleInstance>
      """ format(instanceInternalID)
    apply("getSimpleInstance",input)
  }
  
}