package com.linkpoint.ui.chat;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BoundedFifoBufferTest {

    @Test
    public void testAddAndFifoEviction() {
        BoundedFifoBuffer<String> buffer = new BoundedFifoBuffer<>(3);
        assertEquals(3, buffer.getCapacity());
        assertEquals(0, buffer.size());

        buffer.add("A");
        buffer.add("B");
        buffer.add("C");
        assertEquals(3, buffer.size());
        assertEquals("A", buffer.get(0));
        assertEquals("B", buffer.get(1));
        assertEquals("C", buffer.get(2));

        // Adding 4th item evicts oldest item ("A")
        buffer.add("D");
        assertEquals(3, buffer.size());
        assertEquals("B", buffer.get(0));
        assertEquals("C", buffer.get(1));
        assertEquals("D", buffer.get(2));
    }

    @Test
    public void testSetCapacityShrinkAndExpand() {
        BoundedFifoBuffer<Integer> buffer = new BoundedFifoBuffer<>(5);
        for (int i = 1; i <= 5; i++) {
            buffer.add(i);
        }
        assertEquals(5, buffer.size());

        // Shrink capacity from 5 to 3 -> oldest 2 items (1, 2) dropped
        buffer.setCapacity(3);
        assertEquals(3, buffer.getCapacity());
        assertEquals(3, buffer.size());
        assertEquals(Integer.valueOf(3), buffer.get(0));
        assertEquals(Integer.valueOf(4), buffer.get(1));
        assertEquals(Integer.valueOf(5), buffer.get(2));

        // Expand capacity to 5
        buffer.setCapacity(5);
        assertEquals(5, buffer.getCapacity());
        assertEquals(3, buffer.size());

        buffer.add(6);
        buffer.add(7);
        assertEquals(5, buffer.size());
        assertEquals(Integer.valueOf(3), buffer.get(0));
        assertEquals(Integer.valueOf(7), buffer.get(4));
    }

    @Test
    public void testClear() {
        BoundedFifoBuffer<String> buffer = new BoundedFifoBuffer<>(4);
        buffer.add("X");
        buffer.add("Y");
        assertEquals(2, buffer.size());

        buffer.clear();
        assertEquals(0, buffer.size());
        assertTrue(buffer.isEmpty());
    }

    @Test
    public void testIteration() {
        BoundedFifoBuffer<String> buffer = new BoundedFifoBuffer<>(3);
        buffer.add("1");
        buffer.add("2");
        buffer.add("3");
        buffer.add("4"); // evicts "1"

        List<String> items = new ArrayList<>();
        for (String item : buffer) {
            items.add(item);
        }

        assertEquals(3, items.size());
        assertEquals("2", items.get(0));
        assertEquals("3", items.get(1));
        assertEquals("4", items.get(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCapacity() {
        new BoundedFifoBuffer<>(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIndexOutOfBounds() {
        BoundedFifoBuffer<String> buffer = new BoundedFifoBuffer<>(3);
        buffer.add("Hello");
        buffer.get(1);
    }
}
