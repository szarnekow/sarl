/*
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on http://www.janusproject.io
 * 
 * Copyright (C) 2014 Sebastian RODRIGUEZ, Nicolas GAUD, Stéphane GALLAND.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sarl.util;

import io.sarl.core.SynchronizedSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings({"javadoc"})
public class Collections3_SynchronizedSetTest_Collection extends Assert {

	private Object mutex;
	private TreeSet<String> original;
	private SynchronizedSet<String> collection;
	
	@Before
	public void setUp() {
		this.mutex = new Object();
		this.original = new TreeSet<>();
		for(int i=0; i<50; ++i) {
			this.original.add("0x"+Double.toHexString(Math.random())); //$NON-NLS-1$
		}
		this.collection = Collections3.synchronizedSet(this.original, this.mutex);
	}
	
	@After
	public void tearDown() {
		this.collection = null;
		this.original = null;
		this.mutex = null;
	}

	@Test
    public void size() {
    	assertEquals(this.original.size(), this.collection.size());
    }

	@Test
    public void isEmpty() {
    	assertFalse(this.collection.isEmpty());
    }

	@Test
    public void contains() {
		for(String s : this.original) {
			assertTrue(this.collection.contains(s));
		}
		for(int i=0; i<50; ++i) {
			assertFalse(this.collection.contains(Integer.toString(i)));
		}
	}

	@Test
    public void toArray() {
    	Object[] t = this.collection.toArray();
    	assertEquals(this.original.size(), t.length);
    	Iterator<String> it = this.original.iterator();
    	for(int i=0; i<t.length; ++i) {
    		assertSame(it.next(), t[i]);
    	}
    }

	@Test
    public void toArray_ObjectArray() {
		String[] t = new String[this.original.size()/2];
    	String[] t2 = this.collection.toArray(t);
    	assertNotSame(t, t2);
    	assertEquals(this.original.size(), t2.length);
    	Iterator<String> it = this.original.iterator();
    	for(int i=0; i<t2.length; ++i) {
    		assertSame(it.next(), t2[i]);
    	}
	}

    @Test
    public void add() {
    	this.collection.add(Integer.toString(5));
    	assertTrue(this.collection.contains(Integer.toString(5)));
    	assertTrue(this.original.contains(Integer.toString(5)));
    }
    
    private static <S> S get(Set<S> c, int index) {
    	Iterator<S> it = c.iterator();
    	for(int i=0; i<index; ++i) {
    		it.next();
    	}
    	return it.next();
    }

    @Test
    public void remove() {
    	assertFalse(this.collection.remove(Integer.toString(5)));
    	Random r = new Random();
    	while (!this.original.isEmpty()) {
    		String s = get(this.original, r.nextInt(this.original.size()));
    		assertTrue(this.collection.remove(s));
        	assertFalse(this.collection.contains(s));
        	assertFalse(this.original.contains(s));
    	}
    	assertTrue(this.collection.isEmpty());
    	assertFalse(this.collection.remove(Integer.toString(5)));
    }

    @Test
    public void clear() {
    	this.collection.clear();
    	assertTrue(this.collection.isEmpty());
    	assertTrue(this.original.isEmpty());
    }

    @Test
    public void testEquals() {
    	assertTrue(this.collection.equals(this.collection));
    	assertTrue(this.collection.equals(this.original));
    	Collection<String> c = new ArrayList<>(this.original);
    	assertFalse(this.collection.equals(c));
    	c = new TreeSet<>(this.original);
    	assertTrue(this.collection.equals(c));
    	assertFalse(this.collection.equals(Collections.singleton(5)));
    }

    @Test
    public void testHashCode() {
    	assertEquals(this.collection.hashCode(), this.collection.hashCode());
    	assertEquals(this.original.hashCode(), this.collection.hashCode());
    	Collection<String> c = new ArrayList<>(this.original);
    	assertNotEquals(c.hashCode(), this.collection.hashCode());
    	c = new TreeSet<>(this.original);
    	assertEquals(c.hashCode(), this.collection.hashCode());
    	assertNotEquals(Collections.singleton(5), this.collection.hashCode());
    }

    @Test
    public void iterator() {
    	Iterator<String> it = this.collection.iterator();
    	Iterator<String> oit = this.original.iterator();
    	while (it.hasNext() && oit.hasNext()) {
    		String e = it.next();
    		assertSame(oit.next(), e);
    	}
    	assertFalse(it.hasNext());
    	assertFalse(oit.hasNext());
    }
    
    @Test
    public void mutex() {
    	assertSame(this.mutex, this.collection.mutex());
    }
    
}
