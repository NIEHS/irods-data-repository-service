/**
 * 
 */
package org.irods.jargon.ga4gh.dos.bundlemgmnt.utils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.irods.jargon.core.connection.AuthScheme;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.exception.JargonRuntimeException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.ga4gh.dos.bundle.DosServiceFactory;
import org.irods.jargon.ga4gh.dos.bundle.impl.ExplodedDosBundleManagementServiceImpl;
import org.irods.jargon.ga4gh.dos.bundle.impl.ExplodedDosServiceFactoryImpl;
import org.irods.jargon.ga4gh.dos.bundlemgmnt.DosBundleManagementService;
import org.irods.jargon.ga4gh.dos.configuration.DosConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Supports generation of bundles as a command line utility in support of integration testing
 * @author conwaymc
 *
 */
public class BundleIntegrationTestHelper {
	
	private static final Logger log = LoggerFactory.getLogger(BundleIntegrationTestHelper.class);


	/**
	 * 
	 */
	public BundleIntegrationTestHelper() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		log.info("main()");
		// create the parser
	    CommandLineParser parser = new DefaultParser();
	    
	    Options options = new Options();
	    
	    
	    options.addOption(Option.builder("host").required()
                .argName("host")
                .hasArg()
                .desc("irods host")
                .build());
	    
	    options.addOption(Option.builder("port").required()
                .argName("port")
                .hasArg()
                .desc("irods port")
                .build());
	    
	    options.addOption(Option.builder("zone").required()
                .argName("zone")
                .hasArg()
                .desc("irods zone")
                .build());
	    
	    options.addOption(Option.builder("user").required()
                .argName("user")
                .hasArg()
                .desc("irods user name")
                .build());
	    
	    options.addOption(Option.builder("password").required()
                .argName("password")
                .hasArg()
                .desc("irods password")
                .build());
	    
	    options.addOption(Option.builder("path").required()
                .argName("path")
                .hasArg()
                .desc("path to irods bundle to be created")
                .build());
	    
	    options.addOption(Option.builder("filePrefix").required()
                .argName("filePrefix")
                .hasArg()
                .desc("prefix for bundle files to be created")
                .build());
	    
	    options.addOption(Option.builder("fileCount").required()
                .argName("fileCount")
                .hasArg()
                .desc("number of files in the bundle")
                .build());
	    
	    options.addOption(Option.builder("fileSize").required()
                .argName("fileSize")
                .hasArg()
                .desc("number of files in the bundle")
                .build());
	    
	    
	    try {
	        // parse the command line arguments
	        CommandLine line = parser.parse(options, args);
		        
	        IRODSAccount irodsAccount = IRODSAccount.instance(line.getOptionValue("host"), Integer.valueOf(line.getOptionValue("port")), 
	        		line.getOptionValue("user"), line.getOptionValue("password"), "", line.getOptionValue("zone"), "");
	        IRODSFileSystem irodsFileSystem = IRODSFileSystem.instance();
	        
	        log.info("irodsAccount:{}", irodsAccount);
	        
	        BundleUtils bundleUtils = new BundleUtils(irodsFileSystem.getIRODSAccessObjectFactory(), irodsAccount);
	        String bundlePathBuilt = bundleUtils.createTestBundle(line.getOptionValue("path"), 
	        		Integer.valueOf(line.getOptionValue("fileCount")), Integer.valueOf(line.getOptionValue("fileSize")), line.getOptionValue("filePrefix"));
	        log.info("built test bundle at path:{}", bundlePathBuilt);
	        
	        DosConfiguration dosConfiguration = new DosConfiguration();
	        dosConfiguration.setAuthScheme(AuthScheme.STANDARD.getTextValue());
	        dosConfiguration.setIrodsHost(irodsAccount.getHost());
	        dosConfiguration.setIrodsZone(irodsAccount.getZone());
	        dosConfiguration.setPort(irodsAccount.getPort());
	        
	        DosServiceFactory dosServiceFactory = new ExplodedDosServiceFactoryImpl();
	        dosServiceFactory.setDataTypeResolutionServiceFactory(null);
	        dosServiceFactory.setDosConfiguration(dosConfiguration);
	        dosServiceFactory.setIrodsAccessObjectFactory(irodsFileSystem.getIRODSAccessObjectFactory());
	        log.info("create a bundle at this location");
	        
	        DosBundleManagementService dosBundleManagementService = new ExplodedDosBundleManagementServiceImpl(irodsFileSystem.getIRODSAccessObjectFactory(), irodsAccount, dosServiceFactory, dosConfiguration);	        
	        String guid = dosBundleManagementService.createDataBundle(bundlePathBuilt);
	        System.out.println("GUID:"+ guid);
	        
	        
	    }
	    catch (ParseException exp) {
	        System.err.println("Parsing failed.  Reason: " + exp.getMessage());
	        throw new JargonRuntimeException("error in command line:", exp);
	    } catch (NumberFormatException exp) {
	        System.err.println("Parsing failed.  Reason: " + exp.getMessage());
	        throw new JargonRuntimeException("error in command line:", exp);
		} catch (JargonException exp) {
			// TODO Auto-generated catch block
	        throw new JargonRuntimeException("error in command line:", exp);
		}

	}

}
