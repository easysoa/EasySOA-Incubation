
package org.ow2.frascati.intent.cxf.jaxrs.provider.easysoa;

import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.jaxrs.provider.ProviderFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.ow2.frascati.intent.cxf.AbstractEndpointIntent;

/**
 * Inspired by FraSCAti Jackson Intent.
 *
 * TODO contribute it to Jackson Intent : make JacksonIntent inherit
 * AbstractEndpointIntent so it will also work on server side
 * TODO reuse Jackson Intent : for now org/ow2/.../Jackson.composite file
 * can't be found... And similarly EasySOARegistry.composite can't be deeper
 * than root. 
 *
 * @author Marc Dutoo
 */
public class EasySOARegistryIntent extends AbstractEndpointIntent {

    @Override
    protected void configure(Endpoint endpoint) {
        // Get the provider factory attached to this endpoint.
        ProviderFactory providerFactory = (ProviderFactory) endpoint.get(ProviderFactory.class.getName());
        
        // now let's register providers...
        
        // custom JAXRS provider :
        //providerFactory.registerUserProvider(new JsonMessageReader());
        //providerFactory.registerUserProvider(new JsonMessageWriter());
        
        // Jersey's Jackson provider ;
        //providerFactory.registerUserProvider(new JacksonProviderProxy()); // however its configuration would be Jersey's
        // see http://www.mkyong.com/webservices/jax-rs/json-example-with-jersey-jackson/
        // http://grepcode.com/file/repo1.maven.org/maven2/com.sun.jersey/jersey-bundle/1.11/com/sun/jersey/json/impl/provider/entity/JacksonProviderProxy.java?av=f

        // Jackson's provider :
        //ObjectMapper mapper = new ObjectMapper(); // allows to configure Jackson
        JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider(/*mapper*/); // from jackson-jaxrs
        providerFactory.registerUserProvider(jacksonJsonProvider);
    }
}
