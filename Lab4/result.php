<?
include('db_connect.php');
#The search parameters that were used
$selectedLan = $_POST["lan"];
$selectedTyp = $_POST["typ"];
#Cookie parameters
$cookie_name = "user";
$cookie_value = $selectedLan.";".$selectedTyp;
#Sets cookie
setcookie($cookie_name, $cookie_value, time() + (86400 * 30), "/"); 

?>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>NYA Hemnet</title>
</head>

<body>

<?php
#
if(isset($_POST["user"])) {
	$user = $_POST["user"];
	$userHidden = $_POST["userHidden"];
	
	#Switches between ascending and descending sorting
	if ($_POST["ascdesc"] == "asc") {
		$ascdesc = "desc";
	} else {
		$ascdesc = "asc";
	}
} else {
	$userHidden = "pris";
	$ascdesc = "asc";
}
echo("<h1>".$user."</h1>");
echo("<h1>".$userHidden."</h1>");

function changeSort($usrName, $name) {
	global $selectedLan, $selectedTyp, $ascdesc;
	echo("<form method='post'>
		<input type='submit' name='user' value=".$usrName.">
		<input type='hidden' name='userHidden' value=".$name.">
		<input type='hidden' name='ascdesc' value=".$ascdesc.">
		<input type='hidden' name='lan' value=".$selectedLan.">
		<input type='hidden' name='typ' value=".$selectedTyp.">
		</form>");
}


echo("Län: " . $selectedLan . "<br>");
echo("Typ: " . $selectedTyp . "<br>");

//$ascdesc = "DESC";

$sql = 'SELECT * FROM bostader WHERE lan="'.$selectedLan.'" AND objekttyp="'.$selectedTyp.'" ORDER BY '.$userHidden.' '.$ascdesc;
$rs = $conn -> query($sql);
$arr = $rs -> fetch_all(MYSQLI_ASSOC);

echo("<table border='1'>");
?><tr>
	<td><?changeSort("Län", "lan");?></td>
	<td><?changeSort("Typ", "objekttyp");?></td>
	<td><?changeSort("Adress", "adress");?></td>
	<td><?changeSort("Yta", "area");?></td>
	<td><?changeSort("Rum", "rum");?></td>
	<td><?changeSort("Pris", "pris");?></td>
	<td><?changeSort("Avgift", "avgift");?></td>
</tr><?
foreach($arr as $row) {
echo("<tr>");
	echo("<td>".$row[lan] . "</td><td>" . $row[objekttyp] . "</td><td>" . $row[adress] . "</td><td>" . $row[area] . "</td><td>" . $row[rum] . "</td><td>" . number_format($row[pris], 0, '', ' ') . "</td><td>" . $row[avgift] . "</td> <br>"
	
  	);
echo("</tr>");
}
echo("</table>");

?>

</body>
</html>
