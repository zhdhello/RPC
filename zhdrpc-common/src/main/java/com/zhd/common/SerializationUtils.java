package com.zhd.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * ���л�/�����л�������
 *
 */
public class SerializationUtils {
	private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>();

    private static Objenesis objenesis = new ObjenesisStd(true);
    public static <T> Schema<T> getSchema(Class<T> cls){
    	Schema<T> schema = (Schema<T>)cachedSchema.get(cls);
    	if(schema == null){
    		schema = RuntimeSchema.createFrom(cls);
    		if(schema!=null){
    			cachedSchema.put(cls, schema);
    		}
    	}
    	return schema;
    }
    /**
     * ���л�
     * @param obj
     * @return
     */
   @SuppressWarnings("unchecked")
   public static <T> byte[] serialize(T obj){
	    //����ռ�
	   	LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
	   	try{
	    	Class<T> cls = (Class<T>) obj.getClass();
	    	Schema<T> schema =  getSchema(cls);
	    	return ProtobufIOUtil.toByteArray(obj, schema, buffer);
    	}catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally{
    		buffer.clear();
    	}
    }
   /**
    * �����л�
    * @param bytes
    * @param cls
    * @return
    */
   public static <T>T deserialize(byte[] bytes,Class<T> cls){
	   try {
		   T message = (T) objenesis.newInstance(cls);//ʵ����
		   Schema<T> schema = getSchema(cls);
		   ProtobufIOUtil.mergeFrom(bytes, message, schema);
		   return message;
	   } catch (Exception e) {
	       throw new IllegalStateException(e.getMessage(), e);
	   }

   }
}
