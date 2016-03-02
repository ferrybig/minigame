
package me.ferrybig.javacoding.minecraft.minigame.util;

import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SafeUtil {

	private static final Logger LOG = Logger.getLogger(SafeUtil.class.getName());

	public static String toString(Object obj) {
		try {
			return String.valueOf(obj);
		} catch (Throwable a) {
			LOG.log(Level.SEVERE, "Could not call " + obj.getClass() + ".toString()", a);
			return "ERROR";
		}
	}
	
	public static <E extends Throwable> E createException(
			Function<String, E> creator, String format, Object obj) {
		Objects.requireNonNull(creator, "creator == null");
		Objects.requireNonNull(format, "format == null");
		String val;
		Throwable suppressed;
		try {
			val = String.valueOf(obj);
			suppressed = null;
		} catch (Throwable a) {
			val = "ERROR";
			suppressed = a;
		}
		E ex = creator.apply(String.format(format, val));
		if(suppressed != null)
			ex.addSuppressed(suppressed);
		return ex;
	}
}
