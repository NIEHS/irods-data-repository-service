package org.irods.jargon.ga4gh.dos.api;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-05-14T11:28:18.659Z")

public class NotFoundException extends ApiException {
	private int code;

	public NotFoundException(int code, String msg) {
		super(code, msg);
		this.code = code;
	}
}
