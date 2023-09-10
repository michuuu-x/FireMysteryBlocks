package cz.devfire.mysteryblocks.Database.Enum;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;

public enum ObjectType {

    CHAR(char.class),
    VARCHAR(String.class),
    TEXT(String.class),

    SERIAL(Integer.class),
    BIT(Boolean.class),
    TINYINT(Byte.class),
    BOOLEAN(Boolean.class),
    SMALLINT(Short.class),
    MEDIUMINT(Integer.class),
    INTEGER(Integer.class),
    BIGINT(Long.class),
    FLOAT(Float.class),
    DOUBLE(Double.class),

    DATE(LocalDate.class),
    DATETIME(LocalDateTime.class),
    TIMESTAMP(Timestamp.class),
    TIME(Time.class),
    YEAR(Year.class);

    final Class<?> clazz;

    ObjectType() {
        this.clazz = null;
    }

    ObjectType(Class<?> clazz) {
        this.clazz = clazz;
    }
}
