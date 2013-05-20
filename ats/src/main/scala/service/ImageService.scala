package service

import scala.xml._

object ImageService extends CSDMService {

  def apply(acion: String, req: String)(implicit fw: java.io.FileWriter): String = {
    SoapClient.invoke(base + "services/ImageService.ImageServiceHttpSoap12Endpoint/", "xmlns:ser=\"http://services.RemoteWS.pas.carestreamhealth.com\"", acion, req).getOrElse("")
  }

  def getImageDescription(imageInternalID: String)(implicit fw: java.io.FileWriter): String = {
    val input = """
      <ser:getImageDescription>
         <!--Optional:-->
         <ser:imageInternalID>%s</ser:imageInternalID>
      </ser:getImageDescription>      
    """ format (imageInternalID)
    apply("getImageDescription", input)
  }
  def getImageInfoByImgId(imageInternalID: String)(implicit fw: java.io.FileWriter): String = {
    val input = """
            <ser:getImageInfo>
         <!--Optional:-->
         <ser:imageInternalID>
		<![CDATA[
		 <trophy type="request" version="1.0">
		     <image>
		         <parameter key="internal_id" value="%s" />
		     </image>
		 </trophy>
		]]>
		</ser:imageInternalID>
      </ser:getImageInfo>
      """ format (imageInternalID)
    apply("getImageInfo", input)
  }

  def getImageInfo(req: String)(implicit fw: java.io.FileWriter): String = {
    val input = "<ser:getImageInfo>\n<ser:imageInternalID>\n<![CDATA[\n%s\n]]>\n</ser:imageInternalID></ser:getImageInfo>" format (req)
    apply("getImageInfo", input)
  }

  def createImage(imageInfo: String)(implicit fw: java.io.FileWriter) = {
    val input = """
      <ser:createImage>
         <!--Optional:-->
         <ser:imageInfo>
		<![CDATA[
		%s
		]]>
		</ser:imageInfo>
      </ser:createImage>
      """ format (imageInfo)
    apply("createImage", input)
  }

  def listAllPS(imageInternalID: String)(implicit fw: java.io.FileWriter) = {
    val filter = """
	         <trophy type="request" version="1.0">
		     <filter>
		     </filter>
		 </trophy>
        """
    listPresentationState(imageInternalID, filter)
  }
  def listPresentationState(imageInternalID: String, filter: String)(implicit fw: java.io.FileWriter) = {
    val input = """
      <ser:listPresentationState>
         <ser:filter>
		<![CDATA[
		 %s
		]]>
		</ser:filter>
         <ser:imageInternalID>%s</ser:imageInternalID>
      </ser:listPresentationState>
      """ format (filter, imageInternalID)
    apply("listPresentationState", input)
  }

  def setImageInfo(imageInternalID: String, imageInfo: String)(implicit fw: java.io.FileWriter) = {
    val input = """
      <ser:setImageInfo>
         <!--Optional:-->
         <ser:imageInfo><![CDATA[
          %s
          ]]></ser:imageInfo>
         <!--Optional:-->
         <ser:imageInternalID>%s</ser:imageInternalID>
      </ser:setImageInfo>
      """ format (imageInfo, imageInternalID)
    apply("setImageInfo", input)
  }

  def setCephTracing(imageInternalID: String, cephTracing: String)(implicit fw: java.io.FileWriter) = {
    val input = s"""
      <ser:setCephTracing>
         <!--Optional:-->
         <ser:imageInternalID>$imageInternalID</ser:imageInternalID>
         <!--Optional:-->
         <ser:cephTracing>$cephTracing</ser:cephTracing>
      </ser:setCephTracing>
      """
    apply("setCephTracing", input)
  }

}