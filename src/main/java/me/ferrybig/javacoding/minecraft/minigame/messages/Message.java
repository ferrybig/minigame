package me.ferrybig.javacoding.minecraft.minigame.messages;

import java.util.ArrayList;
import java.util.List;

public abstract class Message {

	protected final List<Runnable> listeners = new ArrayList<>();

	public void addListener(Runnable listener) {
		listeners.add(listener);
	}

	public List<Runnable> getListeners() {
		return listeners;
	}
}
