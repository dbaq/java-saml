package com.dbaq.saml;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.xml.stream.XMLStreamException;

import org.w3c.dom.Element;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.*;
import org.opensaml.saml2.core.impl.AuthnRequestBuilder;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.NameIDPolicyBuilder;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper; 

import org.apache.commons.codec.binary.Base64;

public class AuthRequest{
  private String id;
  private DateTime date;
  private String issuer;
  private int aci;
  private static boolean isBootstrapped = false;
  private final static DateFormat dateFormat;

  static {
    dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  public AuthRequest(){
	if (!isBootstrapped) {
		try {
				DefaultBootstrap.bootstrap();
				isBootstrapped = true;
		} catch (ConfigurationException e) { 
				//TODO
		}
	} 
	this.id = UUID.randomUUID().toString();
	this.aci = 0;
  }

  public AuthRequest(String issuer) {
	this();
    this.issuer = issuer;
    this.date = new DateTime();
  }
  
  public AuthRequest(String issuer, DateTime date) {
	this();
    this.issuer = issuer;
    this.date = date;
  }
  
  public AuthRequest(String issuer, int aci) {
	this();
    this.issuer = issuer;
    this.date = new DateTime();
    this.aci = aci;
  }
  
  public AuthRequest(String issuer, DateTime date, int aci) {
	this();
    this.issuer = issuer;
    this.date = date;
    this.aci = aci;
  }
 
  /**
   * Creates an XML AuthnRequest 
   * @return a byte array of the XML AuthnRequest 
   */
  public byte[] getXMLRequest(){
	  	AuthnRequest authnRequest = this.buildAuthnRequest();
		Marshaller marshaller = org.opensaml.Configuration.getMarshallerFactory().getMarshaller(authnRequest);
		Element authnRequestDOM = null;
		try {
			authnRequestDOM = marshaller.marshall(authnRequest);
		} catch (MarshallingException e) {
			// TODO NEED TO MAKE A SWEET EXCEPTION
			e.printStackTrace();
		}
		StringWriter writer = new StringWriter();
		XMLHelper.writeNode(authnRequestDOM, writer);
		return writer.toString().getBytes();
  }
  
  public String getEncodedRequest() throws XMLStreamException {
	    return encodeSAMLRequest(getXMLRequest());
  }
  
  /**
   * Creates an AuthnRequest 
   * @return an AuthnRequest
   */
  private AuthnRequest buildAuthnRequest() {
		XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();
		AuthnRequestBuilder authnBuilder = (AuthnRequestBuilder) builderFactory.getBuilder(AuthnRequest.DEFAULT_ELEMENT_NAME);
		IssuerBuilder issueBuilder = (IssuerBuilder) builderFactory.getBuilder(Issuer.DEFAULT_ELEMENT_NAME);

		AuthnRequest authnRequest = authnBuilder.buildObject();
		authnRequest.setAttributeConsumingServiceIndex(this.aci);  
		authnRequest.setID(this.id);
		authnRequest.setIssueInstant(this.date);
		authnRequest.setVersion(SAMLVersion.VERSION_20);
		//issuer
		Issuer issuer = issueBuilder.buildObject();
		issuer.setValue(this.issuer);
		authnRequest.setIssuer(issuer);
		//nameIDPolicy
	    NameIDPolicyBuilder nameIdPolicyBuilder = new NameIDPolicyBuilder();
	    NameIDPolicy nameIdPolicy = nameIdPolicyBuilder.buildObject(); 
	    nameIdPolicy.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent");
	    nameIdPolicy.setAllowCreate(true);
	    authnRequest.setNameIDPolicy(nameIdPolicy);

		return authnRequest;
  }

  /**
   * Encodes a 
   * @param pSAMLRequest
   * @return
   * @throws RuntimeException
   */
  private String encodeSAMLRequest(byte[] pSAMLRequest) throws RuntimeException {
    Base64 base64Encoder = new Base64();
    try {
      ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
      Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);

      DeflaterOutputStream def = new DeflaterOutputStream(byteArray, deflater);
      def.write(pSAMLRequest);
      def.close();
      byteArray.close();

      return new String(base64Encoder.encode(byteArray.toByteArray()));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
