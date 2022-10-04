package com.jms.sub.durable;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.InitialContext;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.jms.model.Employee;

public class DurableApp {

	public static void main(String[] args) throws Exception {
		
		InitialContext initialContext = null;
		ActiveMQConnectionFactory cf = null;
		Connection connection = null;
		try {
			initialContext = new InitialContext();

			cf = new ActiveMQConnectionFactory("tcp://localhost:61616");
			cf.setTrustAllPackages(true);
			connection = cf.createConnection();
			connection.setClientID("durableApp");
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Topic topic = (Topic) initialContext.lookup("topic/empTopic");
			
			MessageConsumer consumer = session.createDurableSubscriber(topic, "subscription1");
			consumer.close();
			
			Thread.sleep(20000);
			
			consumer = session.createDurableSubscriber(topic, "subscription1");
			
			ObjectMessage messageReceived = (ObjectMessage) consumer.receive();
			
			Employee employee = (Employee) messageReceived.getObject();
			System.out.println("Message Received: " + employee.getFirstName());
		} finally {
			initialContext.close();
			connection.close();
		}
	}
}
