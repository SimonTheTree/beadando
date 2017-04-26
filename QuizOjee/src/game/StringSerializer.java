package game;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

public class StringSerializer {

	/**
	 * Decomposes an objcet into a string, which can be rebuilt using {@link #deSerialize(String)}
	 * @param o
	 * @return null when something went wrong, the String otherwise
	 */
	public static String serialize(Serializable o) {
		String serializedObject = null;
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream so = new ObjectOutputStream(bo);
			so.writeObject(o);
			so.close();
			serializedObject = Base64.getEncoder().encodeToString(bo.toByteArray()); 
		} catch (Exception e) {
			System.err.println("Exception while serializing!");
			e.printStackTrace();
		}

		return serializedObject;
	}

	/**
	 * Builds an object from {@link #serialize(Object)} string
	 * @param serializedObject
	 * @return null when something went wrong, the Object otherwise
	 */
	public static Object deSerialize(String serializedObject) {
		Object obj = null;
		try {
			byte[] data = Base64.getDecoder().decode(serializedObject);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			return o;
		} catch (Exception e) {
			System.err.println("Exception while deSerializing!");
			e.printStackTrace();
		}
		return obj;
	}

}
