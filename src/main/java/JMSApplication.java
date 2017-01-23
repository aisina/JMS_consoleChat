/**
 * Created by innopolis on 23.01.2017.
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import view.JMSWindow;

public class JMSApplication{
    private static JMSWindow windowjms;

    private static ActiveMQConnectionFactory connectionFactory=null;

    private static Connection connection=null;

    private static Session session;

    private static Destination destination;

    private static String queue=null;
    public static void main(String[] args) {
        windowjms = new JMSWindow();
        windowjms.setVisible(true);
        //подключаем обработчики событий нажатия для кнопок
        windowjms.jBSendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickSendButton();
            }
        });
        windowjms.jBConnectedListener.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickReceivedButton();
            }
        });
        windowjms.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (connection!=null){
                    try {
                        connection.close();
                    } catch (JMSException ex) {
                        Logger.getLogger(JMSApplication.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        });
    }
    /**
     * Нажатие кнопки отправки сообщения.
     */
    public static void clickSendButton(){
        //получаем имя очереди к которой необходимо подключитсья
        queue=windowjms.getQueueSendName().equals("")?"SimpleQueue":windowjms.getQueueSendName();
        if (Connected() && !windowjms.getMessageSendText().equals("")){
            windowjms.SendConnectedSucces();
            if (windowjms.isPtP()){
                destination=getDestinationQueue();
            }else{
                destination=getDestinationTopic();
            }
            if (destination!=null){
                try {
                    MessageProducer producer = session.createProducer(destination);
                    producer.setDeliveryMode(DeliveryMode.PERSISTENT);//парметром PERSISTENT указываем что сообщение
                    //будет хранится до тех пор пока не будет доставлено адресату.
                    //Создаем текстовое сообщение.
                    TextMessage message =session.createTextMessage(windowjms.getMessageSendText());
                    producer.send(message);
                    windowjms.SendSuccess();
                } catch (JMSException ex) {
                    windowjms.SendError();
                    Logger.getLogger(JMSApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{ windowjms.SendError();}
        }else{windowjms.SendError();}
    }
    /**
     * Нажатие кнопки подключения слушателя.
     */
    public static void clickReceivedButton(){
        queue=windowjms.getQueueSendName().equals("")?"SimpleQueue":windowjms.getQueueSendName();
        if (Connected()){
            if (windowjms.isPtP()){
                destination=getDestinationQueue();
            }else{
                destination=getDestinationTopic();
            }
            if (destination!=null){
                try {
                    MessageConsumer consumer=session.createConsumer(destination);
                    consumer.setMessageListener(new MessageListener() {

                        @Override
                        public void onMessage(Message msg) {
                            TextMessage textmessage=(TextMessage)msg;
                            try {
                                windowjms.setTextReceiver(textmessage.getText());
                            } catch (JMSException ex) {
                                Logger.getLogger(JMSApplication.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                    windowjms.ReceivedConnectedSucces();
                } catch (JMSException ex) {
                    Logger.getLogger(JMSApplication.class.getName()).log(Level.SEVERE, null, ex);
                    windowjms.ReceivedConnectedClose();
                }
            }else{windowjms.ReceivedConnectedClose();}
        }else{windowjms.ReceivedConnectedClose();}
    }
    /**
     * Подключаемся к серверу.
     * @return
     */
    public static Boolean Connected(){
        try {
            if (connection==null){
                connectionFactory=getConnectionFactory();
                connection=connectionFactory.createConnection(); //получаем экзмпляр класса подключения
                connection.start(); //стартуем
                session =connection.createSession(false, Session.AUTO_ACKNOWLEDGE); //создаем объект сессию без транзакций
                //параметром AUTO_ACKNOWLEDGE мы указали что отчет о доставке будет
                //отправляться автоматически при получении сообщения.
            }else{
                connection.start();
            }
            return true;
        } catch (JMSException ex) {
            Logger.getLogger(JMSApplication.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    /**
     * Создаем ConnectionFactory ApacheMQ сервера. Вбиваем дефолтные логин и пароль.
     * @return
     */
    private static ActiveMQConnectionFactory getConnectionFactory(){
        return new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,
                ActiveMQConnection.DEFAULT_PASSWORD,
                "failover://tcp://localhost:61616");
    }
    /**
     * Подключаемся к модели точка-точка.
     * @return
     */
    private static Destination getDestinationQueue(){
        try {
            return session.createQueue(queue);
        } catch (JMSException ex) {
            Logger.getLogger(JMSApplication.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    /**
     * Подключаемся к модели подписчик/издатель.
     * @return
     */
    private static Destination getDestinationTopic(){
        try {
            return session.createTopic(queue);
        } catch (JMSException ex) {
            Logger.getLogger(JMSApplication.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

}
