package service

object AnalysisService extends CSDMService {

  def apply(acion: String, req: String)(implicit fw:java.io.FileWriter) = {
    SoapClient.invoke(base + "services/AnalysisService.AnalysisServiceHttpSoap12Endpoint/", "xmlns:ser=\"http://services.RemoteWS.pas.carestreamhealth.com\"", acion, req).getOrElse("")
  }

  def createAnalysis(analysisXml: String, current: String, patientInternalID: String, thumbnail: String, uidsXml: String)(implicit fw:java.io.FileWriter) = {
    val input = """
      <ser:createAnalysis>
         <!--Optional:-->
         <ser:analysisXml>%s</ser:analysisXml>
         <!--Optional:-->
         <ser:current>%s</ser:current>
         <!--Optional:-->
         <ser:patientInternalID>%s</ser:patientInternalID>
         <!--Optional:-->
         <ser:thumbnail>%s</ser:thumbnail>
         <!--Optional:-->
         <ser:uidsXml>
    	  <![CDATA[
            %s
          ]]>
        </ser:uidsXml>
      </ser:createAnalysis>
      """ format (analysisXml, current, patientInternalID, thumbnail, uidsXml)
    apply("createAnalysis", input)
  }

  def listAnalysisObjects(analysisInternalID: String)(implicit fw:java.io.FileWriter) = {
    val input = """
      <ser:listAnalysisObjects>
         <!--Optional:-->
         <ser:analysisInternalID>%s</ser:analysisInternalID>
      </ser:listAnalysisObjects>
      """ format (analysisInternalID)
    apply("listAnalysisObjects", input)
  }

  
  def setAnalysisDescription(analysisInternalID:String,analysisXml:String,current:String="false",uidsXml:String)(implicit fw:java.io.FileWriter) = {
    val input = """
   <ser:setAnalysisDescription>
         <!--Optional:-->
         <ser:analysisInternalID>%s</ser:analysisInternalID>
         <!--Optional:-->
         <ser:analysisXml>%s</ser:analysisXml>
         <!--Optional:-->
         <ser:current>%s</ser:current>
	 <ser:uidsXml><![CDATA[ 
	    %s
	    ]]></ser:uidsXml>       
      </ser:setAnalysisDescription>
      """ format (analysisInternalID,analysisXml,current,uidsXml)
    apply("setAnalysisDescription", input)
  }

    
  def setAnalysisInfo(analysisInternalID:String,indexedInfo:String)(implicit fw:java.io.FileWriter) = {
    val input = """
      <ser:setIndexedInfo>
         <!--Optional:-->
         <ser:analysisInternalID>%s</ser:analysisInternalID>
         <!--Optional:-->
         <ser:indexedInfo>
            <![CDATA[
            %s
            ]]>
	</ser:indexedInfo>
      </ser:setIndexedInfo>
      """ format (analysisInternalID,indexedInfo)
    apply("setAnalysisInfo", input)
  }

    def exportAnalysis(analysisInternalID:String,exportFormat:String,exportType:String)(implicit fw:java.io.FileWriter) = {
    val input = s"""
      <ser:exportAnalysis>
         <!--Optional:-->
         <ser:analysisInternalID>$analysisInternalID</ser:analysisInternalID>
         <!--Optional:-->
         <ser:exportFormat>$exportFormat</ser:exportFormat>
         <!--Optional:-->
         <ser:exportType>
             <![CDATA[
             $exportType
            ]]>
     </ser:exportType>
      </ser:exportAnalysis>
      """  
    apply("exportAnalysis", input)
  }
    
  
}