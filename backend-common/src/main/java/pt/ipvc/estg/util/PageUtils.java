package pt.ipvc.estg.util;

import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;

import java.util.Comparator;
import java.util.List;

public final class PageUtils {
    private PageUtils() {}

    public static <T> PageResult<T> paginate(List<T> source, PageQuery query) {
        List<T> sorted = sort(source, query);
        int page = Math.max(0, query.page());
        int size = Math.max(1, query.size());
        int from = Math.min(page * size, sorted.size());
        int to = Math.min(from + size, sorted.size());
        return new PageResult<>(sorted.subList(from, to), sorted.size(), page, size);
    }

    private static <T> List<T> sort(List<T> source, PageQuery query) {
        if (query.sortField() == null || query.sortField().isBlank()) {
            return List.copyOf(source);
        }
        Comparator<T> comparator = Comparator.comparing(
                item -> String.valueOf(readProperty(item, query.sortField())),
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
        );
        if (query.descending()) {
            comparator = comparator.reversed();
        }
        return source.stream().sorted(comparator).toList();
    }

    private static Object readProperty(Object item, String field) {
        try {
            String getter = "get" + Character.toUpperCase(field.charAt(0)) + field.substring(1);
            return item.getClass().getMethod(getter).invoke(item);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
}
