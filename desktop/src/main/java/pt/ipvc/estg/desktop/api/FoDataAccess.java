package pt.ipvc.estg.desktop.api;

public final class FoDataAccess {

    private FoDataAccess() {
    }

    public static boolean useApi() {
        return AppConfig.isApiEnabled()
                && SessionContext.isAuthenticated()
                && SessionContext.isStudent();
    }
}
