package util

import scala.xml._
import service._
import conf.Conf
import org.specs2.Specification
import java.util.UUID
import java.net.URL
import java.io.File

object DataPrepare {

  def createPatient(implicit fw: java.io.FileWriter, pb: scala.collection.mutable.ListBuffer[String]) = {
    val dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd")

    val patientId = java.util.UUID.randomUUID().toString();
    val dpmpsId = "new DPMS"
    val req = """
         <trophy type="request" version="1.0">
	      <patient>
	          <parameter key="patient_id" value="%s" />  
	          <parameter key="dpms_id" value="%s"/>  
	          <parameter key="first_name" value="Frank"/> 
	          <parameter key="last_name" value="Shark"/>  
	          <parameter key="middle_name" value="X"/>  
	          <parameter key="prefix" value="P"/> 
	          <parameter key="suffix" value="S"/> 
	          <parameter key="birth_date" value="%s"/>  
	      </patient>
	  </trophy>
        """ format (patientId, dpmpsId, dateFormat.format(new java.util.Date()))

    val res = PatientService.createPatient(req)

    val puid = XMLUtils.getInstanceIdFrom(res, "patient")
    pb += puid
    puid
  }

  def createStudy(pid: String)(implicit fw: java.io.FileWriter) = {
    val studyInfo = """
      <trophy type="request" version="1.0">
	    <study>
	        <parameter key="patient_internal_id" value="%s" />
	        <parameter key="dicom_study_instance_uid" value="%s" />
	    </study>
	 </trophy>
      """ format (pid,java.util.UUID.randomUUID().toString())
    val res = StudyService.createStudy(studyInfo)
    XMLUtils.getParameterValue(XML.loadString(res) \ ("study"), "internal_id")
  }

  def acqImage(pid: String)(implicit fw: java.io.FileWriter) = {
    val acqInfo = """
       <trophy type="request" version="1.0">
	 <acq_info>
	   <parameter key="device_id" value="AcqIO.dll"/>
	   <parameter key="line_id" value="58810000"/>
	   <parameter key="patient_internal_id" value="%s"/>
	   <parameter key="series_performing_physician_name" value="What"/>
	   </acq_info>
	 </trophy>
      """ format (pid)

    val acquisitionResult = AcquisitionService.acquire(acqInfo)
    val acquisitionResultXml = XML.loadString(acquisitionResult)
    val imageInfoNode = acquisitionResultXml \ ("object_info")
    val imgId = XMLUtils.getParameterValue(imageInfoNode, ("key","internal_id"), ("type","image"))
    val psId = XMLUtils.getParameterValue(imageInfoNode, ("key","internal_id"), ("type","presentation_state"))

    //make sure the image is upload to remote service
    synchronized {
      var tryImgTime = 10
      var imgExist = false
      do {
        Thread.sleep(2000)
        println("tryImgTime: " + tryImgTime)
        val imgRes = ImageService.getImageInfoByImgId(imgId)
        val imageInfoNode = XML.loadString(imgRes) \ ("image")
        val path = XMLUtils.getParameterValue(imageInfoNode, "path")
        
        imgExist = if(path.contains("http")) {
         FileUtils.exists(new URL(path)) 
        }else{
          new File(path).exists()
        }
        tryImgTime -= 1
      } while (!imgExist && tryImgTime > 0)
    }
    (imgId, psId)
  }
  def imageFile(myOrder: Int)(implicit fw: java.io.FileWriter, pb: scala.collection.mutable.ListBuffer[String]): String = {
    FileUtils.copyToTemp(FileUtils.rawImageFile(),false)
  }

  def tryImportImage(patientId: String, order: Int = 1)(implicit fw: java.io.FileWriter, pb: scala.collection.mutable.ListBuffer[String]) = {
    var tryTime = 0
    var res = ""
    do {
      res = ImportService.importObject(patientId, createStudy(patientId), imageFile(order + tryTime))
      tryTime += 1
      println("tryImportImage: " + tryTime)
    } while (tryTime < 3 && !res.contains("code=\"0\""))
    res
  }

  def tryCreateImage(patientId: String, order: Int = 1, serieseID: String = "", studyId: String = "")(implicit fw: java.io.FileWriter, pb: scala.collection.mutable.ListBuffer[String]) = {
    val res = doCreateImage(patientId, order, serieseID, studyId)
    val imgId = XMLUtils.getInstanceIdFrom(res, "image")
    val serId = XMLUtils.getInstanceIdFrom(res, "series")
    (imgId, preparePS(imgId), serId)
  }

  private def doCreateImage(patientId: String, order: Int = 1, serieseID: String = "", studyId: String = "")(implicit fw: java.io.FileWriter, pb: scala.collection.mutable.ListBuffer[String]) = {

    val serieseInfo: String = if (!serieseID.isEmpty()) {
      """
      <series>
		<parameter key="dicom_series_instance_uid" value="%s"/>
        <parameter key="internal_id" value="%s"/>
      </series>
      """ format (serieseID,serieseID)
    } else {
      ""
    }
    val studyInfo: String = if (!studyId.isEmpty()) {
      """
       <study>
		 <parameter key="internal_id" value="%s"/>
       </study>
      """ format (studyId)
    } else {
      ""
    }

    var tryTime = 0
    var res = ""
    do {
      val rawFile = imageFile(order + tryTime)
      val imageInfo = """
         <trophy type="request" version="1.0">
	     <image>
	         <parameter key="path" value="%s" />
	         <parameter key="patient_internal_id" value="%s" />
	         <parameter key="kvp" value="0"/>
	         <parameter key="xray_tube_current" value="0"/>
	         <parameter key="exposure_time" value="0"/>
	         <parameter key="area_dose_product" value="5"/>
	         <parameter key="acquired" value="true"/>
		 <parameter key="comments" value="test comments for image"/>
	     </image>
         %s
         %s
	 </trophy>
        """ format (rawFile, patientId, serieseInfo, studyInfo)

      Thread.sleep(3000)
      res = ImageService.createImage(imageInfo)
      tryTime += 1
      println("trycreateImage: " + tryTime)
    } while (tryTime < 3 && !res.contains("code=\"0\""))
    res
  }
  def prepareImage(patientId: String, order: Int = 1)(implicit fw: java.io.FileWriter, pb: scala.collection.mutable.ListBuffer[String]): (String, String) = {
    println("prepareImage for : " + patientId)

    val createImageRes = doCreateImage(patientId, order)
    val impRes = if (createImageRes.contains("code=\"0\"")) { createImageRes }
    else { tryImportImage(patientId, order) }

    val imgId = if (impRes.contains("code=\"0\"")) {
      XMLUtils.getInstanceIdFrom(impRes, "image")
    } else {
      println("create PS failed, acquire new image for patient: " + patientId)
      val (_imgId3, _psId3) = acqImage(patientId)
      FileUtils.copyPatientInstanceToTodayImage(patientId)
      _imgId3
    }

    (imgId, preparePS(imgId))
  }

  def preparePS(imageId: String)(implicit fw: java.io.FileWriter) = {
    val res = PresentationStateService.createDefaultCurrentPS(imageId)
    XMLUtils.getInstanceIdFrom(res, "presentationstate")
  }
  def prepareVolume(patientId: String, studyId: String = "")(implicit fw: java.io.FileWriter) = {
    val sid = if (studyId.isEmpty()) {
      createStudy(patientId)
    } else {
      studyId
    }

    val sd_path = List(FileUtils.copyToTemp(FileUtils.raw3dFile()), FileUtils.copyToTemp(FileUtils.raw3dFile()))
    val sd_path_list = sd_path.map("""<parameter key="slice_path" value="%s" />""" format (_))
    val xmlVolumeCompleteInfo = """
	      <trophy type="request" version="1.0"> 
	    <series>
	        <parameter key="dicom_series_instance_uid" value="%s" /> 
	        <parameter key="series_date" value="2001-12-11T12:21:11+0800" /> 
	        <parameter key="modality" value="CT" />  
	    </series>
	    <volume>
	        <parameter key="object_creation_date" value="2001-12-11T12:21:11+0800" /> 
	        <parameter key="images_acquisition_date" value="2001-12-11T12:21:11+0800" /> 
	        <parameter key="3dfunctionalmode" value="Model" />
    		<parameter key="area_dose_product" value="11" />
            <parameter key="is_acquired" value="true" />
	     </volume>
	    <slices_path_list>
    	%s
      </slices_path_list>
	 </trophy>
      """ format (UUID.randomUUID().toString(), sd_path_list.mkString("\n"))
    val res = VolumeService.createVolume(sid, xmlVolumeCompleteInfo)
    XMLUtils.getInstanceIdFrom(res, "volume")
  }

  def prepareFMS(patientId: String)(implicit fw: java.io.FileWriter, pb: scala.collection.mutable.ListBuffer[String]): (String, Seq[String], Seq[String]) = {
    val (imgId1, ps1) = DataPrepare.prepareImage(patientId, 1)
    val (imgId2, ps2) = DataPrepare.prepareImage(patientId, 4)

    val psIds = ps1 :: ps2 :: Nil
    val pss = psIds.map("""<parameter key="internal_id" value="%s"/>""" format (_))
    val fmsInfo = """
        <trophy type="request" version="1.0">
		<fms>
		<parameter key="patient_internal_id" value="%s"/> 
		<parameter key="current" value="true"/>  
		<parameter key="xml_description">fmscreate</parameter>
		</fms>
		<presentationstate>  
		%s
		</presentationstate>
		</trophy>
        """
    val fmsReq = fmsInfo format (patientId, pss.mkString("\n"))
    val res = FMSService.createFMS(fmsReq)
    val (fmsId, imgIds) = if (res.contains("code=\"0\"")) {
      val rid = XMLUtils.getInstanceIdFrom(res, "fms")
      (rid, imgId1 :: imgId2 :: Nil)
    } else {
      fw.write("\ncreate fms failed, try create again with new PS")
      val (imgId3, ps3) = DataPrepare.prepareImage(patientId, 2)
      val (imgId4, ps4) = DataPrepare.prepareImage(patientId, 5)

      val psId2s = ps3 :: ps4 :: Nil
      val pss2 = psId2s.map("""<parameter key="internal_id" value="%s"/>""" format (_))
      val fmsReq2 = fmsInfo format (patientId, pss2.mkString("\n"))
      val res2 = FMSService.createFMS(fmsReq)
      val rid = XMLUtils.getInstanceIdFrom(res2, "fms")
      (rid, imgId3 :: imgId4 :: Nil)
    }

    (fmsId, imgIds, psIds)
  }

  def tryDeleteAllPatient()(implicit fw: java.io.FileWriter) = {
    if (true) {
      val patRes = PatientService.findAllPatient()
      val patNode = XML.loadString(patRes) \ ("patient")
      val pidsDeleted = (patNode \ "parameter").map(p => {
        val pid = (p \ ("@value")).text
        tryDeletePatient(pid)
        !pid.isEmpty()
      })

      if (pidsDeleted.isEmpty) {
        true
      } else {
        pidsDeleted.reduceLeft(_ && _)
      }
    }
  }

  def tryDeletePatient(pid: String)(implicit fw: java.io.FileWriter) = {
    PatientService.deletePatient(pid)
    /*
    var deleted = false
    var tryDeletePatientTime = 30
    do {
      Thread.sleep(10)
      val dRes1 = PatientService.deletePatient(pid)
      tryDeletePatientTime -= 1
      println("tryDeletePatientTime: " + tryDeletePatientTime)
      deleted = dRes1.contains("code=\"0\"") || dRes1.contains("patient not found") || dRes1.contains("is doing post import")
    } while (!deleted && tryDeletePatientTime > 0)
    deleted
    */
  }

  def cleanTemp() {
    new File(Conf.tempPath).listFiles().foreach(_.delete() )
  }
}