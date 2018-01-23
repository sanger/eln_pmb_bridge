A polling service for printing labels from ELN to PMB.

Usage:

A polling folder is specified, along with other property folders
A file, in the expected format, is dropped into the polling folder
A print job request is sent to PMB, printing the created labels

Deployment:

ELN PMB is current deployed at web-cgap-idbstest-01:sccp/eln_pmb_bridge

Build the jar using the jar-with-dependencies in pom.xml:
    Maven > plugins > assembly > assembly:assembly

Execute the jar in the console:
    java -jar target/eln_pmb_bridge-1.0-jar-with-dependencies.jar

Secure copy the jar from local to the server:
    scp target/eln_pmb_bridge-1.0-jar-with-dependencies.jar web-cgap-idbstest-01:/sccp/eln_pmb_bridge/

Run the application:
    /sccp/jre/jre1.8.0_131/bin/java -jar eln_pmb_bridge-1.0-jar-with-dependencies.jar

Secure copy to drop a file into the polling folder:
    scp file.txt web-cgap-idbstest-01:/sccp/eln_pmb_bridge/poll_folder
