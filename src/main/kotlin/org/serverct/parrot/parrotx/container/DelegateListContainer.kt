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

}