package com.jms.filters;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.jms.model.Patient;

public class FilterQueue {

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
			Queue queue = (Queue) initialContext.lookup("queue/filterQueue");
			
			MessageProducer producer = session.createProducer(queue);
			ObjectMessage objectMessage = session.createObjectMessage();
			
			Patient patient = Patient.builder()
					.id(123)
					.name("Bob")
					.insuranceProvider("BBC")
					.copay(100d)
					.amountToBePayed(500d)
					.build();
			
			objectMessage.setIntProperty("thisObjectId", 2);
			objectMessage.setObject(patient);
			producer.send(objectMessage);

			MessageConsumer consumer = session.createConsumer(queue, "thisObjectId=3");
			ObjectMessage consumeObjectMessage = (ObjectMessage) consumer.receive();
			Patient consumePatient = (Patient) consumeObjectMessage.getObject();
			System.out.println("Patient id is: " + consumePatient.getId());
			
		} finally {
			initialContext.close();
			connection.close();
		}
	}

}
