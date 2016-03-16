package me.ferrybig.javacoding.minecraft.minigame.messages;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.event.Cancellable;

public abstract class Message {

	protected final List<Runnable> listeners = new ArrayList<>();

	public void addListener(Runnable listener) {
		listeners.add(listener);
	}

	public void addSuccessListener(Runnable listener) {
		if (this instanceof Cancellable) {
			Cancellable cancellable = (Cancellable) this;
			addListener(() -> {
				if (!cancellable.isCancelled()) {
					listener.run();
				}
			});
		} else {
			addListener(listener);
		}
	}

	public void addFailureListener(Runnable listener) {
		if (this instanceof Cancellable) {
			Cancellable cancellable = (Cancellable) this;
			addListener(() -> {
				if (cancellable.isCancelled()) {
					listener.run();
				}
			});
		}
	}

	public List<Runnable> getListeners() {
		return listeners;
	}
}
