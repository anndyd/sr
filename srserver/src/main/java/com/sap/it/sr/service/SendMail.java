package com.sap.it.sr.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.sap.it.sr.entity.ItemDetail;
import com.sap.it.sr.entity.PickupData;

public class SendMail {
	private static boolean isTest = true;
	
    public void sendEquipmentReadyNotificationEmail(PickupData info, List<String> cc, List<String> admins) {
        String sender = "SAP_IT_CHINA_BO@exchange.sap.corp";
        String receiver = "%s@exchange.sap.corp";
        Properties props = new Properties();
        props.put("mail.smtp.host", "mail.sap.corp");
        Session session = Session.getInstance(props, null);
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
      
            InternetAddress[] adminAdds = new InternetAddress[admins.size()];
            for (int i = 0; i < admins.size(); i++) {
                adminAdds[i] = new InternetAddress(String.format(Locale.ENGLISH, receiver, admins.get(i)));
            }
            if (isTest) {
                message.setRecipients(Message.RecipientType.TO, adminAdds);
            } else {
                String[] receivers = new String[]{info.getEmpId()};
                InternetAddress[] addresses = new InternetAddress[receivers.length];
                for (int i = 0; i < receivers.length; i++) {
                    addresses[i] = new InternetAddress(String.format(Locale.ENGLISH, receiver, receivers[i]));
                }
                message.setRecipients(Message.RecipientType.TO, addresses);
                message.setRecipients(Message.RecipientType.BCC, adminAdds);
            }
            message.setSubject("Global IT - Equipment Ready Notification");
            String logopath = this.getClass().getResource("/").getFile() + "logo.jpg";
            logopath = logopath.replaceAll("%20", " ");
            logopath = logopath.substring(1);
            BodyPart imagePart = new MimeBodyPart();
            DataSource ds = new FileDataSource(logopath);
            imagePart.setDataHandler(new DataHandler(ds));
            imagePart.setHeader("Content-ID", "<sap-logo>");
            String emailPath = this.getClass().getResource("/").getFile() + "confirm-template.html";
            emailPath = emailPath.replaceAll("%20", " ");
            BufferedReader reader = null;
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(emailPath), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
            reader.close();
            String content = builder.toString();
            content = content.replace("@logopath", "cid:sap-logo");
            content = content.replaceAll("@poNumber", info.getItems().get(0).getPoNumber() +"/" + info.getItems().get(0).getPoItem());
            content = content.replaceAll("@empId", info.getEmpId()).replaceAll("@pickupTime", info.getPickupTime().toString()).replaceAll("@description", info.getItems().get(0).getItemDesc());
            content = content.replaceAll("@location", info.getItems().get(0).getLocation());
            StringBuffer serials = new StringBuffer();
            List<ItemDetail> view = info.getItems().get(0).getItemDetails();
            if (view.size() >= 1) {
                serials.append("<table class=\"tdt\"><tbody>");
                if (view.get(0).getSerailNo().equals("")) {
                    serials.append("<tr><td class=\"tds\"><p><span>" + view.size() + " piece(s)</span></p></td></tr>");
                } else {
                    for (int i = 0; i < view.size(); i++) {
                        serials.append("<tr><td class=\"tds\"><p><span>" + view.get(i).getSerailNo() + "</span></p></td><td class=\"tds\"><p><span>" + view.get(i).getEquipNo() + "</span></p></td></tr>");
                    }
                }
                serials.append("</tbody></table>");
            }
            content = content.replaceAll("@serialist", serials.toString());
            MimeBodyPart part = new MimeBodyPart();
            part.setText(content, "GBK");
            part.setContent(content, "text/html;charset=GBK");
            Multipart parts = new MimeMultipart();
            parts.addBodyPart(imagePart);
            parts.addBodyPart(part);
            message.setContent(parts);
            Transport.send(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

}
