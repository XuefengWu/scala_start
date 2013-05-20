package service

object FMSService extends CSDMService {

  def apply(acion: String, req: String)(implicit fw:java.io.FileWriter) = {
    SoapClient.invoke(base + "services/FMSService.FMSServiceHttpSoap12Endpoint/", "xmlns:ser=\"http://services.RemoteWS.pas.carestreamhealth.com\"", acion, req).getOrElse("")
  }

  def createFMS(fmsInfo: String)(implicit fw:java.io.FileWriter) = {
    val input = """
      <ser:createFMS>
         <!--Optional:-->
         <ser:fmsInfo><![CDATA[
		%s
		]]>
		</ser:fmsInfo>
	      </ser:createFMS>
      """ format (fmsInfo)
    apply("createFMS", input)
  }

  def listFMSPresentationState(fmsInternalID: String)(implicit fw:java.io.FileWriter) = {
    val input = """
      <ser:listFMSPresentationState>
         <!--Optional:-->
         <ser:fmsInternalID>%s</ser:fmsInternalID>
      </ser:listFMSPresentationState>
      """ format (fmsInternalID)
    apply("listFMSPresentationState", input)
  }

  def getFMSInfo(fmsInternalID: String)(implicit fw:java.io.FileWriter) = {
    val input = """
      <ser:getFMSInfo>
         <!--Optional:-->
         <ser:fmsInternalID>%s</ser:fmsInternalID>
      </ser:getFMSInfo>
      """ format (fmsInternalID)
    apply("getFMSInfo", input)
  }
  
  def setFMSInfo(fmsInfo: String,fmsInternalID:String)(implicit fw:java.io.FileWriter) = {
    val input = s"""
      <ser:createFMS>
         <!--Optional:-->
         <ser:fmsInfo><![CDATA[
        $fmsInfo
        ]]>
        </ser:fmsInfo>
       <ser:fmsInternalID>$fmsInternalID</ser:fmsInternalID>
          </ser:createFMS>
      """ format (fmsInfo)
    apply("setFMSInfo", input)
  }
    
}

 