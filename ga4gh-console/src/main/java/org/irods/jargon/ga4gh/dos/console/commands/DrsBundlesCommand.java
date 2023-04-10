/**
 * 
 */
package org.irods.jargon.ga4gh.dos.console.commands;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.hibernate.validator.constraints.NotEmpty;
import org.irods.jargon.core.connection.IRODSServerProperties;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.exception.JargonRuntimeException;
import org.irods.jargon.core.packinstr.DataObjInp.OpenFlags;
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileOutputStream;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry.ObjectType;
import org.irods.jargon.ga4gh.dos.bundle.internalmodel.BundleInfoAndPath;
import org.irods.jargon.ga4gh.dos.bundlemgmnt.DosBundleManagementService;
import org.irods.jargon.ga4gh.dos.bundlemgmnt.exception.BundleNotFoundException;
import org.irods.jargon.ga4gh.dos.bundlemgmnt.exception.DuplicateBundleException;
import org.irods.jargon.ga4gh.dos.bundlemgmnt.utils.BundleUtils;
import org.irods.jargon.ga4gh.dos.console.context.DrsConsoleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class DrsBundlesCommand {

	@Autowired
	private DrsConsoleContext drsConsoleContext;

	private static final Logger log = LoggerFactory.getLogger(DrsBundlesCommand.class);

	/**
	 * 
	 */
	public DrsBundlesCommand() {
	}

	@ShellMethod("Initialize connection")
	public String iinit(@NotEmpty String host, @ShellOption(defaultValue = "1247") int port, @NotEmpty String zone,
			@NotEmpty String user, @NotEmpty String password, @ShellOption(defaultValue = "STANDARD") String auth) {
		log.info("connect()");

		try {
			IRODSServerProperties props = drsConsoleContext.init(host, zone, port, user, password, auth);
			log.debug("IRODSServerProperties:{}", props);
			return "Connected to " + zone + " as user: " + user;
		} catch (JargonException e) {
			log.error("error connecting", e);
			throw new JargonRuntimeException("error connecting", e);
		} finally {
			drsConsoleContext.getIrodsAccessObjectFactory().closeSessionAndEatExceptions();
		}
	}

	@ShellMethod("List all DRS bundles")
	public String ilistdrsb() {
		log.info("ilistdrsb");

		try {
			DosBundleManagementService dosBundleManagmentService = drsConsoleContext.getDosServiceFactory()
					.instanceDosBundleManagementService(drsConsoleContext.getIrodsAccount());
			List<BundleInfoAndPath> bundleInfoAndPathList = dosBundleManagmentService.listAllBundles();
			StringBuilder sb = new StringBuilder();
			char cr = '\n';
			char tab = '\t';

			for (BundleInfoAndPath bundleInfo : bundleInfoAndPathList) {
				sb.append(bundleInfo.getId());
				sb.append(tab);
				sb.append(bundleInfo.getIrodsPath());
				sb.append(cr);
			}

			return sb.toString();
		} catch (JargonException e) {
			log.error("exception creating bundle:{}", e);
			throw new JargonRuntimeException("exception creating bundle", e);
		} finally {
			drsConsoleContext.getIrodsAccessObjectFactory().closeSessionAndEatExceptions();
		}
	}

	public Availability ilistdrsbAvailability() {
		return drsConsoleContext.isInitd() ? Availability.available()
				: Availability.unavailable("you are not connected, please do the iinit command");
	}

	@ShellMethod("Remove a DRS bundle by directory path or GUID")
	public String irmdrsb(@ShellOption(defaultValue = "") String path, @ShellOption(defaultValue = "") String guid) {
		log.info("irmdrsb");

		if (path == null) {
			throw new IllegalArgumentException("null path");
		}

		if (guid == null) {
			throw new IllegalArgumentException("null guid");
		}

		log.info("path:{}", path);
		log.info("guid:{}", guid);

		String wd = drsConsoleContext.getCwd();
		log.info("wd:{}", wd);

		String targetGuid = "";

		try {

			DosBundleManagementService dosBundleManagmentService = drsConsoleContext.getDosServiceFactory()
					.instanceDosBundleManagementService(drsConsoleContext.getIrodsAccount());

			if (!guid.isEmpty()) {
				log.info("deleting by guid:{}", guid);
				targetGuid = guid;
			} else if (!path.isEmpty()) {
				log.info("deleting by path:{}", path);
				targetGuid = dosBundleManagmentService.irodsPathToBundleId(path);
			} else {
				return "No --path or --guid specified";
			}

			dosBundleManagmentService.deleteDataBundle(targetGuid);
			return "Removed data bundle:" + targetGuid;

		} catch (BundleNotFoundException bnf) {
			log.warn("bundle not found, will ignore");
			return "Bundle was not found, ignoring";
		} catch (JargonException e) {
			log.error("exception creating bundle:{}", e);
			throw new JargonRuntimeException("exception creating bundle", e);
		} finally {
			drsConsoleContext.getIrodsAccessObjectFactory().closeSessionAndEatExceptions();
		}
	}

	public Availability irmdrsbAvailability() {
		return drsConsoleContext.isInitd() ? Availability.available()
				: Availability.unavailable("you are not connected, please do the iinit command");
	}

	@ShellMethod("Make a DRS bundle at current directory")
	public String imakedrsb() {
		log.info("imakedrsb");
		String wd = drsConsoleContext.getCwd();
		log.info("wd:{}", wd);
		try {
			DosBundleManagementService dosBundleManagmentService = drsConsoleContext.getDosServiceFactory()
					.instanceDosBundleManagementService(drsConsoleContext.getIrodsAccount());
			String guid = dosBundleManagmentService.createDataBundle(wd);
			log.info("created bundle with guid:{}", guid);
			return "created bundle with GUID:" + guid;
		} catch (DuplicateBundleException e) {
			log.warn("duplicate bundle in:{}", wd, e);
			return "Duplicate bundle found in " + wd;
		} catch (JargonException e) {
			log.error("exception creating bundle:{}", e);
			throw new JargonRuntimeException("exception creating bundle", e);
		} finally {
			drsConsoleContext.getIrodsAccessObjectFactory().closeSessionAndEatExceptions();
		}
	}

	public Availability imakedrsbAvailability() {
		return drsConsoleContext.isInitd() ? Availability.available()
				: Availability.unavailable("you are not connected, please do the iinit command");
	}

	@ShellMethod("Print working directory in iRODS")
	public String ipwd() {
		log.info("ipwd");
		return drsConsoleContext.getCwd();
	}

	public Availability ipwdAvailability() {
		return drsConsoleContext.isInitd() ? Availability.available()
				: Availability.unavailable("you are not connected, please do the iinit command");
	}

	@ShellMethod("List directory contents")
	public String ils() {
		log.info("ils");
		String wd = drsConsoleContext.getCwd();
		try {
			CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = drsConsoleContext
					.getIrodsAccessObjectFactory()
					.getCollectionAndDataObjectListAndSearchAO(drsConsoleContext.getIrodsAccount());
			List<CollectionAndDataObjectListingEntry> entries = collectionAndDataObjectListAndSearchAO
					.listDataObjectsAndCollectionsUnderPath(wd);
			StringBuilder sb = new StringBuilder();
			char cr = '\n';
			char tab = '\t';
			sb.append(wd);
			sb.append(cr);
			for (CollectionAndDataObjectListingEntry entry : entries) {
				sb.append(tab);
				sb.append(entry.getPathOrName());
				sb.append(tab);
				sb.append(entry.getObjectType());
				sb.append(tab);
				if (entry.getObjectType() == ObjectType.DATA_OBJECT) {
					sb.append(entry.getDataSize());
				}
				sb.append(cr);
			}

			return sb.toString();

		} catch (JargonException e) {
			log.error("exception getting file:{}", e);
			throw new JargonRuntimeException("error getting file", e);
		} finally {
			drsConsoleContext.getIrodsAccessObjectFactory().closeSessionAndEatExceptions();
		}
	}

	public Availability ilsAvailability() {
		return drsConsoleContext.isInitd() ? Availability.available()
				: Availability.unavailable("you are not connected, please do the iinit command");
	}

	@ShellMethod("Change working directory in iRODS")
	public String icd(String directory) {
		log.info("icd");
		String wd = drsConsoleContext.getCwd();
		try {
			IRODSFile wdFile = drsConsoleContext.getIrodsFileFactory().instanceIRODSFile(wd, directory);
			if (wdFile.exists()) {
				log.debug("file exists");
				drsConsoleContext.setCwd(wdFile.getCanonicalPath());
				return drsConsoleContext.getCwd();
			} else {
				return "No file found at path";
			}
		} catch (JargonException | IOException e) {
			log.error("exception getting file:{}", e);
			throw new JargonRuntimeException("error getting file", e);
		} finally {
			drsConsoleContext.getIrodsAccessObjectFactory().closeSessionAndEatExceptions();
		}
	}

	@ShellMethod("Create test bundle")
	public String maketestbundle(@ShellOption("--directory") String directory,
			@ShellOption(value="--files", defaultValue="10") String numberOfFiles, @ShellOption(value = "--filePrefix", defaultValue="file")String filePrefix) {
		log.info("maketestbundle");
		int fileLength = 100;
		String wd = drsConsoleContext.getCwd();

		if (directory == null || directory.isBlank()) {
			return "please enter a directory for the test bundle";
		}

		int nbrFiles = Integer.parseInt(numberOfFiles);

		log.info("directory:{}", directory);
		log.info("numberOfFiles:{}", numberOfFiles);

		BundleUtils bundleUtils = new BundleUtils(this.drsConsoleContext.getIrodsAccessObjectFactory(), this.drsConsoleContext.getIrodsAccount());
		try {
			return bundleUtils.createTestBundle(directory, Integer.parseInt(numberOfFiles),  Integer.parseInt(numberOfFiles), filePrefix);
		} catch (NumberFormatException | JargonException e) {
			log.error("exception creating test bundle:{}", e);
			throw new JargonRuntimeException("error creating test bundle", e);
		}
	}
	
	
	   /* Random random = new Random();

	    String generatedString = random.ints(leftLimit, rightLimit + 1)
	      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
	      .limit(targetStringLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();

	    Syst*/

	public Availability icdAvailability() {
		return drsConsoleContext.isInitd() ? Availability.available()
				: Availability.unavailable("you are not connected, please do the iinit command");
	}

	public Availability listBundlesAvailability() {
		return drsConsoleContext.isInitd() ? Availability.available()
				: Availability.unavailable("you are not connected, please do the iinit command");
	}

	public DrsConsoleContext getDrsConsoleContext() {
		return drsConsoleContext;
	}

	public void setDrsConsoleContext(DrsConsoleContext drsConsoleContext) {
		this.drsConsoleContext = drsConsoleContext;
	}

}
