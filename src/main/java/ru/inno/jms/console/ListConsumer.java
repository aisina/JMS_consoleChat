package ru.inno.jms.console;

import java.util.ArrayList;

/**
 * Created by innopolis on 23.01.2017.
 */
public class ListConsumer {

    //по сути, здесь должен быть Set, чтобы по несколько раз не добавлять

    private static ArrayList<Consumer> listConsumer = new ArrayList<Consumer>();

    public synchronized static ArrayList<Consumer> getListClient() {
        return ListConsumer.listConsumer;
    }

    public synchronized static void addClientToList(Consumer s) {
        ListConsumer.listConsumer.add(s);
    }

    public synchronized static void removeClientWithList(Consumer s) {
        ListConsumer.listConsumer.remove(s);
    }
}
