package org.irods.jargon.ga4gh.dos.api;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.IRODSSession;
import org.irods.jargon.ga4gh.dos.bundle.DosService;
import org.irods.jargon.ga4gh.dos.bundle.DosServiceFactory;
import org.irods.jargon.ga4gh.dos.bundle.internalmodel.BundleInfoAndPath;
import org.irods.jargon.ga4gh.dos.bundle.internalmodel.IrodsAccessMethod;
import org.irods.jargon.ga4gh.dos.bundle.internalmodel.IrodsDataBundle;
import org.irods.jargon.ga4gh.dos.bundle.internalmodel.IrodsDataObject;
import org.irods.jargon.ga4gh.dos.configuration.DosConfiguration;
import org.irods.jargon.ga4gh.dos.exception.DosDataNotFoundException;
import org.irods.jargon.ga4gh.dos.model.AccessMethod;
import org.irods.jargon.ga4gh.dos.model.AccessURL;
import org.irods.jargon.ga4gh.dos.model.Checksum;
import org.irods.jargon.ga4gh.dos.model.ContentsObject;
import org.irods.jargon.ga4gh.dos.model.DrsObject;
import org.irods.jargon.ga4gh.dos.model.Error;
import org.irods.jargon.ga4gh.dos.security.ContextAccountHelper;
import org.irods.jargon.ga4gh.dos.utils.ServiceUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-06-22T12:15:59.889Z[GMT]")
@RestController
public class ObjectsApiController implements ObjectsApi {

    private static final Logger log = LoggerFactory.getLogger(ObjectsApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    @Autowired
   	private DosServiceFactory dosServiceFactory;

   	@Autowired
   	private ContextAccountHelper contextAccountHelper;

   	@Autowired
   	private DosConfiguration dosConfiguration;

   	@Autowired
   	private IRODSSession irodsSession;
       

    @org.springframework.beans.factory.annotation.Autowired
    public ObjectsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Override
    public Optional<ObjectMapper> getObjectMapper() {
        return Optional.ofNullable(objectMapper);
    }

    @Override
    public Optional<HttpServletRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    public ResponseEntity<AccessURL> getAccessURL(@Parameter(in = ParameterIn.PATH, description = "`DrsObject` identifier", required=true, schema=@Schema()) @PathVariable("object_id") String objectId,@Parameter(in = ParameterIn.PATH, description = "An `access_id` from the `access_methods` list of a `DrsObject`", required=true, schema=@Schema()) @PathVariable("access_id") String accessId) {
    	
             try {

 				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
 				String name = auth.getName();
 				log.info("name:{}", name);
 				IRODSAccount irodsAccount = this.contextAccountHelper.irodsAccountFromAuthentication(name);

 				log.debug("irodsAccount:{}", irodsAccount);

 				DosService dosService = dosServiceFactory.instanceDosService(irodsAccount);

 				try {
 					IrodsAccessMethod irodsAccessMethod = dosService.createAccessUrlForDataObject(objectId, accessId);
 					AccessURL accessUrl = new AccessURL();
 					accessUrl.setHeaders(new ArrayList<String>());
 					accessUrl.setUrl(irodsAccessMethod.getUrl());

 					for (String header : irodsAccessMethod.getHeaders()) {
 						accessUrl.getHeaders().add(header);
 					}

 					log.info("accessUrl:{}", accessUrl);
 					return new ResponseEntity<AccessURL>(accessUrl, HttpStatus.OK);

 				} catch (DosDataNotFoundException e) {
 					log.error("Data not found for id", e);
 					return new ResponseEntity<AccessURL>(HttpStatus.NOT_FOUND);
 				}

 			} catch (Exception e) {
 				log.error("Couldn't serialize response for content type application/json", e);
 				return new ResponseEntity<AccessURL>(HttpStatus.INTERNAL_SERVER_ERROR);
 			}
        
    }

    public ResponseEntity<DrsObject> getObject(@Parameter(in = ParameterIn.PATH, description = "`DrsObject` identifier", required=true, schema=@Schema()) @PathVariable("object_id") String objectId,@Parameter(in = ParameterIn.QUERY, description = "If false and the object_id refers to a bundle, then the ContentsObject array contains only those objects directly contained in the bundle. That is, if the bundle contains other bundles, those other bundles are not recursively included in the result. If true and the object_id refers to a bundle, then the entire set of objects in the bundle is expanded. That is, if the bundle contains aother bundles, then those other bundles are recursively expanded and included in the result. Recursion continues through the entire sub-tree of the bundle. If the object_id refers to a blob, then the query parameter is ignored." ,schema=@Schema()) @Valid @RequestParam(value = "expand", required = false) Boolean expand) {
    	
             	try {
         			log.info("getObject()");
         			if (objectId == null || objectId.isEmpty()) {
         				throw new IllegalArgumentException("null or empty objectId");
         			}

         			log.info("objectId:{}", objectId);

         			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         			String name = auth.getName();
         			log.info("name:{}", name);
         			IRODSAccount irodsAccount = this.contextAccountHelper.irodsAccountFromAuthentication(name);

         			// IRODSAccount irodsAccount = IRODSAccount.instance("server4.local", 1247,
         			// "test1", "test", "", "zone1", "");
         			DosService dosService = dosServiceFactory.instanceDosService(irodsAccount);

         			BundleInfoAndPath bundleInfo = dosService.resolveId(objectId);
         			if (bundleInfo.isCollection()) {
         				DrsObject drsObject = drsObjectFromCollection(objectId, dosService, bundleInfo);
         				log.info("ga4ghObject:{}", drsObject);
         				return new ResponseEntity<DrsObject>(drsObject, HttpStatus.OK);

         			} else {
         				DrsObject drsObject = drsObjecFromDataObject(objectId, dosService, bundleInfo);

         				log.info("ga4ghObject:{}", drsObject);
         				return new ResponseEntity<DrsObject>(drsObject, HttpStatus.OK);

         			}

         		} catch (DosDataNotFoundException e) {
         			log.warn("data not found for objectId:{}", objectId);
         			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

         		} catch (Exception e) {
         			log.error("Couldn't serialize response for content type application/json", e);
         			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

         		} finally {
         			dosServiceFactory.getIrodsAccessObjectFactory().closeSessionAndEatExceptions();
         		}
             
       
    }
    
    private DrsObject drsObjecFromDataObject(String objectId, DosService dosService, BundleInfoAndPath bundleInfo)
			throws DosDataNotFoundException {
		log.info("this is a data object");
		IrodsDataObject irodsDataObject = dosService.retrieveDataObject(bundleInfo);
		log.debug("have the data object:{}", irodsDataObject);

		DrsObject drsObject = new DrsObject();
		drsObject.setId(irodsDataObject.getGuid());
		drsObject.setName(irodsDataObject.getAbsolutePath());
		drsObject.setSelfUri("drs://" + this.getDosConfiguration().getDrsServerUrl() + "/" + objectId);
		drsObject.setSize(irodsDataObject.getSize());
		drsObject.setCreatedTime(ServiceUtils.offsetDateTimeFromDate(irodsDataObject.getCreateDate()));
		drsObject.setUpdatedTime(ServiceUtils.offsetDateTimeFromDate(irodsDataObject.getModifyDate()));
		drsObject.setVersion("");
		drsObject.setMimeType(irodsDataObject.getMimeType());

		List<Checksum> checksums = new ArrayList<>();

		Checksum checksum = new Checksum();
		checksum.setChecksum(irodsDataObject.getChecksum());
		checksum.setType(irodsDataObject.getChecksumType());
		checksums.add(checksum);

		drsObject.setChecksums(checksums);
		List<AccessMethod> accessMethods = new ArrayList<>();

		for (IrodsAccessMethod irodsAccessMethod : irodsDataObject.getIrodsAccessMethods()) {
			AccessMethod accessMethod = new AccessMethod();
			accessMethod.setAccessId(irodsAccessMethod.getAccessId());

			AccessURL accessURL = null;
			if (irodsAccessMethod.getUrl() != null) {
				accessURL = new AccessURL();
				accessURL.setHeaders(irodsAccessMethod.getHeaders());
				accessURL.setUrl(irodsAccessMethod.getUrl());
				accessMethod.setRegion(irodsAccessMethod.getRegion());
			}

			accessMethod.setAccessUrl(accessURL);
			accessMethod.setType(irodsAccessMethod.getType());
			accessMethods.add(accessMethod);

		}

		drsObject.setAccessMethods(accessMethods);
		drsObject.setChecksums(checksums);
		drsObject.setContents(new ArrayList<ContentsObject>());
		drsObject.setDescription(""); // TODO: add formal description AVU
		List<String> aliases = new ArrayList<>();
		aliases.add(irodsDataObject.getAbsolutePath());
		drsObject.setAliases(aliases);
		return drsObject;
	}

	private DrsObject drsObjectFromCollection(String objectId, DosService dosService, BundleInfoAndPath bundleInfo)
			throws DosDataNotFoundException {
		log.info("this is a data bundle");
		IrodsDataBundle irodsDataBundle = dosService.retrieveDataBundle(bundleInfo);
		DrsObject drsObject = new DrsObject();
		drsObject.setId(objectId);
		drsObject.setName(irodsDataBundle.getIrodsAbsolutePath());
		drsObject.setSelfUri("drs://" + this.getDosConfiguration().getDrsServerUrl() + "/" + objectId);
		drsObject.setSize(0L);
		drsObject.setCreatedTime(ServiceUtils.offsetDateTimeFromDate(irodsDataBundle.getCreateDate()));
		drsObject.setUpdatedTime(ServiceUtils.offsetDateTimeFromDate(irodsDataBundle.getUpdatedDate()));
		drsObject.setVersion("0");
		drsObject.setMimeType("text/directory");
		drsObject.setAccessMethods(new ArrayList<AccessMethod>());

		drsObject.addAliasesItem(irodsDataBundle.getIrodsAbsolutePath());
		Checksum checksum = new Checksum();
		checksum.setChecksum(irodsDataBundle.getBundleChecksum());
		checksum.setType(irodsDataBundle.getBundleChecksumType());
		drsObject.addChecksumsItem(checksum);
		ContentsObject bundleObject;
		List<ContentsObject> dataObjects = new ArrayList<>();
		for (IrodsDataObject dataObject : irodsDataBundle.getDataObjects()) {
			bundleObject = new ContentsObject();
			bundleObject.setId(dataObject.getGuid());
			bundleObject.setName(dataObject.getFileName());
			bundleObject.setDrsUri(new ArrayList<String>());
			bundleObject.getDrsUri().add(dataObject.getIrodsAccessMethods().get(0).getUrl());
			bundleObject.setContents(new ArrayList<ContentsObject>());
			dataObjects.add(bundleObject);
		}

		drsObject.setContents(dataObjects);
		drsObject.setDescription(irodsDataBundle.getDescription());
		return drsObject;
	}

    public ResponseEntity<AccessURL> postAccessURL(@Parameter(in = ParameterIn.PATH, description = "`DrsObject` identifier", required=true, schema=@Schema()) @PathVariable("object_id") String objectId,@Parameter(in = ParameterIn.PATH, description = "An `access_id` from the `access_methods` list of a `DrsObject`", required=true, schema=@Schema()) @PathVariable("access_id") String accessId,@Parameter(in = ParameterIn.DEFAULT, description = "", required=true, schema=@Schema()) @Valid @RequestBody Object body) {
       
            try {
                return new ResponseEntity<AccessURL>(objectMapper.readValue("{\n  \"headers\" : \"Authorization: Basic Z2E0Z2g6ZHJz\",\n  \"url\" : \"url\"\n}", AccessURL.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<AccessURL>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
       
    }

    public ResponseEntity<DrsObject> postObject(@Parameter(in = ParameterIn.PATH, description = "`DrsObject` identifier", required=true, schema=@Schema()) @PathVariable("object_id") String objectId,@Parameter(in = ParameterIn.DEFAULT, description = "", required=true, schema=@Schema()) @Valid @RequestBody Object body) {
     
            try {
                return new ResponseEntity<DrsObject>(objectMapper.readValue("{\n  \"checksums\" : [ {\n    \"checksum\" : \"checksum\",\n    \"type\" : \"sha-256\"\n  }, {\n    \"checksum\" : \"checksum\",\n    \"type\" : \"sha-256\"\n  } ],\n  \"created_time\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"updated_time\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"aliases\" : [ \"aliases\", \"aliases\" ],\n  \"description\" : \"description\",\n  \"self_uri\" : \"drs://drs.example.org/314159\",\n  \"version\" : \"version\",\n  \"size\" : 0,\n  \"mime_type\" : \"application/json\",\n  \"access_methods\" : [ {\n    \"access_url\" : {\n      \"headers\" : \"Authorization: Basic Z2E0Z2g6ZHJz\",\n      \"url\" : \"url\"\n    },\n    \"access_id\" : \"access_id\",\n    \"type\" : \"s3\",\n    \"region\" : \"us-east-1\"\n  }, {\n    \"access_url\" : {\n      \"headers\" : \"Authorization: Basic Z2E0Z2g6ZHJz\",\n      \"url\" : \"url\"\n    },\n    \"access_id\" : \"access_id\",\n    \"type\" : \"s3\",\n    \"region\" : \"us-east-1\"\n  } ],\n  \"contents\" : [ {\n    \"contents\" : [ null, null ],\n    \"name\" : \"name\",\n    \"id\" : \"id\",\n    \"drs_uri\" : \"drs://drs.example.org/314159\"\n  }, {\n    \"contents\" : [ null, null ],\n    \"name\" : \"name\",\n    \"id\" : \"id\",\n    \"drs_uri\" : \"drs://drs.example.org/314159\"\n  } ],\n  \"name\" : \"name\",\n  \"id\" : \"id\"\n}", DrsObject.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<DrsObject>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        
    }
    
    public DosServiceFactory getDosServiceFactory() {
		return dosServiceFactory;
	}

	public void setDosServiceFactory(DosServiceFactory dosServiceFactory) {
		this.dosServiceFactory = dosServiceFactory;
	}

	public ContextAccountHelper getContextAccountHelper() {
		return contextAccountHelper;
	}

	public void setContextAccountHelper(ContextAccountHelper contextAccountHelper) {
		this.contextAccountHelper = contextAccountHelper;
	}

	public DosConfiguration getDosConfiguration() {
		return dosConfiguration;
	}

	public void setDosConfiguration(DosConfiguration dosConfiguration) {
		this.dosConfiguration = dosConfiguration;
	}

	public IRODSSession getIrodsSession() {
		return irodsSession;
	}

	public void setIrodsSession(IRODSSession irodsSession) {
		this.irodsSession = irodsSession;
	}

}
