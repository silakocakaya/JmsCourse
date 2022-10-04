package com.jms.clinicals;

import java.util.Iterator;

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

public class ClinicalsAppMultiple {

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
			
			for (int i = 0; i < 10; i++) {
				producer.send(objectMessage);				
			}
		} finally {
			initialContext.close();
			connection.close();
		}
	}
}
