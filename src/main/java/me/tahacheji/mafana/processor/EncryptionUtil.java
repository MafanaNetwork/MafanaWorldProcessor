package me.tahacheji.mafana.processor;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class EncryptionUtil {
    private final String ALGORITHM = "AES";
    private final String CIPHER_INSTANCE = "AES/ECB/PKCS5Padding";
    private final Key SECRET_KEY;
    public EncryptionUtil() {
        SECRET_KEY = getSecretKey("mafana");
    }

    private static final String HASH_ALGORITHM = "SHA-256";


    public UUID stringToUUID(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert the hash bytes to a UUID
            long mostSigBits = 0;
            long leastSigBits = 0;
            for (int i = 0; i < 8; i++) {
                mostSigBits = (mostSigBits << 8) | (hashBytes[i] & 0xff);
            }
            for (int i = 8; i < 16; i++) {
                leastSigBits = (leastSigBits << 8) | (hashBytes[i] & 0xff);
            }

            return new UUID(mostSigBits, leastSigBits);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null; // Handle the exception appropriately in your code
        }
    }

    private Key getSecretKey(String keyString) {
        // Generate a secret key from the provided keyString
        byte[] keyBytes = Arrays.copyOf(keyString.getBytes(StandardCharsets.UTF_8), 16); // AES keys are 128 bits (16 bytes)
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public String encryptList(List<String> list) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY);

            String serializedList = String.join(",", list);
            byte[] encryptedBytes = cipher.doFinal(serializedList.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> decryptToList(String encryptedString) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY);

            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedString);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            String decryptedString = new String(decryptedBytes, StandardCharsets.UTF_8);

            return Arrays.asList(decryptedString.split(","));
        } catch (Exception e) {
            return new ArrayList<>(); // Return an empty list if decryption fails
        }
    }

    public String encodeItem(ItemStack item) {
        return itemToBase64(item);
    }

    public ItemStack decodeItems(String data) throws Exception {
        return itemFromBase64(data);
    }

    public String itemToBase64(ItemStack item) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Save the item
            dataOutput.writeObject(item);

            // Serialize the item
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stack.", e);
        }
    }

    public ItemStack itemFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            // Read the serialized item
            ItemStack item = (ItemStack) dataInput.readObject();

            dataInput.close();
            return item;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}
