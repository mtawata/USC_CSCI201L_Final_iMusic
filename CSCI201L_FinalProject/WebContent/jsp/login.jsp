<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="navbar.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
</head>
<body onload="isLoggedIn();connectToServer();">
<h1 id="loginTitle"><i class="large material-icons">library_music</i>iMusic</h1>
<h4 id="loginSubTitle">Collaborate, create, and share music with others.</h4>
<div id="loginForm">
	<h4>Login</h4>
	<form onsubmit="return false;">
			<div style = "color:green" id = "registerMessage"></div><br/>
			<input type = "text" placeholder="Username" id = "username"><br/>
			<div style = "color:red" id = "userError"></div><br/>
			<input type = "password" placeholder="Password" id = "password"><br/>
			<div style = "color:red" id = "passError"></div><br/>
			<button id="loginButtonSubmission" class="btn waves-effect waves-light">Login
    			<i class="material-icons right">center_focus_weak</i>
  			</button>
  			<a id="registerButtonRedirect" class="btn waves-effect waves-light" href="${pageContext.request.contextPath}/jsp/register.jsp">Register
    			<i class="material-icons right">center_focus_weak</i>
  			</a>
  			<button id="guestButtonRedirect" class="btn waves-effect waves-light">Login as Guest
    			<i class="material-icons right">center_focus_weak</i>
  			</button>
			<div id = "success"></div>
	</form>
</div>
</body>
</html>