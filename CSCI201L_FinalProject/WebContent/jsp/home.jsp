<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="navbar.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

</head>
<body onload="checkValid();connectToServer();loadHomeData();">
<h1 id="loginTitle"><i class="large material-icons">library_music</i>iMusic Home</h1>
<div id="homeDiv">
	<div id="homeImg">
	<!-- pic -->
	</div>
	<div id="homeInfo">
	<span class="homerow" id="home_username"> <b>Username: </b> </span> <br/><br/>
	<span class="homerow" id="home_fname"> <b>Full Name: </b> </span> <br/><br/>
	<span class="homerow" id="home_bio"> <b>Biography: </b> </span> <br/><br/> 
	<form>
		<textarea style="display: none" id="textAreaBio"></textarea>
	</form>
	<button style="display: none" id="home_bio_save">Save</button>
	<button style="display: none" id="home_bio_edit">Edit</button>
	</div>
	<br/>
	<br/>
	<div id="homeSearchBarDiv">
	<div id="searchResults"></div>
	<form onsubmit="return false;">
			<input type ="text" placeholder="Search for a User!" id = "homeSearchBar">
<!-- 			<p>
		      <input name="group1" type="radio" id="songPref"/>
		      <label for="songPref">Song</label>
		    </p> -->
		    <p style="display:none">
		      <input name="group1" type="radio" id="userPref" checked="checked"/>
		      <label for="userPref">User</label>
		    </p>
		    <button class="waves-effect waves-light btn" id="homeSearchButton">Search</button>
	</form>
	</div>
	<br/>
	<br/>
	<div id="homeSongs">
	<table class="highlight">
        <thead>
          <tr>
              <th>Song</th>
 			  <th>Options</th>
          </tr>
        </thead>
        <tbody id="songTableList">
       
        </tbody>
     </table>
	</div>
	<br/>
	<br/>
	<button id="logoutButton" class="waves-effect waves-light btn">Logout</button>
	<!-- <button id="playButton" name = "playButton" onclick = "playSong();">Play</button>
	 --><!-- <button id="pauseButton" name = "pauseButton" onclick = "pauseSong();">Pause</button>-->
	<audio style="display: none" controls="controls">
	<source src= blobUrl type="audio/mp3">
	</audio>
</div>
</body>
</html>