/**
 * 
 */
package org.irods.jargon.ga4gh.dos.bundlemgmnt;

import org.irods.jargon.core.exception.DataNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.ga4gh.dos.bundlemgmnt.exception.BundleNotFoundException;
import org.irods.jargon.ga4gh.dos.bundlemgmnt.exception.DuplicateBundleException;

/**
 * Interface to a service to create a GA4GH Data Bundle on an existing iRODS
 * collection. A bundle is marked with various metadata values, checksums are
 * created for the components of a bundle, etc.
 * 
 * @author Mike Conway - NIEHS
 *
 */
public interface DosBundleManagementService {

	/**
	 * Enum of bundle types, currently an 'exploded' in-place bundle type is
	 * supported, BDBAG or other formats are planned for the future.
	 * 
	 * @author Mike Conway - NIEHS
	 *
	 */
	public enum BundleType {
		EXPLODED
	}

	/**
	 * Create a data bundle based on the provided source root path. Based on the
	 * bundle type the bundle will be created based on the existing policy.
	 * 
	 * @param bundleRootAbsolutePath
	 *            {@code String} with the path to the parent root of the bundle
	 * 
	 * @return {@code String} with the identifier of the bundle (e.g. the GUID)
	 * @throws DataNotFoundException
	 *             {@link
	 * @throws DuplicateBundleException
	 *             {@link DuplicateBundleException}
	 * @throws JargonException
	 *             {@link JargonException}
	 */
	String createDataBundle(final String bundleRootAbsolutePath)
			throws DataNotFoundException, DuplicateBundleException, JargonException;

	/**
	 * Delete a data bundle. Based on the bundle type this may delete an archive, or
	 * simply strip metadata from iRODS files and collections.
	 * 
	 * @param dataBundleId
	 *            {@code String} with a bundle identifier
	 * @throws BundleNotFoundException
	 *             {@link BundleNotFoundException}
	 * @throws JargonException
	 *             {@link JargonException}
	 */
	void deleteDataBundle(final String dataBundleId) throws BundleNotFoundException, JargonException;
}
