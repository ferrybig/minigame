/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.minecraft.minigame;

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
	
}
