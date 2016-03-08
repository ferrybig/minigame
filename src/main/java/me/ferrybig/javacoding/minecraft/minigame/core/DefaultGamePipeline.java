package me.ferrybig.javacoding.minecraft.minigame.core;

import io.netty.util.concurrent.Future;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ferrybig.javacoding.minecraft.minigame.AreaContext;
import me.ferrybig.javacoding.minecraft.minigame.Phase;
import me.ferrybig.javacoding.minecraft.minigame.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.Pipeline;


public class DefaultGamePipeline implements Pipeline {
	
	public int currPhaseIndex;
	
	private static Supplier<List<Phase>> listCreator;
	
	static {
		listCreator = ()->{
			try {
				Class<?> l = DefaultGamePipeline.class.getClassLoader().loadClass(
						System.getProperty("minigameAPI.phaseCreator", "java.util.ArayList"));
				if(!List.class.isAssignableFrom(l)) 
					throw new ClassCastException(l + " not a list");
				@SuppressWarnings("unchecked")
				List<Phase> list = (List<Phase>)l.newInstance();
				return list;
			} catch (ClassNotFoundException | ClassCastException | 
					InstantiationException | IllegalAccessException ex) {
				Logger.getLogger(DefaultGamePipeline.class.getName()).log(Level.WARNING, 
						"Cannot use provided list creator", ex);
				return (listCreator = ArrayList::new).get();
			}
		};
	}
	
	public List<Phase> mainPhases = listCreator.get();

	@Override
	public Pipeline addFirst(Iterable<Phase> phases) {
		if(currPhaseIndex > 0) {
			throw new IllegalStateException("pipeline already running");
		}
		phases.forEach(mainPhases.listIterator()::add);
		return this;
	}

	@Override
	public Pipeline addLast(Iterable<Phase> phases) {
		phases.forEach(mainPhases::add);
		return this;
	}

	@Override
	public boolean contains(Phase phase) {
		return mainPhases.contains(phase);
	}

	@Override
	public PhaseContext entrance() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Phase get(int index) {
		return mainPhases.get(index);
	}

	@Override
	public Future<?> getClosureFuture() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int indexOf(Phase phase) {
		return mainPhases.indexOf(phase);
	}

	@Override
	public Pipeline insert(int index, Phase phase) {
		if(currPhaseIndex > index) {
			throw new IllegalStateException("Cannot change running code");
		}
		mainPhases.listIterator(index).add(phase);
		return this;
	}

	@Override
	public boolean isStopped() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isStopping() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Iterator<Phase> iterator() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Pipeline replace(int index, Phase phase) {
		if(currPhaseIndex > index) {
			throw new IllegalStateException("Cannot change running code");
		}
		mainPhases.listIterator(index).add(phase);
		return this;
	}

	@Override
	public void runLoop(AreaContext area) {
		
	}

	@Override
	public int size() {
		return mainPhases.size();
	}
	
	@Override
	public int getCurrentIndex() {
		return currPhaseIndex;
	}
	
	public void setCurrentIndex(int index) {
		currPhaseIndex = index;
	}

	@Override
	public void stop() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void terminate() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	
	
}
