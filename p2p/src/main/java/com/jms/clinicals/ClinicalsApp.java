package com.jms.clinicals;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.jms.model.Patient;

public class ClinicalsApp {

	public static void main(String[] args) throws Exception {
		
		InitialContext initialContext = null;
		ActiveMQConnectionFactory cf = null;
		Connection connection = null;
		try {
			initialContext = new InitialContext();
			//ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
			
			cf = new ActiveMQConnectionFactory("tcp://localhost:61616");
			cf.setTrustAllPackages(true);
			
			connection = cf.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue queue = (Queue) initialContext.lookup("queue/requestQueue");
			
			MessageProducer producer = session.createProducer(queue);
			ObjectMessage objectMessage = session.createObjectMessage();
			
			Patient patient = Patient.builder()
					.id(123)
					.name("Bob")
					.insuranceProvider("BBC")
					.copay(100d)
					.amountToBePayed(500d)
					.build();
			
			objectMessage.setObject(patient);
			producer.send(objectMessage);
			
			
			Queue replyQueue = (Queue) initialContext.lookup("queue/replyQueue");
			MessageConsumer consumer = session.createConsumer(replyQueue);
			MapMessage replyMapMessage = (MapMessage) consumer.receive(30000);
			System.out.println("Patient eligibility is: " + replyMapMessage.getBoolean("eligible"));
			
		} finally {
			initialContext.close();
			connection.close();
		}
	}
}
