package RateControl.Util;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Serializer {
    public static Serializable deserializeFromString(String serializedKey) {
        try {
            byte[] bytes = serializedKey.getBytes(StandardCharsets.ISO_8859_1);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return (Serializable) ois.readObject();

        } catch (Exception e) {
            e.printStackTrace(); // Handle the exception appropriately
            return null;
        }
    }
}
