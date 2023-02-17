package hu.bnorbi.costtracker.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * DateUtil osztály, mely statikus metódusokat, változokat tartalmaz, melyek dátumok kezelésére, validálásra szolgálnak.
 */
public class DateUtil {

    private static final String UTC = "UTC";
    private static final ZoneId ZONE_ID_UTC = ZoneId.of(UTC);

    public static final String[] MONTHS = {
            "január", "február", "március", "április", "május", "június", "július",
            "augusztus", "szeptember", "október", "november", "december"
    };

    /**
     * Statikus metódus, mely vissza adja az aktuális dátumot (UTC - ZonedDateTime)
     *
     * @return {@code ZonedDateTime} aktuális dátum (UTC)
     */
    public static ZonedDateTime getZonedDateTimeNowInUTC() {
        return ZonedDateTime.now(ZONE_ID_UTC);
    }

    /**
     * Statikus metódus, mely vissza adja az aktuális dátumot (UTC - OffsetDateTime)
     *
     * @return {@code OffsetDateTime} aktuális dátum (UTC)
     */
    public static OffsetDateTime getOffsetDateTimeNowInUTC() {
        return OffsetDateTime.now(ZONE_ID_UTC);
    }

    /**
     * Ellenörzi, hogy a paraméterben megadott date megfelel a második paraméterben megadott dátum formátuknak-e
     *
     * @param date   {@code String} dátum mely ellenörzésre kerül
     * @param format {@code String} dátum formátum
     * @return {@code true} abban az esetben ha a dátum megfelel a formátumnak
     * {@code false} máskülönben
     */
    public static boolean isValidDate(String date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setLenient(false);

        try {
            simpleDateFormat.parse(date.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }
}
