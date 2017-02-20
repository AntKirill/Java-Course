package ru.ifmo.ctddev.antonov.arrayset;

import java.util.*;

import static java.util.Comparator.naturalOrder;

/**
 * Created by kirill on 2/18/17.
 */
public class ArraySet<E> extends AbstractSet<E> implements NavigableSet<E> {

    private List<E> elements;

    private final Comparator<? super E> comparator;

    private void makeSet(final Collection<E> collection) {
        elements = new ArrayList<E>();
        E lastAdded = null;
        for (E el : collection) {
            if (lastAdded == null || compare(el,lastAdded) != 0) {
                elements.add(el);
                lastAdded = el;
            }
        }
        this.elements.sort(comparator);
    }

    private ArrayList<E> makeReverse(ArrayList<E> revElements) {
        revElements.clear();
        for (int i = elements.size() - 1; i >= 0; i--) {
            revElements.add(elements.get(i));
        }
        return revElements;
    }

    public ArraySet(final Collection<E> elements) {
        comparator = null;
        makeSet(elements);
    }

    public ArraySet(final Collection<E> elements, final Comparator<? super E> comparator) {
        this.comparator = comparator;
        makeSet(elements);
    }

    public ArraySet() {
        elements = new ArrayList<>();
        comparator = null;
    }

    @SuppressWarnings("unchecked")
    private int compare(E a, E b) {
        return comparator == null ? ((Comparable<E>) a).compareTo(b) : comparator.compare(a, b);
    }

    //return index of E e or of the first greater element
    private int getIndex(E e) throws NullPointerException {
        if (e == null) throw new NullPointerException();
        int i = Collections.binarySearch(elements, e, comparator);
        if (i >= 0) return i;
        return -i - 1;
    }

    /**
     * Returns the greatest element in this set strictly less than the
     * given element, or {@code null} if there is no such element.
     *
     * @param e the value to match
     * @return the greatest element less than {@code e},
     * or {@code null} if there is no such element
     * @throws ClassCastException   if the specified element cannot be
     *                              compared with the elements currently in the set
     * @throws NullPointerException if the specified element is null
     *                              and this set does not permit null elements
     */
    @Override
    public E lower(E e) throws NullPointerException {
        int i = getIndex(e);
        return i > 0 ? elements.get(i - 1) : null;
    }

    /**
     * Returns the greatest element in this set less than or equal to
     * the given element, or {@code null} if there is no such element.
     *
     * @param e the value to match
     * @return the greatest element less than or equal to {@code e},
     * or {@code null} if there is no such element
     * @throws ClassCastException   if the specified element cannot be
     *                              compared with the elements currently in the set
     * @throws NullPointerException if the specified element is null
     *                              and this set does not permit null elements
     */
    @Override
    public E floor(E e) throws NullPointerException {
        int i = getIndex(e);
        if (i < elements.size() && compare(e, elements.get(i)) == 0) return elements.get(i);
        return i > 0 ? elements.get(i - 1) : null;
    }

    /**
     * Returns the least element in this set greater than or equal to
     * the given element, or {@code null} if there is no such element.
     *
     * @param e the value to match
     * @return the least element greater than or equal to {@code e},
     * or {@code null} if there is no such element
     * @throws ClassCastException   if the specified element cannot be
     *                              compared with the elements currently in the set
     * @throws NullPointerException if the specified element is null
     *                              and this set does not permit null elements
     */
    @Override
    public E ceiling(E e) throws NullPointerException {
        int i = getIndex(e);
        return i == elements.size() ? null : elements.get(i);
    }

    /**
     * Returns the least element in this set strictly greater than the
     * given element, or {@code null} if there is no such element.
     *
     * @param e the value to match
     * @return the least element greater than {@code e},
     * or {@code null} if there is no such element
     * @throws ClassCastException   if the specified element cannot be
     *                              compared with the elements currently in the set
     * @throws NullPointerException if the specified element is null
     *                              and this set does not permit null elements
     */
    @Override
    public E higher(E e) throws NullPointerException {
        int i = getIndex(e);
        if (i < elements.size() && compare(e, elements.get(i)) == 0) i++;
        return i == elements.size() ? null : elements.get(i);
    }

    /**
     * Retrieves and removes the first (lowest) element,
     * or returns {@code null} if this set is empty.
     *
     * @return the first element, or {@code null} if this set is empty
     */
    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException();
    }

    /**
     * Retrieves and removes the last (highest) element,
     * or returns {@code null} if this set is empty.
     *
     * @return the last element, or {@code null} if this set is empty
     */
    @Override
    public E pollLast() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an iterator over the elements contained in this collection.
     *
     * @return an iterator over the elements contained in this collection
     */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E> () {
            int i = -1;

            @Override
            public boolean hasNext() {
                return i < elements.size() - 1;
            }

            @Override
            public E next() {
                return elements.get(++i);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Returns a reverse order view of the elements contained in this set.
     * The descending set is backed by this set, so changes to the set are
     * reflected in the descending set, and vice-versa.  If either set is
     * modified while an iteration over either set is in progress (except
     * through the iterator's own {@code remove} operation), the results of
     * the iteration are undefined.
     * <p>
     * <p>The returned set has an ordering equivalent to
     * <tt>{@link Collections#reverseOrder(Comparator) Collections.reverseOrder}(comparator())</tt>.
     * The expression {@code s.descendingSet().descendingSet()} returns a
     * view of {@code s} essentially equivalent to {@code s}.
     *
     * @return a reverse order view of this set
     */
    @Override
    public ArraySet<E> descendingSet() {
        //return new ArraySet<>(makeReverse(new ArrayList<>(elements)), Collections.reverseOrder(comparator));
        return new ArraySet<>(this);
    }

    private ArraySet (ArraySet<E> rhs) {
        this.elements = new ArrayList<E>();
        this.comparator = Collections.reverseOrder(rhs.comparator);
        for (int i = rhs.elements.size() - 1; i >= 0; i--) {
            this.elements.add(rhs.elements.get(i));
        }
    }

    private ArraySet (List<E> elements, Comparator<? super E> comparator) {
        this.elements = elements;
        this.comparator = comparator;
    }

    /**
     * Returns an iterator over the elements in this set, in descending order.
     * Equivalent in effect to {@code descendingSet().iterator()}.
     *
     * @return an iterator over the elements in this set, in descending order
     */
    @Override
    public Iterator<E> descendingIterator() {
        return new Iterator<E>() {
            int i = elements.size();

            @Override
            public boolean hasNext() {
                return i > 0;
            }

            @Override
            public E next() {
                return elements.get(--i);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Returns a view of the portion of this set whose elements range from
     * {@code fromElement} to {@code toElement}.  If {@code fromElement} and
     * {@code toElement} are equal, the returned set is empty unless {@code
     * fromInclusive} and {@code toInclusive} are both true.  The returned set
     * is backed by this set, so changes in the returned set are reflected in
     * this set, and vice-versa.  The returned set supports all optional set
     * operations that this set supports.
     * <p>
     * <p>The returned set will throw an {@code IllegalArgumentException}
     * on an attempt to insert an element outside its range.
     *
     * @param fromElement   low endpoint of the returned set
     * @param fromInclusive {@code true} if the low endpoint
     *                      is to be included in the returned view
     * @param toElement     high endpoint of the returned set
     * @param toInclusive   {@code true} if the high endpoint
     *                      is to be included in the returned view
     * @return a view of the portion of this set whose elements range from
     * {@code fromElement}, inclusive, to {@code toElement}, exclusive
     * @throws ClassCastException       if {@code fromElement} and
     *                                  {@code toElement} cannot be compared to one another using this
     *                                  set's comparator (or, if the set has no comparator, using
     *                                  natural ordering).  Implementations may, but are not required
     *                                  to, throw this exception if {@code fromElement} or
     *                                  {@code toElement} cannot be compared to elements currently in
     *                                  the set.
     * @throws NullPointerException     if {@code fromElement} or
     *                                  {@code toElement} is null and this set does
     *                                  not permit null elements
     * @throws IllegalArgumentException if {@code fromElement} is
     *                                  greater than {@code toElement}; or if this set itself
     *                                  has a restricted range, and {@code fromElement} or
     *                                  {@code toElement} lies outside the bounds of the range.
     */
    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return this.tailSet(fromElement, fromInclusive).headSet(toElement, toInclusive);
    }

    /**
     * Returns a view of the portion of this set whose elements are less than
     * (or equal to, if {@code inclusive} is true) {@code toElement}.  The
     * returned set is backed by this set, so changes in the returned set are
     * reflected in this set, and vice-versa.  The returned set supports all
     * optional set operations that this set supports.
     * <p>
     * <p>The returned set will throw an {@code IllegalArgumentException}
     * on an attempt to insert an element outside its range.
     *
     * @param toElement high endpoint of the returned set
     * @param inclusive {@code true} if the high endpoint
     *                  is to be included in the returned view
     * @return a view of the portion of this set whose elements are less than
     * (or equal to, if {@code inclusive} is true) {@code toElement}
     * @throws ClassCastException       if {@code toElement} is not compatible
     *                                  with this set's comparator (or, if the set has no comparator,
     *                                  if {@code toElement} does not implement {@link Comparable}).
     *                                  Implementations may, but are not required to, throw this
     *                                  exception if {@code toElement} cannot be compared to elements
     *                                  currently in the set.
     * @throws NullPointerException     if {@code toElement} is null and
     *                                  this set does not permit null elements
     * @throws IllegalArgumentException if this set itself has a
     *                                  restricted range, and {@code toElement} lies outside the
     *                                  bounds of the range
     */
    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) throws NullPointerException {
        int i = getIndex(toElement);
        if (inclusive && (i < elements.size() && compare(elements.get(i), toElement) == 0)) i++;
        return new ArraySet<>(elements.subList(0, i), comparator);
    }

    /**
     * Returns a view of the portion of this set whose elements are greater
     * than (or equal to, if {@code inclusive} is true) {@code fromElement}.
     * The returned set is backed by this set, so changes in the returned set
     * are reflected in this set, and vice-versa.  The returned set supports
     * all optional set operations that this set supports.
     * <p>
     * <p>The returned set will throw an {@code IllegalArgumentException}
     * on an attempt to insert an element outside its range.
     *
     * @param fromElement low endpoint of the returned set
     * @param inclusive   {@code true} if the low endpoint
     *                    is to be included in the returned view
     * @return a view of the portion of this set whose elements are greater
     * than or equal to {@code fromElement}
     * @throws ClassCastException       if {@code fromElement} is not compatible
     *                                  with this set's comparator (or, if the set has no comparator,
     *                                  if {@code fromElement} does not implement {@link Comparable}).
     *                                  Implementations may, but are not required to, throw this
     *                                  exception if {@code fromElement} cannot be compared to elements
     *                                  currently in the set.
     * @throws NullPointerException     if {@code fromElement} is null
     *                                  and this set does not permit null elements
     * @throws IllegalArgumentException if this set itself has a
     *                                  restricted range, and {@code fromElement} lies outside the
     *                                  bounds of the range
     */
    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) throws NullPointerException {
        int i = getIndex(fromElement);
        if (i < elements.size() && compare(elements.get(i), fromElement) == 0 && !inclusive) i++;
        return new ArraySet<>(elements.subList(i, elements.size()), comparator);
    }

    /**
     * Returns the comparator used to order the elements in this set,
     * or <tt>null</tt> if this set uses the {@linkplain Comparable
     * natural ordering} of its elements.
     *
     * @return the comparator used to order the elements in this set,
     * or <tt>null</tt> if this set uses the natural ordering
     * of its elements
     */
    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>Equivalent to {@code subSet(fromElement, true, toElement, false)}.
     *
     * @param fromElement
     * @param toElement
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException     {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return this.subSet(fromElement, true, toElement, false);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>Equivalent to {@code headSet(toElement, false)}.
     *
     * @param toElement
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException     {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public SortedSet<E> headSet(E toElement) {
        return headSet(toElement, false);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>Equivalent to {@code tailSet(fromElement, true)}.
     *
     * @param fromElement
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException     {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return tailSet(fromElement, true);
    }

    /**
     * Returns the first (lowest) element currently in this set.
     *
     * @return the first (lowest) element currently in this set
     * @throws NoSuchElementException if this set is empty
     */
    @Override
    public E first() throws NoSuchElementException {
        if (size() == 0) throw new NoSuchElementException();
        return elements.get(0);
    }

    /**
     * Returns the last (highest) element currently in this set.
     *
     * @return the last (highest) element currently in this set
     * @throws NoSuchElementException if this set is empty
     */
    @Override
    public E last() throws NoSuchElementException {
        if (size() == 0) throw new NoSuchElementException();
        return elements.get(elements.size() - 1);
    }

    @Override
    public int size() {
        return elements.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        return Collections.binarySearch(elements, (E) o, comparator()) >= 0;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }
}
