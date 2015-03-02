<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">

<title>NYA Hemnet</title>
</head>


<body>

<? 

$selOp = $_POST["taskOp"];

echo("<h1>".$selOp."</h1><br>");

$DBServer = "mysql-vt2015.csc.kth.se";
$DBUser = 'viktorbjadmin';
$DBPass = 'Ogm7axfJ';
$DBName = 'viktorbj';

$conn = new mysqli($DBServer, $DBUser, $DBPass, $DBName);

$conn -> set_charset("utf8");

if($conn->connect_error){
	trigger_error('Database connection failed: ' .$conn->connect_error, E_USER_ERROR);
}

echo("<h1>Welcome</h1>");

$sql = 'SELECT * FROM bostader';# WHERE lan ="'.$selOp.'"';

$rs = $conn -> query($sql);

$arr = $rs -> fetch_all(MYSQLI_ASSOC);
foreach($arr as $row) {
	echo($row[lan] . " " . $row[objekttyp] . " " . $row[adress] . " " . $row[area] . " " . $row[rum] . " " . $row[pris] . " " . $row[avgift] . " <br>"
	
  	);
}


?>


<form method='post' action='lab4.php'>
<!--for loop-->
<select name="taskOp">
<?

$sql = 'SELECT distinct lan FROM bostader';
$rs = $conn -> query($sql);

$arr = $rs -> fetch_all(MYSQLI_ASSOC);
foreach($arr as $row) {
	echo("<option value='".$row[lan]."'>".$row[lan]."</option>");
}
  #<option value="Uppsala" select='selected'>Uppsala</option>
  #<option value="Stockholm">Stockholm</option>
?>
</select>

<select name="taskOp">
<?

$sql = 'SELECT distinct lan FROM bostader';
$rs = $conn -> query($sql);

$arr = $rs -> fetch_all(MYSQLI_ASSOC);
foreach($arr as $row) {
	echo("<option value='".$row[lan]."'>".$row[lan]."</option>");
}
  #<option value="Uppsala" select='selected'>Uppsala</option>
  #<option value="Stockholm">Stockholm</option>
?>
</select>


<input type='submit' value='Visa'>
</form>

</body>
</html>

