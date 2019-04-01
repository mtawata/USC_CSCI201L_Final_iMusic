<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="navbar.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
</head>
<body onload="connectToServer()">
<h1 id="loginTitle"><i class="large material-icons">library_music</i>iMusic</h1>
<h4 id="loginSubTitle">Collaborate, create, and share music with others.</h4>
<div id="registerForm">
	<h4>Register</h4>
	<form onsubmit = "return false">
			<div style = "color:red" id = "registerError"></div><br/>
			<input type = "text" placeholder="Username" id = "username"><br/>
			<input type = "text" placeholder="Full Name" id = "fullname"><br/>
			<input type = "password" placeholder="Password" id = "password"><br/>
			<input type = "password" placeholder="Re-enter Password" id = "reenterpassword"><br/>
			<button id="registerButtonSubmission" class="btn waves-effect waves-light">Register
    			<i class="material-icons right">center_focus_strong</i>
  			</button>
			<div id = "success"></div>
	</form>
</div>
</body>
</html>