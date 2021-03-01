package net.sayaya.document.data;

import jsinterop.annotations.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
@Setter(onMethod_={@JsOverlay, @JsIgnore})
@Getter(onMethod_={@JsOverlay, @JsIgnore})
@Accessors(fluent=true)
public final class Slice<T> {
	@JsProperty(name="total_element")
	private long totalElement;
	@JsProperty(name="total_page")
	private long totalPage;
	@JsProperty(name="current_page")
	private long currentPage;
	private T[] content;
}
