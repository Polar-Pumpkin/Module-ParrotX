package org.serverct.parrot.parrotx.ui.extension

import org.bukkit.inventory.Inventory
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import taboolib.module.ui.Menu
import taboolib.module.ui.buildMenu
import taboolib.module.ui.type.Linked

@Suppress("unused")
class Mapped<E>(title: String) : Menu(title) {

    private lateinit var config: MenuConfiguration
    private lateinit var template: String
    private lateinit var elements: () -> Iterable<E>
    private var args: Array<out Any?> = arrayOf()
    private var prev: String = "Previous"
    private var next: String = "Next"

    fun configuration(config: MenuConfiguration) {
        this.config = config
    }

    fun template(keyword: String) {
        this.template = keyword
    }

    fun elements(elements: () -> Iterable<E>) {
        this.elements = elements
    }

    fun argument(vararg args: Any?) {
        this.args = args
    }

    fun setPrevPage(keyword: String) {
        this.prev = keyword
    }

    fun setNextPage(keyword: String) {
        this.next = keyword
    }

    override fun build(): Inventory {
        return buildMenu<Linked<E>>(title) {
            val (shape, templates) = config
            val slots = shape[template].toList()
            rows(shape.rows)
            slots(slots)
            elements { elements().toList() }

            val _template = templates.require(template)
            onGenerate { _, element, index, slot ->
                _template(slot, index, element, *args)
            }
            onClick { event, element ->
                _template.handle(event, this, element, *args)
            }

            config.setPreviousPage(this)
            config.setNextPage(this)

            onBuild(false) { _, inventory ->
                shape.all(template, prev, next) { slot, index, item, _ ->
                    inventory.setItem(slot, item(slot, index, *args))
                }
            }
            onClick { event ->
                event.isCancelled = true
                if (event.rawSlot in shape && event.rawSlot !in slots) {
                    templates[event.rawSlot]?.handle(event, this, *args)
                }
            }
        }
    }

}