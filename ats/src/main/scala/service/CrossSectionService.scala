package service

import util.FileUtils

object CrossSectionService  extends CSDMService  {
  def apply(acion: String, req: String) (implicit fw:java.io.FileWriter)= {
    SoapClient.invoke(base + "services/CrossSectionService.CrossSectionServiceHttpSoap12Endpoint/", "xmlns:ser=\"http://services.RemoteWS.pas.carestreamhealth.com\"", acion, req).getOrElse("")
  }

  def createDefaultCrossSection(volumeId:String,slices_paths:Seq[String] = List(FileUtils.raw3dFile(),FileUtils.raw3dFile(),FileUtils.raw3dFile()))(implicit fw:java.io.FileWriter) = {
    val first_localizer_path = FileUtils.raw3dFile()
    val second_localizer_path = FileUtils.raw3dFile()
    val slices_dicom_series_instance_uid = java.util.UUID.randomUUID().toString()
    val slices_path_list = slices_paths.map("""<parameter key="slice_path" value="%s" />""" format(_))
    val slices_ps_xml_annotation_list = slices_paths.map(p => """<parameter key="slice_ps_xml_annotation" value="%s" />""" format( p + ".xml"))
    val slices_ps_xml_general_list = slices_paths.map(p => """<parameter key="slice_ps_xml_general" value="%s" />""" format( p + ".xml"))
    val slices_ps_xml_process_list = slices_paths.map(p => """<parameter key="slice_ps_xml_processing" value="%s" />""" format( p + ".xml"))
    val xmlCrossSectionCompleteInfo = """
      <trophy type="request" version="1.0"> 
    <crosssection>
        <parameter key="object_creation_date" value="2010-09-09T08:08:08+09:00" /> 
        <parameter key="images_acquisition_date" value="2010-09-09T08:08:08+09:00" /> 
        <parameter key="images_modality" value="CT" />  
        <parameter key="presentationstates_classification" value="presentationstates_classification" />  
        <parameter key="slices_dicom_series_instance_uid" value="%s" />
        <parameter key="first_localizer_dicom_series_instance_uid" value="loc1_dicom_series_instance_uidikuy_1" /><!--O-->
        <parameter key="first_localizer_series_date" value="2010-09-09T08:08:08+09:00" /> <!--O "yyyy-MM-ddThh:mm:ss+/-hh:mm" -->
        <parameter key="first_localizer_path" value="%s" /> <!--O-->
        <parameter key="first_localizer_ps_xml_annotation" value="first_localizer_ps_xml_annotation" /> <!--O-->
        <parameter key="first_localizer_ps_xml_general" value="first_localizer_ps_xml_general" /> <!--O-->
        <parameter key="first_localizer_ps_xml_processing" value="first_localizer_ps_xml_processing" /> <!--O-->
        <parameter key="second_localizer_dicom_series_instance_uid" value="loc1_dicom_series_instance_uidikuy_2" /><!--O-->
        <parameter key="second_localizer_series_date" value="2010-09-09T08:08:08+09:00" /> <!--O "yyyy-MM-ddThh:mm:ss+/-hh:mm" -->
        <parameter key="second_localizer_path" value="%s" /> <!--O-->
        <parameter key="second_localizer_ps_xml_annotation" value="second_localizer_ps_xml_annotation" /> <!--O-->
        <parameter key="second_localizer_ps_xml_general" value="second_localizer_ps_xml_general" /> <!--O-->
        <parameter key="second_localizer_ps_xml_processing" value="second_localizer_ps_xml_processing" /> <!--O-->

     </crosssection>
     <slices_path_list>  
        %s
     </slices_path_list>
     <slices_ps_xml_annotation_list>
         %s
     </slices_ps_xml_annotation_list>
     <slices_ps_xml_general_list>
         %s
     </slices_ps_xml_general_list>
     <slices_ps_xml_processing_list>
         %s
     </slices_ps_xml_processing_list>
 </trophy>
      """ format(slices_dicom_series_instance_uid,first_localizer_path,second_localizer_path,
      slices_path_list.mkString("\n"),
      slices_ps_xml_annotation_list.mkString("\n"),
      slices_ps_xml_general_list.mkString("\n"),
      slices_ps_xml_process_list.mkString("\n"))
    createCrossSection(volumeId,xmlCrossSectionCompleteInfo)
  }
  def createCrossSection(volumeUID: String, xmlCrossSectionCompleteInfo: String)(implicit fw:java.io.FileWriter) = {
    val input = """
      <ser:createCrossSection>
         <!--Optional:-->
         <ser:p01_volumeUID>%s</ser:p01_volumeUID>
         <!--Optional:-->
         <ser:p02_xmlCrossSectionCompleteInfo>
      <![CDATA[
    		%s
    		]]>
        </ser:p02_xmlCrossSectionCompleteInfo>
      </ser:createCrossSection>
      """ format (volumeUID, xmlCrossSectionCompleteInfo)
    apply("createCrossSection", input)
  }
  
    def listImagesOfCrossSection(crossSectionUID: String)(implicit fw:java.io.FileWriter) = {
    val input = """
      <ser:listImagesOfCrossSection>
         <!--Optional:-->
         <ser:crossSectionUID>%s</ser:crossSectionUID>
      </ser:listImagesOfCrossSection>
      """ format (crossSectionUID)
    apply("listImagesOfCrossSection", input)
  } 
  
  def getCrossSectionCompleteInfo(crossSectionUID: String)(implicit fw:java.io.FileWriter) = {
    val input = """
      <ser:getCrossSectionCompleteInfo>
         <!--Optional:-->
         <ser:crossSectionUID>%s</ser:crossSectionUID>
      </ser:getCrossSectionCompleteInfo>
      """ format (crossSectionUID)
    apply("getCrossSectionCompleteInfo", input)
  }

}