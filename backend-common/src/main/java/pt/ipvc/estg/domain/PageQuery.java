package pt.ipvc.estg.domain;

public record PageQuery(int page, int size, String sortField, boolean descending) {
    public static PageQuery of(int page, int size) {
        return new PageQuery(page, size, null, false);
    }

    public static PageQuery of(int page, int size, String sortField, boolean descending) {
        return new PageQuery(page, size, sortField, descending);
    }
}
