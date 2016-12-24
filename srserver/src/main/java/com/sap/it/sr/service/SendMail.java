package com.sap.it.sr.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sap.it.sr.entity.PickupData;
import com.sap.it.sr.util.SessionHolder;

public class SendMail {
	private static final Logger LOGGER = Logger.getLogger(SendMail.class);
	
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
            message.setSubject("Global IT - Equipment Received Notification");
            BodyPart imagePart = new MimeBodyPart();
            DataSource ds = new ByteArrayDataSource(this.getClass().getClassLoader().getResourceAsStream("META-INF/logo.jpg"), "image/jpeg");
            imagePart.setDataHandler(new DataHandler(ds));
            imagePart.setHeader("Content-ID", "<sap-logo>");
            BufferedReader reader = null;
            reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("META-INF/confirm-template.htm"), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
            reader.close();
            List<String> pos = new ArrayList<>();
            List<String> desc = new ArrayList<>();
            info.getItems().forEach(itm->{
                pos.add(itm.getPoNumber());
                desc.add(itm.getItemDesc());
            });

            String content = builder.toString();
            content = content.replace("@logopath", "cid:sap-logo");
            content = content.replaceAll("@empName", info.getEmpName());
            content = content.replaceAll("@poNumber", StringUtils.join(pos, ", "));
            content = content.replaceAll("@description", StringUtils.join(desc, ", "));
            content = content.replaceAll("@ITAA", SessionHolder.getUserName());
//            content = content.replaceAll("@empId", recId).
//                    replaceAll("@pickupTime", info.getPickupTime().toString()).
//                    replaceAll("@description", desc.toString());
//            content = content.replaceAll("@location", info.getItems().get(0).getLocation());
            MimeBodyPart part = new MimeBodyPart();
            part.setText(content, "GBK");
            part.setContent(content, "text/html;charset=GBK");
            Multipart parts = new MimeMultipart();
            parts.addBodyPart(imagePart);
            parts.addBodyPart(part);
            message.setContent(parts);
            Transport.send(message);
            LOGGER.info("----Mail sent.----");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
