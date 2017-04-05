package com.sun;

import com.sun.dao.LoginTicketDao;
import com.sun.dao.QuestionDao;
import com.sun.dao.UserDao;
import com.sun.model.LoginTicket;
import com.sun.model.Question;
import com.sun.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
@Sql("/init-schema.sql")
public class InterTest {

	@Autowired
	LoginTicketDao loginTicketDao;

	@Test
	public void getLoginTicket(){
		Assert.assertEquals(loginTicketDao.selectByTicket("672b80e21c2c4dcd85c6321903ac2ce4")
		.getId(),7);
	}

}
