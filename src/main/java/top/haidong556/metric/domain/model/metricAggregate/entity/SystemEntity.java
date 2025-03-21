package top.haidong556.metric.domain.model.metricAggregate.entity;

import lombok.Builder;
import lombok.Getter;

/**
 * SystemEntity类用于描述机器的整体状态，包括网络和进程相关信息。
 */
@Getter
@Builder
public class SystemEntity {
    public Network network; // 网络状态信息
    public Process process; // 进程状态信息
    public String state;
    @Override
    public String toString() {
        return "{\n" +
                "    \"process\": " + (process != null ? process.toString() : "null") + ",\n" +
                "    \"state\": \"" + state + "\"\n" +
                "}";
    }



    /**
     * Network类用于封装机器的网络状态信息。
     */
    @Getter
    @Builder
    public static class Network {
        public String name; // 网络接口名称
        public In in; // 网络输入统计信息
        public Out out; // 网络输出统计信息
        @Override
        public String toString() {
            return "{\n" +
                    "        \"name\": \"" + name + "\",\n" +
                    "        \"in\": " + (in != null ? in.toString() : "null") + ",\n" +
                    "        \"out\": " + (out != null ? out.toString() : "null") + "\n" +
                    "    }";
        }
        /**
         * In类用于封装网络输入的统计信息。
         */
        @Getter
        @Builder
        public static class In {
            public long packets; // 接收的数据包数量
            public long errors; // 接收时发生的错误数量
            public long dropped; // 接收时丢弃的数据包数量
            public long bytes; // 接收的字节数
            @Override
            public String toString() {
                return "{\n" +
                        "            \"packets\": " + packets + ",\n" +
                        "            \"errors\": " + errors + ",\n" +
                        "            \"dropped\": " + dropped + ",\n" +
                        "            \"bytes\": " + bytes + "\n" +
                        "        }";
            }
        }

        /**
         * Out类用于封装网络输出的统计信息。
         */
        @Getter
        @Builder
        public static class Out {
            public long bytes; // 发送的字节数
            public long errors; // 发送时发生的错误数量
            public long dropped; // 发送时丢弃的数据包数量
            public long packets; // 发送的数据包数量
            @Override
            public String toString() {
                return "{\n" +
                        "            \"bytes\": " + bytes + ",\n" +
                        "            \"errors\": " + errors + ",\n" +
                        "            \"dropped\": " + dropped + ",\n" +
                        "            \"packets\": " + packets + "\n" +
                        "        }";
            }
        }
    }

    /**
     * Process类用于封装机器上运行进程的状态信息。
     */
    @Getter
    @Builder
    public static class Process {
        public Cgroup cgroup; // 控制组信息
        public String state; // 进程状态
        public long numThreads; // 进程中的线程数量
        public String cmdLine; // 启动进程的命令行
        public Memory memory; // 进程的内存使用情况
        public Cpu cpu; // CPU 使用情况
        public Fd fd; // 文件描述符信息
        @Override
        public String toString() {
            return "{\n" +
                    "        \"num_threads\": " + numThreads + ",\n" +
                    "        \"cmdline\": \"" + cmdLine + "\",\n" +
                    "        \"memory\": " + (memory != null ? memory.toString() : "null") + ",\n" +
                    "        \"cpu\": " + (cpu != null ? cpu.toString() : "null") + ",\n" +
                    "        \"fd\": " + (fd != null ? fd.toString() : "null") + ",\n" +
                    "        \"cgroup\": " + (cgroup != null ? cgroup.toString() : "null") + ",\n" +
                    "        \"state\": \"" + state + "\"\n" +
                    "    }";
        }

        /**
         * Cgroup类用于封装进程的控制组信息。
         */
        @Getter
        @Builder
        public static class Cgroup {
            public String id; // 控制组ID
            public String path; // 控制组路径
            public long cgroupsVersion; // 控制组版本
            @Override
            public String toString() {
                return "{\n" +
                        "            \"id\": \"" + id + "\",\n" +
                        "            \"path\": \"" + path + "\",\n" +
                        "            \"cgroups_version\": " + cgroupsVersion + "\n" +
                        "        }";
            }
        }

        /**
         * Memory类用于封装进程的内存使用情况。
         */
        @Getter
        @Builder
        public static class Memory {
            public long share; // 进程共享内存大小
            public Rss rss; // 进程常驻集大小
            public long size; // 进程总内存大小
            @Override
            public String toString() {
                return "{\n" +
                        "            \"size\": " + size + ",\n" +
                        "            \"share\": " + share + ",\n" +
                        "            \"rss\": " + (rss != null ? rss.toString() : "null") + "\n" +
                        "        }";
            }

            /**
             * Rss类用于封装进程的常驻集信息。
             */
            @Getter
            @Builder
            public static class Rss {
                public long bytes; // 常驻集字节数
                public double pct; // 常驻集占系统总内存的百分比
                @Override
                public String toString() {
                    return "{\n" +
                            "                \"bytes\": " + bytes + ",\n" +
                            "                \"pct\": " + pct + "\n" +
                            "            }";
                }
            }
        }

        /**
         * Cpu类用于封装进程的 CPU 使用情况。
         */
        @Getter
        @Builder
        public static class Cpu {
            public String startTime; // 进程的启动时间
            public Total total; // CPU 总使用情况
            public String toString() {
                return "{\n" +
                        "            \"start_time\": \"" + startTime + "\",\n" +
                        "            \"total\": " + (total != null ? total.toString() : "null") + "\n" +
                        "        }";
            }

            /**
             * Total类用于封装 CPU 总使用情况。
             */
            @Getter
            @Builder
            public static class Total {
                public Norm norm; // 归一化 CPU 使用率
                public long value; // CPU 使用总时间
                public double pct; // CPU 使用百分比
                @Override
                public String toString() {
                    return "{\n" +
                            "                \"norm\": " + (norm != null ? norm.toString() : "null") + ",\n" +
                            "                \"value\": " + value + ",\n" +
                            "                \"pct\": " + pct + "\n" +
                            "            }";
                }
                /**
                 * Norm类用于封装归一化 CPU 使用率信息。
                 */
                @Getter
                @Builder
                public static class Norm {
                    public double pct; // 归一化 CPU 使用百分比

                    @Override
                    public String toString() {
                        return "{\n" +
                                "                    \"pct\": " + pct + "\n" +
                                "                }";
                    }
                }
            }


        }

        /**
         * Fd类用于封装进程的文件描述符信息。
         */
        @Getter
        @Builder
        public static class Fd {
            public long open; // 进程打开的文件描述符数量
            public Limit limit; // 进程的文件描述符限制

            @Override
            public String toString() {
                return "{\n" +
                        "            \"open\": " + open + ",\n" +
                        "            \"limit\": " + (limit != null ? limit.toString() : "null") + "\n" +
                        "        }";
            }
            /**
             * Limit类用于封装文件描述符限制信息。
             */
            @Getter
            @Builder
            public static class Limit {
                public long hard; // 文件描述符硬限制
                public long soft; // 文件描述符软限制

                @Override
                public String toString() {
                    return "{\n" +
                            "                \"hard\": " + hard + ",\n" +
                            "                \"soft\": " + soft + "\n" +
                            "            }";
                }
            }
        }
    }
}
