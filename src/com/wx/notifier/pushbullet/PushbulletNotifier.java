/*
    The jpushbullet libraries used here have been deprecated and no longer work.
    In order to retrieve this implementation, it should integrate the latest (different) API.
 */

//package com.wx.notifier.pushbullet;
//
//import com.wx.console.UserConsoleInterface;
//import com.wx.crypto.Crypter;
//import com.wx.crypto.CryptoException;
//import com.wx.notifier.Notifier;
//import com.wx.notifier.NotifierFactory;
//import com.wx.properties.page.ResourcePage;
//import net.iharder.jpushbullet2.PushbulletClient;
//import net.iharder.jpushbullet2.PushbulletException;
//
//import java.io.IOException;
//
///**
// * Created on 31/03/2015
// *
// * @author Raffaele Canale (raffaelecanale@gmail.com)
// * @version 0.1
// */
//public class PushbulletNotifier implements NotifierFactory {
//
//    private static final String API_KEY = "pushbullet.apikey";
//
//    @Override
//    public void inputAndStore(UserConsoleInterface in, ResourcePage config, Crypter crypter) throws CryptoException, IOException {
//        in.getConsole().println("Enter your PushBullet API key (can be found at https://www.pushbullet.com/account)");
//        String apiKey = in.readLine("API key: ");
//
//        if (crypter != null) {
//            byte[] encryptedKey = crypter.encrypt(apiKey.getBytes("UTF-8"));
//            config.setProperty(API_KEY, encryptedKey);
//        } else {
//            config.setProperty(API_KEY, apiKey);
//        }
//    }
//
//    @Override
//    public Notifier loadFrom(ResourcePage config, Crypter crypter) throws CryptoException, IOException {
//        if (!config.containsKey(API_KEY)) {
//            return null;
//        }
//
//        String apiKey;
//        if (crypter != null) {
//            byte[] encryptedKey = config.getBytes(API_KEY);
//            apiKey = new String(crypter.decrypt(encryptedKey), "UTF-8");
//        } else {
//            apiKey = config.getString(API_KEY);
//        }
//
//        return loadFrom(apiKey);
//    }
//
//
//    public Notifier loadFrom(String apiKey) {
//        return new PushBulletNotifier(apiKey);
//    }
//
//    private class PushBulletNotifier implements Notifier {
//
//        private final String apiKey;
//
//        public PushBulletNotifier(String apiKey) {
//            this.apiKey = apiKey;
//        }
//
//        @Override
//        public void notify(String title, String message) throws IOException {
//            PushbulletClient client = new PushbulletClient(apiKey);
//            try {
//                client.sendNote(null, title, message);
//            } catch (PushbulletException e) {
//                throw new IOException(e);
//            }
//        }
//
//        @Override
//        public String toString() {
//            return "PushBullet";
//        }
//    }
//}
