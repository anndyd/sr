package com.sap.it.sr.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.it.sr.entity.User;

public class InsertCCC {
	private static final String PERSISTENCE_UNIT_NAME = "srserver";
    private EntityManagerFactory factory;
    private EntityManager em;
    
    @Before
    public void setUp() throws Exception {
    	Properties props = new Properties();
    	props.setProperty("javax.persistence.jdbc.driver", "org.mariadb.jdbc.Driver");
    	props.setProperty("javax.persistence.jdbc.url", "jdbc:mysql://pekitwin2008vm.pek.sap.corp:3306/srmbk");
    	props.setProperty("javax.persistence.jdbc.user", "root");
    	props.setProperty("javax.persistence.jdbc.password", "Sybase123");
    	
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, props);
        em = factory.createEntityManager();
        
    }
    
//    @Test
    public void testUser() {
    	List<User> userList = em.createQuery("select t from User t where t.userName=?1", User.class)
                .setParameter(1, "I063098").setMaxResults(1).getResultList();
        System.out.println(userList.get(0).getFullName());
    }
    
    @Test
    public void insertCCC() {
 		BufferedReader br = null;
 		Map<String, List<String>> all3Cs = new HashMap<>();
        try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader("C:\\temp\\ccc.txt"));
			while ((sCurrentLine = br.readLine()) != null) {
				Pattern pattern = Pattern.compile("(\\d+)\\t(\\w\\d+)");
				Matcher matcher = pattern.matcher(sCurrentLine);
				if (matcher.find()) {
					String empId = matcher.group(2);
					String cc = "0" + matcher.group(1);
				    System.out.print("group 1: " + cc);
				    System.out.println("group 2: " + empId);
				    if (null == all3Cs.get(empId)) {
				    	List<String> cs = new ArrayList<>();
				    	cs.add(cc);
				    	all3Cs.put(empId, cs);
				    } else {
				    	all3Cs.get(empId).add(cc);
				    }
				}
			}
			int i = 0;
			// Begin a new local transaction so that we can persist a new entity
	        em.getTransaction().begin();
			
			all3Cs.forEach((k,v)->{
				User user = new User();
				user.setUserName(k);
				user.setRole("3");
				user.setStatus(true);
				user.setChargeCC(v);
				em.persist(user);
				System.out.println("emp: " + k);
			});
			
			// Commit the transaction, which will cause the entity to
	        // be stored in the database
	        em.getTransaction().commit();
	        System.out.println("Done. ");
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try{
				if (br != null)	br.close();
            } catch (Exception ex){}
        }

    }
    
    @After
    public void tearDown() throws Exception {
    	if (null != em) {
    		em.close();
    	}
    	if (null != factory) {
    		factory.close();
    	}
    }

}
