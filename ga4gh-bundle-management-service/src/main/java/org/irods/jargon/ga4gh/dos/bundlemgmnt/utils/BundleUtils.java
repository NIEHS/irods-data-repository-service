/**
 *
 */
package org.irods.jargon.ga4gh.dos.bundlemgmnt.utils;

import java.io.IOException;
import java.util.Random;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.exception.JargonRuntimeException;
import org.irods.jargon.core.packinstr.DataObjInp.OpenFlags;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.pub.io.IRODSFileOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for bundle management
 * @author conwaymc
 *
 */
public class BundleUtils {

	private final IRODSAccessObjectFactory irodsAccessObjectFactory;
	private final IRODSAccount irodsAccount;

	private static final Logger log = LoggerFactory.getLogger(BundleUtils.class);


	public IRODSAccessObjectFactory getIrodsAccessObjectFactory() {
		return irodsAccessObjectFactory;
	}

	public IRODSAccount getIrodsAccount() {
		return irodsAccount;
	}


	
	public BundleUtils(IRODSAccessObjectFactory irodsAccessObjectFactory, IRODSAccount irodsAccount) {

		this.irodsAccessObjectFactory = irodsAccessObjectFactory;
		this.irodsAccount = irodsAccount;

	}
	
	/**
	 * Create a test directory in iRODS that is tagged as a data bundel
	 * @param bundlePath {@code String} with the path in iRODS for the new bundle
	 * @param fileCount {@code int} with the number of files to generate
	 * @param fileSize {@code int}with the desired file size
	 * @param fileNamePrefix {@code String} with the file name prefix
	 * @return {@code String} with iRODS path where files are generated
	 * @throws JargonException
	 */
	public String createTestBundle(String bundlePath, int fileCount, int fileSize, String fileNamePrefix) throws JargonException {
		log.info("createTestBundle");
		
		if (bundlePath == null || bundlePath.isBlank()) {
			throw new IllegalArgumentException("null or empty bundle path");
		}
		
		if (fileCount <= 0) {
			throw new IllegalArgumentException("fileCount must be > 0");
		}
		
		if (fileSize <= 0) {
			throw new IllegalArgumentException("fileSize must be > 0");
		}
		
		if (fileNamePrefix == null || fileNamePrefix.isBlank()) {
			throw new IllegalArgumentException("fileNamePrefix is null or empty");
		}
		
		log.info("bundlePath:{}", bundlePath);
		log.info("fileCount:{}", fileCount);
		log.info("fileSize:{}", fileSize);
		log.info("fileNamePrefix:{}", fileNamePrefix);


		// verify that the dir is empty

		IRODSFile bundleDir;
		IRODSFileFactory irodsFileFactory = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount);

		try {

			if (bundlePath.startsWith("/")) {
				log.info("process as absolute path");
				bundleDir = irodsFileFactory.instanceIRODSFile(bundlePath);
			} else {
				throw new IllegalArgumentException("bundleDir must be an absolute path");
			}

			if (bundleDir.exists()) {
				log.error("file exists, cannot create a test directory");
				return "The directory exists, cannot create a test directory with that name";
			}
			
			// make the dir, then add a number of child files
			bundleDir.mkdirs();
			
			
			for (int i=0; i < fileCount; i++) {
				String fileName = fileNamePrefix + String.valueOf(i);
				
				IRODSFile irodsFile = irodsFileFactory.instanceIRODSFile(bundleDir.getAbsolutePath(), fileName);
				IRODSFileOutputStream irodsFileOutputStream = irodsFileFactory.instanceIRODSFileOutputStream(irodsFile,
						OpenFlags.READ_WRITE, true);

				byte[] myBytesArray = this.randomString(fileSize).getBytes();
				irodsFileOutputStream.write(myBytesArray);
				irodsFile.close();	
				
			}
			
			log.info("wrote bundle files");
			
			return bundleDir.getAbsolutePath();
		} catch (JargonException | IOException e) {
			log.error("exception getting file:{}", e);
			throw new JargonException("error generating ", e);
		} finally {
			irodsAccessObjectFactory.closeSessionAndEatExceptions();
		}
		
		
		
	}
	
	/**
	 * Generate a random string of given length
	 * @param stringLength {@code int} with length of string to generate
	 * @return {@code String} that is a random String
	 */
	
	public String randomString(int stringLength) {
	    int leftLimit = 48; // numeral '0'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = stringLength;
	    Random random = new Random();

	    String generatedString = random.ints(leftLimit, rightLimit + 1)
	      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
	      .limit(targetStringLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();
	    
	    return generatedString;
	}

}
