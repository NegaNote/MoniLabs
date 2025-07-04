package net.neganote.monilabs.utils;

import com.ibm.icu.util.Calendar;
import net.neganote.monilabs.client.render.effects.PrismFX;
import net.neganote.monilabs.config.MoniConfig;

public abstract class CalendarUtil {
    public static final boolean IS_PRIDE_MONTH;

    static {
        int currentMonth = Calendar
        .getInstance()
            .get(Calendar.MONTH);
        IS_PRIDE_MONTH = currentMonth == Calendar.JUNE
            || MoniConfig.INSTANCE.values.forcePrideMonth;
    }

    public static void init() {
        if (IS_PRIDE_MONTH) { PrismFX.PositionalColor.initPride(); }
    }
}
