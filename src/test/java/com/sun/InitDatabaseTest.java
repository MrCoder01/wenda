package com.sun;

import com.sun.dao.QuestionDao;
import com.sun.dao.UserDao;
import com.sun.model.Question;
import com.sun.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
@Sql("/init-schema.sql")
public class InitDatabaseTest {

	@Autowired
	UserDao userDao;

	@Autowired
	QuestionDao questionDao;

	@Test
	public void testAdd() {
		Random random = new Random();
		for (int i = 0; i < 12; i++) {
			User user = new User();
			user.setName(String.format("Name(%d)", i));
			user.setPassword("12345");
			user.setSalt("12345");
			user.setHeadUrl(String.format("/images/res/%d.jpg", random.nextInt(12)));
			userDao.addUser(user);

			Question question = new Question();
			question.setCommentCount(i);
			question.setContent(String.format("Balabababasababl coment(%d)", i));
			question.setTitle(String.format("Title(%d)", i));
			question.setCreatedDate(new Date());
			question.setUserId(random.nextInt(12));
			questionDao.addQuestion(question);
		}

	}

	@Test
	public void testAddA() {
		userDao.getById(2);

	}


}
