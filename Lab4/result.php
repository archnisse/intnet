<?
include('db_connect.php');
#The search parameters that were chosen in search
$selectedLan = $_POST["lan"];
$selectedTyp = $_POST["typ"];
$maxprice = $_POST["maxprice"];
$numrooms = $_POST["numrooms"];
$minarea = $_POST["minarea"];


#Cookie parameters
$cookie_name = "user";
$cookie_value = $selectedLan.";".$selectedTyp.";".$maxprice.";".$numrooms.";".$minarea;
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
	$maxprice = $_POST["maxprice"];
	$numrooms = $_POST["numrooms"];
	$minarea = $_POST["minarea"];
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

/* echo("<h1>".$user."</h1>");
echo("<h1>".$userHidden."</h1>"); */

function changeSort($usrName, $name) {
	#Function that creates header buttons
	global $selectedLan, $selectedTyp, $ascdesc, $maxprice;//, $numrooms;
	echo("<form method='post'>
		<input type='submit' name='user' value=".$usrName.">
		<input type='hidden' name='userHidden' value=".$name.">
		<input type='hidden' name='ascdesc' value=".$ascdesc.">
		<input type='hidden' name='lan' value=".$selectedLan.">
		<input type='hidden' name='typ' value=".$selectedTyp.">
		<input type='hidden' name='maxprice' value=".$maxprice.">
		<input type='hidden' name='numrooms' value=".$numrooms.">
		<input type='hidden' name='minarea' value=".$minarea.">
		</form>"
		
		);
		
}

#Chosen search parameters
echo("Län: " . $selectedLan . "<br>");
echo("Typ: " . $selectedTyp . "<br>");
echo("Max pris: ".number_format($maxprice, 0, '', ' ')."<br>");
echo("Antal rum: " . $numrooms . "<br>");
echo("Min. area: " . $minarea . "<br>");
//echo("ascdesc: ". $ascdesc .", userHidden: ".$userHidden."<br>");
#Gets search result
$escaped = $conn -> real_escape_string(''.$userHidden.' '. $ascdesc.'');
$sql = $conn -> prepare('SELECT * FROM bostader WHERE lan=? AND objekttyp=? AND pris <=? AND rum=? AND area >=? ORDER BY '.$escaped.'');

$sql -> bind_param("ssiii", $selectedLan, $selectedTyp, $maxprice, $numrooms, $minarea); 
$sql -> execute();
//$arr = ["lan","objekt","adress","area","rum","pris","avgift"]
$sql -> bind_result($lan[0], $objekttyp[0], $adress[0], $area[0], $rum[0], $pris[0], $avgift[0]);
$i = 1;
while($sql->fetch()) {
	$sql -> bind_result($lan[$i], $objekttyp[$i], $adress[$i], $area[$i], $rum[$i], $pris[$i], $avgift[$i]);
	$i = $i + 1;
}

$sql -> close();
#$rs = $conn -> query($sql);
#$arr = $rs -> fetch_all(MYSQLI_ASSOC);

//echo(count($lan));
if (sizeof($lan) < 2) {
	#If a type doesn't exist in a county
	echo("<h1>Det finns inga '".$selectedTyp."' i ".$selectedLan." med pris lägre än '".$maxprice."' och ".$numrooms." rum med en area större än ".$minarea."m²</h1>");
} else {
	#If all search parameters OK, sets search results table
	echo("<table border='1'>");
	?>
	<tr>
	<td><?changeSort("Län", "lan");?></td>
	<td><?changeSort("Typ", "objekttyp");?></td>
	<td><?changeSort("Adress", "adress");?></td>
	<td><?changeSort("Yta", "area");?></td>
	<td><?changeSort("Rum", "rum");?></td>
	<td><?changeSort("Pris", "pris");?></td>
	<td><?changeSort("Avgift", "avgift");?></td>
	</tr>
	<?
	
	#Fills table
	for($i = 0; $i < count($lan) - 1; $i++) {
		echo("<tr>");
			echo("<td>".$lan[$i] . "</td>
			<td>" . $objekttyp[$i] . "</td>
			<td>" . $adress[$i] . "</td>
			<td>" . $area[$i] . "</td>
			<td>" . $rum[$i] . "</td>
			<td>" . number_format($pris[$i], 0, '', ' ') . "</td>
			<td>" . $avgift[$i] . "</td> <br>"
	
		  	);
		echo("</tr>");
	}
	echo("</table>");
}
?>

<br>

<!-- Button on result page that loads search again -->
<a href='search.php'>
	<button>Tillbaka</button>
</a>

</body>
</html>
