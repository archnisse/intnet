$(document).ready(function(){
	
	var size = 9;
	var grid = new Array(size);;
	
	
	for (i = 1; i <= size; i++) {
		grid[i] = new Array(size);
		for (j = 1; j <= size; j++) {
			$("#squareBox").append("<div id="+i+";"+j+" class='square'></div>");
			grid[i][j] = {chosen: false, selected: false};
			if (Math.random() > 0.8) {
				grid[i][j].chosen = true;
			}
		}
		$("#squareBox").append("<div style='clear: both;'></div>");
	}

 $(".square").click(function(){
	var set = false;
	var id = $(this).attr("id");
	var x = id.split(";")[0];
	var y = id.split(";")[1];
	if (grid[x][y].chosen) {
		(this).style.background = "red";
	} else {
		(this).style.background = "green";
	}
    document.getElementById("textInBox").innerHTML = $(this).attr("id");
 });
});

	
