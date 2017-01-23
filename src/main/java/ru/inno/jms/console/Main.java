package ru.inno.jms.console;

import ru.inno.jms.App;

import java.util.Scanner;

/**
 * Created by innopolis on 23.01.2017.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        //Thread producer = new Thread(new Producer());
        //Thread consumer = new Thread(new Consumer());
        //consumer.setDaemon(true);
        //producer.start();
        //consumer.start();

        Scanner sc = new Scanner(System.in);
        String name;
        String text;

        while(true){
             name = sc.nextLine();
             text = sc.nextLine();
             if("exit".equals(name)){
                sc.close();
                 break;
             }else {
                 Producer p = new Producer(name, text);
                 ListConsumer.addClientToList(new Consumer(name));
                 new Thread(p).start();
             }
        }
    }
}
