/**
 * NOTE: This class is auto generated by the swagger code generator program (2.4.0).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package org.irods.jargon.ga4gh.dos.api;

import javax.validation.Valid;

import org.irods.jargon.ga4gh.dos.model.CreateBundleRequest;
import org.irods.jargon.ga4gh.dos.model.CreateBundleResponse;
import org.irods.jargon.ga4gh.dos.model.DeleteBundleResponse;
import org.irods.jargon.ga4gh.dos.model.ErrorResponse;
import org.irods.jargon.ga4gh.dos.model.GetBundleResponse;
import org.irods.jargon.ga4gh.dos.model.GetBundleVersionsResponse;
import org.irods.jargon.ga4gh.dos.model.ListBundlesResponse;
import org.irods.jargon.ga4gh.dos.model.UpdateBundleRequest;
import org.irods.jargon.ga4gh.dos.model.UpdateBundleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-02-07T17:18:44.860Z")

@Api(value = "bundles", description = "the bundles API")
public interface BundlesApi {

	@ApiOperation(value = "Create a new Data Bundle", nickname = "createBundle", notes = "", response = CreateBundleResponse.class, tags = {
			"DataRepositoryService", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "The Data Bundle was successfully created.", response = CreateBundleResponse.class),
			@ApiResponse(code = 400, message = "The request is malformed.", response = ErrorResponse.class),
			@ApiResponse(code = 401, message = "The request is unauthorized.", response = ErrorResponse.class),
			@ApiResponse(code = 403, message = "The requester is not authorized to perform this action.", response = ErrorResponse.class),
			@ApiResponse(code = 500, message = "An unexpected error occurred.", response = ErrorResponse.class) })
	@RequestMapping(value = "/bundles", produces = { "application/json" }, consumes = {
			"application/json" }, method = RequestMethod.POST)
	ResponseEntity<CreateBundleResponse> createBundle(
			@ApiParam(value = "", required = true) @Valid @RequestBody CreateBundleRequest body);

	@ApiOperation(value = "Delete a Data Bundle", nickname = "deleteBundle", notes = "", response = DeleteBundleResponse.class, tags = {
			"DataRepositoryService", })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "", response = DeleteBundleResponse.class) })
	@RequestMapping(value = "/bundles/{bundle_id}", produces = { "application/json" }, consumes = {
			"application/json" }, method = RequestMethod.DELETE)
	ResponseEntity<DeleteBundleResponse> deleteBundle(
			@ApiParam(value = "", required = true) @PathVariable("bundle_id") String bundleId);

	@ApiOperation(value = "Retrieve a Data Bundle", nickname = "getBundle", notes = "", response = GetBundleResponse.class, tags = {
			"DataRepositoryService", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully found the Data Bundle.", response = GetBundleResponse.class),
			@ApiResponse(code = 400, message = "The request is malformed.", response = ErrorResponse.class),
			@ApiResponse(code = 401, message = "The request is unauthorized.", response = ErrorResponse.class),
			@ApiResponse(code = 403, message = "The requester is not authorized to perform this action.", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "The requested Data Bundle wasn't found.", response = ErrorResponse.class),
			@ApiResponse(code = 500, message = "An unexpected error occurred.", response = ErrorResponse.class) })
	@RequestMapping(value = "/bundles/{bundle_id}", produces = { "application/json" }, consumes = {
			"application/json" }, method = RequestMethod.GET)
	ResponseEntity<GetBundleResponse> getBundle(
			@ApiParam(value = "", required = true) @PathVariable("bundle_id") String bundleId,
			@ApiParam(value = "If provided will return the requested version of the selected Data Bundle. Otherwise, only the latest version is returned.") @Valid @RequestParam(value = "version", required = false) String version);

	@ApiOperation(value = "Retrieve all versions of a Data Bundle", nickname = "getBundleVersions", notes = "", response = GetBundleVersionsResponse.class, tags = {
			"DataRepositoryService", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "The versions for the Data Bundle were found successfully.", response = GetBundleVersionsResponse.class),
			@ApiResponse(code = 400, message = "The request is malformed.", response = ErrorResponse.class),
			@ApiResponse(code = 401, message = "The request is unauthorized.", response = ErrorResponse.class),
			@ApiResponse(code = 403, message = "The requester is not authorized to perform this action.", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "The requested Data Bundle wasn't found.", response = ErrorResponse.class),
			@ApiResponse(code = 500, message = "An unexpected error occurred.", response = ErrorResponse.class) })
	@RequestMapping(value = "/bundles/{bundle_id}/versions", produces = { "application/json" }, consumes = {
			"application/json" }, method = RequestMethod.GET)
	ResponseEntity<GetBundleVersionsResponse> getBundleVersions(
			@ApiParam(value = "", required = true) @PathVariable("bundle_id") String bundleId);

	@ApiOperation(value = "List the Data Bundles", nickname = "listBundles", notes = "", response = ListBundlesResponse.class, tags = {
			"DataRepositoryService", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully listed Data Bundles.", response = ListBundlesResponse.class),
			@ApiResponse(code = 400, message = "The request is malformed.", response = ErrorResponse.class),
			@ApiResponse(code = 401, message = "The request is unauthorized.", response = ErrorResponse.class),
			@ApiResponse(code = 403, message = "The requester is not authorized to perform this action.", response = ErrorResponse.class),
			@ApiResponse(code = 500, message = "An unexpected error occurred.", response = ErrorResponse.class) })
	@RequestMapping(value = "/bundles", produces = { "application/json" }, consumes = {
			"application/json" }, method = RequestMethod.GET)
	ResponseEntity<ListBundlesResponse> listBundles(
			@ApiParam(value = "If provided returns Data Bundles that have any alias that matches the request.") @Valid @RequestParam(value = "alias", required = false) String alias,
			@ApiParam(value = "The hexlified checksum that one would like to match on.") @Valid @RequestParam(value = "checksum", required = false) String checksum,
			@ApiParam(value = "If provided will restrict responses to those that match the provided type.  possible values: md5                # most blob stores provide a checksum using this multipart-md5      # multipart uploads provide a specialized tag in S3 sha256 sha512") @Valid @RequestParam(value = "checksum_type", required = false) String checksumType,
			@ApiParam(value = "Specifies the maximum number of results to return in a single page. If unspecified, a system default will be used.") @Valid @RequestParam(value = "page_size", required = false) Integer pageSize,
			@ApiParam(value = "The continuation token, which is used to page through large result sets. To get the next page of results, set this parameter to the value of `next_page_token` from the previous response.") @Valid @RequestParam(value = "page_token", required = false) String pageToken);

	@ApiOperation(value = "Update a Data Bundle", nickname = "updateBundle", notes = "", response = UpdateBundleResponse.class, tags = {
			"DataRepositoryService", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "The Data Bundle was updated successfully.", response = UpdateBundleResponse.class),
			@ApiResponse(code = 400, message = "The request is malformed.", response = ErrorResponse.class),
			@ApiResponse(code = 401, message = "The request is unauthorized.", response = ErrorResponse.class),
			@ApiResponse(code = 403, message = "The requester is not authorized to perform this action.", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "The requested Data Bundle wasn't found.", response = ErrorResponse.class),
			@ApiResponse(code = 500, message = "An unexpected error occurred.", response = ErrorResponse.class) })
	@RequestMapping(value = "/bundles/{bundle_id}", produces = { "application/json" }, consumes = {
			"application/json" }, method = RequestMethod.PUT)
	ResponseEntity<UpdateBundleResponse> updateBundle(
			@ApiParam(value = "The ID of the Data Bundle to update", required = true) @PathVariable("bundle_id") String bundleId,
			@ApiParam(value = "The new content for the Data Bundle identified by the given bundle_id. If the ID specified in the request body is different than that specified in the path, the Data Bundle's ID will be replaced with the one in the request body.", required = true) @Valid @RequestBody UpdateBundleRequest body);

}
