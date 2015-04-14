$(document).ready(function(){
	
	var size = 9;
	var grid = new Array(size);
	var condition = 0.8;
	var shots = 0;
	var hits = 0;
	var ships = new Array(2,3,3,4,5);
	
	for (i = 1; i <= size; i++) {
		grid[i] = new Array(size);
		for (j = 1; j <= size; j++) {
			$("#squareBox").append("<div id="+i+";"+j+" class='square'></div>");
			grid[i][j] = {chosen: false, selected: false};
		}
		$("#squareBox").append("<div style='clear: both;'></div>");
	}

	//måste vara 1 ruta emellan
	// Array med 5 skepp
		//1 med längd 5
		//2 med längd 3
		//1 med längd 2
		//1 med längd 4
	
	for(i=0; i<5; i++) {
		//slumpa riktning. om 1 gå i y-led, om 0 gå i x-led
		var direction = Math.round(Math.random());
		//slumpa startposition
		var xorigin = Math.round(Math.random()*(9-1)+1);
		var yorigin = Math.round(Math.random()*(9-1)+1);
		//vandra skeppets längd i slumpade riktningen
		if(direction==0) {
			for(j=1; j<=ships[i]; j++) {
				//Kolla kors varje ruta innan chosen sätts false/true
				grid[yorigin][xorigin+j].chosen=true;
			}
		} else {
			for(j=1; j<=ships[i]; j++) {
				grid[yorigin+j][xorigin].chosen=true;
			}
		}
	}
		//för varje ruta: kolla vänster, uppåt, nedåt, högerr







		

/* $("#switch").click(function() {
 	// Wooo do stuff with the grid.
 	var string = "textInBox";
 	shots = 0;
 	hits = 0;
 	document.getElementById(string).innerHTML = "Switch!";

 	for (i = 1; i <= size; i++) {
		for (j = 1; j <= size; j++) {
			var idString = ""+i+";"+j+"";
			document.getElementById(idString).style.background = "#65a9d7";
			if (grid[i][j].selected) {
				grid[i][j].chosen = true;
			} else {
				grid[i][j].chosen = false;
			}
			grid[i][j].selected = false;
		}
	}
 });*/
 
 $(".square").click(function(){
 	shots = shots + 1;
	var id = $(this).attr("id");
	var x = id.split(";")[0];
	var y = id.split(";")[1];
	



	if (grid[x][y].chosen) {
		(this).style.background = "red";
		if (grid[x][y].selected==false) {
				hits = hits+1;
		}
		

	} else {
		(this).style.background = "green";
		

	}
	
    document.getElementById("textInBox").innerHTML = $(this).attr("id") + "<br> shots: "+shots+"<br> hits: "+hits ;

    grid[x][y].selected = true;
    return false;
 });






});
	
