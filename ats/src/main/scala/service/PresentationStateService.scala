package service

import util.FileUtils

object PresentationStateService extends CSDMService {

  def apply(action: String, req: String)(implicit fw: java.io.FileWriter) = {
    SoapClient.invoke(base + "services/PresentationStateService.PresentationStateServiceHttpSoap11Endpoint/", "xmlns:ser=\"http://services.RemoteWS.pas.carestreamhealth.com\"", action, req).getOrElse("")
  }

  def createDefaultCurrentPS(imageInternalID: String)(implicit fw: java.io.FileWriter) = {
    val thumbnail = FileUtils.copyToTemp("/OtherData/07.png")
    val psInfo = s"""
         <trophy type="request" version="1.0">
	     <presentationstate>
	         <parameter key="general.xml"></parameter>
	         <parameter key="processing.xml"></parameter>
	         <parameter key="annotation.xml"></parameter>
	         <parameter key="current" value="true"/>
            <parameter key="thumbnail" value="$thumbnail" />
            <parameter key="thumbnail_with_drawings" value="$thumbnail" />
	      </presentationstate>
	  </trophy>
        """
    createPresentationState(imageInternalID, psInfo)
  }
  def createPresentationState(imageInternalID: String, psInfo: String)(implicit fw: java.io.FileWriter) = {
    val req = """
      <ser:createPresentationState>
	         <ser:imageInternalID>%s</ser:imageInternalID>
	         <!--Optional:-->
			         <ser:presentationState>
			<![CDATA[
			 %s
			]]>
			</ser:presentationState>
	      </ser:createPresentationState>
	      """ format (imageInternalID, psInfo)
    apply("createPresentationState", req)
  }

  def getPresentationStateInfo(psId: String)(implicit fw: java.io.FileWriter) = {
    val req = """
      <ser:getPresentationStateInfo>
         <!--Optional:-->
         <ser:presentationStateUidList>
		<![CDATA[
		 <trophy type="request" version="1.0">
		     <presentationstate>
		         <parameter key="internal_id" value="%s"/>
		     </presentationstate>
		</trophy>
		]]>
		</ser:presentationStateUidList>
		      </ser:getPresentationStateInfo>
	      """ format (psId)
    apply("getPresentationStateInfo", req)
  }

  def setPresentationState(presentationStateInternalID: String, presentationStateInfo: String)(implicit fw: java.io.FileWriter) = {
    val req = """
            <ser:setPresentationState>
         <!--Optional:-->
         <ser:presentationStateInfo><![CDATA[%s]]></ser:presentationStateInfo>
         <!--Optional:-->
         <ser:presentationStateInternalID>%s</ser:presentationStateInternalID>
      </ser:setPresentationState>
          """ format (presentationStateInfo, presentationStateInternalID)
    apply("setPresentationState", req)
  }

  def setPresentationStateInfo(presentationStateInternalID: String, presentationStateInfo: String)(implicit fw: java.io.FileWriter) = {
    val req = """
            <ser:setPresentationStateInfo>
         <!--Optional:-->
         <ser:indexInfo><![CDATA[%s]]></ser:indexInfo>
         <!--Optional:-->
         <ser:presentationStateInternalID>%s</ser:presentationStateInternalID>
      </ser:setPresentationStateInfo>
          """ format (presentationStateInfo, presentationStateInternalID)
    apply("setPresentationStateInfo", req)
  }

  def exportPresentationStateAsJpg(presentationStateInternalID: String)(implicit fw: java.io.FileWriter) = {
    val exportType =
      """
     <trophy type="request" version="1.0">
         <presentationstate>
             <parameter key="export_format" value="jpeg" />  M (bmp,jpeg,png,tiff)
             <parameter key="export_type" value="simple_copy" />  M (simple_copy,resize,bestfit)
             <parameter key="resize_width" value="\d+" />  C, required if the export_type is resize or bestfit
             <parameter key="resize_height" value="\d+" /> C, required if the export_type is resize or bestfit
             <parameter key="burn_drawing" value="" />  O (true, false) default: false
         </presentationstate>
     </trophy>
      """
    exportPresentationState(exportType, presentationStateInternalID)

  }
  def exportPresentationState(exportType: String, presentationStateInternalID: String)(implicit fw: java.io.FileWriter) = {
    val req = """
            <ser:exportPresentationState>
         <!--Optional:-->
         <ser:exportType><![CDATA[
          %s
          ]]></ser:exportType>
         <!--Optional:-->
         <ser:presentationStateInternalID>%s</ser:presentationStateInternalID>
      </ser:exportPresentationState>
          """ format (exportType, presentationStateInternalID)
    apply("exportPresentationState", req)
  }

}