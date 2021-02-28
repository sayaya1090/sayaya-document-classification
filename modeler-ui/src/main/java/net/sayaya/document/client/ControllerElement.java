package net.sayaya.document.client;

import elemental2.dom.HTMLDivElement;
import net.sayaya.ui.Button;
import net.sayaya.ui.HTMLElementBuilder;
import org.jboss.elemento.HtmlContentBuilder;

import static org.jboss.elemento.Elements.div;

public class ControllerElement extends HTMLElementBuilder<HTMLDivElement, ControllerElement> {
	public static ControllerElement instance() {
		return new ControllerElement(div());
	}
	private final HtmlContentBuilder<HTMLDivElement> _this;
	private ControllerElement(HtmlContentBuilder<HTMLDivElement> e) {
		super(e);
		_this = e.css("controller");
	}
	public ControllerElement add(Button btn) {
		_this.add(btn);
		return that();
	}
	@Override
	public ControllerElement that() {
		return this;
	}
}
