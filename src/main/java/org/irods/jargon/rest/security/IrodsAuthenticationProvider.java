/**
 * 
 */
package org.irods.jargon.rest.security;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.IRODSSession;
import org.irods.jargon.core.connection.auth.AuthResponse;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.rest.configuration.RestConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Spring security authentication provider for iRODS
 * 
 * @author mconway
 *
 */
@Component
public class IrodsAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private RestConfiguration restConfiguration;
	@Autowired
	private IRODSSession irodsSession;
	@Autowired
	private IRODSAccessObjectFactory irodsAccessObjectFactory;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.authentication.AuthenticationProvider#
	 * authenticate(org.springframework.security.core.Authentication)
	 */
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {

		log.info("authenticate()");
		if (authentication == null) {
			throw new IllegalArgumentException("null authentication");
		}

		log.info("user name of principal is {}", authentication.getPrincipal());
		String password = authentication.getCredentials().toString();
		Authentication authenticatedCredential;

		try {
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromUserPassword(authentication
							.getPrincipal().toString(), password,
							restConfiguration);

			log.info("authenticating:{}", irodsAccount);

			try {
				AuthResponse authResponse = irodsAccessObjectFactory
						.authenticateIRODSAccount(irodsAccount);
				authenticatedCredential = new IrodsAuthentication(irodsAccount,
						authResponse);
				authenticatedCredential.setAuthenticated(true);
			} catch (Exception e) {
				log.warn("authentication failure", e);
				AuthResponse authResponse = new AuthResponse();
				authResponse.setAuthenticatingIRODSAccount(irodsAccount);
				authResponse.setAuthenticatedIRODSAccount(irodsAccount);
				authResponse.setSuccessful(false);
				authenticatedCredential = new IrodsAuthentication(irodsAccount,
						authResponse);
				authenticatedCredential.setAuthenticated(false);
			}

			// TODO: add group gathering?

		} catch (JargonException e) {
			log.error("error getting creds", e);
			throw new AuthenticationServiceException(e.getLocalizedMessage());
		}

		return authenticatedCredential;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz == UsernamePasswordAuthenticationToken.class;
	}

	/**
	 * @return the restConfiguration
	 */
	public RestConfiguration getRestConfiguration() {
		return restConfiguration;
	}

	/**
	 * @param restConfiguration
	 *            the restConfiguration to set
	 */
	public void setRestConfiguration(RestConfiguration restConfiguration) {
		this.restConfiguration = restConfiguration;
	}

	/**
	 * @return the irodsSession
	 */
	public IRODSSession getIrodsSession() {
		return irodsSession;
	}

	/**
	 * @param irodsSession
	 *            the irodsSession to set
	 */
	public void setIrodsSession(IRODSSession irodsSession) {
		this.irodsSession = irodsSession;
	}

	/**
	 * @return the irodsAccessObjectFactory
	 */
	public IRODSAccessObjectFactory getIrodsAccessObjectFactory() {
		return irodsAccessObjectFactory;
	}

	/**
	 * @param irodsAccessObjectFactory
	 *            the irodsAccessObjectFactory to set
	 */
	public void setIrodsAccessObjectFactory(
			IRODSAccessObjectFactory irodsAccessObjectFactory) {
		this.irodsAccessObjectFactory = irodsAccessObjectFactory;
	}

}
