package com.sensedia.jaya.api.access;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

public class RequestUserProvider implements InjectableProvider<RequestUser, Parameter> {

	RequestUserInjectable injectable;

	public RequestUserProvider(RequestUserInjectable injectable) {
		super();
		this.injectable = injectable;
	}

	public Injectable<?> getInjectable(ComponentContext ctx, RequestUser requestUserAnnotation, Parameter param) {
		return injectable;
	}

	public ComponentScope getScope() {
		return ComponentScope.PerRequest;
	}

}
