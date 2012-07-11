
package jserver.util;

import jserver.*;
import jserver.exception.*;

import java.util.*;
import java.lang.reflect.*;

/**
*
* @author Jimmy
*/
public class Libary<T> {

	private int amt = 0;
	private List<Item> itemList;
	private Object signal = new Object();
	private volatile boolean waiting = false;

	public Libary(){
		itemList = new LinkedList<Item>();
	}

	public void add( T item ){
		itemList.add( new Item( item, amt++ ) );
	}

	public synchronized T checkOutAny(){
		T item = null;
		//Go through each item to find a free one.
		for( Item i : itemList ){
			if( ! i.checkedOut() ){
				i.checkedOut( true );
				item = i.value();
			}
		}
		//Check to see if an item is found.
		if( item == null ){
			//Keep on waiting util one free item is found.
			try{
				synchronized( signal ){
					waiting = true;
					signal.wait();
				}
			}catch(InterruptedException e){}

			return checkOutAny();
		}
		//Return the item.
		return item;
	}

	public void checkIn( T obj ){
		for( Item item : itemList ){
			if( item.matches(obj) ){
				item.checkedOut(false);
				if( waiting ){
					waiting = false;
					synchronized( signal ){
						signal.notifyAll();
					}
				}
			}
		}
	}

	private class Item{
		private T value;
		private int index;
		private boolean checkedOut = false;
		public Item( T val, int i ){
			//Assignment.
			value = val;
			index = i;
		}

		public boolean matches( T obj ){
			return ( value.equals(obj) );
		}

		//Accessors.
		public T value(){ return value; }
		public int index(){ return index; }
		public boolean checkedOut(){ return checkedOut; }
		public void checkedOut( boolean out ){ checkedOut = out; }
	}
}