/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.minecraft.minigame;

import java.util.function.Consumer;

public interface Area extends AreaInformation {

	public AreaContext newInstance();

	public default AreaContext newInstance(Consumer<AreaContext> decorator) {
		AreaContext inst = newInstance();
		decorator.accept(inst);
		return inst;
	}

	public AreaCreator editArea();
}
