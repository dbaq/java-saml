# Basic java example of initiating a SAML authentication flow and consuming the resulting assertion.


## Requirements

Maven 3

/etc/hosts edited to have your return url to resolve to localhost

## Install

	mvn clean install
	mvn eclipse:eclipse

Edit the file src/main/webapp/index.jsp, line 10 and put your SID

## Run

	mvn jetty:run
	http://your_address:8080/java-saml