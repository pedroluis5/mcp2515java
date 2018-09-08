/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainPack;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import java.io.IOException;

/**
 *
 * @author pedronascimento
 */
public class MCP2515 {

    // SPI device
    private static SpiDevice spi = null;
    private boolean canOpened = false;
    
    private GpioController gpio = GpioFactory.getInstance();
    // provision gpio pin #02 as an input pin with its internal pull down resistor enabled
    public GpioPinDigitalInput mcp2515Interrupt = gpio.provisionDigitalInputPin(RaspiPin.GPIO_06); 

    int txprio = 3;

    public class CAN_Message 
    {
        int id; 			// identifier (11 or 29 bit)
        boolean rtr;
        boolean extended;
        int dlc;                  // data length code
        byte data[] = new byte[8];		// payload data
    };

    public boolean init() 
    {
        boolean result = false;

        try {
            // create SPI object instance for SPI for communication
            spi = SpiFactory.getInstance(SpiChannel.CS0,
                    8000000, // default spi speed 8 MHz
                    SpiDevice.DEFAULT_SPI_MODE.MODE_0); // default spi mode 0
            System.out.println("Device SPI opened!");

            mcp2515_reset();

            mcp2515_write_register(MCP2515_defs.MCP2515_REG_CANCTRL, (byte) 0x80); // set config mode

            byte flags = mcp2515_read_register(MCP2515_defs.MCP2515_REG_CANCTRL);
            if (flags == (byte) 0x80) {
                System.out.println("MCP2515 Present");
                // configure filter
                mcp2515_write_register(MCP2515_defs.MCP2515_REG_RXB0CTRL, (byte) 0x04); // use filter for standard and extended frames
                mcp2515_write_register(MCP2515_defs.MCP2515_REG_RXB1CTRL, (byte) 0x00); // use filter for standard and extended frames

                // initialize filter mask
                mcp2515_write_register(MCP2515_defs.MCP2515_REG_RXM0SIDH, (byte) 0x00);
                mcp2515_write_register(MCP2515_defs.MCP2515_REG_RXM0SIDL, (byte) 0x00);
                mcp2515_write_register(MCP2515_defs.MCP2515_REG_RXM0EID8, (byte) 0x00);
                mcp2515_write_register(MCP2515_defs.MCP2515_REG_RXM0EID0, (byte) 0x00);
                mcp2515_write_register(MCP2515_defs.MCP2515_REG_RXM1SIDH, (byte) 0x00);
                mcp2515_write_register(MCP2515_defs.MCP2515_REG_RXM1SIDL, (byte) 0x00);
                mcp2515_write_register(MCP2515_defs.MCP2515_REG_RXM1EID8, (byte) 0x00);
                mcp2515_write_register(MCP2515_defs.MCP2515_REG_RXM1EID0, (byte) 0x00);

                mcp2515_write_register(MCP2515_defs.MCP2515_REG_RXF0SIDL, (byte) 0x00);
                mcp2515_write_register(MCP2515_defs.MCP2515_REG_RXF1SIDL, (byte) 0x08);
                mcp2515_write_register(MCP2515_defs.MCP2515_REG_RXF2SIDL, (byte) 0x00);
                mcp2515_write_register(MCP2515_defs.MCP2515_REG_RXF3SIDL, (byte) 0x08);

                mcp2515_write_register(MCP2515_defs.MCP2515_REG_CANINTE, (byte)0x03); // RX interrupt
                //mcp2515_setInterrupt();
                
                mcp2515_set_bittiming(MCP2515_defs.MCP2515_TIMINGS_1M); //1Mbit Baudrate

                mcp2515_bit_modify(MCP2515_defs.MCP2515_REG_CANCTRL, (byte) 0xE0, (byte) 0x00); // set normal operating mode
                mcp2515_write_register(MCP2515_defs.MCP2515_REG_EFLG, (byte) 0x00);	//limpa flags de erro

                result = true;
                canOpened = true;

            } else {
                System.err.println("MCP2515 Not Present!");
            }

        } catch (IOException ex) {
            System.err.println(ex);
        }

        return result;
    }

    /**
     * \brief Write to given register
     *
     * \param address Register address \param data Value to write to given
     * register
     */
    void mcp2515_reset() 
    {
        try {
            byte buffer[] = new byte[]{
                (byte) 0xC0
            };
            spi.write(buffer);

            Thread.sleep(100);
        } catch (Exception e) {
            System.err.println("Erro on Reset MCP2515");
        }

    }

    /**
     * \brief Write to given register
     *
     * \param address Register address \param data Value to write to given
     * register
     */
    void mcp2515_write_register(byte address, byte data) throws IOException
    {
        byte buffer[] = new byte[]{
            MCP2515_defs.MCP2515_CMD_WRITE,
            address,
            data
        };
        spi.write(buffer);
    }

    /**
     * \brief Read from given register
     *
     * \param address Register address \return register value
     */
    byte mcp2515_read_register(byte address) throws IOException 
    {
        byte buffer[] = new byte[]{
            MCP2515_defs.MCP2515_CMD_READ,
            address,
            (byte) 0xFF
        };
        byte[] result = spi.write(buffer);

        return result[2];
    }

    /**
     * \brief Modify bit of given register
     *
     * \param address Register address \param mask Mask of bits to set \param
     * data Values to set
     *
     * This function works only on a few registers. Please check the datasheet!
     */
    void mcp2515_bit_modify(byte address, byte mask, byte data) throws IOException 
    {
        byte buffer[] = new byte[]{
            MCP2515_defs.MCP2515_CMD_BIT_MODIFY,
            address,
            mask,
            data
        };
        spi.write(buffer);
    }

    /**
     * \brief Set bit timing registers
     *
     * \param cnf1 Configuration register 1 \param cnf2 Configuration register 2
     * \param cnf3 Configuration register 3
     *
     * This function has only affect if mcp2515 is in configuration mode
     */
    void mcp2515_set_bittiming(byte[] cnfs) throws IOException 
    {
        mcp2515_write_register(MCP2515_defs.MCP2515_REG_CNF1, cnfs[0]);
        mcp2515_write_register(MCP2515_defs.MCP2515_REG_CNF2, cnfs[1]);
        mcp2515_write_register(MCP2515_defs.MCP2515_REG_CNF3, cnfs[2]);
    }

    /**
     * \brief Read status byte of MCP2515
     *
     * \return status byte of MCP2515
     */
    byte mcp2515_read_status() throws IOException 
    {
        byte buffer[] = new byte[]{
            MCP2515_defs.MCP2515_CMD_READ_STATUS,
            (byte) 0xFF
        };
        byte[] result = spi.write(buffer);

        return result[1];
    }

    /**
     * \brief Read RX status byte of MCP2515
     *
     * \return RX status byte of MCP2515
     */
    byte mcp2515_rx_status() throws IOException 
    {
        byte buffer[] = new byte[]{
            MCP2515_defs.MCP2515_CMD_RX_STATUS,
            (byte) 0xFF
        };
        byte[] result = spi.write(buffer);

        return result[1];
    }

    /**
     * \brief Send given CAN message
     *
     * \ p_canmsg Pointer to can message to send \return 1 if transmitted
     * successfully to MCP2515 transmit buffer, 0 on error (= no free buffer
     * available)
     */
    public boolean send_message(CAN_Message p_canmsg) 
    {
        if (canOpened == true) {
            
            try {
                byte status = mcp2515_read_status();
                byte address;
                byte ctrlreg;
                int length;
                byte txBuffer[] = new byte[20];

                // check length
                length = p_canmsg.dlc;
                if (length > 8) {
                    length = 8;
                }

                // do some priority fiddling to get fifo behavior
                switch (status & (byte) 0x54) {
                    case 0x00:
                        // all three buffers free
                        ctrlreg = MCP2515_defs.MCP2515_REG_TXB2CTR;
                        address = (byte) 0x04;
                        txprio = 3;
                        break;

                    case 0x40:
                    case 0x44:
                        ctrlreg = MCP2515_defs.MCP2515_REG_TXB1CTR;
                        address = (byte) 0x02;
                        break;

                    case 0x10:
                    case 0x50:
                        ctrlreg = MCP2515_defs.MCP2515_REG_TXB0CTR;
                        address = (byte) 0x00;
                        break;

                    case 0x04:
                    case 0x14:
                        ctrlreg = MCP2515_defs.MCP2515_REG_TXB2CTR;
                        address = (byte) 0x04;

                        if (txprio == 0) {
                            // set priority of buffer 1 and buffer 0 to highest
                            mcp2515_bit_modify(MCP2515_defs.MCP2515_REG_TXB1CTR, (byte) 0x03, (byte) 0x03);
                            mcp2515_bit_modify(MCP2515_defs.MCP2515_REG_TXB0CTR, (byte) 0x03, (byte) 0x03);
                            txprio = 2;
                        } else {
                            txprio--;
                        }
                        break;

                    default:
                        // no free transmit buffer
                        return false;
                }

                // pull SS to low level
                int index = 0;
                txBuffer[index++] = (byte) (MCP2515_defs.MCP2515_CMD_LOAD_TX | address);

                if (p_canmsg.extended) {
                    txBuffer[index++] = (byte) ((p_canmsg.id >> 21));
                    txBuffer[index++] = (byte) ((((p_canmsg.id >> 13) & (byte) 0xe0) | ((p_canmsg.id >> 16) & (byte) 0x03) | (byte) 0x08));
                    txBuffer[index++] = (byte) ((p_canmsg.id >> 8));
                    txBuffer[index++] = (byte) (p_canmsg.id);
                } else {
                    txBuffer[index++] = (byte) (p_canmsg.id >> 3);
                    txBuffer[index++] = (byte) (p_canmsg.id << 5);
                    txBuffer[index++] = 0;
                    txBuffer[index++] = 0;
                }

                // length and data
                if (p_canmsg.rtr) {
                    txBuffer[index++] = (byte) (length | (byte) 0x40);
                } else {
                    txBuffer[index++] = (byte) length;
                    for (int i = 0; i < length; i++) {
                        txBuffer[index + i] = (p_canmsg.data[i]);
                    }
                    index += length;
                }

                spi.write(txBuffer, 0, index);

                mcp2515_write_register(ctrlreg, (byte) (txprio | (byte) 0x08));

            } catch (Exception e) {
                System.err.println("Erro on Send Message");
            }

            return true;
        } else {
            return false;
        }
    }
    
    /*
    * \brief Read out one can message from MCP2515
    *
    * \param p_canmsg Pointer to can message structure to fill
    * \return 1 on success, 0 if there is no message to read
    */
    CAN_Message receive_message() 
    {
        CAN_Message canMessage = null;
        
        if (canOpened == true) {
                        
            try {
                byte status = mcp2515_rx_status();
                int index = 0;
                byte address;
                byte txBuffer[] = new byte[20];

                if ((status & (byte)0x40) == (byte)0x40) {
                    address = 0x00;
                } else if ((status & (byte)0x80) == (byte)0x80) {
                    address = 0x04;
                } else {
                    // no message in receive buffer
                    return canMessage;
                }
                
                canMessage = new CAN_Message();
                // store flags
                canMessage.rtr = ((status >> 3) & 0x01) == 0x01 ? true : false;
                canMessage.extended = ((status >> 4) & 0x01) == 0x01 ? true : false;

                txBuffer[index] = (byte)(MCP2515_defs.MCP2515_CMD_READ_RX | address);
                index += 14;
                byte[] rxBuffer = spi.write(txBuffer, 0, index);

                if (canMessage.extended) {
                    canMessage.id = (int) rxBuffer[1] << 21;
                    int temp = rxBuffer[2];
                    canMessage.id |= (temp & 0xe0) << 13;
                    canMessage.id |= (temp & 0x03) << 16;
                    canMessage.id |= (int)(rxBuffer[3] << 8) & 0x0000FFFF;
                    canMessage.id |= (int) rxBuffer[4] & 0x000000FF;
                    canMessage.id &= 0x1FFFFFFF;
                } else {
                    canMessage.id = (int) (rxBuffer[1] << 3) & 0x000007FF;
                    canMessage.id |= (int) (rxBuffer[2] >> 5) & 0x000007FF;
                }
                
                canMessage.dlc = (rxBuffer[5] & (byte)0x0f);
                if (!canMessage.rtr) {            

                    for (int i = 0; i < canMessage.dlc; i++) {
                        canMessage.data[i] = rxBuffer[6 + i];
                    }
                }

            } catch (Exception e) {
                System.err.println("Erro on Receive Message");
            }

            return canMessage;
        } else {
            return canMessage;
        }
    }
}
