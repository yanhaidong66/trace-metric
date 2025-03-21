package top.haidong556.metric.application.common.filterChainTemplate;
import java.util.*;

public class FilterChain<T> {
    private final List<AbstractFilter<T>> filters = new ArrayList<>();

    // 添加过滤器
    public FilterChain<T> addFilter(AbstractFilter<T> filter) {
        filters.add(filter);
        return this;
    }


    // 执行过滤器链
    public void executeFilters(T input) throws Exception {
        // 按优先级排序
        filters.sort(Comparator.comparingInt(AbstractFilter::getOrder));

        // 依次执行过滤器
        for (AbstractFilter<T> filter : filters) {
            if (filter.isEnabled()) {
                filter.apply(input);  // 执行过滤器
            }
        }
    }

    // 获取当前的过滤器列表
    public List<AbstractFilter<T>> getFilters() {
        return filters;
    }

    // 清除过滤器
    public void clearFilters() {
        filters.clear();
    }
}
