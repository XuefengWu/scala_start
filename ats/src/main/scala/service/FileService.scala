package service


object FileService extends CSDMService{
  
  def apply(acion:String,req:String)(implicit fw:java.io.FileWriter) = {
    SoapClient.invoke(base + "services/FileService.FileServiceHttpSoap12Endpoint/","xmlns:ser=\"http://services.RemoteWS.pas.carestreamhealth.com\"",acion,req).getOrElse("")
  }
  
  def listFileURL(imageUid:String)(implicit fw:java.io.FileWriter) = {
    val input = """
      <ser:listFileURL>
         <!--Optional:-->
         <ser:imageUid>%s</ser:imageUid>
      </ser:listFileURL>
      """ format(imageUid)
    apply("listFileURL",input)
  }
}

 