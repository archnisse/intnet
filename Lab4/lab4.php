<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">

<title>NYA Hemnet</title>
</head>


<body>

<? 

$DBServer = "127.0.0.1";
$DBUser = 'ellasadmin';
$DBPass = 'iMEj8tYy';
$DBName = 'mysql';

$conn = new mysqli($DBServer, $DBUser, $DBPass, $DBName);

if($conn->connect_error){
	trigger_error('Database connection failed: ' .$conn->connect_error, 		E_USER_ERROR);
}
?>
</body>
</html>

