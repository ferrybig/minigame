package me.ferrybig.javacoding.minecraft.minigame.exceptions;

/**
 *
 * @author Fernando
 */
public class ConfigurationException extends MinigameException {

    /**
     * Creates a new instance of <code>ConfigurationException</code> without detail message.
     */
    public ConfigurationException() {
        super();
    }


    /**
     * Constructs an instance of <code>ConfigurationException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ConfigurationException(String msg) {
        super(msg);
    }

    /**
	 * Constructs an instance of <code>ConfigurationException</code> with the specified
	 * detail message and cause.
	 *
	 * @param message the detail message.
	 * @param cause the cause
	 */
	public ConfigurationException(String message, Throwable cause) {
		super(message);
        this.initCause(cause);
	}

	/**
	 * Constructs an instance of <code>ConfigurationException</code> with the specified
	 * cause.
	 *
	 * @param cause the cause
	 */
	public ConfigurationException(Throwable cause) {
		this(cause.toString());
        this.initCause(cause);
	}
}
