package pt.ipvc.estg.desktop.api;

public final class BoDataAccess {

    private BoDataAccess() {
    }

    public static boolean useApi() {
        return AppConfig.isApiEnabled()
                && SessionContext.isAuthenticated()
                && SessionContext.isAdmin();
    }
}
