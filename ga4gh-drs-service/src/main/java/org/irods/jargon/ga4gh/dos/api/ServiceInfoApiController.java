package org.irods.jargon.ga4gh.dos.api;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.IRODSSession;
import org.irods.jargon.ga4gh.dos.bundle.DosServiceFactory;
import org.irods.jargon.ga4gh.dos.bundlemgmnt.ServiceInfoService;
import org.irods.jargon.ga4gh.dos.configuration.DosConfiguration;
import org.irods.jargon.ga4gh.dos.model.InlineResponse200;
import org.irods.jargon.ga4gh.dos.security.ContextAccountHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-06-22T12:15:59.889Z[GMT]")
@RestController
public class ServiceInfoApiController implements ServiceInfoApi {

    private static final Logger log = LoggerFactory.getLogger(ServiceInfoApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;


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

	@Autowired
   	private DosServiceFactory dosServiceFactory;

   	@Autowired
   	private ContextAccountHelper contextAccountHelper;

   	@Autowired
   	private DosConfiguration dosConfiguration;

   	@Autowired
   	private IRODSSession irodsSession;


    @org.springframework.beans.factory.annotation.Autowired
    public ServiceInfoApiController(ObjectMapper objectMapper, HttpServletRequest request) {
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

    @Override
	public ResponseEntity<InlineResponse200> getServiceInfo() {
    	log.info("getServiceInfo()");
    	log.info("dosConfiguration:{}", dosConfiguration);
        String accept = request.getHeader("Accept");
        if (accept == null || accept.contains("application/json")) {
            	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
 				String name = auth.getName();
 				log.info("name:{}", name);
 				IRODSAccount irodsAccount = this.contextAccountHelper.irodsAccountFromAuthentication(name);

 				log.debug("irodsAccount:{}", irodsAccount);

 				ServiceInfoService serviceInfoService =  dosServiceFactory.instanceServiceInfoService(irodsAccount);
 				return new ResponseEntity<>(serviceInfoService.generateServiceInfoFromConfig(), HttpStatus.OK);

        }

        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
