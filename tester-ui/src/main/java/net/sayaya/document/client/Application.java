package net.sayaya.document.client;

import com.google.gwt.core.client.EntryPoint;
import elemental2.dom.DomGlobal;

public class Application implements EntryPoint {
	@Override
	public void onModuleLoad() {
		DomGlobal.alert("Tester");
	}
}
