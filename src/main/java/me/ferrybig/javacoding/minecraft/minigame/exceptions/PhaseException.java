
package me.ferrybig.javacoding.minecraft.minigame.exceptions;

/**
 *
 * @author Fernando
 */
public class PhaseException extends MinigameException {

    /**
     * Creates a new instance of <code>PhaseException</code> without detail message.
     */
    public PhaseException() {
        super();
    }


    /**
     * Constructs an instance of <code>PhaseException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public PhaseException(String msg) {
        super(msg);
    }

    /**
	 * Constructs an instance of <code>PhaseException</code> with the specified
	 * detail message and cause.
	 *
	 * @param message the detail message.
	 * @param cause the cause
	 */
	public PhaseException(String message, Throwable cause) {
		super(message);
        this.initCause(cause);
	}

	/**
	 * Constructs an instance of <code>PhaseException</code> with the specified
	 * cause.
	 *
	 * @param cause the cause
	 */
	public PhaseException(Throwable cause) {
		this(cause.toString());
        this.initCause(cause);
	}
}
