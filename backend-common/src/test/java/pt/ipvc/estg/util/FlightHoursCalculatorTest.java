package pt.ipvc.estg.util;

import org.junit.jupiter.api.Test;
import pt.ipvc.estg.entities.Flight;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FlightHoursCalculatorTest {

    @Test
    void completedHours_sumsOnlyCompletedFlights() {
        Flight completed = new Flight();
        completed.setStatus("completed");
        completed.setDuration(2.0);

        Flight scheduled = new Flight();
        scheduled.setStatus("scheduled");
        scheduled.setDuration(5.0);

        assertEquals(2.0, FlightHoursCalculator.completedHours(List.of(completed, scheduled)));
    }

    @Test
    void localHours_filtersByType() {
        Flight local = new Flight();
        local.setStatus("completed");
        local.setFlightType("local");
        local.setDuration(1.5);

        Flight nav = new Flight();
        nav.setStatus("completed");
        nav.setFlightType("navigation");
        nav.setDuration(3.0);

        assertEquals(1.5, FlightHoursCalculator.localHours(List.of(local, nav)));
    }
}
