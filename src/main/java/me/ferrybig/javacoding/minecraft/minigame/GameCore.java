
package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.concurrent.Future;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import me.ferrybig.javacoding.minecraft.minigame.listener.GameListener;
import org.bukkit.OfflinePlayer;

public interface GameCore extends AutoCloseable {
	
	public Collection<? extends GameListener> getGameListeners();
	
	public boolean addGameListener(GameListener listener);
	
	public boolean removeGameListener(GameListener listener);

	public Optional<Area> getArea(String name);
	
	public AreaCreator createArea(String name);
	
	public boolean removeArea(String area);
	
	public Collection<? extends Area> getAreas();
	
	public Optional<AreaContext> getGameOfPlayer(OfflinePlayer p);
	
	public Collection<? extends AreaContext> getAreaContexts();
	
	public Future<AreaContext> createRandomGameContext();
	
	public Future<AreaContext> createRandomGameContext(long maxDelay, TimeUnit unit);
	
	public boolean isRunning();
	
	public boolean isStopping();
	
	public boolean isTerminating();
	
	public Future<?> setRunning(boolean stopping);
	
	public Future<?> terminationFuture();
	
	@Override
	public void close();
	
	public Future<?> startingFuture();
	
	public boolean isStarted();
	
	public Future<?> initializeAndStart();
	
	public CoreInformationContext getInfo();
	
}
