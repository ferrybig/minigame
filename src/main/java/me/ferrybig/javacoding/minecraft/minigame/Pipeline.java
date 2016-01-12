/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.ferrybig.javacoding.minecraft.minigame;

public interface Pipeline {
	
	Pipeline addLast(String name, Phase phase);
	
	Pipeline addFirst(String name, Phase phase);
	
	Pipeline firePhaseUpdate();
	
	Pipeline firePhaseUpdate(String phase);
	
	Pipeline firePhaseUpdate(Class<? extends Phase> phase);
    
    Pipeline fireExceptionCaught(Throwable cause);

    Pipeline fireUserEventTriggered(Object event);
}
