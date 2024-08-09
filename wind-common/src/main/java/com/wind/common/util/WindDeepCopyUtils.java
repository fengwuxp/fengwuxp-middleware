package com.wind.common.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.esotericsoftware.kryo.util.Pool;
import com.wind.common.exception.BaseException;
import com.wind.common.exception.DefaultExceptionCode;
import org.springframework.lang.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 深 copy 工具类
 *
 * @author wuxp
 * @date 2024-08-07 14:42
 **/
public final class WindDeepCopyUtils {

    private static final KryoCodec CODEC = new KryoCodec();

    private WindDeepCopyUtils() {
        throw new AssertionError();
    }

    /**
     * java 深 copy 工具类
     * 注意：被 copy 对象的类类型必须存在可见的构造
     *
     * @param object 原对象
     * @return 深 copy 后的新对象
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T copy(@Nullable T object) {
        if (object == null) {
            return null;
        }
        try {
            return (T) CODEC.decode(CODEC.encode(object));
        } catch (IOException exception) {
            throw new BaseException(DefaultExceptionCode.COMMON_ERROR, "deep copy error", exception);
        }
    }

    /**
     * @see https://github.com/EsotericSoftware/kryo
     */
    private static final class KryoCodec {

        private final Pool<Kryo> kryoPool;

        private final Pool<Input> inputPool;

        private final Pool<Output> outputPool;

        public KryoCodec() {
            this(null);
        }

        public KryoCodec(ClassLoader classLoader) {

            this.kryoPool = new Pool<Kryo>(true, false, 1024) {
                @Override
                protected Kryo create() {
                    return createKryo(classLoader);
                }
            };

            this.inputPool = new Pool<Input>(true, false, 512) {
                @Override
                protected Input create() {
                    return new Input(8192);
                }
            };

            this.outputPool = new Pool<Output>(true, false, 512) {
                @Override
                protected Output create() {
                    return new Output(8192, -1);
                }
            };
        }

        private Kryo createKryo(ClassLoader classLoader) {
            Kryo kryo = new Kryo();
            if (classLoader != null) {
                kryo.setClassLoader(classLoader);
            }
            kryo.setRegistrationRequired(false);
            kryo.setReferences(false);
            kryo.addDefaultSerializer(Throwable.class, new JavaSerializer());
            return kryo;
        }

        public Object decode(byte[] bytes) throws IOException {
            Kryo kryo = kryoPool.obtain();
            Input input = inputPool.obtain();
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
                input.setInputStream(inputStream);
                return kryo.readClassAndObject(input);
            } finally {
                kryoPool.free(kryo);
                inputPool.free(input);
            }
        }

        public byte[] encode(Object in) throws IOException {
            Kryo kryo = kryoPool.obtain();
            Output output = outputPool.obtain();
            try (OutputStream outputStream = new ByteArrayOutputStream()) {
                output.setOutputStream(outputStream);
                kryo.writeClassAndObject(output, in);
                output.flush();
                return output.getBuffer();
            } finally {
                kryoPool.free(kryo);
                outputPool.free(output);
            }
        }
    }
}
