package com.example.gustavo.photostest.utils;

import android.util.Base64;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Created by gustavomedina on 11/04/17.
 */

public class GsonUtils {
    /**
     * {@link ExclusionStrategy} that skips fields without {@link SerializedName} attribute.
     *
     */
    public static class ExcludeFieldsWithoutSerializedName implements ExclusionStrategy {
        @Override
        public boolean shouldSkipClass(Class<?> clss) {

            return false;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes fieldAttributes) {
            // get all annotations, skip these without @SerializedName
            Collection<Annotation> annotations = fieldAttributes.getAnnotations();
            for (Annotation a : annotations) {
                if (a instanceof SerializedName) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Serializes byte arrays into base64 strings
     *
     */
    public static final class ByteArrayToBase64Serializer
            implements
            JsonSerializer<byte[]>,
            JsonDeserializer<byte[]> {
        @Override
        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Base64.encodeToString(src, Base64.NO_PADDING | Base64.NO_WRAP));
        }

        @Override
        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return Base64.decode(json.getAsString(), Base64.NO_PADDING | Base64.NO_WRAP);
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
