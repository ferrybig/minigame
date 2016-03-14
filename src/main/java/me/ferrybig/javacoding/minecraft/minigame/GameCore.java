package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import me.ferrybig.javacoding.minecraft.minigame.context.AreaContext;
import me.ferrybig.javacoding.minecraft.minigame.information.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.listener.GameListenable;
import org.bukkit.OfflinePlayer;

/**
 * Main core for the minigame management. This class allows you to address every
 * part of the system.
 * @author Fernando
 */
public interface GameCore extends AutoCloseable, GameListenable {

	/**
	 * Adds a area based on an area definition. The gamecore should not attempt
	 * to save this using the config.
	 * @param area The area to add.
	 */
	public void addArea(AreaInformation area);

	/**
	 * Gets a area by name.
	 * @param name The name to find
	 * @return The area wrapped in a Optional, or a empty Optional on failure
	 */
	public Optional<Area> getArea(String name);

	/**
	 * Creates a new area by a name. The new area is saved if create() is
	 * called on the creator.
	 * @param name The name of the new area.
	 * @return A AreaCreator for the new area
	 * @see AreaCreator
	 */
	public AreaCreator createArea(String name);

	/**
	 * Removed an area by name
	 * @param area Area name to remove
	 * @return true if the area was found and removed
	 */
	public boolean removeArea(String area);

	/**
	 * Gets a list of loaded area's for this <code>GameCore</code>
	 * @return
	 */
	public Collection<? extends Area> getAreas();

	/**
	 * Gets the game of a player
	 * @param player The player to find
	 * @return a Optional containg the game of a player,
	 * or a empty optional on failure
	 */
	public Optional<AreaContext> getGameOfPlayer(OfflinePlayer player);

	/**
	 * Checks if a player is ingame
	 * @param player the player to check
	 * @return true if the player is in game
	 */
	public default boolean isInGame(OfflinePlayer player) {
		return getGameOfPlayer(player).isPresent();
	}

	/**
	 * Get the loaded <code>AreaContext</code>s
	 * @return the loaded area contexts
	 */
	public Collection<? extends AreaContext> getAreaContexts();

	/**
	 * Creates a random game context. he core may choose an area to generate a
	 * <code>AreaContext</code> from. This choose may or may not be fairly
	 * distributed over the existing area's
	 * @return a <code>Future</code> containg a random <code>AreaContext</code>
	 * @see Area#newInstance()
	 */
	public Future<AreaContext> createRandomGameContext();

	/**
	 * Creates a random game context. he core may choose an area to generate a
	 * <code>AreaContext</code> from. This choose may or may not be fairly
	 * distributed over the existing area's
	 * @param maxDelay Max delay to wait for the area
	 * @param unit TimeUnit of the <code>maxDelay</code>
	 * @return a <code>Future</code> containg a random <code>AreaContext</code>
	 * @see Area#newInstance()
	 */
	public Future<AreaContext> createRandomGameContext(long maxDelay, TimeUnit unit);

	/**
	 * Initializes and start the gamecore. Repeated calls to this method should be ignored.
	 * @return the starting future
	 * @see #startingFuture()
	 */
	public Future<?> initializeAndStart();

	/**
	 * Returns the starting future. This <code>isDone()</code> method will return true
	 * when the clas sis succesfully loaded.
	 * @return 
	 */
	public Future<?> startingFuture();

	/**
	 * Returns if started
	 * @return true if this <code>GameCore</code> is fully started
	 */
	public default boolean isStarted() {
		return startingFuture().isDone();
	}

	/**
	 * Returns if this <code>GameCore</code> is fully running.
	 * @return true if its running and ready to accept new calls
	 */
	public boolean isRunning();

	/**
	 * Returns true if the <code>GameCore</code> terminationsequence has started.
	 * @return true if it is terminating
	 * @see #gracefulStop()
	 * @see #close()
	 */
	public boolean isTerminating();

	/**
	 * Returns if this <code>GameCore</code> is fully closed and terminated.
	 * @return 
	 */
	public default boolean isTerminated() {
		return terminationFuture().isDone();
	}

	/**
	 * Gracefully stop the gamecore, finishing when all running contexts have
	 * finished executing.
	 * @return the termination future.
	 * @see #terminationFuture()
	 */
	public Future<?> gracefulStop();

	/**
	 * Gets the <code>TerminationFuture</code>. This future will be done when
	 * the core has been terminated.
	 * @return 
	 */
	public Future<?> terminationFuture();

	/**
	 * Closes and directly terminates this <code>GameCore</code>.
	 * Repeated calls to this method should be ignored
	 */
	@Override
	public void close();

	/**
	 * Gets the information that has been used to construct this <code>GameCore</code>
	 * @return the information context.
	 * @see InformationContext
	 */
	public InformationContext getInfo();

	/**
	 * Gets the global event executor. This executor is used for scheduling of
	 * tasks that will be run later. The executor may choose to directly execute
	 * a passed in task, instead of waiting until the current method call is
	 * complete.
	 * This method delegates to <code>getInfo().getExecutor()</code>
	 * @return The global event executor
	 */
	public default EventExecutor getExecutor() {
		return getInfo().getExecutor();
	}

}
