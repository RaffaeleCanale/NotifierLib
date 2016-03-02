package com.wx.notifier;


import com.wx.console.UserConsoleInterface;
import com.wx.crypto.Crypter;
import com.wx.crypto.CryptoException;
import com.wx.notifier.gmail.GMailNotifierFactory;
import com.wx.properties.page.ResourcePage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Model of a Notifier factory. A Notifier factory is able to generate a notifier by requesting the necessary parameters
 * directly from the user. It will the store the parameters in a {@link ResourcePage} and will be able to reload it
 * again without user input.
 * <p>
 * Additionally, some static methods allow to store and load several notifiers in a single file, identifying them by
 * name.
 * <p>
 * Created on 27/03/2015
 *
 * @author Raffaele Canale (raffaelecanale@gmail.com)
 * @version 0.1
 */
public interface NotifierFactory {

    /**
     * Load all notifiers store in the given config file.
     *
     * @param config  Config to read
     * @param crypter {@code Crypter} to decrypt notifiers parameters (or {@code null} if they are not encrypted)
     *
     * @return The set of notifiers contained in the file mapped by their name
     *
     * @throws CryptoException
     * @throws IOException
     */
    static Map<String, Notifier> loadAllFrom(ResourcePage config, Crypter crypter) throws CryptoException, IOException {
        Map<String, Notifier> result = new HashMap<>();

        for (Map.Entry<String, NotifierFactory> entry : compatibleNotifiers().entrySet()) {
            Notifier notifier = entry.getValue().loadFrom(config, crypter);
            result.put(entry.getKey(), notifier);
        }

        return result;
    }

    /**
     * Load a specific notifier from the given config. Additionally, this method accepts the name 'all' that will
     * generate a combined notifier using all notifiers present in the config at the same time.
     *
     * @param name    Name of the notifier to load. If the name is 'all', a special notifier combining all notifiers
     *                will be constructed
     * @param config  Config to read
     * @param crypter {@code Crypter} to decrypt notifiers parameters (or {@code null} if they are not encrypted)
     *
     * @return Notifier corresponding the given name or {@code null} if the config contains no such notifier
     *
     * @throws CryptoException
     * @throws IOException
     */
    static Notifier loadByName(String name, ResourcePage config, Crypter crypter) throws CryptoException, IOException {
        Map<String, Notifier> notifiers = loadAllFrom(config, crypter);

        name = name.toLowerCase();
        if (name.equals("all")) {
            return new Notifier() {
                @Override
                public void notify(String title, String message) throws IOException {
                    for (Notifier notifier : notifiers.values()) {
                        notifier.notify(title, message);
                    }
                }

                @Override
                public String toString() {
                    return "All (" + notifiers + ")";
                }
            };
        }

        return notifiers.get(name);
    }

    /**
     * Get the set of all notifier factories implemented in this library mapped by name.
     *
     * @return Set of all implementations in this library mapped by name
     */
    static Map<String, NotifierFactory> compatibleNotifiers() {
        Map<String, NotifierFactory> map = new HashMap<>();
        map.put("gmail", new GMailNotifierFactory());

        // PushBullet has been deprecated...
//        map.put("pushbullet", new PushbulletNotifier());
        return map;
    }

    /**
     * Requests the parameters for this notifier implementation directly from the user and stores them in the given
     * config.
     *
     * @param in      User interface
     * @param config  Config where the generated notifier will be stored
     * @param crypter {@code Crypter} to use to encrypt parameters or {@code null} to use plain storage
     *
     * @throws CryptoException
     * @throws IOException
     */
    void inputAndStore(UserConsoleInterface in, ResourcePage config, Crypter crypter) throws CryptoException, IOException;

    /**
     * Load a Notifier object of this implementation using the parameters stored in the given config.
     *
     * @param config  Parameters will be read from this config
     * @param crypter {@code Crypter} to use to decrypt parameters or {@code null} if no encryption was used
     *
     * @return A loaded Notifier
     *
     * @throws CryptoException
     * @throws IOException
     */
    Notifier loadFrom(ResourcePage config, Crypter crypter) throws CryptoException, IOException;

    /**
     * Load a Notifier object of this implementation using the parameters stored in the given config.
     *
     * @param config  Parameters will be read from this config
     *
     * @return A loaded Notifier
     *
     * @throws CryptoException
     * @throws IOException
     */
    default Notifier loadFrom(ResourcePage config) throws CryptoException, IOException {
        return loadFrom(config, null);
    }

}
