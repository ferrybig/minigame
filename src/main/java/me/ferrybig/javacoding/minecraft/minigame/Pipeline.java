package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.concurrent.Future;
import java.util.Iterator;
import java.util.NoSuchElementException;
import me.ferrybig.javacoding.minecraft.minigame.context.AreaContext;
import me.ferrybig.javacoding.minecraft.minigame.phase.Phase;

public interface Pipeline extends Iterable<Phase> {

	public Pipeline addLast(Phase phase);

	public Pipeline addFirst(Phase phases);

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
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				return get(index++);
			}

		};
	}

	public int size();

	public int getCurrentIndex();

	public void runLoop(AreaContext area);

	public Future<?> terminate();

	public Triggerable entrance();

	public boolean isStopping();

	public boolean isStopped();

	public Future<?> getClosureFuture();

}
