package igna778.suppressiontool.utils;

import static carpet.api.settings.RuleCategory.COMMAND;

import carpet.api.settings.Rule;

public class STSettings {

    public static boolean entityUUID = true;

    @Rule(categories = COMMAND)
    public static boolean jigsawOOM = false;

}
