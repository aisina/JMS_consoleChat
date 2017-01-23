package ru.inno.jms.console;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Scanner;

/**
 * Created by innopolis on 23.01.2017.
 */
public class Producer implements Runnable {

    private Producer p;
    private String name;
    private String message;


    public Producer() {
    }

    public Producer(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public void run() {
        try {
            System.out.println("Start producer");

            //ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,
                    ActiveMQConnection.DEFAULT_PASSWORD,
                    "failover://tcp://localhost:61616");

            Connection connection = connectionFactory.createConnection();
            connection.start();



            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            //Destination destination = session.createQueue("TEST.FOO");
            Destination destination = session.createQueue(name);

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create a messages
            String text = "Text From (Producer): " + name + " : " + message;
            TextMessage message = session.createTextMessage(text);

            // Tell the producer to send the message
            /*System.out.println(this.name + ":");
            System.out.println(this.message);*/
            producer.send(message);

            //вызов всех consumer
            for(Consumer c : ListConsumer.getListClient()){
                c.run();
            }
            session.close();
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
