$(document).ready(function(){
	
	var size = 10;
	var grid = [];
	var condition = 0.8;
	var shots = 0;
	var hits = 0;
	var ships = [2,3,3,4,5];
	
	var createGrid = function(){ // Skapar en spelplan
		var grid = [];
		for (row=0;row<size;row++){
			grid.push([]);
			for (column=0;column<size;column++){
				grid[row].push({chosen: false, selected: false, coords: {row: row, col: column}});
				$("#squareBox").append("<div id=row:"+row+";col:"+column+" class='square'></div>");
			}
			$("#squareBox").append("<div style='clear: both;'></div>");
		}
		return grid
	}
	
	var generateRandom = function(){ // Slumpar riktning och startposition
		var direction = Math.round(Math.random());
		// console.log("DIR: "+direction);
		//slumpa startposition
		var x0 = Math.floor(Math.random()*(size));
		var y0 = Math.floor(Math.random()*(size));
		// console.log("y0, x0 "+y0,x0);



		return [direction, x0, y0];
	}

	

	var checkLength = function(startPosition, ship) { //Kollar om skeppet får plats utifrån x0,y0
		var dir = startPosition[0];
		var startX = startPosition[1];
		var startY = startPosition[2];
		var shipLength = ships[ship];
		// console.log("Start: "+startX+" "+startY);
		if(dir==0) { // Horisontell
			var endCoordinate = (startX+shipLength)-1;
			// console.log("Slut: "+endCoordinate+" "+startY);
		} else if (dir==1) { // Vertikal
			var endCoordinate = startY+(shipLength-1);
			// console.log("Slut: "+startX+" "+endCoordinate);

		}

		if(endCoordinate<10) {
			return true;
		} else {
			return false;
		}
	}

	var checkXY = function(x0,y0) { //Kollar om y0 och x0 är 1
		var y = y0-1;
		var x = x0-1;
		if(x==0) {
			x=1;
			console.log("check x: "+x);
		} 
		if(y==0) {
			y=1;
			console.log("check y: "+y);
		}

		return [x, y];
	}

	var placeShips = function() {
		for (ship in ships){
			var startPosition = generateRandom(); // Generera startposition
			var shipLength = ships[ship];
			var startX = startPosition[1];
			var startY = startPosition[2];
			var okSquares = [];
			if(checkLength(startPosition, ship)){ // Kolla om skeppet kommer gå utanför spelplanen
				// skeppet går innanför, yaaay, ella är sötast
				if (startPosition[0]===0){ // Om horisontell
					// Utför kontroll om något ligger på rutorna redan
					for (i=0;i<shipLength;i++){
						console.log("Längd: "+shipLength+" Provar ruta: "+(startX+i)+"; "+startY);
						//spara ok rutors koordinater i en array, om array.size == shiplength - sätt alla koordinater till chosen och nollställ array
						if(grid[startX+i][startY].chosen){
							// om rutan är tagen
							// slumpa ny startposition
							
							okSquares.length=0;
						} else{
							// rutan är ledig, sparar rutans koordinater i en array
							var x = startX+i;
							okSquares.push(x);
							console.log("OK SQUARE X: "+x);
							//om alla rutor som kollas är okej så sätts de till true
							if(okSquares.length==shipLength) {
								for(i=0;i<shipLength;i++) {
									x = okSquares[i];
									grid[x][startY].chosen=true;

								}
								okSquares.length=0;
							}
							// gå vidare till nästa ruta
						}
					}
				} else{ // Vertikal
					// Utför kontroll om något ligger på rutorna redan
					for (i=0;i<shipLength;i++){
						console.log("Längd: "+shipLength+" Provar ruta: "+(startX)+"; "+(startY+i));
						console.log(grid[startX][startY+i].chosen);

						if(grid[startX][startY+i].chosen){
							// om rutan är tagen
							// slumpa ny startposition
							
							okSquares.length=0;
						} else{
							// rutan är ledig, sparar rutans koordinater i en array
							var y= startY+i;
							okSquares.push(y);
							console.log("OK SQUARE Y: "+y);
							//om alla rutor som kollas är okej så sätts de till true
							if(okSquares.length==shipLength) {
								for(i=0;i<shipLength;i++) {
									y = okSquares[i];
									grid[startX][y].chosen=true;

								}
								okSquares.length=0;
							}
							// gå vidare till nästa ruta
						}
					} 
				}
			} else{
				console.log("Skepp nr "+ship+" gick utanför spelplanen")
				// skeppet går utanför spelplanen
				// slumpa om ny startposition
			}
		}
	};
	
 
 $(".square").click(function(){
 	shots = shots + 1;
	var id = $(this).attr("id");
	var x = id.split(";")[1];
	var y = id.split(";")[0];
	
	if (grid[x][y].chosen) {
		(this).style.background = "red";
		if (grid[y][x].selected==false) {
				hits = hits+1;
		}
	} else {
		(this).style.background = "green";
	}

	/*for (i = 0; i < size; i++) {
		for (j = 0; j < size; j++) {
			if (grid[i][j].chosen==true) {
				console.log("Y, X: "+y, x)
			}
		}
	}*/

    document.getElementById("textInBox").innerHTML = x+";"+y + "<br> shots: "+shots+"<br> hits: "+hits ;
    grid[x][y].selected = true;
    return false;
 });



var grid = createGrid();
placeShips();


});
	
