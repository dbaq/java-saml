package com.onelogin.saml;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.codec.binary.Base64;

import com.onelogin.AppSettings;
import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;

public class AuthRequest {
  private String id;
  private Date date;
  private String issuer;

  private final static DateFormat dateFormat;
  private static final String SAML_V2_NAMESPACE_PREFIX = "urn:oasis:names:tc:SAML:2.0:";

  static {
    dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  public AuthRequest(String issuer, Date date) {
    this.issuer = issuer;
    id = UUID.randomUUID().toString();
    this.date = date;
  }

  public AuthRequest(AppSettings appSettings, Date date) {
    this(appSettings.getIssuer(), date);
  }

  public byte[] getRequestXML() throws XMLStreamException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    XMLOutputFactory factory = XMLOutputFactory.newInstance();
    XMLStreamWriter writer = new IndentingXMLStreamWriter(factory.createXMLStreamWriter(baos, "UTF-8"));
 
    writer.writeStartDocument("UTF-8", "1.0");
    writer.writeStartElement("saml2p", "AuthnRequest", SAML_V2_NAMESPACE_PREFIX + "protocol");
    writer.writeNamespace("saml2p", SAML_V2_NAMESPACE_PREFIX + "protocol");

    writer.writeAttribute("AttributeConsumingServiceIndex", "2");
    writer.writeAttribute("ID", id);
    writer.writeAttribute("IssueInstant", dateFormat.format(date));
    writer.writeAttribute("Version", "2.0");

    writer.writeStartElement("saml2", "Issuer", SAML_V2_NAMESPACE_PREFIX + "assertion");
    writer.writeNamespace("saml2", SAML_V2_NAMESPACE_PREFIX + "assertion");

    writer.writeCharacters(issuer);
    writer.writeEndElement();
    writer.writeEmptyElement("saml2p", "NameIDPolicy", SAML_V2_NAMESPACE_PREFIX + "protocol");
    writer.writeAttribute("AllowCreate", "true");
    writer.writeAttribute("Format", SAML_V2_NAMESPACE_PREFIX + "nameid-format:persistent");
    writer.writeEndElement();
    writer.writeEndDocument(); 
    

    return baos.toByteArray();
  }

  public String getRequest() throws XMLStreamException {
	    return encodeSAMLRequest(getRequestXML());
  }

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
