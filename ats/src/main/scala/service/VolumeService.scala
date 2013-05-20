package service

object VolumeService extends CSDMService {

  def apply(acion: String, req: String)(implicit fw: java.io.FileWriter) = {
    SoapClient.invoke(base + "services/VolumeService.VolumeServiceHttpSoap12Endpoint/", "xmlns:ser=\"http://services.RemoteWS.pas.carestreamhealth.com\"", acion, req).getOrElse("")
  }

  def setVolume(volumeUID: String, xmlVolumeCompleteInfo: String)(implicit fw: java.io.FileWriter) = {
    val input = """
      <ser:setVolume>
         <ser:p01_volumeUID>%s</ser:p01_volumeUID>
         <ser:p02_xmlVolumeCompleteInfo>
		<![CDATA[
		%s
		]]>
		</ser:p02_xmlVolumeCompleteInfo>
		      </ser:setVolume>
                """ format (volumeUID, xmlVolumeCompleteInfo)
    apply("setVolume", input)
  }

  def createVolume(studyUID: String, xmlVolumeCompleteInfo: String)(implicit fw: java.io.FileWriter) = {
    val input = """
      <ser:createVolume>
         <ser:p01_studyUID>%s</ser:p01_studyUID>
         <ser:p02_xmlVolumeCompleteInfo>
		<![CDATA[
		%s
		]]>
		</ser:p02_xmlVolumeCompleteInfo>
		      </ser:createVolume>
      """ format (studyUID, xmlVolumeCompleteInfo)
    apply("createVolume", input)
  }

  def getVolumeInfoById(volumeId: String)(implicit fw: java.io.FileWriter) = {
    val xmlVolumeInfoFilter = """
       <trophy type="request" version="1.0">
    <volume>
        <parameter key="internal_id" value="%s" />       
    </volume>  
     </trophy>
      """ format (volumeId)
    getVolumeInfo(xmlVolumeInfoFilter)
  }

  def getVolumeInfo(xmlVolumeInfoFilter: String)(implicit fw: java.io.FileWriter) = {
    val input = """
      <ser:getVolumeInfo>
         <!--Optional:-->
         <ser:xmlVolumeInfoFilter>
    		<![CDATA[
    		%s
      ]]></ser:xmlVolumeInfoFilter>
      </ser:getVolumeInfo>
      """ format (xmlVolumeInfoFilter)
    apply("getVolumeInfo", input)
  }

  def deleteVolume(volumeUID: String)(implicit fw: java.io.FileWriter) = {
    val input = """
      <ser:deleteVolume>
         <ser:volumeUID>%s</ser:volumeUID>
      </ser:deleteVolume>
      """ format (volumeUID)
    apply("deleteVolume", input)
  }

  def listChildVolumesOfVolume(volumeUID: String)(implicit fw: java.io.FileWriter) = {
    val input = s"""
      <ser:listChildVolumesOfVolume>
         <!--Optional:-->
         <ser:volumeUID>$volumeUID</ser:volumeUID>
      </ser:listChildVolumesOfVolume>
      """
    apply("listChildVolumesOfVolume", input)
  }

  def listImagesOfVolume(volumeUID: String)(implicit fw: java.io.FileWriter) = {
    val input = s"""
      <ser:listImagesOfVolume>
         <!--Optional:-->
         <ser:volumeUID>$volumeUID</ser:volumeUID>
      </ser:listImagesOfVolume>
      """
    apply("listImagesOfVolume", input)
  }

  def listAnalyses3DOfVolume(volumeUID: String, filterCriteria: String)(implicit fw: java.io.FileWriter) = {
    val input = s"""
      <ser:listAnalyses3DOfVolume>
         <!--Optional:-->
         <ser:p01_volumeUID>$volumeUID</ser:p01_volumeUID>
         <!--Optional:-->
         <ser:p02_filterCriteria>$filterCriteria</ser:p02_filterCriteria>
      </ser:listAnalyses3DOfVolume>
      """
    apply("listAnalyses3DOfVolume", input)
  }

  def getStudyUID(volumeUID: String)(implicit fw: java.io.FileWriter) = {
    val input = s"""
      <ser:getStudyUID>
         <!--Optional:-->
         <ser:volumeUID>$volumeUID</ser:volumeUID>
      </ser:getStudyUID>
      """ 
    apply("getStudyUID", input)
  }
    
}
 