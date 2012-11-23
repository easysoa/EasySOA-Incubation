
package fr.axxx.pivotal.app.api;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.osoa.sca.annotations.Service;



/**
 * Allows (html / velocity) UI to make AJAX calls to webapp, see js/pivotalActions.js .
 * For now all methods are only samples.
 * 
 * @author mdutoo
 *
 */
@Service
public interface RESTCall {
    
  @POST
  @Path("/addElement")
  @Produces("text/plain")
  void addElement(@FormParam("userId")String userId, @FormParam("elementId")String elementId,@FormParam("action")String action);
  
  @POST
  @Path("/saveElement")
  @Produces("text/plain")
  void saveElement(@FormParam("userId")String userId, @FormParam("params")String params);
  
  @GET
  @Path("/implementationContent")
  @Produces("text/plain")
  String getImplementationContent(@FormParam("userId")String userId, @FormParam("modelId")String modelId,@FormParam("elementId")String elementId);
  
  @GET
  @Path("/interfaceContent")
  @Produces("text/plain")
  String getInterfaceContent(@FormParam("userId")String userId, @FormParam("modelId")String modelId, @FormParam("elementId")String elementId);
  
  @GET
  @Path("/bindingContent")
  @Produces("text/plain")
  String getBindingContent(@FormParam("userId")String userId, @FormParam("modelId")String modelId, @FormParam("elementId")String elementId);
  
  @GET
  @Path("/fileContent")
  @Produces("text/plain")
  String getFileContent(@FormParam("type")String type);

  @GET
  @Path("/editorMode")
  @Produces("text/plain")
  String getEditorMode(@FormParam("type")String type);
  
  @POST
  @Path("/saveFileContent")
  @Produces("text/plain")
  void saveFileContent(@FormParam("content")String content, @FormParam("type")String type);
  
  @GET
  @Path("/existingImplementations")
  @Produces("text/plain")
  String getExistingImplementations(@FormParam("userId")String userId);

  @POST
  @Path("/createNewImplementation")
  @Produces("text/plain")
  void createNewImplementation(@FormParam("userId")String userId, @FormParam("elementId")String elementId, @FormParam("className")String className, @FormParam("implemType")String implemType, @FormParam("createFile")boolean createFile);
  
  @POST
  @Path("/createNewInterface")
  @Produces("text/plain")
  void createNewInterface(@FormParam("userId")String userId, @FormParam("elementId")String elementId, @FormParam("className")String className, @FormParam("interfaceType")String interfaceType,@FormParam("createFile")boolean createFile,@FormParam("choice")String choice);

  @POST
  @Path("/createNewBinding")
  @Produces("text/plain")
  void createNewBinding(@FormParam("userId")String userId, @FormParam("elementId")String elementId, @FormParam("bindingType")String bindingType, @FormParam("uri")String bindingUri);

  @GET
  @Path("/isExistingTown")
  @Produces("text/plain")
  boolean isExistingTown(@FormParam("town")String town,@FormParam("country")String country);
  
  @GET
  @Path("/hasExistingImplementation")
  @Produces("text/plain")
  boolean hasAnExistingImplementation(@FormParam("userId")String userId,@FormParam("elementId")String elementId);

  @GET
  @Path("/hasExistingInterface")
  @Produces("text/plain")
  boolean hasAnExistingInterface(@FormParam("userId")String userId,@FormParam("elementId")String elementId);
  
  @GET
  @Path("/intentImplementation")
  @Produces("text/plain")
  String getIntentImplementation(@FormParam("name")String name,@FormParam("userId")String userId);

  @POST
  @Path("/saveIntent")
  @Produces("text/plain")
  void saveIntent(@FormParam("name")String name, @FormParam("userId")String userId, @FormParam("content")String content);
}
