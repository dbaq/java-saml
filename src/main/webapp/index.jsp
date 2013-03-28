<%@page import="java.util.Date,org.apache.log4j.Logger,org.apache.commons.lang.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.dbaq.saml.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Auth Request</title>
  <%
    AuthRequest authReq = new AuthRequest("YOUR ISSUER", 18);
	String ssoUrl = "http://auth-int.orange.fr/sso";
  %>
</head>
<body>
	<h1>SAML V2 Auth test</h1>
  <form method="GET" action="<%= ssoUrl %>">
    <input type="hidden" name="SAMLRequest" value="<%= authReq.getEncodedRequest()%>" />
    <input type="submit" value="Connect" />
  </form>
	<a href="<%= ssoUrl %>?SAMLRequest=<%= authReq.getEncodedRequest()%>" title="">Or click on this link.</a>

  <div>
   <h2>SAML Auth request to be submitted</h2>
   <code><%= StringEscapeUtils.escapeXml(new String(authReq.getXMLRequest())) %></code>
  </div>
  <div>
   <h2>Encoded SAML Auth request to be submitted</h2>
   <code><%= authReq.getEncodedRequest()%></code>
  </div>

</body>
</html>