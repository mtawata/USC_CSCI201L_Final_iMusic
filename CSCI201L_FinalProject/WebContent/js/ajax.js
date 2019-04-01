var socket;
var srcElement;

function connectToServer() {
	socket = new WebSocket("ws://localhost:8080/CSCI201L_FinalProject/socket");
	socket.onopen = function(event) {
		console.log("connected!");
	}
	
	socket.onmessage = function(event) {
		var message = event.data;
		console.log(message);
		if(message instanceof Blob){
			var blobUrl = URL.createObjectURL(message);
			srcElement = document.getElementsByTagName("source")[0];
			srcElement.src = blobUrl;
			console.log("blob received");
			srcElement = new window.Audio();
			srcElement.src = window.URL.createObjectURL(message);
			srcElement.play();
		}else{
			var type = message.substring(0,1);
			var content = message.substring(2);
			if (type === "0") {
				console.log("message for login: " + content + ".");
				if (content === "username") {
					document.cookie = "valid=false";
					document.getElementById("userError").innerHTML = "Username does not exist";
					document.getElementById("passError").innerHTML = "";
				} else if (content === "password") {
					document.cookie = "valid=false";
					document.getElementById("userError").innerHTML = "";
					document.getElementById("passError").innerHTML = "Password is incorrect";
				} else {
					document.cookie = "valid=true";
					window.location.replace("/" + window.location.pathname.split("/")[1] + "/jsp/home.jsp");
				}
			} else if (type == "1") {
				console.log("message for register: " + content);
				if (content === "false") {
					document.getElementById("registerError").innerHTML = "All fields must be filled and 9-25 characters. Username may already be taken";
				} else if (content === "true") {
					document.getElementById("registerError").innerHTML = "Registration successful!  You may now login.  Redirecting to login in five seconds.";
					window.setTimeout(function() {
						window.location.replace("/" + window.location.pathname.split("/")[1] + "/jsp/login.jsp");
					}, 5000);
				}
			} else if (type == "2") {
				var array = content.split(',');
				var result = "";

				document.getElementById("resultTable").innerHTML = content;
			} else if (type == "3") {
				//no response
			} else if (type == "4") {
				var array = content.split(',');
				var result = "<br /><br /><table class=responsive-table><thead><tr><th>Results</th></tr></thead>";
				var num = 1;
				arr.forEach(function(song) {
					result += "<tr><a href=" + song + "> Song " + num++ + "</a></tr>";
				})
				result += "</table>";
				document.getElementById("songTable").innerHTML = result;
			} else if (type == "5") {
				
			} else if (type == "6") {
				var array = content.split(',');
				var result = "<br /><br /> <span class=\"homerow\"> <b>Results:</b> </span> <br /> <table class=responsive-table>";
				var num = 1;
				if (content.includes("Error")) {
					result += "No user was found.";
					result += "</table>";
					document.getElementById("searchResults").innerHTML = result;
				}
				else {
					document.cookie = "visiting_username=" + array[1];
					window.location.replace("/" + window.location.pathname.split("/")[1] + "/jsp/user.jsp");
				}
			}
			else if (type == "7") {
				$("#songTableList").empty();
				console.log(content);
				var array = content.split(',');
				$("#home_username").append(array[1]);
				$("#home_fname").append(array[0]);
				$("#home_bio").append(array[3]);
				for (var i = 4; i < array.length - 1; i++) {
					if (array[i] != "null") {
					$("#songTableList").append("<tr><td>" +
							array[i] + "</td><td>" + 
							"<button onclick=playSong('" + array[i] + "')>Play</button>"
							+ "<button onclick=pauseSong()>Pause</button>"
							+ "<button onclick=resumeSong()>Resume</button>"
							+ "</td></tr>");
					}
				}
			} 
			else if (type == "9"){
				console.log(content);
				window.alert(content);
			}
		}
		
	}
	socket.onclose = function(event) {
		console.log("disconnected!");
		connectToServer();
	}
}

function sendMessage(val, string) {
	socket.send(val + "," + string);
	return false;
}

function playSong(song){
	console.log("play");
	sendMessage(5,getCookie("username") + "," + song);
}

function pauseSong(){
	srcElement.pause();
}

function resumeSong() {
	srcElement.play();
}
		
function loadVisitedData() {
	window.setTimeout(function() {
		sendMessage(7, getCookie("visiting_username"));
	}, 1000);
}

function loadHomeData() {
	// if the document is ready, check if its the home page, if so, populate with correct data
	if (getCookie("username") != "guest" && checkValid()) {
		var username = getCookie("username");
		window.setTimeout(function() {
			sendMessage(7, username);
		}, 1000);
		$("#home_bio_edit").show();
	}
	else {
		$("#home_username").append("Guest");
		$("#home_fname").append("Guest");
		$("#home_bio").append("You're a guest, so you have no bio!");
		$("#home_bio_edit").hide();
	}
}
$( document ).ready(function() {
	if (getCookie("username") == "guest" || getCookie("valid") == "false") {
		$("#songuploadForm").hide();
		$("#songuploadTitle").html("You can't upload a song if you're a guest. :(");
	}
	
	$("#home_bio_edit").click(function() {
		console.log("PLS");
		$("#home_bio_save").show();
		$("#textAreaBio").show();
	});
	
	$("#home_bio_save").click(function() {
		$("#home_bio_save").hide();
		$("#textAreaBio").hide();
		sendMessage(8, getCookie("username") + "," + $("#textAreaBio").val());
		$("#home_username").html("<b>Username:</b>");
		$("#home_fname").html("<b>Full Name:</b>");
		$("#home_bio").html("<b>Biography:</b>");
		loadHomeData();
	})
	
	$("#loginButtonSubmission").click(function() {
		document.getElementById("userError").innerHTML = "";
		document.getElementById("passError").innerHTML = "";
		var currentUsername = $("#username").val();
		sessionStorage.setItem("currentUsername",currentUsername);
		var error = false;
		clearCookie("username");
		clearCookie("valid");
		document.cookie = "username=" + $("#username").val();
		if ($("#username").val().length == 0 || $("#username").val().length > 25) {
			document.getElementById("userError").innerHTML = "Username does not exist";
			error = true;
		}
		if ($("#password").val().length < 9) {
			document.getElementById("passError").innerHTML = "Password is incorrect";
			error = true;
		}
		if (error == false) {
			sendMessage(0, $("#username").val() + "," + Sha256.hash($("#password").val()));
		}
	});
	
	$("#registerButtonSubmission").click(function() {
		if ($("#password").val().length < 9) { 
			document.getElementById("registerError").innerHTML = "All fields must be filled and 9-25 characters. Password must be longer than 8 characters";
		} else {
			sendMessage(1, $("#username").val() + "," + Sha256.hash($("#password").val()) + "," + Sha256.hash($("#reenterpassword").val()) + "," + $("#fullname").val());
		}
	});
	
	$("#guestButtonRedirect").click(function() {
		clearCookie("username");
		clearCookie("valid");
		document.cookie = "username=guest";
		document.cookie = "valid=true";
		window.location.replace("/" + window.location.pathname.split("/")[1] + "/jsp/home.jsp");
	});

	$("#homeSearchButton").click(function() {
		var username = getCookie("username");
		sendMessage(6, $("#homeSearchBar").val());
	});

	$("#logoutButton").click(function() {
		clearCookie("username");
		clearCookie("valid");
		document.cookie = "valid=false";
		window.location.replace("/" + window.location.pathname.split("/")[1] + "/jsp/login.jsp");
	});
});


function submit(){
	var file = document.querySelector('input[type="file"]').files[0];
	socket.send(file);
	$("#songuploadwarning").append("Your song has been uploaded!");
	console.log("The song has been sent");
}


function sendSongName(songName){
	var currentUsername = getCookie("username"); 
	socket.send("3,"+currentUsername+","+songName)
}

//returns cookie with given name
function getCookie(cookiename) {
	var name = cookiename + "=";
	var cookieString = decodeURIComponent(document.cookie);
	var cookieArray = cookieString.split(';');
	for (var i = 0; i < cookieArray.length; i++) {
		var c = cookieArray[i];
		while (c.charAt(0) == ' ') {
			c = c.substring(1);
		}
		if (c.indexOf(name) == 0) {
			return c.substring(name.length, c.length);
		}
	}
	return "";
}

//removes/expires cookies
function clearCookie(cookiename) {
	document.cookie = cookiename + "=; expires=Thu, 01 Jan 1970 00:00:00 GMT";
}

//checks if valid cookie is set to true
function checkValid() {
	var valid = getCookie("valid");
	if (valid == "false") {
		window.location.replace("/" + window.location.pathname.split("/")[1] + "/jsp/login.jsp");
		return false;
	}
	else if (valid == "true") {
		return true;
	}
}

//checks if valid cookie
function isLoggedIn() {
	var valid = getCookie("valid");
	if (valid == "true") {
		window.location.replace("/" + window.location.pathname.split("/")[1] + "/jsp/home.jsp");
	}
}

function populateUserPage() {
	var searchedUser = getCookie("searchedUser");
	clearCookie("searchedUser");
	sendMessage(6, searchedUser);
}