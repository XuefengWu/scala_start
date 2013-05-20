package service

object GenericInstanceService extends CSDMService {

  def apply(acion: String, req: String)(implicit fw: java.io.FileWriter) = {
    SoapClient.invoke(base + "services/GenericInstanceService.GenericInstanceServiceHttpSoap12Endpoint/", "xmlns:ser=\"http://services.RemoteWS.pas.carestreamhealth.com\"", acion, req).getOrElse("")
  }

  def moveInstance(target_patient_internal_id: String, internal_ids: Seq[String], auto_move_instances_in_same_series: String = "false")(implicit fw: java.io.FileWriter) = {
    val instanceParameters = internal_ids.map("""<parameter key="internal_id" value="%s"/> """ format (_))
    val input = """
      <ser:moveInstance>
         <!--Optional:-->
         <ser:moveInformation>
		<![CDATA[
		<trophy type="request" version="1.0">
		      <move_information>
		          <parameter key="target_patient_internal_id" value="%s"/>  
		          <parameter key="auto_move_instances_in_same_series" value="%s"/>  
		      </move_information>
		      <instance>  
		          %s
		      </instance>
		  </trophy>
		]]>
			</ser:moveInformation>
      </ser:moveInstance>
      """ format (target_patient_internal_id, auto_move_instances_in_same_series, instanceParameters.mkString("\n"))
    apply("moveInstance", input)
  }

  def moveInstance(moveInformation: String)(implicit fw: java.io.FileWriter) = {
    val input = """
      <ser:moveInstance>
         <!--Optional:-->
         <ser:moveInformation>
		<![CDATA[
		%s
		]]>
	</ser:moveInformation>
      </ser:moveInstance>
      """ format (moveInformation)
    apply("moveInstance", input)
  }

  def linkInstance(parent_instance_internal_id: String, child_instance_internal_id: String, is_flat_link: String)(implicit fw: java.io.FileWriter) = {
    val input = """
      <ser:linkInstance>
         <!--Optional:-->
         <ser:linkInformation>
		<![CDATA[ 
		  <trophy type="request" version="1.0">
		      <link_information>
		          <parameter key="parent_instance_internal_id" value="%s"/>  
		          <parameter key="child_instance_internal_id" value="%s"/>  
		          <parameter key="is_flat_link" value="%s"/>  
		      </link_information>
		  </trophy>
		]]>
		</ser:linkInformation>
	      </ser:linkInstance>
      """ format (parent_instance_internal_id, child_instance_internal_id, is_flat_link)
    apply("linkInstance", input)
  }

  def linkInstance(linkInformation: String)(implicit fw: java.io.FileWriter) = {
    val input = """
      <ser:linkInstance>
         <!--Optional:-->
         <ser:linkInformation>
		<![CDATA[ 
		  %s
		]]>
			</ser:linkInformation>
		      </ser:linkInstance>
      """ format (linkInformation)
    apply("linkInstance", input)
  }

  def listInstances(parent_instance_internal_id: String, child_instance_type: String)(implicit fw: java.io.FileWriter) = {
    val input = """
      <ser:listInstances>
         <!--Optional:-->
         <ser:listFilter>
        <![CDATA[ 
          <trophy type="request" version="1.0">
              <filter>
                  <parameter key="parent_instance_internal_id" value="%s"/>  
                  <parameter key="child_instance_type" value="%s"/>
              </filter>
          </trophy>
        ]]>
    </ser:listFilter>
      </ser:listInstances>
      """ format (parent_instance_internal_id, child_instance_type)
    apply("listInstances", input)
  }

  def listLinkedInstance(current_instance_internal_id: String, link_type: String, instanceType: String)(implicit fw: java.io.FileWriter) = {
    val input = """
      <ser:listLinkedInstance>
         <!--Optional:-->
         <ser:listFilter>
		<![CDATA[ 
		  <trophy type="request" version="1.0">
		      <filter>
		          <parameter key="current_instance_internal_id" value="%s"/>  
		          <parameter key="link_type" value="%s"/>
    			  <parameter key="instance_type" value="%s"/>  
		      </filter>
		  </trophy>
		]]>
	</ser:listFilter>
      </ser:listLinkedInstance>
      """ format (current_instance_internal_id, link_type, instanceType)
    apply("listLinkedInstance", input)
  }

  def listLinkedInstance(current_instance_internal_id: String, link_type: String)(implicit fw: java.io.FileWriter) = {
    val input = """
      <ser:listLinkedInstance>
         <!--Optional:-->
         <ser:listFilter>
		<![CDATA[ 
		  <trophy type="request" version="1.0">
		      <filter>
		          <parameter key="current_instance_internal_id" value="%s"/>  
		          <parameter key="link_type" value="%s"/>   
		      </filter>
		  </trophy>
		]]>
	</ser:listFilter>
      </ser:listLinkedInstance>
      """ format (current_instance_internal_id, link_type)
    apply("listLinkedInstance", input)
  }

  def unlinkInstance(unlinkInformation: String)(implicit fw: java.io.FileWriter): String = {
    val input = """
      <ser:unlinkInstance>
         <!--Optional:-->
         <ser:unlinkInformation>
        <![CDATA[
              %s
        ]]>
        </ser:unlinkInformation>
          </ser:unlinkInstance>
      """ format (unlinkInformation)
    apply("unlinkInstance", input)
  }
  def unlinkInstance(pareintUid: String, childUid: String)(implicit fw: java.io.FileWriter): String = {
    val unlinkInformation = """
            <trophy type="request" version="1.0">
          <unlink_information>
              <parameter key="parent_instance_internal_id" value="%s"/>  
              <parameter key="child_instance_internal_id" value="%s"/>  
          </unlink_information>
      </trophy>
      """ format (pareintUid, childUid)
    unlinkInstance(unlinkInformation)
  }
}