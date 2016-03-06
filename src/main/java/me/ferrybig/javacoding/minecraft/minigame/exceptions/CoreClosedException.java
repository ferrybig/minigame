package me.ferrybig.javacoding.minecraft.minigame.exceptions;

/**
 *
 * @author Fernando
 */
public class CoreClosedException extends MinigameException {

	/**
	 * Creates a new instance of <code>CoreClosedException</code> without detail
	 * message.
	 */
	public CoreClosedException() {
	}

	/**
	 * Constructs an instance of <code>CoreClosedException</code> with the
	 * specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public CoreClosedException(String msg) {
		super(msg);
	}
	
	/**
	 * Constructs an instance of <code>CoreClosedException</code> with the specified
	 * detail message and cause.
	 *
	 * @param message the detail message.
	 * @param cause the cause
	 */
	public CoreClosedException(String message, Throwable cause) {
		super(message);
        this.initCause(cause);
	}

	/**
	 * Constructs an instance of <code>CoreClosedException</code> with the specified
	 * cause.
	 *
	 * @param cause the cause
	 */
	public CoreClosedException(Throwable cause) {
		this(cause.toString());
        this.initCause(cause);
	}
}
