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
		var x0 = Math.round(Math.random()*(9-1)+1);
		var y0 = Math.round(Math.random()*(9-1)+1);
		//vandra skeppets längd i slumpade riktningen
		//x-led (betecknas av y0 pga hur grid görs)
		if(direction==0 && grid[x0][y0].chosen==false) {
			//Kolla kors varje ruta innan chosen sätts false/true
			//behöver bara kolla bakåt i första rutan, om okej stega framåt
			if(grid[y0-1][x0].chosen==false) {
				for(j=1; j<=ships[i]; j++) {
					//kolla framåt, vänster och höger
					if(grid[y0+1][x0].chosen==false && grid[y0][x0+1].chosen==false && grid[y0][x0-1].chosen==false) {
						grid[y0][x0].chosen=true;
						y0++
					} else {
						//Börja om från början med samma skepp, eller alla?
					}
				}
			}
		}
	
	//y-led, betecknas av x0
	} if(direction==1 && grid[y0][x0].chosen==false) {
			//Kolla kors varje ruta innan chosen sätts false/true
			//behöver bara kolla bakåt i första rutan, om okej stega framåt
			if(grid[y0][x0-1].chosen=false) {
				for(j=1; j<=ships[i]; j++) {
					//kolla framåt, vänster och höger
					if(grid[y0][x0+1].chosen==false && grid[y0+1][x0].chosen==false && grid[y0-1][x0].chosen==false) {
						grid[y0][x0].chosen=true;
						x0++
					} else {
						//Börja om från början med samma skepp, eller alla?
					}
				}
			}
		}
	

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
	
