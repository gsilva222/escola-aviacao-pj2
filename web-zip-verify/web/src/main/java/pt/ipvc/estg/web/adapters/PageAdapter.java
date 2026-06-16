package pt.ipvc.estg.web.adapters;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;

import java.util.List;

public final class PageAdapter {
    private PageAdapter() {}

    public static Pageable toPageable(PageQuery query) {
        if (query.sortField() == null || query.sortField().isBlank()) {
            return PageRequest.of(query.page(), query.size());
        }
        Sort.Direction direction = query.descending() ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(query.page(), query.size(), Sort.by(direction, query.sortField()));
    }

    public static <T> PageResult<T> fromPage(List<T> content, long total, PageQuery query) {
        return new PageResult<>(content, total, query.page(), query.size());
    }
}
