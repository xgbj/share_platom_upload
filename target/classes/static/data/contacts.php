<?php
	require("../codebase/connector/grid_connector.php");
	$res=mysql_connect("localhost","root","");
    	mysql_select_db("dhtmlx_tutorial");
	
	$conn = new GridConnector($res,"MySQL");
	$conn->render_table("contacts","contact_id","fname,lname,email");
?>