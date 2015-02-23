$(document).ready(function(){
	var chosen = [""];
	for (i = 1; i <= 9; i++) {
		for (j = 1; j <= 9; j++) {
			$("#squareBox").append("<div id="+i+";"+j+" class='square'></div>");
			if (Math.random() > 0.8) {
				chosen[chosen.length] = ""+i+";"+j+"";
			}
		}
		$("#squareBox").append("<div style='clear: both;'></div>");
	}

 $(".square").click(function(){
	var set = false;
	for (i = 0; i < chosen.length; i++) {
		if (chosen[i] == $(this).attr("id")) {
			(this).style.background = "red";
			set = true;
		}
	}
        document.getElementById("textInBox").innerHTML = $(this).attr("id");
	/*if ($("#textBox").attr("id")) !=0 {
		$("#1").replaceWith(
	}
	 $("#textBox").append("<br>");*/
	if (!set) {
		(this).style.background = "green";
	}
 });
});

	
