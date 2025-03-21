package top.haidong556.metric.application.common.filterChainTemplate;

/**
 * 通用过滤器接口，支持优先级排序和启用控制。
 */
public interface AbstractFilter<T> {

    /**
     * 获取过滤器的执行优先级。
     * 数值越小，优先级越高。
     *
     * @return 执行优先级
     */
    int getOrder();

    /**
     * 是否启用该过滤器。
     * 默认返回 true，子类可以覆盖该方法。
     *
     * @return true: 启用，false: 禁用
     */
    default boolean isEnabled() {
        return true;
    }

    /**
     * 执行过滤逻辑（无参数）。
     *
     * @throws Exception 过滤器执行异常
     */
    default void apply() throws Exception {
        apply(null);
    }

    /**
     * 执行过滤逻辑（带泛型参数）。
     *
     *
     * @param input 传入的数据
     * @throws Exception 过滤器执行异常
     */
    void apply(T input) throws Exception;
}
