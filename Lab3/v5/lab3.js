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
				$("#squareBox").append("<div id="+row+";"+column+" class='square'></div>");
			}
			$("#squareBox").append("<div style='clear: both;'></div>");
		}
		return grid;
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

	

	var checkLength = function(startPosition, ship) { //Kollar om skeppet får plats inom grid
		var dir = startPosition[0];
		var startX = startPosition[1];
		var startY = startPosition[2];
		var shipLength = ships[ship];

		// console.log("Start: "+startX+" "+startY);
		if(dir==0) { // Horisontell
			var plusOne = startX;
			var endCoordinate = (startX+shipLength)-1;
			if(endCoordinate==9) {
				plusOne=9;
			} else {
				plusOne = endCoordinate+1;
			}


//Kolla oxå om diagonalt bakåt är OK

			// console.log("Slut: "+endCoordinate+" "+startY);
			if(endCoordinate<10 && !grid[plusOne][startY].chosen) {
				return true;
			} else {
				return false;
			}


		} else if (dir==1) { // Vertikal
			var endCoordinate = startY+(shipLength-1);
			var plusOne = startY;
			if(endCoordinate==9) {
				plusOne=9;
			} else {
				plusOne = endCoordinate+1;
			}

//Kolla oxå om diagonalt bakåt är OK

			// console.log("Slut: "+startX+" "+endCoordinate);
			if(endCoordinate<10 && !grid[startX][plusOne].chosen) {
				return true;
			} else {
				return false;
			}

		}

		


		
	}

	var checkXY = function(startPosition) { //Hanterar gränsfall i grid
		
		var startX=startPosition[1];
		var startY=startPosition[2];
		if (startPosition[0]==0){ // Om horisontell
			// Hantera gränsfallen 0 och 9 för Y
			if(startY===9) {
				var startYplus=9;
				var startYminus=startY-1;
			} else if(startY===0) {
				var startYminus=0;
				var startYplus=startY+1;
			} else if(startY<9 && startY>0){
				var startYplus=startY+1;
				var startYminus=startY-1;
			}
			//Hantera gränsfallet 0 för X
			if(startX==0) {
				var startXminus = 0;
			} else {
				var startXminus=startX-1;
			}


			//Hantera gränsfall vid diagonal koll



			return [startXminus, startYminus, 0, startYplus, XdiagPlus, xdiagMinus];

		} else { //Vertikal
			// Hantera gränsfallen 0 och 9 för X
			if(startX===9) {
				var startXplus=9;
				var startXminus=startX-1;
								
			} else if(startX===0) {
				var startXplus=startX+1;
				var startXminus=0;
			} else if(startX<9 && startX>0){
				var startXplus=startX+1;
				var startXminus=startX-1;
			}

			//Hantera gränsfallet 0 för Y
			if(startY==0) {
				var startYminus =0;
			} else {
				startYminus=startY-1;
			}



			return [startXminus, startYminus, startXplus, 0];


		}


		
		
	}


	var placeShips = function() {	// Placerar skepp i grid
		for (ship in ships){
			var placeShipOK = false;
			while (placeShipOK != true) {
				var startPosition = generateRandom(); // Generera startposition
				var shipLength = ships[ship];
				var startX = startPosition[1];
				var startY = startPosition[2];
				var okSquares = [];
				//Hanterar gränsfall
				var borderCheck = checkXY(startPosition);
				var startXminus = borderCheck [0];
				var startYminus = borderCheck [1];
				var startXplus = borderCheck [2];
				var startYplus = borderCheck [3];  

				
			
				if(checkLength(startPosition, ship)){ // Kolla om skeppet kommer gå utanför spelplanen
					// skeppet går innanför

					if (startPosition[0]===0){ // Om horisontell
						//kolla bakåt behövs endast första gången
						if(!grid[startXminus][startY].chosen) {
							for (i=0;i<shipLength;i++){
								console.log("Längd: "+shipLength+" Provar ruta: "+(startX+i)+"; "+startY);
								//kolla runtomkring
								if(grid[startX+i][startY].chosen || grid[startX+i][startYminus].chosen || grid[startX+i][startYplus].chosen
									|| grid[startX+i+1][startYplus].chosen || grid[startX+i+1][startYminus].chosen) {
									// om rutan är tagen
									// slumpa ny startposition
									console.log("----------------------------------------");
									console.log("Skepp nr "+ship+" kunde ej placeras");
									console.log("----------------------------------------");
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
										console.log("----------------------------------------");
										console.log("SKEPP "+ship+" PLACERAT");
										console.log("----------------------------------------");
										//nollställer array
										okSquares.length=0;
										//går ur while-loopen, placering har lyckats
										placeShipOK=true;
									}
										
								}
							}
						}

					} else if(startPosition[0]===1){ // Vertikal
						

						//kolla bakåt behövs endast första gången
						if(!grid[startX][startYminus].chosen) {

							for (i=0;i<shipLength;i++){
								console.log("Längd: "+shipLength+" Provar ruta: "+(startX)+"; "+(startY+i));
								//console.log(grid[startX][startY+i].chosen);
								//console.log("startY: " + startY + " startXminus: " +startXminus + " startXplus: "+startXplus);
								if(grid[startX][startY+i].chosen || grid[startXminus][startY+i].chosen ||grid[startXplus][startY+i].chosen
									|| grid[startXplus][startY+i+1].chosen || grid[startXminus][startY+i+1].chosen){
									// om rutan är tagen
									// slumpa ny startposition
									console.log("----------------------------------------");
									console.log("Skepp nr "+ship+" kunde ej placeras");
									console.log("----------------------------------------");
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
									console.log("----------------------------------------");
									console.log("SKEPP "+ship+" PLACERAT");
									console.log("----------------------------------------");
									//tömmer array
									okSquares.length=0;
									//går ur while-loop, placering lyckad
									placeShipOK=true;
									}

									}
								}
							} 
						}
					} 
				}
		}
	}
	
 
 $(document).on("click", ".square", function(){
 	//console.log("click");
 	shots = shots + 1;
	var id = $(this).attr("id");
	var x = id.split(";")[0]; //row
	var y = id.split(";")[1]; //col
	//console.log("x "+x+" "+"y "+y);
	//console.log(grid[x][y].chosen);
	if (grid[x][y].chosen) {
		(this).style.background = "red";
		if (grid[x][y].selected==false) {
				hits = hits+1;
		}
	} else {
		(this).style.background = "#87CEFA";
	}


    document.getElementById("textInBox").innerHTML = x+";"+y + "<br> shots: "+shots+"<br> hits: "+hits ;
    grid[x][y].selected = true;
    return false;
 });



var grid = createGrid();
placeShips();





});
	

