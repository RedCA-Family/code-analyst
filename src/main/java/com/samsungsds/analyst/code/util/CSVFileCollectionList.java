/*
Copyright 2018 Samsung SDS

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.samsungsds.analyst.code.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CSVFileCollectionList<E extends CSVFileResult> implements List<E>, Closeable {
	private static final Logger LOGGER = LogManager.getLogger(CSVFileCollectionList.class);

	private final Class<E> clazz;
	private int size = 0;
	private boolean isReadingStatus = false;

	protected File file;
	protected PrintWriter writer;
	protected BufferedReader reader;

	private transient Object[] elementData;

	public CSVFileCollectionList(Class<E> clazz) {
		this.clazz = clazz;

		createFile();
		openWriter();
	}

	public boolean isTypeOf(Class<?> typeClass) {
        return clazz.getName().equals(typeClass.getName());
	}

	private void createFile() {
		try {
			file = File.createTempFile("collection", ".csv");
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	protected void openReader() {
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}
		LOGGER.info("Reader opened : {} (File = {})", clazz.getSimpleName(), file);
	}

	protected void closeReader() {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
		LOGGER.debug("Reader closed : {}", clazz.getSimpleName());
	}

	protected void openWriter() {
		try {
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file))));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		LOGGER.info("Writer opened : {} (File = {})", clazz.getSimpleName(), file);
	}

	protected void closeWriter() {
		if (writer != null) {
			writer.close();
		}
		LOGGER.debug("Writer closed : {}", clazz.getSimpleName());
	}

	protected void reset() {
		size = 0;
		elementData = null;

		closeWriter();
		closeReader();

		isReadingStatus = false;
	}

	@Override
	public void close() throws IOException {
		reset();

		if (file != null) {
			file.deleteOnExit();
			file.delete();
		}
	}

	private void changeToReadingStatus() {
		closeWriter();
		openReader();

		isReadingStatus = true;
	}

	private void changeToReadingEndedStatus() {
		closeReader();

		isReadingStatus = false;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean contains(Object o) {
		throw new UnsupportedOperationException("contains");
	}

	@Override
	public Iterator<E> iterator() {
		LOGGER.debug("iterator : {}", clazz.getSimpleName());
		changeToReadingStatus();

		return new Itr();
	}

	private class Itr implements Iterator<E> {
        int cursor;	// index of next element to return
        int lastRet = -1; // index of last element returned; -1 if no such

        public boolean hasNext() {
        	if (cursor == size) {
        		changeToReadingEndedStatus();

        		return false;
        	} else {
        		return true;
        	}
        }

        public E next() {
            int i = cursor;
            if (i >= size) {
                throw new NoSuchElementException();
            }

            E e = getElementInstance();

            cursor = i + 1;

			lastRet = i;

            return e;
        }

		private E getElementInstance() {
			E e;
			try {
				e = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException ex) {
				throw new RuntimeException(ex);
			}

			try (Reader in = new StringReader(reader.readLine())) {
				Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);
				for (CSVRecord record : records) {
					for (int columnIndex = 0; columnIndex < e.getColumnSize(); columnIndex++) {
						e.setDataIn(columnIndex, record.get(columnIndex));
					}
				}
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
			return e;
		}
    }

	private class ListItr extends Itr implements ListIterator<E> {
        ListItr(int index) {
        	super();
            cursor = index;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor - 1;
        }

		@SuppressWarnings("unchecked")
		public E previous() {
            int i = cursor - 1;
            if (i < 0) {
                throw new NoSuchElementException();
            }

            cursor = i;

			return (E) elementData[lastRet = i];
        }

        public void set(E e) {
        	if (lastRet < 0) {
                throw new IllegalStateException();
        	}

        	elementData[lastRet] = e;
        }

        public void add(E e) {
        	throw new UnsupportedOperationException("ListIterator's add");
        }

		@Override
		public void remove() {
			throw new UnsupportedOperationException("ListIterator's remove");
		}

		@SuppressWarnings("unchecked")
		@Override
		public E next() {
			int i = cursor;
            if (i >= size) {
                throw new NoSuchElementException();
            }

            cursor = i + 1;

            return (E) elementData[lastRet = i];
		}
    }

	@Override
	public Object[] toArray() {
		changeToReadingStatus();

		elementData = new Object[size];

		Itr itr = new Itr();
		int index = 0;
		while (itr.hasNext()) {
			elementData[index++] = itr.next();
		}

		return elementData;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException("toArray with type");
	}

	@Override
	public boolean add(E e) {
		if (isReadingStatus) {
			throw new IllegalStateException("Currently reading status!!!");
		}

		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < e.getColumnSize(); i++) {
			if (i != 0) {
				builder.append(",");
			}
			builder.append(CSVUtil.getCSVStyleString(e.getDataIn(i)));
		}

		writer.println(builder.toString());

		size++;

		return true;
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("remove");
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException("containsAll");
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		if (isReadingStatus) {
			throw new IllegalStateException("Currently reading status!!!");
		}

		for (E elem : c) {
			add(elem);
		}

		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException("addAll with index");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("removeAll");
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("retainAll");
	}

	@Override
	public void clear() {
		reset();

		createFile();
		openWriter();
	}

	@Override
	public E get(int index) {
		throw new UnsupportedOperationException("get with index");
	}

	@Override
	public E set(int index, E element) {
		throw new UnsupportedOperationException("set with index");
	}

	@Override
	public void add(int index, E element) {
		throw new UnsupportedOperationException("add with index");
	}

	@Override
	public E remove(int index) {
		throw new UnsupportedOperationException("remove with index");
	}

	@Override
	public int indexOf(Object o) {
		throw new UnsupportedOperationException("indexOf");
	}

	@Override
	public int lastIndexOf(Object o) {
		throw new UnsupportedOperationException("lastIndexOf");
	}

	@Override
	public ListIterator<E> listIterator() {
		LOGGER.debug("listIterator : {}", clazz.getSimpleName());

		if (elementData == null) {
			toArray();
		}

		return new ListItr(0);
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		throw new UnsupportedOperationException("listIterator with index");
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException("subList");
	}
}
