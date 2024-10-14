package igna778.suppressiontool.utils;

import carpet.logging.HUDLogger;

import java.lang.reflect.Field;

public class EntityUuidLogger extends HUDLogger {
    public static EntityUuidLogger create() {
        try {
            return new EntityUuidLogger(STSettings.class.getDeclaredField("entityUUID"), "entityUUID", "all", new String[]{"all", "total", "overworld", "nether", "end"}, false);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
    public EntityUuidLogger(Field field, String logName, String def, String[] options, boolean strictOptions) {
        super(field, logName, def, options, strictOptions);
    }
}