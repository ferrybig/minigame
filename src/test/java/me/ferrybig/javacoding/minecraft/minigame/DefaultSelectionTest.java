/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.minecraft.minigame;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.bukkit.World;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assume;

/**
 *
 * @author Fernando
 */
public class DefaultSelectionTest {
	
	private static final World MOCK_WORLD;
	
	static {
		MOCK_WORLD = (World) Proxy.newProxyInstance(DefaultSelectionTest.class.getClassLoader(),
				new Class<?>[]{World.class}, (Object proxy, Method method, Object[] args) -> {
					if("toString".equals(method.getName()))
						return "MOCK_WORLD";
					throw new AssertionError();
		});
	}
	
	@Test
	public void selectionDoesntStartWithNullTest() {
		DefaultSelection s = new DefaultSelection(MOCK_WORLD);
		Assert.assertNotNull(s);
		Assert.assertSame(MOCK_WORLD, s.getWorld());
		Assert.assertNotNull(s.getFirstPoint());
		Assert.assertNotNull(s.getSecondPoint());
	}

	@Test
	public void selectionStartsZeroTest() {
		DefaultSelection s = new DefaultSelection(MOCK_WORLD);
		Assert.assertEquals(0, s.getFirstPoint().getX(), 0);
		Assert.assertEquals(0, s.getFirstPoint().getY(), 0);
		Assert.assertEquals(0, s.getFirstPoint().getZ(), 0);
		Assert.assertEquals(0, s.getSecondPoint().getX(), 0);
		Assert.assertEquals(0, s.getSecondPoint().getY(), 0);
		Assert.assertEquals(0, s.getSecondPoint().getZ(), 0);
	}
	
	@Test
	public void canChangePoints() {
		DefaultSelection s = new DefaultSelection(MOCK_WORLD);
		s.getFirstPoint().setX(1);
		s.getFirstPoint().setY(2);
		s.getFirstPoint().setZ(3);
		s.getSecondPoint().setX(4);
		s.getSecondPoint().setY(5);
		s.getSecondPoint().setZ(6);
		Assert.assertEquals(1, s.getFirstPoint().getX(), 0);
		Assert.assertEquals(2, s.getFirstPoint().getY(), 0);
		Assert.assertEquals(3, s.getFirstPoint().getZ(), 0);
		Assert.assertEquals(4, s.getSecondPoint().getX(), 0);
		Assert.assertEquals(5, s.getSecondPoint().getY(), 0);
		Assert.assertEquals(6, s.getSecondPoint().getZ(), 0);
	}
	
	@Test(expected = NullPointerException.class)
	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public void doesntAcceptNullWorldTest() {
		new DefaultSelection(null);
	}
	
	@Test
	public void cloneIsFullyTest() {
		DefaultSelection s = new DefaultSelection(MOCK_WORLD);
		DefaultSelection s2 = s.clone();
		Assert.assertNotSame(s,s2);
		Assert.assertEquals(s,s2);
		Assert.assertNotSame(s.getFirstPoint(),s2.getFirstPoint());
		Assert.assertEquals(s.getFirstPoint(),s2.getFirstPoint());
		Assert.assertNotSame(s.getSecondPoint(),s2.getSecondPoint());
		Assert.assertEquals(s.getFirstPoint(),s2.getFirstPoint());
	}
	
}
