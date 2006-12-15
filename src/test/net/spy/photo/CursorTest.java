// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;

public class CursorTest extends TestCase {
    private Collection<Integer> getIntegers(int max) {
        Collection<Integer> in=new HashSet<Integer>();
        for(int i=0; i<max; i++) {
            in.add(i);
        }
        return in;
    }
    
    private Cursor<Integer> getCursor(int max) {
        return new Cursor<Integer>(getIntegers(max));
    }

    // convert a Cursor to an normal collection by walking through pagination
    private Collection<Integer> cursorToCollection(Cursor<Integer> c) {
        Collection<Integer> rv=new ArrayList<Integer>();
        for(int i=0; i<c.getNumPages(); i++) {
            c.setPageNumber(i);
            for(Integer x : c.getPage()) {
                rv.add(x);
            }
        }
        return rv;
    }   
    
    public void testBasic() throws Exception {
        Cursor<Integer> c=getCursor(63);
        c.setPageSize(20);
        assertEquals(4, c.getNumPages());
        assertEquals(63, cursorToCollection(c).size());
    }

    public void testExtraction() {
        Cursor<Integer> c=getCursor(63);
        assertEquals(c.getAllObjects(), cursorToCollection(c));
    }

    public void testSorting() {
        Cursor<Integer> c=getCursor(63);
        // Sort it forwards and see that it still works.
        Collections.sort(c.getAllObjects());
        List<Integer> theList=new ArrayList<Integer>(cursorToCollection(c));
        for(int i=0; i<63; i++) {
            assertEquals("Broken at " + i, new Integer(i), theList.get(i));
        }

        // Try it backwards now.
        Collections.sort(c.getAllObjects(), Collections.reverseOrder());
        theList=new ArrayList<Integer>(cursorToCollection(c));
        for(int i=0; i<63; i++) {
            assertEquals("Broken at " + i, new Integer(i), theList.get(63-i-1));
        }
    }

}
