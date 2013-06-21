/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands;

import java.util.ArrayList;
import java.util.List;

import net.t7seven7t.swornguard.SwornGuard;

/**
 * @author t7seven7t
 * Represents a command that can have pages
 */
public abstract class PaginatedCommand extends SwornGuardCommand {
	
	public PaginatedCommand(SwornGuard plugin) {
		super(plugin);
	}

	protected int linesPerPage = 10;
	
	int pageArgIndex = 0;
	
	public void perform() {
		int index = 1;
		if (this.args.length > pageArgIndex) {
			try {
				index = Integer.parseInt(args[pageArgIndex]);
				if (index < 1 || index > getPageCount())
					throw new IndexOutOfBoundsException();
			} catch (NumberFormatException ex) {
				err(plugin.getMessage("error_invalid_number"), args[0]);
				return;
			} catch (IndexOutOfBoundsException ex) {
				err(plugin.getMessage("error_no_page_with_index"), args[0]);
				return;
			}
		}
		
		for (String s : getPage(index))
			sendMessage(s);
	}
	
	/**
	 * Gets the number of pages in the list associated with this command
	 * 
	 * @return The number of pages
	 */
	public int getPageCount() {
		return (getListSize() + linesPerPage - 1) / linesPerPage;
	}
	
	/**
	 * Gets the size of the list associated with this command
	 * 
	 * @return The size of the list
	 */
	public abstract int getListSize();
	
	/**
	 * Gets all of the page lines for the specified page index
	 * 
	 * @param index The page index
	 * @return List of page lines
	 */
	public List<String> getPage(int index) {
		List<String> lines = new ArrayList<String>();
		lines.add(getHeader(index));
		lines.addAll(getLines((index - 1) * linesPerPage, index * linesPerPage));
		return lines;
	}
	
	/**
	 * Gets the header {@link String} for this command
	 * 
	 * @param index The page index
	 * @return String header for this page
	 */
	public abstract String getHeader(int index);
	
	/**
	 * Gets all lines from startIndex up to but not including endIndex
	 * 
	 * @param startIndex The starting index in the list
	 * @param endIndex The end index in the list
	 * @return All lines between start and end indexes
	 */
	public List<String> getLines(int startIndex, int endIndex) {
		List<String> lines = new ArrayList<String>();
		for (int i = startIndex; i < endIndex && i < getListSize(); i++)
			lines.add(getLine(i));
		return lines;
	}
	
	/**
	 * Gets a {@link String} representation of the line at the
	 * specified index in the list
	 * 
	 * @param index The index of the entry in the list
	 * @return A string representation of the line
	 */
	public abstract String getLine(int index);
}