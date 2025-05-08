package io.github.rusted.simplestock.util;

import android.icu.text.NumberFormat;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class FormatUtil {
    @NotNull
    public static String format(double value) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.FRANCE);
        return numberFormat.format(value);
    }
}
