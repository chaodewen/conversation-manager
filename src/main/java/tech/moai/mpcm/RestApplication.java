package tech.moai.mpcm;


import tech.moai.mpcm.api.ConversationApiImpl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Application;

@ApplicationPath("")
public class RestApplication extends Application {
	Set<Object> singletons = new HashSet<Object>();
	
	public RestApplication() {
		// 增加跨域内容
    	singletons.add(new ContainerResponseFilter() {
			@Override
			public void filter(ContainerRequestContext requestContext
					, ContainerResponseContext responseContext)
					throws IOException {
				responseContext.getHeaders().add("Access-Control-Allow-Origin"
						, "*");
				responseContext.getHeaders().add("Access-Control-Allow-Headers"
						, "origin, content-type, accept, authorization");
				responseContext.getHeaders().add("Access-Control-Allow-Methods"
						, "GET, POST, PUT, DELETE, OPTIONS, HEAD");
				responseContext.getHeaders().add("Access-Control-Max-Age"
						, "1209600");
			}
    	});
    	
    	// 增加资源实例
		singletons.add(new ConversationApiImpl());
	}

    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> clazzes = new HashSet<>();
        return clazzes;
    }

    @Override
    public Set<Object> getSingletons() {
		return singletons;
	}
}