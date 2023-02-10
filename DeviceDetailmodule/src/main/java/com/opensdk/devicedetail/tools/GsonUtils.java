package com.opensdk.devicedetail.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.lang.reflect.Type;


/**
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2018/04/05
 * desc  : utils about gson
 *
 * 作者: 布兰柯基
 * 博客  : http://blankj.com
 * 时间  : 2018/04/05
 * 描述  : GSON工具类
 */
public final class GsonUtils {

    private static final Gson GSON = createGson(true);

    private static final Gson GSON_NO_NULLS = createGson(false);

    private GsonUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Gets pre-configured {@link Gson} instance
     * @return Gson  {@link Gson} instance
     *
     * 获取预配置的{@link Gson}实例
     * @return Gson  {@link Gson}实例
     */
    public static Gson getGson() {
        return getGson(true);
    }

    /**
     * Gets pre-configured {@link Gson} instance.
     * @param serializeNulls  determines if nulls will be serialized.
     * @return Gson  {@link Gson} instance.
     *
     * 获取预配置的{@link Gson}实例
     * @param serializeNulls 确定是否将空值序列化
     * @return Gson  {@link Gson}实例
     */
    public static Gson getGson(final boolean serializeNulls) {
        return serializeNulls ? GSON_NO_NULLS : GSON;
    }

    /**
     * Serializes an object into json
     * @param object the object to serialize
     * @return String  object serialized into json
     *
     * 将对象序列化为json
     * @param object  要序列化的对象
     * @return String  对象序列化为json
     */
    public static String toJson(final Object object) {
        return toJson(object, true);
    }

    /**
     * Serializes an object into json
     * @param object  the object to serialize
     * @param includeNulls  determines if nulls will be included
     * @return object  serialized into json
     *
     * 将对象序列化为json
     * @param object  要序列化的对象
     * @param includeNulls  确定是否包含空值
     * @return object  序列化为json
     */
    public static String toJson(final Object object, final boolean includeNulls) {
        return includeNulls ? GSON.toJson(object) : GSON_NO_NULLS.toJson(object);
    }


    /**
     * Converts {@link String} to given type
     * @param json  the json to convert.
     * @param type  type type json will be converted to.
     * @return T  instance of type
     *
     * 将{@link String}转换为给定类型
     * @param json  要转换的json
     * @param type Type json将被转换为的类型。
     * @return T  类型的实例
     */
    public static <T> T fromJson(final String json, final Class<T> type) {
        return GSON.fromJson(json, type);
    }

    /**
     * Converts {@link String} to given type
     * @param json  the json to convert.
     * @param type  type type json will be converted to.
     * @return T  instance of type
     *
     * 将{@link String}转换为给定类型
     * @param json  要转换的json
     * @param type Type json将被转换为的类型。
     * @return T  类型的实例
     */
    public static <T> T fromJson(final String json, final Type type) {
        return GSON.fromJson(json, type);
    }

    /**
     * Converts {@link Reader} to given type
     * @param reader  the reader to convert
     * @param type   type type json will be converted to
     * @return T  instance of type
     *
     * 将{@link Reader}转换为给定类型
     * @param reader  要被转换的reader
     * @param type Type json将被转换为的类型。
     * @return T  类型的实例
     */
    public static <T> T fromJson(final Reader reader, final Class<T> type) {
        return GSON.fromJson(reader, type);
    }

    /**
     * Converts {@link Reader} to given type
     * @param reader  the reader to convert
     * @param type   type type json will be converted to
     * @return T  instance of type
     *
     * 将{@link Reader}转换为给定类型
     * @param reader  要被转换的reader
     * @param type  Type json将被转换为的类型。
     * @return T  类型的实例
     */
    public static <T> T fromJson(final Reader reader, final Type type) {
        return GSON.fromJson(reader, type);
    }

    /**
     * Create a pre-configured {@link Gson} instance
     * @param serializeNulls  determines if nulls will be serialized
     * @return Gson  {@link Gson} instance
     *
     * 创建一个预配置的{@link Gson}实例。
     * @param serializeNulls  确定是否将空值序列化
     * @return Gson  {@link Gson}实例
     */
    private static Gson createGson(final boolean serializeNulls) {
        final GsonBuilder builder = new GsonBuilder();
        if (serializeNulls) builder.serializeNulls();
        return builder.create();
    }
}
