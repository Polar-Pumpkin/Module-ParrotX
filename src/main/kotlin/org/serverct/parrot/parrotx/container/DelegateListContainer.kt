package org.serverct.parrot.parrotx.container

abstract class DelegateListContainer<E> : MutableList<E> {

    abstract val container: MutableList<E>

    override val size: Int
        get() = container.size

    override fun contains(element: E): Boolean = element in container

    override fun containsAll(elements: Collection<E>): Boolean = container.containsAll(elements)

    override fun get(index: Int): E = container[index]

    override fun indexOf(element: E): Int = container.indexOf(element)

    override fun lastIndexOf(element: E): Int = container.lastIndexOf(element)

    override fun isEmpty(): Boolean = container.isEmpty()

    override fun iterator(): MutableIterator<E> = container.iterator()

    override fun listIterator(): MutableListIterator<E> = container.listIterator()

    override fun listIterator(index: Int): MutableListIterator<E> = container.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = container.subList(fromIndex, toIndex)

    override fun add(element: E): Boolean = container.add(element)

    override fun add(index: Int, element: E) = container.add(index, element)

    override fun addAll(elements: Collection<E>): Boolean = container.addAll(elements)

    override fun addAll(index: Int, elements: Collection<E>): Boolean = container.addAll(index, elements)

    override fun clear() = container.clear()

    override fun remove(element: E): Boolean = container.remove(element)

    override fun removeAll(elements: Collection<E>): Boolean = container.removeAll(elements)

    override fun removeAt(index: Int): E = container.removeAt(index)

    override fun retainAll(elements: Collection<E>): Boolean = container.retainAll(elements)

    override fun set(index: Int, element: E): E = container.set(index, element)

}