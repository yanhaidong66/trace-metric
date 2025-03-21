package top.haidong556.metric.domain.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

// 假设 JsonReflectiveBuilder 类在当前包中
import static top.haidong556.metric.domain.common.JsonReflectiveBuilder.*;

// 基础测试类
class BaseTestClass {
    protected String baseField = "baseValue";
}

// 注解测试类
class AnnotatedClass {
    @JsonProperty("@timestamp")
    private String timestamp;

    @JsonProperty("nested.annotated.field")
    private String nestedField;

    @JsonProperty("list_of_strings")
    private List<String> stringList;

    public String getTimestamp() { return timestamp; }
    public String getNestedField() { return nestedField; }
    public List<String> getStringList() { return stringList; }
}

// 复杂嵌套结构类
class ComplexRoot extends BaseTestClass {
    private String rootField;
    private AnnotatedClass annotated;
    private List<NestedObject> nestedList;
    private Map<String, Object> mapField;

    public String getRootField() { return rootField; }
    public AnnotatedClass getAnnotated() { return annotated; }
    public List<NestedObject> getNestedList() { return nestedList; }
    public Map<String, Object> getMapField() { return mapField; }
}

class NestedObject {
    private int id;
    private String name;
    private Map<String, Integer> properties;

    public int getId() { return id; }
    public String getName() { return name; }
    public Map<String, Integer> getProperties() { return properties; }
}

class MixedClass {
    @JsonProperty("@timestamp")
    private String timestamp;
    private String regular_field;

    public String getTimestamp() { return timestamp; }
    public String getRegularField() { return regular_field; }
}

class EdgeCaseClass {
    private Integer integerField;
    private boolean booleanField;
    private double doubleField;
    private String stringField;

    public Integer getIntegerField() { return integerField; }
    public boolean isBooleanField() { return booleanField; }
    public double getDoubleField() { return doubleField; }
    public String getStringField() { return stringField; }
}

class LargeData {
    private List<Map<String, Object>> data;
    public List<Map<String, Object>> getData() { return data; }
}


// 枚举测试类
enum TestEnum { VALUE1, VALUE2 }

class EnumTestClass {
    @JsonProperty("enum_value")
    private TestEnum enumValue;

    public TestEnum getEnumValue() { return enumValue; }
}


// 最终测试类
class JsonReflectiveBuilderTest {

    // 1. 基础功能测试
    @Test
    void basicFunctionalityTest() throws Exception {
        String json = "{" +
                "\"rootField\": \"testRoot\"," +
                "\"annotated\": {" +
                "\"@timestamp\": \"2025-03-13T16:46:29.861Z\"," +
                "\"nested.annotated.field\": \"nestedValue\"," +
                "\"list_of_strings\": [\"a\", \"b\"]" +
                "}," +
                "\"nestedList\": [" +
                "{" +
                "\"id\": 1," +
                "\"name\": \"obj1\"," +
                "\"properties\": {\"key1\": 100}" +
                "}," +
                "{" +
                "\"id\": 2," +
                "\"name\": \"obj2\"," +
                "\"properties\": {\"key2\": 200}" +
                "}" +
                "]," +
                "\"mapField\": {" +
                "\"keyA\": \"valueA\"," +
                "\"keyB\": 123" +
                "}" +
                "}";

        ComplexRoot root = build(json, ComplexRoot.class);

        // 继承字段验证
        assertEquals("baseValue", root.baseField);

        // 根对象字段
        assertEquals("testRoot", root.getRootField());

        // 注解字段验证
        assertEquals("2025-03-13T16:46:29.861Z", root.getAnnotated().getTimestamp());
        assertEquals("nestedValue", root.getAnnotated().getNestedField());
        assertEquals(Arrays.asList("a", "b"), root.getAnnotated().getStringList());

        // 列表验证
        List<NestedObject> nestedList = root.getNestedList();
        assertEquals(2, nestedList.size());
        assertEquals(1, nestedList.get(0).getId());
        assertEquals("obj1", nestedList.get(0).getName());
        assertEquals(Collections.singletonMap("key1", 100), nestedList.get(0).getProperties());

        // Map验证
        Map<String, Object> map = root.getMapField();
        assertEquals("valueA", map.get("keyA"));
        assertEquals(123, map.get("keyB"));
    }

    // 2. 枚举类型测试
    @Test
    void enumTypeTest() throws Exception {
        String json = "{" +
                "\"enum_value\": \"VALUE2\"" +
                "}";

        EnumTestClass obj = build(json, EnumTestClass.class);
        assertEquals(TestEnum.VALUE2, obj.getEnumValue());
    }

    // 3. 混合注解与默认字段测试
    @Test
    void mixedAnnotationTest() throws Exception {
        String json = "{" +
                "\"@timestamp\": \"2025-03-13T16:46:29.861Z\"," +
                " \"regular_field\": \"defaultValue\"" +
                "}";

        MixedClass obj = build(json, MixedClass.class);
        assertEquals("2025-03-13T16:46:29.861Z", obj.getTimestamp());
        assertEquals("defaultValue", obj.getRegularField());
    }

    // 4. 边界条件测试
    @Test
    void edgeCaseTest() throws Exception {
        String json = "{" +
                "\"integerField\": null," +
                "\"booleanField\": false," +
                "\"doubleField\": 3.14," +
                "\"stringField\": null" +
                "}";

        EdgeCaseClass obj = build(json, EdgeCaseClass.class);
        assertNull(obj.getIntegerField());
        assertFalse(obj.isBooleanField());
        assertEquals(3.14, obj.getDoubleField(), 0.001);
        assertNull(obj.getStringField());
    }

    // 5. 性能测试（简单验证）
    @Test
    void performanceTest() throws Exception {
        String largeJson = "{" +
                "\"data\": [" +
                "{\"id\": 1, \"value\": 100}, " +
                "{\"id\": 2, \"value\": 200}, " +
                "{\"id\": 3, \"value\": 300}" +
                "]" +
                "}";

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            build(largeJson, LargeData.class);
        }
        long duration = System.currentTimeMillis() - startTime;

        System.out.println("Performance test: " + duration + "ms for 1000 builds");
        assertTrue(duration < 50); // 简单验证性能
    }
}