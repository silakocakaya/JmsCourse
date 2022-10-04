package jms.grouping;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

public class MessageGroupingDemo {

	public static void main(String[] args) throws Exception {

		InitialContext initialContext = null;
		Connection connection = null;
		Map<String, String> receivedMessages = new ConcurrentHashMap<String, String>();
		try {
			initialContext = new InitialContext();
			ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
			connection = cf.createConnection();
			connection.start();
			
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue queue = (Queue) initialContext.lookup("queue/myQueue");
			
			MessageProducer producer = session.createProducer(queue);
			MessageConsumer consumer = session.createConsumer(queue);
			consumer.setMessageListener(new MyListener("Consumer-1", receivedMessages));
			MessageConsumer consumer2 = session.createConsumer(queue);
			consumer2.setMessageListener(new MyListener("Consumer-2", receivedMessages));
			
			
			int count = 10;
			TextMessage[] messages = new TextMessage[count];
			for (int i = 0; i < count; i++) {
				messages[i] = session.createTextMessage("Group-0 message" + i);
				messages[i].setStringProperty("JMSXGroupID", "Group-0");
				producer.send(queue, messages[i]);
			}
			
			Thread.sleep(2000);
			
			for (TextMessage message : messages) {
				if (!receivedMessages.get(message.getText()).equals("Consumer-1")) {
					throw new IllegalStateException(
							"Group Message" + message.getText() + "has gone to the wrong receiver");
				}
			}
		} finally {
			initialContext.close();
			connection.close();
		}
	}
}

class MyListener implements MessageListener {

	private final String name;
	private final Map<String, String> receivedMessages;

	MyListener(String name, Map<String, String> receivedMessages) {
		this.name = name;
		this.receivedMessages = receivedMessages;
	}

	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage) message;
		try {
			System.out.println("Message Received is" + textMessage.getText());
			System.out.println("Listnener Name" + name);
			receivedMessages.put(textMessage.getText(), name);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
}