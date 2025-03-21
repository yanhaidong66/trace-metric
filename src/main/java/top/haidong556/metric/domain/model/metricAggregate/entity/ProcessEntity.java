package top.haidong556.metric.domain.model.metricAggregate.entity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
@Getter
@Builder
public class ProcessEntity {
    public String commandLine;
    public String executable;
    public String name;
    public long pid;
    public String state;
    public Parent parent;
    public String workingDirectory;
    public List<String> args;
    public long pgId;
    public Cpu cpu;
    public Memory memory;

    @Builder
    @Getter
    public static class Parent {
        public long pid;
        @Override
        public String toString() {
            return "{\n" +
                    "            \"pid\": " + pid + "\n" +
                    "        }";
        }
    }
    @Builder
    @Getter
    public static class Cpu {
        public String startTime;
        public double pct;
        @Override
        public String toString() {
            return "{\n" +
                    "            \"start_time\": \"" + startTime + "\",\n" +
                    "            \"pct\": " + pct + "\n" +
                    "        }";
        }
    }
    @Builder
    @Getter
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
                "        \"command_line\": \"" + commandLine + "\",\n" +
                "        \"executable\": \"" + executable + "\",\n" +
                "        \"name\": \"" + name + "\",\n" +
                "        \"pid\": " + pid + ",\n" +
                "        \"state\": \"" + state + "\",\n" +
                "        \"parent\": " + (parent != null ? parent.toString() : "null") + ",\n" +
                "        \"working_directory\": \"" + workingDirectory + "\",\n" +
                "        \"args\": " + jsonArray(args) + ",\n" +
                "        \"pgid\": " + pgId + ",\n" +
                "        \"cpu\": " + (cpu != null ? cpu.toString() : "null") + ",\n" +
                "        \"memory\": " + (memory != null ? memory.toString() : "null") + "\n" +
                "    }";
    }

    private String jsonArray(List<String> list) {
        return list != null ? "[" + String.join(", ", list.stream().map(s -> "\"" + s + "\"").toArray(String[]::new)) + "]" : "null";
    }
}

