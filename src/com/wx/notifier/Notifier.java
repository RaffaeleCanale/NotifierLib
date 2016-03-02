package com.wx.notifier;

import java.io.IOException;

/**
 * A Notifier is a general model of: object that sends notifications/messages to a destination.
 * <p>
 * Created on 27/03/2015
 *
 * @author Raffaele Canale (raffaelecanale@gmail.com)
 * @version 0.1
 */
@FunctionalInterface
public interface Notifier {

    /**
     * Send a notification
     *
     * @param title   Notification title
     * @param message Notification content
     *
     * @throws IOException
     */
    void notify(String title, String message) throws IOException;

}
