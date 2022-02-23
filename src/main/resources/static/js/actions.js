
function onFileSelected(e) {
	var selectedFile = e.target.files[0];
	//validation for max file size limit 
	if (selectedFile.size > 1048576) {
		alert("Maximum file size to upload is 1MB. Please try to reduce its resolution to make it under 1MB.")
	} else {
		var reader = new FileReader();
		var imgtag = document.getElementById("ocrimg");

		imgtag.title = selectedFile.name;

		reader.onload = function(event) {
			imgtag.src = event.target.result;
		};
		reader.readAsDataURL(selectedFile);



	}
}

function loadingResult() {
	document.getElementById("resultpanel").classList.add("is-loading");
	document.getElementById("submitbtn").classList.add("is-loading");
}
