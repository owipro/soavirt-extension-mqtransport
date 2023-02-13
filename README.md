# soavirt-extension-mqtransport
Custom MQTransport for Parasoft SOAtest

## How to Build
 * Prerequisites
    * Java 8+
    * Maven 3+
 * Command line
    * cd [custom extension folder]
    * mvn clean install
 * Eclipse/SOAtest/Virtualize
    * File > Import > Maven > Existing Maven Projects > Next
    * Select the custom extension folder and click Finish

## Custom Transports
 * MQTransport

Tranport takes the following parameters:
MQ Details:
Queue Manager
Put Queue
Get Queue
