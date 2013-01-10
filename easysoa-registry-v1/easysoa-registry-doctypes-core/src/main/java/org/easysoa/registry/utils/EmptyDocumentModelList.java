package org.easysoa.registry.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

import com.google.common.collect.ImmutableList;

public class EmptyDocumentModelList implements DocumentModelList {

    private static final long serialVersionUID = 216367698245285286L;
    
    private static final List<DocumentModel> delegate = ImmutableList.of();
    
    public static final DocumentModelList INSTANCE = new EmptyDocumentModelList();
    
    private EmptyDocumentModelList() {
        
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<DocumentModel> iterator() {
        return delegate.iterator();
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }

    @Override
    public boolean add(DocumentModel e) {
        return delegate.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends DocumentModel> c) {
        return delegate.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends DocumentModel> c) {
        return delegate.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return delegate.retainAll(c);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public DocumentModel get(int index) {
        return delegate.get(index);
    }

    @Override
    public DocumentModel set(int index, DocumentModel element) {
        return delegate.set(index, element);
    }

    @Override
    public void add(int index, DocumentModel element) {
        delegate.add(index, element);
    }

    @Override
    public DocumentModel remove(int index) {
        return delegate.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return delegate.lastIndexOf(o);
    }

    @Override
    public ListIterator<DocumentModel> listIterator() {
        return delegate.listIterator();
    }

    @Override
    public ListIterator<DocumentModel> listIterator(int index) {
        return delegate.listIterator(index);
    }

    @Override
    public List<DocumentModel> subList(int fromIndex, int toIndex) {
        return delegate.subList(fromIndex, toIndex);
    }

    @Override
    public long totalSize() {
        return 0;
    }

}
