/**
 * 
 */
package org.irods.jargon.rest.security;

import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.irods.jargon.core.connection.AuthScheme;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.rest.utils.DefaultHttpClientAndContext;
import org.irods.jargon.rest.utils.RestTestingProperties;
import org.irods.jargon.testutils.TestConfigurationException;
import org.irods.jargon.testutils.TestingPropertiesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import gov.nih.niehs.ods.ga4gh.dos.exception.DosRuntimeException;
import gov.nih.niehs.ods.ga4gh.rest.configuration.DosConfigInterface;

/**
 * @author Mike Conway - DICE (www.irods.org)
 * 
 */
@SuppressWarnings("deprecation")
public class RestAuthUtils {

	private static Logger log = LoggerFactory.getLogger(RestAuthUtils.class);

	/**
	 * Retreive the account information from the security context
	 * 
	 * @return {@link IRODSAccount} for the logged in iRODS user
	 */
	public static IRODSAccount irodsAccountFromContext() {
		log.info("authentication:{}", SecurityContextHolder.getContext().getAuthentication());
		IrodsAuthentication irodsAuthentication = (IrodsAuthentication) SecurityContextHolder.getContext()
				.getAuthentication();
		return irodsAuthentication.getIrodsAccount();
	}

	public static String basicAuthTokenFromIRODSAccount(final IRODSAccount irodsAccount) {
		if (irodsAccount == null) {
			throw new IllegalArgumentException("null irodsAccount");
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Basic ");

		StringBuilder toEncode = new StringBuilder();
		toEncode.append(irodsAccount.getUserName());
		toEncode.append(":");
		toEncode.append(irodsAccount.getPassword());

		sb.append(Base64.encodeBase64String(toEncode.toString().getBytes()));
		return sb.toString();
	}

	/**
	 * 
	 * @param user
	 * @param password
	 * @param restConfiguration
	 * @return
	 * @throws JargonException
	 */
	public static IRODSAccount getIRODSAccountFromUserPassword(final String user, final String password,
			final DosConfigInterface restConfiguration) {
		log.info("getIRODSAccountFromUserPassword()");
		if (user == null || user.isEmpty()) {
			throw new IllegalArgumentException("null user");
		}

		if (password == null) {
			throw new IllegalArgumentException("null password, use blank if not provided");
		}

		String myUser = user;

		log.info("user:{}", user);

		log.info("restConfiguration:{}", restConfiguration);

		AuthScheme authScheme = determineAuthSchemeFromConfig(restConfiguration);

		log.info("see if auth scheme is overrideen by the provided credentials");
		/*
		 * Ids can be prepended with STANDARD: or PAM:
		 */

		if (user.startsWith(AuthScheme.STANDARD.toString())) {
			log.info("authScheme override to Standard");
			authScheme = AuthScheme.STANDARD;
			myUser = user.substring(AuthScheme.STANDARD.toString().length() + 1);
		} else if (user.startsWith(AuthScheme.PAM.toString())) {
			log.info("authScheme override to PAM");
			authScheme = AuthScheme.PAM;
			myUser = user.substring(AuthScheme.PAM.toString().length() + 1);
		}

		try {
			return IRODSAccount.instance(restConfiguration.getIrodsHost(), restConfiguration.getIrodsPort(), myUser,
					password, "", restConfiguration.getIrodsZone(), restConfiguration.getDefaultStorageResource(),
					authScheme);
		} catch (JargonException e) {
			log.error("Jargon exception building irods account", e);
			throw new DosRuntimeException("error creating irods account", e);
		}

	}

	/**
	 * Given the raw 'basic' auth header (with the Basic prefix), build an iRODS
	 * account
	 * 
	 * @param basicAuthData
	 * @param restConfiguration
	 * @return
	 * @throws JargonException
	 */
	public static IRODSAccount getIRODSAccountFromBasicAuthValues(final String basicAuthData,
			final DosConfigInterface restConfiguration) throws JargonException {

		log.info("getIRODSAccountFromBasicAuthValues");

		if (basicAuthData == null || basicAuthData.isEmpty()) {
			throw new IllegalArgumentException("null or empty basicAuthData");
		}

		if (restConfiguration == null) {
			throw new IllegalArgumentException("null restConfiguration");
		}

		final int index = basicAuthData.indexOf(' ');
		log.info("index of end of basic prefix:{}", index);
		String auth = basicAuthData.substring(index);

		String decoded = new String(Base64.decodeBase64(auth));

		log.info("index of end of basic prefix:{}", index);
		if (decoded.isEmpty()) {
			throw new JargonException("user and password not in credentials");

		}
		final String[] credentials = decoded.split(":");

		log.info("credentials:{}", credentials.toString());

		if (credentials.length != 2) {
			throw new JargonException("user and password not in credentials");
		}

		log.info("restConfiguration:{}", restConfiguration);

		AuthScheme authScheme = determineAuthSchemeFromConfig(restConfiguration);

		log.info("see if auth scheme is overrideen by the provided credentials");
		/*
		 * Ids can be prepended with STANDARD: or PAM:
		 */

		String userId = credentials[0];
		if (userId.startsWith(AuthScheme.STANDARD.toString())) {
			log.info("authScheme override to Standard");
			authScheme = AuthScheme.STANDARD;
			userId = userId.substring(AuthScheme.STANDARD.toString().length() + 1);
		} else if (userId.startsWith(AuthScheme.PAM.toString())) {
			log.info("authScheme override to PAM");
			authScheme = AuthScheme.PAM;
			userId = userId.substring(AuthScheme.PAM.toString().length() + 1);
		}

		log.debug("userId:{}", userId);

		return IRODSAccount.instance(restConfiguration.getIrodsHost(), restConfiguration.getIrodsPort(), userId,
				credentials[1], "", restConfiguration.getIrodsZone(), restConfiguration.getDefaultStorageResource(),
				authScheme);

	}

	private static AuthScheme determineAuthSchemeFromConfig(final DosConfigInterface restConfiguration) {
		return restConfiguration.getAuthScheme();
	}

	/**
	 * Create an <code>IRODSAccount</code> suitable for anonymous access.
	 *
	 * @param restConfiguration
	 * @return <code>IRODSAccount</code> suitable for anonymous access
	 * @throws JargonException
	 */
	public static IRODSAccount instanceForAnonymous(final DosConfigInterface restConfiguration) throws JargonException {

		if (restConfiguration == null) {
			throw new IllegalArgumentException("null restConfiguration");
		}

		return IRODSAccount.instance(restConfiguration.getIrodsHost(), restConfiguration.getIrodsPort(),
				IRODSAccount.PUBLIC_USERNAME, "anonymous", "", restConfiguration.getIrodsZone(),
				restConfiguration.getDefaultStorageResource());
	}

	/**
	 * Return boilerplate http client for testing that uses basic auth
	 * 
	 * @param irodsAccount
	 * @param testingProperties
	 * @return
	 * @throws TestConfigurationException
	 * @throws TestingUtilsException
	 */
	public static DefaultHttpClientAndContext httpClientSetup(final IRODSAccount irodsAccount,
			final Properties testingProperties) throws TestConfigurationException {

		if (irodsAccount == null) {
			throw new IllegalArgumentException("null irodsAccount");
		}

		if (testingProperties == null) {
			throw new IllegalArgumentException("null testingProperties");
		}

		TestingPropertiesHelper testingPropertiesHelper = new TestingPropertiesHelper();
		HttpHost targetHost = new HttpHost("localhost", testingPropertiesHelper.getPropertyValueAsInt(testingProperties,
				RestTestingProperties.REST_PORT_PROPERTY), "http");

		DefaultHttpClient httpclient = new DefaultHttpClient();
		log.info("UserName={} password={}", irodsAccount.getUserName(), irodsAccount.getPassword());
		httpclient.getCredentialsProvider().setCredentials(
				new AuthScope(targetHost.getHostName(), targetHost.getPort()),
				new UsernamePasswordCredentials(irodsAccount.getUserName(), irodsAccount.getPassword()));
		// Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();
		// Generate BASIC scheme object and add it to the local
		// auth cache
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(targetHost, basicAuth);

		// Add AuthCache to the execution context
		BasicHttpContext localcontext = new BasicHttpContext();
		localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
		DefaultHttpClientAndContext clientAndContext = new DefaultHttpClientAndContext();
		clientAndContext.setHost(targetHost.getHostName());
		clientAndContext.setHttpClient(httpclient);
		clientAndContext.setHttpContext(localcontext);
		return clientAndContext;
	}
}
