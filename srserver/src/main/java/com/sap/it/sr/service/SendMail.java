package com.sap.it.sr.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.log4j.Logger;

import com.sap.it.sr.entity.ItemInfo;
import com.sap.it.sr.entity.PickupData;
import com.sap.it.sr.util.SessionHolder;

public class SendMail {
	private static final Logger LOGGER = Logger.getLogger(SendMail.class);
	private static String templatePath = "C:/srmreceive/template/";
	
    public void sendPickedEmail(PickupData info, List<String> cc, List<String> admins) {
        String sender = "SAP_IT_CHINA_BO@exchange.sap.corp";
        String receiver = "%s@exchange.sap.corp";
        Properties props = new Properties();
        props.put("mail.smtp.host", "mail.sap.corp");
        Session session = Session.getInstance(props, null);
        try {
            String recId = info.getEmpId();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
      
            List<InternetAddress> adminAddr = new ArrayList<>();
            if (admins != null && admins.size() > 0) {
                admins.forEach(itm->{
                    try {
                        if (!"".equals(itm)) {
                            adminAddr.add(new InternetAddress(String.format(Locale.ENGLISH, receiver, itm)));
                        }
                    } catch (AddressException e) {
                        e.printStackTrace();
                    }
                });
                message.setRecipients(Message.RecipientType.BCC, adminAddr.toArray(new Address[adminAddr.size()]));
            }
            List<InternetAddress> toAddr = new ArrayList<>();
            toAddr.add(new InternetAddress(String.format(Locale.ENGLISH, receiver, info.getEmpId())));
            message.setRecipients(Message.RecipientType.TO, toAddr.toArray(new Address[toAddr.size()]));
            
            List<InternetAddress> ccAddr = new ArrayList<>();
            if (info.getAgentId() == null || "".equals(info.getAgentId())) {
            } else {
                recId = info.getAgentId();
                ccAddr.add(new InternetAddress(String.format(Locale.ENGLISH, receiver, info.getAgentId())));
            }
            if (cc != null && cc.size() > 0) {
                cc.forEach(itm->{
                    try {
                        if (!"".equals(itm)) {
                            ccAddr.add(new InternetAddress(String.format(Locale.ENGLISH, receiver, itm)));
                        }
                    } catch (AddressException e) {
                        e.printStackTrace();
                    }
                });
            }
            if (ccAddr.size() > 0) {
                message.setRecipients(Message.RecipientType.CC, ccAddr.toArray(new Address[ccAddr.size()]));
            }
            message.setSubject("China IT - Equipment Received Notification");
            
            String content = generateMailContent(info);
            content = content.replace("@logopath", "cid:sap-logo");
            content = content.replaceAll("@empName", info.getEmpName());
            content = content.replaceAll("@ITAA", SessionHolder.getUserName());
            
            Multipart parts = new MimeMultipart();
            parts.addBodyPart(createImagePart(this.getClass().getClassLoader().getResourceAsStream("META-INF/logo.jpg"), "<sap-logo>"));
            handleImage(parts, content);
            
            message.setContent(parts);
            Transport.send(message);
            LOGGER.info("----Mail sent.----");
        } catch (Exception e) {
            LOGGER.error("----Send mail failed.----, message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private BodyPart createImagePart(InputStream is, String partName) throws Exception {
        BodyPart imagePart = new MimeBodyPart();
        DataSource ds = new ByteArrayDataSource(is, "image/jpeg");
        imagePart.setDataHandler(new DataHandler(ds));
        imagePart.setHeader("Content-ID", partName);
        
        return imagePart;
    }
    
    private void handleImage(Multipart parts, String content) throws Exception {
		Pattern pImageFilename = Pattern.compile("image[0-9]*\\.jpg");
		Pattern pattern = Pattern.compile("(?<=\")[^\"]*image[0-9]*\\.jpg(?=\")");
		Matcher matcher = pattern.matcher(content);
		StringBuffer sbr = new StringBuffer();
		while (matcher.find()) {
			String m = matcher.group();
			Matcher mF = pImageFilename.matcher(m);
			if (mF.find()) {
				String imageFilename = mF.group();
				
				try {
					parts.addBodyPart(createImagePart(new FileInputStream(templatePath + "/image/" + imageFilename), "<" + imageFilename + ">"));
				} catch (Exception e) {
					LOGGER.info("Add image file fail: " + imageFilename);
				}
			    matcher.appendReplacement(sbr, "cid:" + imageFilename);
			}
		}
		matcher.appendTail(sbr);
		content = sbr.toString();
        MimeBodyPart part = new MimeBodyPart();
        part.setText(content, "GBK");
        part.setContent(content, "text/html;charset=GBK");
        parts.addBodyPart(part);
    }
    
    private String generateMailContent(PickupData info) throws Exception {
        String content = "";
        BufferedReader reader = null;
        StringBuilder builder;
        List<ItemInfo> infoItems = info.getItems();
        String location = infoItems.get(0).getLocation();
        location = location == null || location.trim().equals("") ? "" : location;
        String path = templatePath + "confirm-template-table";
        String fileName = path + "-" + location + ".htm";
        if (Files.notExists(Paths.get(fileName))) {
            fileName = path  + ".htm";
        }
        reader = new BufferedReader(new FileReader(fileName));
        builder = new StringBuilder();
        StringBuilder header = new StringBuilder();
        StringBuilder tableLine = null;
        boolean isLineStart = false;
        boolean isLine = false;
        boolean isLineEnd = false;
        StringBuilder ender = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            if (!isLine && line.contains("<tr>")) {
                tableLine = new StringBuilder();
                isLineStart = true;
            } else if (line.contains("@poNumber")) {
                isLine = true;
            }
            if (isLine && !isLineEnd && line.contains("</tr>")) {
                isLineEnd = true;
            }
            if (isLineStart) {
                tableLine.append(line);
            }
            if (isLineEnd && !isLineStart) {
                ender.append(line);
            } else {
                header.append(line);
            }
            if (isLineEnd) {
                isLineStart = false;
            }
            line = reader.readLine();
        }
        reader.close();
        String lineContent = header.toString();
        for (int i=0; i<infoItems.size(); i++) {
            if (i>0) {
                lineContent = tableLine.toString();
            }
            lineContent = lineContent.replaceAll("@poNumber", infoItems.get(i).getPoNumber());
            lineContent = lineContent.replaceAll("@description", infoItems.get(i).getItemDesc());
            lineContent = lineContent.replaceAll("@quantity", String.valueOf(infoItems.get(i).getQuantity()));
            builder.append(lineContent);
        }
        builder.append(ender);
        content = builder.toString();

        return content;
    }

}
