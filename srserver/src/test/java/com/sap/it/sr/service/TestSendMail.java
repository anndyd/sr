package com.sap.it.sr.service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;

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
}
