package service

object ImportService extends CSDMService{
 
  def apply(acion:String,req:String)(implicit fw:java.io.FileWriter): String= {
    SoapClient.invoke(base + "services/ImportService.ImportServiceHttpSoap12Endpoint/","xmlns:ser=\"http://service.pas.carestream.com\"",acion,req).getOrElse("")
  }
 
  def importObject(patientInternalId:String,studyInternalId:String,objectFileFullPath:String,archivePath :String="",ord:String="false",mv:String="false")(implicit fw:java.io.FileWriter) = {
    val input = """
      <ser:importObject>
         <ser:patientInternalId>%s</ser:patientInternalId>
         <ser:studyInternalId>%s</ser:studyInternalId>
         <ser:objectFileFullPath>%s</ser:objectFileFullPath>
         <ser:archivePath>%s</ser:archivePath>
         <ser:override>%s</ser:override>
         <ser:move>%s</ser:move>
      </ser:importObject>
      """ format(patientInternalId,studyInternalId,objectFileFullPath,archivePath,ord,mv)
    apply("importObject",input)
  }

  def importDir(patientUid:String,dir:String)(implicit fw:java.io.FileWriter)={
    val input =
      """
        <ser:importDir>
         <!--Optional:-->
         <ser:patientInternalId>%s</ser:patientInternalId>
         <!--Optional:-->
         <ser:dirPath>%s</ser:dirPath>
         <!--Optional:-->
         <ser:flag>import</ser:flag>
      </ser:importDir>
      """.format(patientUid,dir)

    apply("importDir",input)

  }
}

 