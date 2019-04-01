<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="navbar.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script>
	function submitSong(){
		var songName = document.getElementById("file").value;
		songName = songName.substring(12);
		sendSongName(songName);
		submit();
	}
</script>
</head>
<body onload="connectToServer()">
		<h1 id="songuploadTitle">Upload a song to your iMusic portfolio!</h1>
		<div id="songuploadForm">
			<h4 style="color:red" id="songuploadwarning"></h4>
			<h4>Please upload an .mp3 file and we will handle the rest!</h4>
			<form onsubmit="return false;" enctype = "multipart/form-data">
				<div class="file-field input-field">
	      			<div class="btn">
				        <span>File</span>
				        <input type="file" id="file" accept=".mp3">
			      	</div>
			      	<div class="file-path-wrapper">
			        		<input class="file-path validate" type="text" placeholder="Upload a single .mp3 file">
			      	</div>
			    </div>
				<input style="padding-top: 7.5px" type ="button" name = "butttton" value = "Click to upload!" id="songUploadSubmission" class="btn waves-effect waves-light" onclick="submitSong();"/>
			</form>
		</div>
</body>
</html>