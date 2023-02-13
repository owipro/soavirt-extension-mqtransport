# soavirt-extension-mqtransport
Custom MQTransport for Parasoft SOAtest

## How to Build
 * Prerequisites
    * Java 8+
    * Maven 3+
 * Command line
    * Clone folder
    * cd [custom extension folder]
    * mvn clean install
 * Eclipse/SOAtest/Virtualize
    * File > Import > Maven > Existing Maven Projects > Next
    * Select the custom extension folder and click Finish
    * Add project classes: SOATest > Window > Preferences > Parasoft > System Preferences > Add Java Project

## Custom Transports
 * MQTransport

Tranport takes the following parameters:
MQ Details:
Queue Manager
Put Queue
Get Queue
