package com.sap.it.sr.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.junit.Test;

import com.sap.it.sr.entity.ItemDetail;
import com.sap.it.sr.entity.ItemInfo;
import com.sap.it.sr.entity.PickupData;

public class TestSendMail {
	private final SendMail sm = new SendMail();
	@Test
	public void TestSendEquipmentReadyNotificationEmail() {
		PickupData pd = new PickupData();
				ItemInfo itm = new ItemInfo();
				itm.setPoNumber("testpo");
				itm.setPoItem(10);
				itm.setItemDesc("test asset");
				itm.setLocation("SHA");
				itm.setQuantity(1);
				
						ItemDetail itd = new ItemDetail();
						itd.setPoNumber(itm.getPoNumber());
						itd.setPoItem(itm.getPoItem());
						itd.setPoItemDetail(100);
						itd.setSerailNo("");
						itd.setEquipNo("500xxx");
						itm.getItemDetails().add(itd);
				pd.getItems().add(itm);

				ItemInfo itm1 = new ItemInfo();
                itm1.setPoNumber("testpo2");
                itm1.setPoItem(10);
                itm1.setItemDesc("test asset 2");
                itm1.setLocation("BJ");
                itm1.setQuantity(1);
                pd.getItems().add(itm1);
		pd.setEmpId("I063098");
		pd.setAgentId("I063098");
		
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		  String dateInString = "2016-12-20 15:23:01";
		
		  Timestamp currentTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
//		  try {
//		      currentTime = new Timestamp(formatter.parse(dateInString).getTime());
//		  } catch (ParseException e) {}

		pd.setPickupTime(currentTime);
		sm.sendPickedEmail(pd, Arrays.asList(""), Arrays.asList("I063098"));
	}
	
	//@Test
	public void SendMailSSL() {
        String sender = "Tester@exchange.sap.corp";
        String receiver = "%s@exchange.sap.corp";
        Properties props = new Properties();
        props.put("mail.smtp.host", "mail.sap.corp");
        props.put("mail.smtp.socketFactory.port", "587");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
//        Session session = Session.getInstance(props, null);
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("i063098@sap.com","Asdfjkl0");
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
      
            List<InternetAddress> toAddr = new ArrayList<>();
            toAddr.add(new InternetAddress(String.format(Locale.ENGLISH, receiver, "i063098")));
            message.setRecipients(Message.RecipientType.TO, toAddr.toArray(new Address[toAddr.size()]));
            message.setSubject("Test SSL mail.");
            
            String content = "This is a test mail.";
            Multipart parts = new MimeMultipart();
            MimeBodyPart part = new MimeBodyPart();
            part.setText(content, "GBK");
            part.setContent(content, "text/html;charset=GBK");
            parts.addBodyPart(part);
            message.setContent(parts);
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

	}
}
