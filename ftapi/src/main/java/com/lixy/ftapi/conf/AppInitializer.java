package com.lixy.ftapi.conf;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletException;

import org.springframework.web.WebApplicationInitializer;

public class AppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(javax.servlet.ServletContext servletContext) throws ServletException {
        /* Other code omitted for brevity */
        
        FilterRegistration.Dynamic corsFilter = servletContext.addFilter("corsFilter", CORSFilter.class);
        corsFilter.addMappingForUrlPatterns(null, false, "/*");
    }
}