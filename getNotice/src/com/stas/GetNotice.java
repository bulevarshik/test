package com.stas;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.Arrays;

public class GetNotice {
    Document document;
    public GetNotice() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        document = documentBuilder.parse(new File("C:\\Users\\Stas\\Desktop\\work\\20191016\\20191009153753463_request_b10f47dd-e9bd-11e9-9674-aaaaaa2ca900_meta.xml"));
    }
    public String getBase64Text() throws XPathExpressionException {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();
        String sBase64 = (String)xPath.compile("/MetaInfo/RequestXml").evaluate(document, XPathConstants.STRING);
        return sBase64;
    }

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, Base64DecodingException, ClassNotFoundException, SQLException {
        GetNotice getNotice = new GetNotice();
        String sBase64 = getNotice.getBase64Text();
        byte[] bytes = Base64.decode(sBase64);
/*        Writer writer = new FileWriter(new File("C:\\Users\\Stas\\Desktop\\work\\base64.xml"));
        writer.write(bytes);
        writer.flush();
        writer.close();*/
        File file = new File("C:\\Users\\Stas\\Desktop\\work\\base64.txt");
        if(!file.exists()){
            OutputStream outputStream = new FileOutputStream(file);
            outputStream.write(bytes);
         //   outputStream.flush();
            outputStream.close();
        }

        XPath xpath = XPathFactory.newInstance().newXPath();
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        //
        // Достаем идентификатор заявления на ЕПГУ
        String orderId = (String) xpath.compile("/CbrNoticeRequest/orderId/text()").evaluate(doc, XPathConstants.STRING);
        String uploadDateTimeStr = (String) xpath.compile("/CbrNoticeRequest/StatementDate/text()").evaluate(doc, XPathConstants.STRING);
        String noticeType = (String) xpath.compile("local-name(/CbrNoticeRequest/*[starts-with(local-name(), 'Notice-')])").evaluate(doc);
        String fullname =(String) xpath.compile("/CbrNoticeRequest/Notice-1144/LegalPersonFullName/text()").evaluate(doc);

        System.out.println(orderId);
        System.out.println(uploadDateTimeStr);
        System.out.println(noticeType);
        System.out.println(fullname);
        /////////////////////////////
        StringBuilder stringBuilder = new StringBuilder();
        char[] buf = new char[512];
        int count;
        Reader reader = new FileReader(file);
        while((count=reader.read(buf))!=-1){
            if(count<buf.length) {
                buf = Arrays.copyOf(buf,count);
                System.out.println(buf.length);
            }
            stringBuilder.append(buf);
        }

        String user = "tester";
        String pass = "12345678";
        String url = "jdbc:oracle:thin:@localhost:1521:XE";
        Class.forName("oracle.jdbc.driver.OracleDriver");
        try(Connection connection = DriverManager.getConnection(url,user,pass)){
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("insert into xml_table values (?,?)");
            preparedStatement.setInt(1,1);
            preparedStatement.setString(2,stringBuilder.toString());
            preparedStatement.executeQuery();

            preparedStatement = connection.prepareStatement("select * from xml_table");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                System.out.println(resultSet.getInt(1));
                System.out.println(resultSet.getString(2));
            }


            Blob blob = connection.createBlob();
            byte[] bytes1 = new byte[(int)file.length()];
            InputStream inputStream = new FileInputStream(file);
            while(inputStream.read(bytes1)!=-1){
                byte[] xmlHeader = ("<?xml version=\"1.0\" encoding=\"" + "utf-8" + "\"?>").getBytes();
                byte[] tmp = new byte[xmlHeader.length + bytes1.length];
                System.arraycopy(xmlHeader, 0, tmp, 0,                xmlHeader.length);
                System.arraycopy(bytes1,       0, tmp, xmlHeader.length, bytes1.length);
                bytes1 = tmp;
                blob.setBytes(1,bytes1);
            }


            preparedStatement = connection.prepareStatement("insert into blob_table values (?,?)");
            preparedStatement.setInt(1,1);
            preparedStatement.setBlob(2,blob);
            //preparedStatement.setBinaryStream(2,inputStream,file.length());
            preparedStatement.execute();
            connection.commit();

            resultSet = preparedStatement.executeQuery("select * from blob_table");
            while(resultSet.next()){
                blob=resultSet.getBlob(2);
            }
            int i;
            stringBuilder = new StringBuilder();
            inputStream = blob.getBinaryStream();
            while((i=inputStream.read())!=-1){
                stringBuilder.append((char)i);
            }
            System.out.println(stringBuilder.toString());

/*            BufferedImage image = ImageIO.read(new File("D:\\IJ\\smile.jpg"));
            Blob blob = connection.createBlob();
            try(OutputStream outputStream = blob.setBinaryStream(1)) {
                ImageIO.write(image, "jpg", outputStream);
            }
            preparedStatement = connection.prepareStatement("insert into blob_table values (?,?)");
            preparedStatement.setInt(1,1);
            preparedStatement.setBlob(2,blob);
            preparedStatement.execute();

            resultSet = preparedStatement.executeQuery("select * from blob_table");
            while(resultSet.next()){
                blob=resultSet.getBlob(2);
            }
            try(InputStream inputStream = blob.getBinaryStream()){
                BufferedImage image2 = ImageIO.read(inputStream);
                ImageIO.write(image2,"png",new File("D:\\IJ\\smile.png"));
            }*/



            connection.rollback();
        }
    }
}
