package pt.ipvc.estg.bootstrap;

import pt.ipvc.estg.dal.mock.MockDataSeeder;

public final class MockDataBootstrap {
    private static volatile boolean seeded;

    private MockDataBootstrap() {}

    public static void ensureSeeded() {
        if (!seeded) {
            synchronized (MockDataBootstrap.class) {
                if (!seeded) {
                    MockDataSeeder.seedAllData();
                    seeded = true;
                }
            }
        }
    }

    static void resetForTests() {
        seeded = false;
    }
}
