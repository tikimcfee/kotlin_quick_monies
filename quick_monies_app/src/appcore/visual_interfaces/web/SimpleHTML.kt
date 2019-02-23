package appcore.visual_interfaces.web

class SimpleHTML {
    open class Tag(val name: String) {
        val children = mutableListOf<Tag>()
        val attributes = mutableListOf<Attribute>()

        override fun toString(): String {
            return "<$name" +
                    (if (attributes.isEmpty()) "" else attributes.joinToString(separator = " ", prefix = " ")) + ">" +
                    (if (children.isEmpty()) "" else children.joinToString(separator = "")) +
                    "</$name>"
        }
    }

    class Attribute(val name: String, val value: String) {
        override fun toString() = """$name="$value""""
    }

    fun <T : Tag> T.set(name: String, value: String?): T {
        if (value != null) {
            attributes.add(Attribute(name, value))
        }
        return this
    }

    fun <T : Tag> Tag.doInit(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    class Html : Tag("html")
    class Table : Tag("table")
    class Center : Tag("center")
    class TR : Tag("tr")
    class TD : Tag("td")
    class Text(val text: String) : Tag("b") {
        override fun toString() = text
    }

    class Form : Tag("form")
    class Label() : Tag("label")

    class Input : Tag("input")

    class Button : Tag("button")

    fun html(init: Html.() -> Unit): Html =
            Html().apply(init)

    fun Html.table(init: Table.() -> Unit) =
            doInit(Table(), init)

    fun Html.center(init: Center.() -> Unit) =
            doInit(Center(), init)

    fun Html.form(init: Form.() -> Unit) =
            doInit(Form(), init)

    fun Table.tr(color: String? = null, init: TR.() -> Unit) =
            doInit(TR(), init)
                    .set("bgcolor", color)

    fun TR.td(color: String? = null, align: String = "left", init: TD.() -> Unit) =
            doInit(TD(), init)
                    .set("align", align)
                    .set("bgcolor", color)

    fun Tag.text(s: Any?) =
            doInit(Text(s.toString()), {})

    fun Form.newField(forAttr: String,
                      label: Label.() -> Unit,
                      input: Input.() -> Unit
    ) {
        doInit(Label().apply {
            set("for", forAttr)
            text("Put stuff in here... ")
        }, label)

        doInit(Input().apply {
            set("name", forAttr)
            set("id", forAttr)
        }, input)
    }

    fun Form.button(init: Button.() -> Unit) =
            doInit(Button(), init)
}