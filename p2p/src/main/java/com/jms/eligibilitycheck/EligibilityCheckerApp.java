package com.jms.eligibilitycheck;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.jms.eligibilitycheck.listener.EligibilityCheckListener;	

public class EligibilityCheckerApp {

	public static void main(String[] args) throws Exception {

		InitialContext initialContext = null;
		Connection connection = null;
		ActiveMQConnectionFactory cf = null;
		try {
			initialContext = new InitialContext();
			//ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
			
			cf = new ActiveMQConnectionFactory("tcp://localhost:61616");
			cf.setTrustAllPackages(true);
			
			connection = cf.createConnection();
			connection.start();
			
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue queue = (Queue) initialContext.lookup("queue/requestQueue");
			
			MessageConsumer consumer = session.createConsumer(queue);
			consumer.setMessageListener(new EligibilityCheckListener());

			Thread.sleep(10000);
		} finally {
			initialContext.close();
			connection.close();
		}
	}
}
