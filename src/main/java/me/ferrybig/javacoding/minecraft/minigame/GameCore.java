package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.concurrent.Future;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import me.ferrybig.javacoding.minecraft.minigame.listener.GameListenable;
import org.bukkit.OfflinePlayer;

public interface GameCore extends AutoCloseable, GameListenable {

	public void addArea(AreaInformation area);

	public Optional<Area> getArea(String name);

	public AreaCreator createArea(String name);

	public boolean removeArea(String area);

	public Collection<? extends Area> getAreas();

	public Optional<AreaContext> getGameOfPlayer(OfflinePlayer player);

	public Collection<? extends AreaContext> getAreaContexts();

	public Future<AreaContext> createRandomGameContext();

	public Future<AreaContext> createRandomGameContext(long maxDelay, TimeUnit unit);

	public Future<?> initializeAndStart();

	public Future<?> startingFuture();

	public boolean isStarted();

	public boolean isRunning();

	public boolean isTerminating();
	
	public default boolean isTerminated() {
		return terminationFuture().isDone();
	}

	public Future<?> gracefulStop();

	public Future<?> terminationFuture();

	@Override
	public void close();

	public InformationContext getInfo();

}
