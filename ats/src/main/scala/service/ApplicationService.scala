package service

object ApplicationService extends CSDMService {

  def apply(acion: String, req: String)(implicit fw: java.io.FileWriter) = {
    SoapClient.invoke(base + "services/ApplicationService.ApplicationServiceHttpSoap12Endpoint/", "xmlns:app=\"http://application.services.LocalWS.pas.carestreamhealth.com\"", acion, req).getOrElse("")
  }

  def createDefault2DViewer()(implicit fw: java.io.FileWriter) = {

    val config = """
      <trophy  type="pas_set_config">
      <config>
       <parameter key="patient_internal_id" value=""/> O
       <parameter key="study_internal_id" value=""/> O
       <parameter key="title" value=""/> O
       <parameter key="app_icon" value=""/> O (local path for icon file.)
       <parameter key="app_name" value=""/> O
       <parameter key="mode" value=""/> O, value range[browser]
       <parameter key="state" value=""/> O, value range[restored,minimized, maximized]
       <parameter key="tooth_notation" value=""/> O, value range[european, american, unknown]
      </config>
     </trophy>
      """
    createApplication("2DViewer", config)
  }

  def createApplication(application: String, config: String)(implicit fw: java.io.FileWriter) = {
    val input = s"""
      <app:createApplication>
         <!--Optional:-->
         <app:application>$application</app:application>
         <!--Optional:-->
         <app:config><![CDATA[
             $config
           ]]></app:config>
      </app:createApplication>
      """
    apply("createApplication", input)
  }

  def getConfiguration(appInstanceId: String)(implicit fw: java.io.FileWriter) = {
    val input = s"""
      <app:getConfiguration>
         <!--Optional:-->
         <app:appliInstanceID>$appInstanceId</app:appliInstanceID>
      </app:getConfiguration>
      """
    apply("getConfiguration", input)
  }

  def closeApplication(appInstanceId: String)(implicit fw: java.io.FileWriter) = {
    val input = s"""
      <app:closeApplication>
         <!--Optional:-->
         <app:appliInstanceId>$appInstanceId</app:appliInstanceId>
      </app:closeApplication>
      """
    apply("closeApplication", input)
  }

  def setConfiguration(appInstanceId: String, config: String)(implicit fw: java.io.FileWriter) = {
    val input = s"""
      <app:setConfiguration>
         <!--Optional:-->
         <app:appliInstanceID>$appInstanceId</app:appliInstanceID>
         <!--Optional:-->
         <app:config><![CDATA[
         $config
         ]]></app:config>
      </app:setConfiguration>
      """
    apply("setConfiguration", input)
  }

} 