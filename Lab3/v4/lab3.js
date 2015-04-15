$(document).ready(function(){
	
	var size = 10;
	var grid = new Array(size);
	var condition = 0.8;
	var shots = 0;
	var hits = 0;
	var ships = new Array(2,3,3,4,5);
	
	var createGrid = function(){ // Skapar en spelplan
		for (i = 1; i <= size; i++) {
			grid[i] = new Array(size);
			for (j = 1; j <= size; j++) {
				$("#squareBox").append("<div id="+i+";"+j+" class='square'></div>");
				grid[i][j] = {chosen: false, selected: false};
			}
			$("#squareBox").append("<div style='clear: both;'></div>");
		}
	}
	
	var generateRandom = function(){ // Slumpar riktning och startposition
		var direction = Math.round(Math.random());
		console.log("DIR: "+direction);
		//slumpa startposition
		var x0 = Math.round(Math.random()*(10-1)+1);
		var y0 = Math.round(Math.random()*(10-1)+1);
		console.log("y0, x0 "+y0,x0);



		return [direction, x0, y0];
	}

	

	var checkLength = function(x0, y0, direction, shipLength) { //Kollar om skeppet får plats utifrån x0,y0
		var endCoordinate
		if(direction==0) {
			endCoordinate = y0+shipLength;
			console.log("END y0: "+endCoordinate);
		} else if (direction==1) {
			endCoordinate= x0+shipLength;
			console.log("END x0: "+endCoordinate);
		}

		if(endCoordinate<=10) {
				return true;
		} else {
				return false
		}
	}

	var checkXY = function(x0,y0) { //Kollar om y0 och x0 är 1
		var y = y0-1;
		var x = x0-1;
		if(x==0) {
			x=1;
		} 
		if(y==0) {
			y=1;
		}

		return [x, y]
	}

	var placeShips = function() { //Placerar skeppen

		for(i=0; i<5; i++) {
			console.log("NYTT SKEPP nr "+i);
			placeringOK = false;
			while(!placeringOK) {
				//slumpa riktning. om 1 gå i y-led, om 0 gå i x-led
				var random = generateRandom();
				var direction = random[0];
				var x0 = random[1];
				var y0 = random[2];
				var conditionXY = checkXY(x0, y0);
				var x = conditionXY[0];
				var y = conditionXY[1];
				
				// kontrollera om skepp får plats inom grid utifrån sin startposition
				var shipLength = ships[i];
				console.log("Längdkontroll: "+checkLength(x0, y0, direction, shipLength));
				if(checkLength(x0, y0, direction, shipLength)) { 
					//vandra skeppets längd i slumpade riktningen
					//x-led (betecknas av y0 pga hur grid görs)
					if(direction==0 && grid[x0][y0].chosen==false) {
						//behöver bara kolla bakåt i första rutan, om okej stega framåt
						if(grid[y0][x].chosen==false) {
							for(j=1; j<=ships[i]; j++) {
								//kolla framåt, vänster och höger
								if(grid[y0+1][x0].chosen==false && grid[y0][x0+1].chosen==false && grid[y0][x].chosen==false) {
									grid[y0][x0].chosen=true;
									y0++;
								} else {
									//om fail sätt till false
									console.log("Kunde inte placera skepp");
									//Börja om från början med samma skepp, eller alla?
								}
							}
						}
					//y-led (betecknas av x0 pga hur grid görs)
					} else if(direction==1 && grid[y0][x0].chosen==false) {
						//behöver bara kolla bakåt i första rutan, om okej stega framåt
						if(grid[y][x0].chosen=false) {
							for(j=1; j<=ships[i]; j++) {
								//kolla framåt, vänster och höger
								if(grid[y0][x0+1].chosen==false && grid[y0+1][x0].chosen==false && grid[y][x0].chosen==false) {
									grid[y0][x0].chosen=true;
									x0++;
								} else {
									console.log("Kunde inte placera skepp");
									//Börja om från början med samma skepp, eller alla?
								}
							}
						}
					}

					placeringOK=true;
					console.log("Placerade skepp " + i);
					console.log("-----------------------------------");
				}
			}
		}
	
 
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
}


createGrid();
placeShips();


});
	
