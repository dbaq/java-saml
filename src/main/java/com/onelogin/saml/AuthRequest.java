package com.onelogin.saml;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
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

public class AuthRequest {
  private String id;
  private Date date;
  private String issuer;

  private final static DateFormat dateFormat;
  private static final String SAML_V2_NAMESPACE_PREFIX = "urn:oasis:names:tc:SAML:2.0:";

  static {
    dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  public AuthRequest(String issuer, Date date) {
    this.issuer = issuer;
    id = "_" + UUID.randomUUID().toString();
    this.date = date;
  }

  public AuthRequest(AppSettings appSettings, Date date) {
    this(appSettings.getIssuer(), date);
  }

  public byte[] getRequestXML() throws XMLStreamException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    XMLOutputFactory factory = XMLOutputFactory.newInstance();
    XMLStreamWriter writer = factory.createXMLStreamWriter(baos);

    writer.writeStartElement("", "AuthnRequest", SAML_V2_NAMESPACE_PREFIX + "protocol");
    writer.writeNamespace("", SAML_V2_NAMESPACE_PREFIX + "protocol");

    writer.writeAttribute("ID", id);
    writer.writeAttribute("Version", "2.0");
    writer.writeAttribute("IssueInstant", dateFormat.format(date));

    writer.writeStartElement("", "Issuer", SAML_V2_NAMESPACE_PREFIX + "assertion");
    writer.writeNamespace("", SAML_V2_NAMESPACE_PREFIX + "assertion");

    writer.writeCharacters(issuer);
    writer.writeEndElement();
    writer.writeStartElement("", "NameIDPolicy", SAML_V2_NAMESPACE_PREFIX + "protocol");
    writer.writeAttribute("Format", SAML_V2_NAMESPACE_PREFIX + "nameid-format:persistent");
    writer.writeAttribute("AllowCreate", "true");
    writer.writeEndElement();
    writer.writeEndElement();
    writer.flush();

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

      return URLEncoder.encode(new String(base64Encoder.encode(byteArray.toByteArray())), "UTF-8");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
