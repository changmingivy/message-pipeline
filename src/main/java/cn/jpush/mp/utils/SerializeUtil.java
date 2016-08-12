package cn.jpush.mp.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeUtil {
	public static byte[] getBytes(Object object) throws IOException {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        byteArrayOutputStream = new ByteArrayOutputStream();
        oos = new ObjectOutputStream(byteArrayOutputStream);
        oos.writeObject(object);
        return byteArrayOutputStream.toByteArray();
    }

    public static Object getObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = null;
        byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(byteArrayInputStream);
        return ois.readObject();

    }

}
