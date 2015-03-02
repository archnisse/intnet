$(document).ready(function(){
	
	var size = 9;
	var grid = new Array(size);;
	var condition = 0.8;
	var shots = 0;
	var hits = 0;
	
	for (i = 1; i <= size; i++) {
		grid[i] = new Array(size);
		for (j = 1; j <= size; j++) {
			$("#squareBox").append("<div id="+i+";"+j+" class='square'></div>");
			grid[i][j] = {chosen: false, selected: false};
			if (Math.random() > condition) {
				grid[i][j].chosen = true;
			}
		}
		$("#squareBox").append("<div style='clear: both;'></div>");
	}

 $("#switch").click(function() {
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
 });
 
 $(".square").click(function(){
 	shots = shots + 1;
	var id = $(this).attr("id");
	var x = id.split(";")[0];
	var y = id.split(";")[1];
	if (grid[x][y].chosen) {
		(this).style.background = "red";
		if (!grid[x][y].selected) {
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

	
