package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.concurrent.Future;
import java.util.Arrays;
import java.util.Iterator;

public interface Pipeline extends Iterable<Phase> {

	public default Pipeline addLast(Phase... phases) {
		return addLast(Arrays.asList(phases));
	}

	public Pipeline addLast(Iterable<Phase> phases);

	public default Pipeline addFirst(Phase... phases) {
		return addFirst(Arrays.asList(phases));
	}

	public Pipeline addFirst(Iterable<Phase> phases);

	public default boolean replace(Phase existing, Phase phase) {
		int index = indexOf(existing);
		if (index < 0) {
			return false;
		}
		replace(index, phase);
		return true;
	}

	public Pipeline replace(int index, Phase phase);

	public Pipeline insert(int index, Phase phase);

	public boolean contains(Phase phase);

	public int indexOf(Phase phase);

	public Phase get(int index);

	@Override
	public default Iterator<Phase> iterator() {
		return new Iterator<Phase>() {
			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < size();
			}

			@Override
			public Phase next() {
				return get(index++);
			}

		};
	}

	public int size();
	
	public int getCurrentIndex();
	
	public void runLoop(AreaContext area);
	
	public void stop();
	
	public void terminate();
	
	public PhaseContext entrance();
	
	public boolean isStopping();
	
	public boolean isStopped();
	
	public Future<?> getClosureFuture();
	
}
