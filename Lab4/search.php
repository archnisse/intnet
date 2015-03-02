<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">

<title>NYA Hemnet</title>
</head>


<body>

<? 
include('db_connect.php');
#Check if cookie exists
$cookie_name = 'user';
if(!isset($_COOKIE[$cookie_name])) {
    echo "Cookie named '" . $cookie_name . "' is not set!";
} else {
    echo "Cookie '" . $cookie_name . "' is set!<br>";
    echo "Value is: " . $_COOKIE[$cookie_name];
    $cookieValue = $_COOKIE[$cookie_name];
    $cookieValue = explode(";", $cookieValue);
    $cookieLan = $cookieValue[0];
    $cookieTyp = $cookieValue[1];
}
?>


<!-- form is the list of objects that are submitted (searchresult) -->
<form method='post' action='result.php'>
	<!-- lan field-->
	<select name='lan'>
	<?
	#gets all of the counties from the database
	$sql = 'SELECT distinct lan FROM bostader';
	$rs = $conn -> query($sql);
	$arr = $rs -> fetch_all(MYSQLI_ASSOC);
	
	foreach($arr as $row) {
		#Checks if there are saved settings from cookie
		if ($row[lan] == $cookieLan) {
			echo("<option value='".$row[lan]."' selected='selected'>".$row[lan]."</option>");
		} else {
			#Sets default settings
			echo("<option value='".$row[lan]."'>".$row[lan]."</option>");
		}
	}
	?>
	</select>
	
	<!-- objekttyp field-->
	<select name='typ'>
	<?
	#gets all of the object types from the database
	$sql = 'SELECT distinct objekttyp FROM bostader';
	$rs = $conn -> query($sql);
	$arr = $rs -> fetch_all(MYSQLI_ASSOC);
	
	foreach($arr as $row) {
		#Checks if there are saved settings from cookie
		if ($row[objekttyp] == $cookieTyp) {
			echo("<option value='".$row[objekttyp]."' 				selected='selected'>".$row[objekttyp]."</option>");
		} else {
			#Sets default settings
			echo("<option value='".$row[objekttyp]."'>".$row[objekttyp]."</option>");
		}
		
	}
	  
	?>
	</select>

	<!-- Submit button, leads to result page-->
	<input type='submit' value='Visa'>
</form>

</body>
</html>

