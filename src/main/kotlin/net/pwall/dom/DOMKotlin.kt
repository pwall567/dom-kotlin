/*
 * @(#) DOMKotlin.kt
 *
 * dom-kotlin   Dom access in Kotlin
 * Copyright (c) 2020 Peter Wall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.pwall.dom

import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

/**
 * An [Iterator] over the contents of a DOM [NodeList].
 */
class NodeListIterator(private val nodeList: NodeList) : Iterator<Node> {

    private var index = 0

    override fun hasNext(): Boolean = index < nodeList.length

    override fun next(): Node = nodeList.item(index++)

}

/**
 * A [List] backed by a DOM [NodeList].
 */
class NodeListList(private val nodeList: NodeList) : AbstractList<Node>() {

    override val size: Int
        get() = nodeList.length

    override fun get(index: Int): Node = nodeList.item(index)

    override fun iterator(): Iterator<Node> = NodeListIterator(nodeList)

}

/**
 * Get the children of a [Node] as a [List].
 */
fun Node.children(): List<Node> = NodeListList(childNodes)

/**
 * Get the children of a [Node] that are of a specific type.
 */
inline fun <reified T: Node> Node.childrenByType() = children().filterIsInstance<T>()

/**
 * Find the first [Element] of the current [Element] or its descendants that satisfies the specified predicate.
 */
fun Element.findElement(predicate: (Element) -> Boolean): Element? {
    if (predicate(this))
        return this
    childrenByType<Element>().forEach { element ->
        element.findElement(predicate)?.let { return it }
    }
    return null
}

/**
 * Find all child [Element]s of the current [Element] the satisfy the specified predicate.
 */
fun Element.findElements(predicate: (Element) -> Boolean): List<Element> =  childrenByType<Element>().filter(predicate)

/**
 * Find the first [Element] of the current [Element] or its descendants with a given tag name.
 */
fun Element.findElementByTagName(tagName: String): Element? = findElement { it.tagName == tagName }

/**
 * Find all child [Element]s of a parent [Element] with a given tag name.
 */
fun Element.findAllElementsByTagName(tagName: String): List<Element> = findElements { it.tagName == tagName }

/**
 * Find [Element] by id (there should be only one).
 */
fun Element.findElementById(id: String): Element? = findElement { it.getAttribute("id") == id }
