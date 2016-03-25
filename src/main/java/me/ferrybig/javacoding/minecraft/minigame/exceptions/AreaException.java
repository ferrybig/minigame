package me.ferrybig.javacoding.minecraft.minigame.exceptions;

/**
 *
 * @author Fernando
 */
public class AreaException extends MinigameException {

	/**
	 * Creates a new instance of <code>AreaException</code> without detail
	 * message.
	 */
	public AreaException() {
		super();
	}

	/**
	 * Constructs an instance of <code>AreaException</code> with the specified
	 * detail message.
	 *
	 * @param msg the detail message.
	 */
	public AreaException(String msg) {
		super(msg);
	}

	/**
	 * Constructs an instance of <code>AreaException</code> with the specified
	 * detail message and cause.
	 *
	 * @param message the detail message.
	 * @param cause the cause
	 */
	public AreaException(String message, Throwable cause) {
		super(message);
		this.initCause(cause);
	}

	/**
	 * Constructs an instance of <code>AreaException</code> with the specified
	 * cause.
	 *
	 * @param cause the cause
	 */
	public AreaException(Throwable cause) {
		this(cause.toString());
		this.initCause(cause);
	}
}
