package kotlin_quick_monies.visual_interfaces.web.htmlComponents

import kotlin_quick_monies.visual_interfaces.web.BasicTableRenderer
import kotlin_quick_monies.visual_interfaces.web.JavalinWebFrameworkWrapper
import kotlinx.css.CSSBuilder

object SimpleHTML {
    
    fun <T : Tag> T.setAttribute(name: String, value: String?): T = apply {
        if (value != null) {
            attributes.add(Attribute(name, value))
        }
    }
    
    fun <T : Tag> T.setCssClasses(vararg classes: String): T = apply {
        classes.joinToString(" ").let {
            attributes.add(Attribute("class", it))
        }
    }
    
    fun <T : Tag> Tag.initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }
    
    class Html : Tag("html")
    class Head : Tag("head")
    class Style : Tag("style")
    class Meta : Tag("meta")
    class Body : Tag("body")
    
    class Table : Tag("table")
    class TR : Tag("tr")
    class TD : Tag("td")
    class Text(val text: String) : Tag("b") {
        override fun toString() = text
    }
    
    class Center : Tag("center")
    class Span : Tag("span")
    class Div : Tag("div")
    
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
    
    fun Html.head(init: Head.() -> Unit) =
        initTag(Head(), init)
    
    fun Html.style(init: Style.() -> Unit) =
        initTag(Style(), init)
    
    fun Html.meta(init: Meta.() -> Unit) =
        initTag(Meta(), init)
    
    fun Html.body(init: Body.() -> Unit) =
        initTag(Body(), init)
    
    fun Html.center(init: Center.() -> Unit) =
        initTag(Center(), init)
    
    
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
        //<input type = "hidden" name = "topic" value = "something" />
        initTag(Input()) {
            setAttribute("style", "visibility: hidden;")
            setAttribute("name", forAttr.id)
            input(this)
        }
    }
    
    fun Form.addActionAndMethod(route: JavalinWebFrameworkWrapper.Route) {
        with(SimpleHTML) {
            this@addActionAndMethod.setAttribute("action", route.path)
            this@addActionAndMethod.setAttribute("method", route.method)
        }
    }
    
    // ------------------------------------
    // Simple Children
    // ------------------------------------
    fun Tag.table(init: Table.() -> Unit) =
        initTag(Table(), init)
    
    fun Tag.form(init: Form.() -> Unit) =
        initTag(Form(), init)
    
    fun Tag.span(spanner: ((Span) -> Unit) = {}) =
        initTag(Span(), spanner)
    
    fun Tag.text(s: Any?) =
        initTag(Text(s.toString()), {})
    
    fun Tag.lineBreak() =
        initTag(LineBreak(), {})
    
    fun Tag.button(init: Button.() -> Unit) =
        initTag(Button(), init)
    
    fun Tag.div(init: Div.() -> Unit) =
        initTag(Div(), init)
    
    // ------------------------------------
    // CSS
    // ------------------------------------
    fun Html.setStyles(receiver: CSSBuilder.() -> Unit) {
        head {
            style {
                text(
                    CSSBuilder().apply(receiver).toString()
                )
            }
        }
    }
    
    fun Html.applyDeviceDependentSizing() {
        meta {
            // <meta name="viewport" content="width=device-width, initial-scale=1.0">
            setAttribute("name", "viewport")
            setAttribute("content", "width=device-width, initial-scale=1.0")
        }
    }
}
