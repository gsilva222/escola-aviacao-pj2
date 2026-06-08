package pt.ipvc.estg.util;

import pt.ipvc.estg.domain.PageQuery;

public final class PageQueryParser {
    private PageQueryParser() {}

    public static PageQuery parse(int page, int size, String sort) {
        if (sort == null || sort.trim().isEmpty()) {
            return PageQuery.of(page, size);
        }
        String[] parts = sort.split(",", 2);
        String property = parts[0].trim();
        boolean descending = parts.length == 2 && "desc".equalsIgnoreCase(parts[1].trim());
        return PageQuery.of(page, size, property.isEmpty() ? null : property, descending);
    }
}
