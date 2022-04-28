# soavirt-extension-examples
Custom Extension Examples for Parasoft SOAtest and Virtualize

## How to Build
 * Prerequisites
    * Java 7+
    * Maven 3+
 * Command line
    * cd [custom extension folder]
    * mvn clean install

## Custom Transports
 * FileTransport is a file-based custom transport example.

 * SimpleHTTPTransport is a simple HTTP custom transport example.

## Custom Message Formats
 * SimpleMessage is a simple message example provided to demonstrate how to create Custom Message Formats in SOAtest. The message consists of key values joined by "=" and has the " " (space) for delimiter. For example: key1=value1 key2=value2 key3=value3 key4=value4.