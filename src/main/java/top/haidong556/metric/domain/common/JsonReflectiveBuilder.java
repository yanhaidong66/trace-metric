package top.haidong556.metric.domain.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 高性能 JSON 反射构建器：支持嵌套对象、List、Map 和 @JsonProperty 注解映射
 */
public class JsonReflectiveBuilder {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 缓存 Field 映射，避免重复反射
    private static final Map<Class<?>, Map<String, Field>> fieldCache = new ConcurrentHashMap<>();

    public static <T> T build(String json, Class<T> clazz) throws Exception {
        JsonNode rootNode = objectMapper.readTree(json);
        return buildFromNode(rootNode, clazz);
    }

    private static <T> T buildFromNode(JsonNode node, Class<T> clazz) throws Exception {
        T instance = clazz.getDeclaredConstructor().newInstance();
        Map<String, Field> fieldMap = getCachedFields(clazz);

        for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
            String jsonFieldName = entry.getKey();
            Field field = entry.getValue();

            if (!node.has(jsonFieldName)) continue;
            JsonNode childNode = node.get(jsonFieldName);

            if (childNode.isValueNode() || childNode.isNull()) {
                setPrimitiveValue(instance, field, childNode);
            } else if (childNode.isObject()) {
                setObjectValue(instance, field, childNode);
            } else if (childNode.isArray()) {
                setArrayValue(instance, field, childNode);
            }
        }
        return instance;
    }

    // 缓存字段及@JsonProperty处理
    private static Map<String, Field> getCachedFields(Class<?> clazz) {
        return fieldCache.computeIfAbsent(clazz, cls -> {
            Map<String, Field> map = new HashMap<>();
            for (Field field : cls.getDeclaredFields()) {
                field.setAccessible(true);
                String jsonName = Optional.ofNullable(field.getAnnotation(JsonProperty.class))
                        .map(JsonProperty::value)
                        .orElse(field.getName());
                map.put(jsonName, field);
            }
            return map;
        });
    }

    private static void setPrimitiveValue(Object instance, Field field, JsonNode node) throws Exception {
        if (node.isNull()) {
            if (!field.getType().isPrimitive()) {
                field.set(instance, null);
            }
            return;
        }
        Class<?> type = field.getType();
        Object value;

        if (type.isEnum()) {
            // 安全Enum处理
            value = Enum.valueOf((Class<Enum>) type, node.asText());
        } else if (type == String.class) {
            value = node.asText();
        } else if (type == Long.class || type == long.class) {
            value = node.asLong();
        } else if (type == Double.class || type == double.class) {
            value = node.asDouble();
        } else if (type == Boolean.class || type == boolean.class) {
            value = node.asBoolean();
        } else if (type == Integer.class || type == int.class) {
            value = node.asInt();
        } else {
            // fallback 防止遗漏
            value = objectMapper.treeToValue(node, type);
        }
        field.set(instance, value);
    }

    private static void setObjectValue(Object instance, Field field, JsonNode node) throws Exception {
        Class<?> fieldType = field.getType();

        if (Map.class.isAssignableFrom(fieldType)) {
            field.set(instance, buildMap(node));
        } else {
            Object nestedObject = buildFromNode(node, fieldType);
            field.set(instance, nestedObject);
        }
    }

    private static void setArrayValue(Object instance, Field field, JsonNode node) throws Exception {
        if (!List.class.isAssignableFrom(field.getType())) return;

        List<Object> list = new ArrayList<>(node.size());
        Class<?> genericType = getListGenericType(field);

        for (JsonNode elementNode : node) {
            if (genericType != null && !isPrimitiveOrWrapper(genericType) && !genericType.isEnum()) {
                Object nestedElement = buildFromNode(elementNode, genericType);
                list.add(nestedElement);
            } else {
                list.add(convertJsonNode(elementNode));
            }
        }
        field.set(instance, list);
    }

    private static Map<String, Object> buildMap(JsonNode node) {
        Map<String, Object> map = new HashMap<>();
        node.fields().forEachRemaining(entry -> map.put(entry.getKey(), convertJsonNode(entry.getValue())));
        return map;
    }

    private static Object convertJsonNode(JsonNode node) {
        if (node.isTextual()) return node.asText();
        if (node.isInt()) return node.asInt();
        if (node.isLong()) return node.asLong();
        if (node.isDouble()) return node.asDouble();
        if (node.isBoolean()) return node.asBoolean();

        if (node.isObject()) {
            Map<String, Object> map = new HashMap<>();
            node.fields().forEachRemaining(e -> map.put(e.getKey(), convertJsonNode(e.getValue())));
            return map;
        }
        if (node.isArray()) {
            List<Object> list = new ArrayList<>();
            node.forEach(element -> list.add(convertJsonNode(element)));
            return list;
        }
        return null;
    }

    private static Class<?> getListGenericType(Field field) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            Type[] typeArgs = ((ParameterizedType) genericType).getActualTypeArguments();
            if (typeArgs.length > 0 && typeArgs[0] instanceof Class<?>) {
                return (Class<?>) typeArgs[0];
            }
        }
        return null;
    }

    private static boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() ||
                type == String.class ||
                type == Integer.class ||
                type == Long.class ||
                type == Double.class ||
                type == Boolean.class ||
                type == Float.class ||
                type == Short.class ||
                type == Byte.class ||
                type == Character.class;
    }
}
