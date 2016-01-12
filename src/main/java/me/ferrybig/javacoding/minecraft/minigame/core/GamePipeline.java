/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.ferrybig.javacoding.minecraft.minigame.core;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import me.ferrybig.javacoding.minecraft.minigame.Phase;
import me.ferrybig.javacoding.minecraft.minigame.Pipeline;

public class GamePipeline implements Pipeline {
	private final LinkedList<PhaseHolder> phases = new LinkedList<>();
	private ListIterator<PhaseHolder> phaseIterator;
	private int currIndex;

	private void resetIteratorState() {
		phaseIterator = null;
	}
	
	@Override
	public Pipeline addFirst(String name, Phase phase) {
		resetIteratorState();
		phases.addFirst(new PhaseHolder(name,phase));
	}

	@Override
	public Pipeline addLast(String name, Phase phase) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Pipeline fireExceptionCaught(Throwable cause) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Pipeline firePhaseUpdate() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Pipeline firePhaseUpdate(String phase) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Pipeline firePhaseUpdate(Class<? extends Phase> phase) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Pipeline fireUserEventTriggered(Object event) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	private class PhaseHolder {
		public Phase phase;
		public String name;

		public PhaseHolder(Phase phase, String name) {
			this.phase = phase;
			this.name = name;
		}
	}
	
	
}
