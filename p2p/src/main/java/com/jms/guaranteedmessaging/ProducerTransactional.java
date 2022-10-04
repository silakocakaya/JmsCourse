package com.jms.guaranteedmessaging;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

public class ProducerTransactional {

	public static void main(String[] args) throws Exception {

		InitialContext initialContext = null;
		Connection connection = null;
		try {
			initialContext = new InitialContext();
			ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
			connection = cf.createConnection();
			connection.start();
			
			Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
			Queue queue = (Queue) initialContext.lookup("queue/guaranteedQueue");
			
			MessageProducer producer = session.createProducer(queue);
			TextMessage message = session.createTextMessage("I am acknowledge");
			producer.send(message);
			
			session.commit();
			System.out.println("Message Sent: " + message.getText());

		} finally {
			initialContext.close();
			connection.close();
		}
	}
}
