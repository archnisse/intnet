<?php

#initalizes parameters
$DBServer = "mysql-vt2015.csc.kth.se";
$DBUser = 'viktorbjadmin';
$DBPass = 'Ogm7axfJ';
$DBName = 'viktorbj';

#Connects to database
$conn = new mysqli($DBServer, $DBUser, $DBPass, $DBName);
$conn -> set_charset("utf8");

#Database connection check
if($conn->connect_error){
	trigger_error('Database connection failed: ' .$conn->connect_error, E_USER_ERROR);
}

?>
