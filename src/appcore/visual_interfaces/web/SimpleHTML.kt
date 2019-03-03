package appcore.visual_interfaces.web

object SimpleHTML {
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
    
    fun <T : Tag> T.setAttribute(name: String, value: String?): T = apply {
        if (value != null) {
            attributes.add(Attribute(name, value))
        }
    }
    
    fun <T : Tag> Tag.initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }
    
    class Html : Tag("html")
    class Table : Tag("table")
    class Span : Tag("span")
    class Center : Tag("center")
    class TR : Tag("tr")
    class TD : Tag("td")
    class Text(val text: String) : Tag("b") {
        override fun toString() = text
    }
    
    class Form : Tag("form")
    class Label : Tag("label")
    class Input : Tag("input")
    class Button : Tag("button")
    class LineBreak : Tag("br")
    
    // ------------------------------------
    // HTML
    // ------------------------------------
    fun html(init: Html.() -> Unit): Html =
        Html().apply(init)
    
    fun Html.table(init: Table.() -> Unit) =
        initTag(Table(), init)
    
    fun Html.center(init: Center.() -> Unit) =
        initTag(Center(), init)
    
    fun Html.form(init: Form.() -> Unit) =
        initTag(Form(), init)
    
    // ------------------------------------
    // Tables
    // ------------------------------------
    fun Table.tr(color: String? = null, init: TR.() -> Unit) =
        initTag(TR(), init)
            .setAttribute("bgcolor", color)
    
    fun TR.td(color: String? = null, align: String = "right", init: TD.() -> Unit) =
        initTag(TD(), init)
            .setAttribute("align", align)
            .setAttribute("bgcolor", color)
    
    
    // ------------------------------------
    // Forms
    // ------------------------------------
    fun Form.newField(
        labelText: String,
        forAttr: BasicTableRenderer.FormParam,
        label: ((Label) -> Unit) = {},
        input: ((Input) -> Unit) = {}
    ) {
        initTag(Label()) {
            setAttribute("for", forAttr.id)
            text(labelText)
            label(this)
        }
        
        span()
        
        initTag(Input()) {
            setAttribute("name", forAttr.id)
            setAttribute("id", forAttr.id)
            input(this)
        }
    }
    
    fun Form.hiddenInput(
        forAttr: BasicTableRenderer.FormParam,
        input: ((Input) -> Unit) = {}
    ) {
        initTag(Input()) {
            setAttribute("style", "visibility: hidden;")
            setAttribute("name", forAttr.id)
            input(this)
        }
    }
    
    //<input type = "hidden" name = "topic" value = "something" />
    // ------------------------------------
    // Simple Children
    // ------------------------------------
    fun Tag.span(spanner: ((Span) -> Unit) = {}) =
        initTag(Span(), spanner)
    
    fun Tag.text(s: Any?) =
        initTag(Text(s.toString()), {})
    
    fun Tag.lineBreak() =
        initTag(LineBreak(), {})
    
    fun Tag.button(init: Button.() -> Unit) =
        initTag(Button(), init)
}