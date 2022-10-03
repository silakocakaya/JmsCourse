package jms.messagestructure;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MessagePriority {

	public static void main(String[] args) throws NamingException, Exception {

		InitialContext initialContext = new InitialContext();
		Queue queue = (Queue) initialContext.lookup("queue/myQueue");

		ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
		Connection connection = cf.createConnection();
		connection.start();
		
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		MessageProducer producer = session.createProducer(queue);

		TextMessage messages1 = session.createTextMessage("Message One");
		TextMessage messages2 = session.createTextMessage("Message Two");
		TextMessage messages3 = session.createTextMessage("Message Three");

		producer.setPriority(3);
		producer.send(messages1);

		producer.setPriority(4);
		producer.send(messages2);

		producer.setPriority(9);
		producer.send(messages3);

		MessageConsumer consumer = session.createConsumer(queue);

		for (int i = 0; i < 3; i++) {
			TextMessage receivedMessage = (TextMessage) consumer.receive();
			System.out.println(receivedMessage.getJMSPriority());
			System.out.println(receivedMessage.getText());
		}
		
		connection.close();
		initialContext.close();

	}

}