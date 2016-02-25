package me.ferrybig.javacoding.minecraft.minigame.exceptions;

/**
 *
 * @author Fernando
 */
public class MinigameException extends Exception {

    /**
     * Creates a new instance of <code>MinigameException</code> without detail message.
     */
    public MinigameException() {
        super();
    }


    /**
     * Constructs an instance of <code>MinigameException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public MinigameException(String msg) {
        super(msg);
    }

    /**
	 * Constructs an instance of <code>MinigameException</code> with the specified
	 * detail message and cause.
	 *
	 * @param message the detail message.
	 * @param cause the cause
	 */
	public MinigameException(String message, Throwable cause) {
		super(message);
        this.initCause(cause);
	}

	/**
	 * Constructs an instance of <code>MinigameException</code> with the specified
	 * cause.
	 *
	 * @param cause the cause
	 */
	public MinigameException(Throwable cause) {
		this(cause.toString());
        this.initCause(cause);
	}
}
