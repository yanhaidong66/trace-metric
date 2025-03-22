package top.haidong556.metric.domain.model.metricAggregate.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter

@AllArgsConstructor
@NoArgsConstructor
public class ProcessEntity {
    public String command_line;
    public String executable;
    public String name;
    public long pid;
    public String state;
    public Parent parent;
    public String working_directory;
    public List<String> args;
    public long pgid;
    public Cpu cpu;
    public Memory memory;

    
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Parent {
        public long pid;
        @Override
        public String toString() {
            return "{\n" +
                    "            \"pid\": " + pid + "\n" +
                    "        }";
        }
    }
    
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Cpu {
        public String start_time;
        public double pct;
        @Override
        public String toString() {
            return "{\n" +
                    "            \"start_time\": \"" + start_time + "\",\n" +
                    "            \"pct\": " + pct + "\n" +
                    "        }";
        }
    }
    
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Memory {
        public double pct;
        @Override
        public String toString() {
            return "{\n" +
                    "            \"pct\": " + pct + "\n" +
                    "        }";
        }
    }

    @Override
    public String toString() {
        return "{\n" +
                "        \"command_line\": \"" + command_line + "\",\n" +
                "        \"executable\": \"" + executable + "\",\n" +
                "        \"name\": \"" + name + "\",\n" +
                "        \"pid\": " + pid + ",\n" +
                "        \"state\": \"" + state + "\",\n" +
                "        \"parent\": " + (parent != null ? parent.toString() : "null") + ",\n" +
                "        \"working_directory\": \"" + working_directory + "\",\n" +
                "        \"args\": " + jsonArray(args) + ",\n" +
                "        \"pgid\": " + pgid + ",\n" +
                "        \"cpu\": " + (cpu != null ? cpu.toString() : "null") + ",\n" +
                "        \"memory\": " + (memory != null ? memory.toString() : "null") + "\n" +
                "    }";
    }

    private String jsonArray(List<String> list) {
        return list != null ? "[" + String.join(", ", list.stream().map(s -> "\"" + s + "\"").toArray(String[]::new)) + "]" : "null";
    }
}

