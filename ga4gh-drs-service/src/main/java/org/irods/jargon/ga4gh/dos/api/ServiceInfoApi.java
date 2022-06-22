/**
 * NOTE: This class is auto generated by the swagger code generator program (3.0.34).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package org.irods.jargon.ga4gh.dos.api;

import org.irods.jargon.ga4gh.dos.model.Error;
import org.irods.jargon.ga4gh.dos.model.InlineResponse200;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.CookieValue;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-06-22T12:15:59.889Z[GMT]")
@Validated
public interface ServiceInfoApi {

    Logger log = LoggerFactory.getLogger(ServiceInfoApi.class);

    default Optional<ObjectMapper> getObjectMapper(){
        return Optional.empty();
    }

    default Optional<HttpServletRequest> getRequest(){
        return Optional.empty();
    }

    default Optional<String> getAcceptHeader() {
        return getRequest().map(r -> r.getHeader("Accept"));
    }

    @Operation(summary = "Retrieve information about this service", description = "Returns information about the DRS service  Extends the [v1.0.0 GA4GH Service Info specification](https://github.com/ga4gh-discovery/ga4gh-service-info) as the standardized format for GA4GH web services to self-describe.  According to the  [service-info type registry](https://github.com/ga4gh/TASC/blob/master/service-info/ga4gh-service-info.json) maintained by the [Technical Alignment Sub Committee (TASC)](https://github.com/ga4gh/TASC), a DRS service MUST have:   * a `type.group` value of `org.ga4gh`   * a `type.artifact` value of `drs`  e.g. ``` {     \"id\": \"com.example.drs\",     \"description\": \"Serves data according to DRS specification\",     ...     \"type\": {         \"group\": \"org.ga4gh\",         \"artifact\": \"drs\"     }     ... } ```  See the [Service Registry Appendix](#tag/GA4GH-Service-Registry) for more information on how to register a DRS service with a service registry.", security = {
        @SecurityRequirement(name = "BasicAuth"),
@SecurityRequirement(name = "BearerAuth")    }, tags={ "Service Info" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Retrieve info about the DRS service", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse200.class))),
        
        @ApiResponse(responseCode = "500", description = "An unexpected error occurred.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))) })
    @RequestMapping(value = "/service-info",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<InlineResponse200> getServiceInfo() {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("\"\"", InlineResponse200.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ServiceInfoApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}

