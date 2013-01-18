<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.onelogin.*,com.onelogin.saml.*,org.apache.log4j.Logger" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>SAML Assertion Page</title>
</head>
<body>
<%
String certificateS = "YOUR_BASE_64_CERTIFICATE";

  // user account specific settings. Import the certificate here
  AccountSettings accountSettings = new AccountSettings();
  accountSettings.setCertificate(certificateS);

  Response samlResponse = new Response(accountSettings);
  samlResponse.loadXmlFromBase64(request.getParameter("SAMLResponse"));

  if (samlResponse.isValid()) {
%>
	SAML Response is valid. The source is trusted.<br/>
  The SAML NameId is '<%= samlResponse.getNameId() %>'

<% } else { %>

	Failed. :(

<% } %>
</body>
</html>