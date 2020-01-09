# PMB ELN Bridge

[![Build Status](https://travis-ci.org/sanger/eln_pmb_bridge.svg)](https://travis-ci.org/sanger/eln_pmb_bridge)
[![Maintainability](https://api.codeclimate.com/v1/badges/e8292513bf0c61d22acf/maintainability)](https://codeclimate.com/github/sanger/eln_pmb_bridge/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/e8292513bf0c61d22acf/test_coverage)](https://codeclimate.com/github/sanger/eln_pmb_bridge/test_coverage)

Description
---

A polling service for printing labels from IDBS-ELN via Print My Barcode (PMB).

Usage
---

- Property folders contain config information for the ELN PMB Bridge, Printers and Mailing, and various setup directories.
- A file (with _TEMP extension) is dropped by IDBS-ELN into the poll folder.
- IDBS-ELN populate the content of this file, then rename the file when completed (removing the TEMP extension.)
- The Watch Service pulls the newly named file, and create a print job request.
- This request is then sent to a print service, which prints the labels.

Running
---
Execute the jar in the console:

    java -jar target/eln_pmb_bridge-1.0-jar-with-dependencies.jar

Pass in a command line argument into Main such as `env=devel`

Or when running it from IntelliJ:
`ctrl + alt + r > Edit Configurations > Program Arguments`


Deployment
---

* ELN PMB WIP is currently deployed at `web-cgap-idbstest-01:sccp/eln_pmb_bridge` (used locally for test)
* ELN PMB UAT is currently deployed at `web-cgap-idbstest-02:sccp/eln_pmb_bridge` (used by ELN for test)
* ELN PMB PROD is currently deployed at `web-cgap-idbsprod-02:sccp/eln_pmb_bridge` (used by ELN for prod)

Build the jar using the jar-with-dependencies in pom.xml:

    Maven > Lifecycle > clean
    Maven > Lifecycle > package

Secure copy the jar from local to the server:

    scp target/eln_pmb_bridge-1.0-jar-with-dependencies.jar [host]:/sccp/eln_pmb_bridge/`

Secure copy the jre (if it doesn't exist) from one server to another
    scp -r /sccp/jre/ [host]:/sccp`

Change user to sccp

    sudo -u sccp bash

Run the application (this will only create the folders, then error out):

    /sccp/jre/jre1.8.0_131/bin/java -jar eln_pmb_bridge-1.0-jar-with-dependencies.jar

Change permission on folders to 777 (poll/prop/error/archive)

    chmod 777 archive_folder/ error_folder/ properties_folder/ poll_folder/

Copy over the property files

    scp -r properties_folder/devel [host]:/sccp/eln_pmb_bridge/properties_folder/devel

(or `prod` in place of `devel` when deploying to production)

Copy over the java control file

    scp -r java_control.sh [host]:/sccp/eln_pmb_bridge

Make sure all files are owned by the sccp user.  
`chown` is not available to normal users, so the way to get the files under the correct ownership is:
  
* become sccp: `sudo -u sccp bash`
* rename the old file: `mv myfile myfile_old`
* create a copy (owned by sccp): `cp myfile_old myfile`
* delete the old file: `rm myfile_old`
   
You can do this on a whole directory at once, if you use the `-r` flag for `cp` and `rm`. (Be careful with `rm`.)

Change permission on java control file to 755 (only executable by the owner)

    chmod 755 java_control.sh


Start the application using `java_control`.

The `java_control.sh` script allows you to start, stop and restart the java process:

    ./java_control.sh stop env=abc
    ./java_control.sh start env=abc
    ./java_control.sh restart env=abc

The `env=abc` part can be omitted if the `java_control.sh` script includes:

    ENV=${2:-env=abc}

using the appropriate environment for that server in place of `abc`.

Different environments are:

- test
- devel
- wip
- uat
- prod

Test
---

Secure copy to drop a file into the polling folder on the server with the _TEMP extension:

    scp file.txt web-cgap-idbstest-01:/sccp/eln_pmb_bridge/poll_folder/file.txt_TEMP


On the server, rename the file to remove the _TEMP extension:

    mv poll_folder/file.txt_TEMP poll_folder/file.txt


Example File
---

[An example IDBS-ELN file](https://github.com/sanger/eln_pmb_bridge/blob/master/test_examples/correct_request.txt)

Request
---

Format of the request built in PMBClient buildJson():

	{
	    "data":{
	        "attributes":{
	            "printer_name":"",
	            "label_template_id":,
	            "labels":{
	                "body":[
	                    { "label_1":
	                        {
	                            "date":"",
	                            "media_type":"",
	                            "cell_line":"",
	                            "passage_number":"",
	                            "user":""
	                        }
	                    }
	                ]
	            }
	        }
	    }
	}
