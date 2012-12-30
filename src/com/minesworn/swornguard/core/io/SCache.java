package com.minesworn.swornguard.core.io;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.minesworn.swornguard.core.SPlugin;


public class SCache<E extends Entity> {
	final String name;
	final File FOLDER; 
	
	volatile transient Map<String, E> entities = new HashMap<String, E>();
	
	private EFactory<E> factory;

	public SCache(String name, EFactory<E> factory) {
		this.name = name;
		this.FOLDER = new File(SPlugin.p.getDataFolder(), this.name);
		this.factory = factory;
		init();
	}
	
	private void init() {
		if (!this.FOLDER.exists())
			this.FOLDER.mkdir();
	}
	
	// Loads entity from file or creates a new file for them if none exists.
	protected synchronized E loadEntity(final String name) {
		E e = this.factory.newEntity();
		SPersist.load(e, e.getClass(), new File(this.FOLDER, name));
		return e;
	}
	
	// Returns entity.
	protected E getEntity(String name) {
		E e = this.entities.get(name);
		if (e == null) {
			File f = new File(FOLDER, name);
			if (f.exists())
				e = addEntity(name);
		}
		
		return e;
	}
	
	// Returns every entity in the FOLDER, but does not add unloaded entities to the entities map. 
	// Please use a thread to call this method...
	protected Map<String, E> getAllEntities() {
		Map<String, E> ret = new HashMap<String, E>();
		ret.putAll(entities);
		for (File f : FOLDER.listFiles()) {
			if (!isFileAlreadyLoaded(f.getName(), ret))
				ret.put(f.getName(), loadEntity(f.getName()));
		}
		return Collections.unmodifiableMap(ret);
	}
	
	protected int getDatabaseSize() {
		return (FOLDER.listFiles().length);
	}
	
	// Checks if the file name is already the name of an entity in the map
	protected boolean isFileAlreadyLoaded(String fileName, Map<String, E> map) {
		for (String s : map.keySet()) {
			if (s.equalsIgnoreCase(fileName)) {
				return true;
			}
		}
		return false;
	}
	
	// Adds entity to cache for future use and also returns entity.
	protected E addEntity(final String name) {
		E e = loadEntity(name);	
		add(name, e);
		return e;
	}
	
	// Removes entity from the cache to clear memory.
	protected void removeEntity(final String name) {
		E e = getEntity(name);
		new SaveEntityThread<E>(this, name, e);
		remove(name);
	}
	
	// Deletes entity from disk and the cache.
	protected synchronized void deleteEntity(final String name) {
		removeEntity(name);
		new File(FOLDER, name).delete();
	}
	
	protected Map<String, E> getEntities() {
		return Collections.unmodifiableMap(this.entities);
	}
	
	// Saves all entities currently stored in the collection.
	public synchronized void save() {
		SPlugin.log("Saving " + this.name + " to disk...");
		try {
			long start = System.currentTimeMillis();
			cleanupEntities();
			for (Entry<String, E> entry : getEntities().entrySet()) {
				SPersist.save(entry.getValue(), entry.getValue().getClass(), new File(FOLDER, entry.getKey()));
			}
			SPlugin.log(this.name + " saved! [" + (System.currentTimeMillis() - start) + "ms]");
		} catch (Exception e) {
			SPlugin.log("Cannot save " + this.name + " before they have even loaded!");
		}
	}
	
	// Override this to add cleanup for entities. Be sure to use the removeEntity method when using this so as to save the entities on unload.
	public void cleanupEntities() {
		
	}
		
	synchronized void add(final String name, final E e) {
		Map<String, E> copy = new HashMap<String, E>();
		copy.putAll(this.entities);
		copy.put(name, e);
		
		this.entities = Collections.unmodifiableMap(copy);
	}
	
	synchronized void remove(final String name) {
		Map<String, E> copy = new HashMap<String, E>();
		copy.putAll(this.entities);
		copy.remove(name);
		
		this.entities = Collections.unmodifiableMap(copy);
	}
	
}
