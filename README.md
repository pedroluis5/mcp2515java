# MCP2515 JAVA Drive for Raspberry Pi 3
A java driver for read and write CAN messages with the MCP2515 and Raspberry Pi 3B.

This project work with raspberry pi 3B, and the "MCP2515 CAN Bus Module Board TJA1050 Receiver SPI"

See the Wiki above for more information about how to install

This Example automatically connect to MCP2515 and create two Timers the first one with 100ms that send a CAN message continuously, and the second with 1ms that continuously read new incoming messages and print on Netbeans Console.
The button on click send a message CAN.
