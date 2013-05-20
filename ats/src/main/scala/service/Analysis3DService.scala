package service

object Analysis3DService extends CSDMService {

  def apply(acion: String, req: String)(implicit fw: java.io.FileWriter) = {
    SoapClient.invoke(base + "services/Analysis3DService.Analysis3DServiceHttpSoap12Endpoint/", "xmlns:ser=\"http://services.RemoteWS.pas.carestreamhealth.com\"", acion, req).getOrElse("")
  }

  def createDefaultAnalysis3D(volumeUID: String, current: String = "true")(implicit fw: java.io.FileWriter) = {
    val xmlAnalysis3DInfo = """
         <trophy type="request" version="1.0">
	    <analysis3d>
	        <parameter key="object_creation_date" value="2011-01-22T14:12:33+0800" />
	        <parameter key="name" value="test arable" /> 
	        <parameter key="comments" value="العربيةasdfdfddaeda" /> 
	        <parameter key="analysis3D_xml" value="analysis3Dxml" />  
	        <parameter key="current" value="%s" />
	     </analysis3d>
	 </trophy>
        """ format (current)
    createAnalysis3D(volumeUID, xmlAnalysis3DInfo)
  }
  def createAnalysis3D(volumeUID: String, xmlAnalysis3DInfo: String)(implicit fw: java.io.FileWriter) = {
    val input = """
      <ser:createAnalysis3D>
         <ser:p01_instanceUID>%s</ser:p01_instanceUID>
        <ser:p02_xmlAnalysis3DInfo>
	<![CDATA[
	 %s
	]]>
	</ser:p02_xmlAnalysis3DInfo>
	      </ser:createAnalysis3D>
      """ format (volumeUID, xmlAnalysis3DInfo)
    apply("createAnalysis3D", input)
  }

  def linkImageToAnalysis3D(analysis3DUID: String, imageUID: String)(implicit fw: java.io.FileWriter) = {
    val input = """
      <ser:linkImageToAnalysis3D>
         <ser:p01_analysis3DUID>%s</ser:p01_analysis3DUID>
         <ser:p02_imageUID>%s</ser:p02_imageUID>
      </ser:linkImageToAnalysis3D>
      """ format (analysis3DUID, imageUID)
    apply("linkImageToAnalysis3D", input)
  }

  def setAnalysis3DInfo(analysis3DUID: String, analysis3DInfo: String)(implicit fw: java.io.FileWriter) = {
    val input = """
      <ser:setAnalysis3DInfo>
         <!--Optional:-->
         <ser:p01_analysis3DUID>%s</ser:p01_analysis3DUID>
         <!--Optional:-->
         <ser:p02_xmlAnalysis3DInfo>
            <![CDATA[
             %s
            ]]>
         </ser:p02_xmlAnalysis3DInfo>
      </ser:setAnalysis3DInfo>
      """ format (analysis3DUID, analysis3DInfo)
    apply("setAnalysis3DInfo", input)
  }

  def getAnalysis3DInfo(analysis3DUID: String)(implicit fw: java.io.FileWriter) = {
    val input = """
       <ser:getAnalysis3DInfo>
         <!--Optional:-->
         <ser:analysis3DUID>%s</ser:analysis3DUID>
      </ser:getAnalysis3DInfo>
      """ format (analysis3DUID)
    apply("getAnalysis3DInfo", input)
  }

  def listImagesOfAnalysis3D(analysis3DUID: String)(implicit fw: java.io.FileWriter) = {
    val input = """
      <ser:listImagesOfAnalysis3D>
         <!--Optional:-->
         <ser:analysis3DUID>%s</ser:analysis3DUID>
      </ser:listImagesOfAnalysis3D>
      """ format (analysis3DUID)
    apply("listImagesOfAnalysis3D", input)
  }

}
 
 