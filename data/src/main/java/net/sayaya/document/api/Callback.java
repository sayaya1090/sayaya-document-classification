package net.sayaya.document.api;

import elemental2.dom.DomGlobal;

@FunctionalInterface
public interface Callback<T> {
	void onSuccess(T var);
	default void onFailure(Throwable reason) {
		DomGlobal.console.log(reason.getMessage());
	}
}
