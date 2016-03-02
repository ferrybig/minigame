
package me.ferrybig.javacoding.minecraft.minigame.util;

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
}
