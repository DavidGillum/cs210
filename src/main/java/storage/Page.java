package storage;

import java.util.Map;
import java.util.List;
import java.nio.file.Path;

/** 
 * This is a concrete implementation of a page.
 */
public class Page extends AbstractPage {
	/*
	 * TODO: Implement this stub class for Module 7.
	 * Until then, this class is never used.
	 */

	public Page(Path path, List<String> field_types,int key_index) {
		return;
	}
	
	@Override
	public int length() {
		return 0;
	}

	@Override
	public void length(int length) {
		return;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public void size(int size) {
		return;
	}

	@Override
	public void write(int index, Map.Entry<Object, List<Object>> entry) {
		return;
	}

	@Override
	public void writeNull(int index) {
		return;
	}

	@Override
	public Map.Entry<Object, List<Object>> read(int index) {
		return null;
	}

	@Override
	public boolean isEntry(int index) {
		return false;
	}

	@Override
	public boolean isNull(int index) {
		return false;
	}
}
