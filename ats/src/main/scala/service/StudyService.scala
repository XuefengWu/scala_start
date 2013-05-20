package service

object StudyService extends CSDMService{
  
  def apply(acion:String,req:String)(implicit fw:java.io.FileWriter) = {
    SoapClient.invoke(base + "services/StudyService.StudyServiceHttpSoap11Endpoint/","xmlns:ser=\"http://services.RemoteWS.pas.carestreamhealth.com\"",acion,req).getOrElse("")
  }
  
  def createStudy(studyInfo:String)(implicit fw:java.io.FileWriter) = {
    val input = """
            <ser:createStudy>
         <!--Optional:-->
		 <ser:studyInfo>
		<![CDATA[
		 %s
		]]>
		</ser:studyInfo>
      """ format(studyInfo)
    apply("createStudy",input)
  }
}