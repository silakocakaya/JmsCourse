package com.jms.eligibilitycheck.listener;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.jms.model.Patient;

public class EligibilityCheckListener implements MessageListener {

	public void onMessage(Message message) {

		ObjectMessage objectMessage = (ObjectMessage) message;
		
		InitialContext initialContext = null;
		Connection connection = null;
		try {
			initialContext = new InitialContext();
			ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
			connection = cf.createConnection();
			connection.start();
			
			Patient patient = (Patient) objectMessage.getObject();
			
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue queue = (Queue) initialContext.lookup("queue/replyQueue");
			
			MessageProducer producer = session.createProducer(queue);
			
			MapMessage replyMapMessage = session.createMapMessage();
			System.out.println("EligibilityCheckListener");
			if(patient.getInsuranceProvider().equals("BBC")) {
				if(patient.getCopay() > 40) {
					replyMapMessage.setBoolean("eligible", true);
				} else {
					replyMapMessage.setBoolean("eligible", false);
				}
			} else {
				replyMapMessage.setBoolean("eligible", false);
			}
			System.out.println("EligibilityCheckListener send");
			producer.send(replyMapMessage);
			
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		} finally {
			try {
				initialContext.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
			try {
				connection.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

}
