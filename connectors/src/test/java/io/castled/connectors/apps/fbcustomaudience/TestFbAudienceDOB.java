package io.castled.connectors.apps.fbcustomaudience;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.time.Month;
import java.time.MonthDay;
import java.time.Year;
import java.time.format.DateTimeFormatter;

public class TestFbAudienceDOB {

    @Test
    public void formatValue() {

        DateTimeFormatter formatY = DateTimeFormatter.ofPattern("yyyy");
        assertTrue("1980".equals(Year.of(Integer.parseInt("1980")).format(formatY)));
        DateTimeFormatter formatM = DateTimeFormatter.ofPattern("MM");
        assertTrue("09".equals(MonthDay.of(9, 1).format(formatM)));
        DateTimeFormatter formatD =  DateTimeFormatter.ofPattern( "dd");
        assertTrue("08".equals(MonthDay.of(1, 8).format(formatD)));
        System.out.println(Month.of(9));
    }
}
