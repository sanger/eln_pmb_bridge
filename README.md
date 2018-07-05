# PMB ELN Bridge

[![Build Status](https://travis-ci.org/sanger/eln_pmb_bridge.svg?branch=master)](https://travis-ci.org/sanger/eln_pmb_bridge)
[![Maintainability](https://api.codeclimate.com/v1/badges/e8292513bf0c61d22acf/maintainability)](https://codeclimate.com/github/sanger/eln_pmb_bridge/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/e8292513bf0c61d22acf/test_coverage)](https://codeclimate.com/github/sanger/eln_pmb_bridge/test_coverage)

Description
---

A polling service for printing labels from IDBS-ELN via Print My Barcode (PMB).

Usage
---

- Property folders contain config information for the ELN PMB Bridge, Printers and Mailing, and various setup directories.
- A file, in the expected format is dropped by IDBS-ELN into the poll folder.
- The Watch Service pulls the new file and create a print job request.
- This request is then sent to PMB, which prints the labels.

Running
---
Execute the jar in the console:

    java -jar target/eln_pmb_bridge-1.0-jar-with-dependencies.jar

Deployment
---

ELN PMB test is currently deployed at web-cgap-idbstest-01:sccp/eln_pmb_bridge

- Build the jar using the jar-with-dependencies in pom.xml:

    Maven > Lifecycle > clean
    Maven > Lifecycle > package

- Secure copy the jar from local to the server:

    scp target/eln_pmb_bridge-1.0-jar-with-dependencies.jar web-cgap-idbstest-01:/sccp/eln_pmb_bridge/

- Run the application:

The java_control.sh script allows you to start, stop and restard the java process automatically

    ./java_control.sh stop
    ./java_control.sh start

or

    ./java_control.sh restart


Test
---

Secure copy to drop a file into the polling folder on the server:


    scp file.txt web-cgap-idbstest-01:/sccp/eln_pmb_bridge/poll_folder


Example File
---

[An example IDBS-ELN file](https://github.com/sanger/eln_pmb_bridge/blob/refactor/test_examples/correct_request.txt)
