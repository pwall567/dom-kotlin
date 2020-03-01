/*
 * @(#) DOMKotlinTest.kt
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

import org.w3c.dom.Comment
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.test.Test
import kotlin.test.expect
import kotlin.test.fail

class DOMKotlinTest {

    @Test fun `should get child nodes as List`() {
        val dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        dom.appendChild(dom.createComment("Comment1"))
        dom.appendChild(dom.createElement("html"))
        val children = dom.children()
        expect(2) { children.size }
        expect(true) { children[0] is Comment && children[0].textContent == "Comment1" }
        expect(true) { children[1].let { it is Element && it.tagName == "html" } }
    }

    @Test fun `should get child nodes by type`() {
        val dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        dom.appendChild(dom.createComment("Comment1"))
        dom.appendChild(dom.createElement("html"))
        val elements = dom.childrenByType<Element>()
        expect(1) { elements.size }
        expect(true) { elements[0].tagName == "html" }
        val comments = dom.childrenByType<Comment>()
        expect(1) { comments.size }
        expect(true) { comments[0].textContent == "Comment1" }
    }

    @Test fun `should find first child element by tag name`() {
        val dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        dom.appendChild(dom.createElement("html").apply {
            appendChild(dom.createElement("head"))
            appendChild(dom.createElement("body").apply {
                appendChild(dom.createElement("p").apply {
                    setAttribute("id", "12345")
                })
            })
        })
        expect("12345") {
            dom.documentElement.findElementByTagName("body")?.findElementByTagName("p")?.getAttribute("id")
        }
    }

    @Test fun `should find all child elements by tag name`() {
        val dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        dom.appendChild(dom.createElement("html").apply {
            appendChild(dom.createElement("head"))
            appendChild(dom.createElement("body").apply {
                appendChild(dom.createElement("p").apply {
                    setAttribute("id", "12345")
                })
                appendChild(dom.createElement("p").apply {
                    setAttribute("id", "67890")
                })
            })
        })
        val elements = dom.documentElement.findElementByTagName("body")?.findAllElementsByTagName("p") ?: fail()
        expect(2) { elements.size }
        expect("12345") { elements[0].getAttribute("id") }
        expect("67890") { elements[1].getAttribute("id") }
    }

    @Test fun `should find element by id`() {
        val dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        dom.appendChild(dom.createElement("html").apply {
            appendChild(dom.createElement("head"))
            appendChild(dom.createElement("body").apply {
                appendChild(dom.createElement("p").apply {
                    setAttribute("id", "12345")
                })
            })
        })
        expect("p") { dom.documentElement.findElementById("12345")?.tagName }
    }

}
