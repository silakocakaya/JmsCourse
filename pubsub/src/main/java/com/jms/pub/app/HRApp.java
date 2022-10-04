package com.jms.pub.app;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.jms.model.Employee;

public class HRApp {

	public static void main(String[] args) throws Exception {
		
		InitialContext initialContext = null;
		Connection connection = null;
		ActiveMQConnectionFactory cf = null;
		try {
			initialContext = new InitialContext();
			cf = new ActiveMQConnectionFactory("tcp://localhost:61616");
			cf.setTrustAllPackages(true);
			connection = cf.createConnection();
			connection.start();
			
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Topic topic = (Topic) initialContext.lookup("topic/empTopic");
			
			MessageProducer producer = session.createProducer(topic);

			Employee employee = Employee.builder()
								.id(0)
								.firstName("Sila")
								.lastName("Kocakaya")
								.designation("SE")
								.email("a@a.com")
								.phone("123456")
								.build();
			
			ObjectMessage objectMessage = session.createObjectMessage();
			objectMessage.setObject(employee);
			producer.send(objectMessage);
			System.out.println("Message sent");
		} finally {
			initialContext.close();
			connection.close();
		}
	}
}

