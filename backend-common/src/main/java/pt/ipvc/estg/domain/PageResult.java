package pt.ipvc.estg.domain;

import java.util.List;

public record PageResult<T>(List<T> items, long totalElements, int page, int size) {
}
