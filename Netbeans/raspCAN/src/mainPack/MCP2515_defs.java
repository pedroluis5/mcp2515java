/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainPack;


/**
 *
 * @author pedronascimento
 */
public interface MCP2515_defs {
        
    public static final byte MCP2515_TIMINGS_10K[]  = {(byte)0x0F, (byte)0xBF, (byte)0x87};
    public static final byte MCP2515_TIMINGS_20K[]  = {(byte)0x07, (byte)0xBF, (byte)0x87};
    public static final byte MCP2515_TIMINGS_50K[]  = {(byte)0x03, (byte)0xB4, (byte)0x86};
    public static final byte MCP2515_TIMINGS_100K[] = {(byte)0x01, (byte)0xB4, (byte)0x86};
    public static final byte MCP2515_TIMINGS_125K[] = {(byte)0x01, (byte)0xB1, (byte)0x85};
    public static final byte MCP2515_TIMINGS_250K[] = {(byte)0x00, (byte)0xB1, (byte)0x85};
    public static final byte MCP2515_TIMINGS_500K[] = {(byte)0x00, (byte)0x90, (byte)0x82};
    public static final byte MCP2515_TIMINGS_1M[]   = {(byte)0x00, (byte)0x80, (byte)0x80};

    // command definitions
    public static final byte MCP2515_CMD_RESET = (byte)0xC0;
    public static final byte MCP2515_CMD_READ = (byte)0x03;
    public static final byte MCP2515_CMD_WRITE = (byte)0x02;
    public static final byte MCP2515_CMD_BIT_MODIFY = (byte)0x05;
    public static final byte MCP2515_CMD_READ_STATUS = (byte)0xA0;
    public static final byte MCP2515_CMD_LOAD_TX = (byte)0x40;
    public static final byte MCP2515_CMD_RTS = (byte)0x80;
    public static final byte MCP2515_CMD_RX_STATUS = (byte)0xB0;
    public static final byte MCP2515_CMD_READ_RX = (byte)0x90;

    public static final byte MCP_RXB_RX_ANY      = (byte)0x60;
    public static final byte MCP_RXB_RX_EXT      = (byte)0x40;
    public static final byte MCP_RXB_RX_STD      = (byte)0x20;
    public static final byte MCP_RXB_RX_STDEXT   = (byte)0x00;
    public static final byte MCP_RXB_RX_MASK     = (byte)0x60;

    public static final byte MCP_STAT_RXIF_MASK  = ((byte)0x03);
    public static final byte MCP_STAT_RX0IF      = (1<<0);
    public static final byte MCP_STAT_RX1IF      =  (1<<1);

    public static final byte MCP_RXB0SIDH    = (byte)0x61;
    public static final byte MCP_RXB1SIDH    = (byte)0x71;

    // register definitions
    public static final byte MCP2515_REG_CNF1 = (byte)0x2A;
    public static final byte MCP2515_REG_CNF2 = (byte)0x29;
    public static final byte MCP2515_REG_CNF3 = (byte)0x28;
    public static final byte MCP2515_REG_CANCTRL = (byte)0x0F;
    public static final byte MCP2515_REG_RXB0CTRL = (byte)0x60;
    public static final byte MCP2515_REG_RXB1CTRL = (byte)0x70;
    public static final byte MCP2515_REG_BFPCTRL = (byte)0x0C;
    public static final byte MCP2515_REG_CANINTF = (byte)0x2C;
    public static final byte MCP2515_REG_CANINTE = (byte)0x2B;
    public static final byte MCP2515_REG_TXB0CTR = (byte)0x30;
    public static final byte MCP2515_REG_TXB1CTR = (byte)0x40;
    public static final byte MCP2515_REG_TXB2CTR = (byte)0x50;

    public static final byte MCP2515_REG_RXF0SIDH = (byte)0x00;
    public static final byte MCP2515_REG_RXF0SIDL = (byte)0x01;
    public static final byte MCP2515_REG_RXF1SIDH = (byte)0x04;
    public static final byte MCP2515_REG_RXF1SIDL = (byte)0x05;
    public static final byte MCP2515_REG_RXF2SIDH = (byte)0x08;
    public static final byte MCP2515_REG_RXF2SIDL = (byte)0x09;
    public static final byte MCP2515_REG_RXF3SIDH = (byte)0x10;
    public static final byte MCP2515_REG_RXF3SIDL = (byte)0x11;
    public static final byte MCP2515_REG_RXF4SIDH = (byte)0x14;
    public static final byte MCP2515_REG_RXF4SIDL = (byte)0x15;
    public static final byte MCP2515_REG_RXF5SIDH = (byte)0x18;
    public static final byte MCP2515_REG_RXF5SIDL = (byte)0x19;

    public static final byte MCP2515_REG_RXM0SIDH = (byte)0x20;
    public static final byte MCP2515_REG_RXM0SIDL = (byte)0x21;
    public static final byte MCP2515_REG_RXM0EID8 = (byte)0x22;
    public static final byte MCP2515_REG_RXM0EID0 = (byte)0x23;
    public static final byte MCP2515_REG_RXM1SIDH = (byte)0x24;
    public static final byte MCP2515_REG_RXM1SIDL = (byte)0x25;
    public static final byte MCP2515_REG_RXM1EID8 = (byte)0x26;
    public static final byte MCP2515_REG_RXM1EID0 = (byte)0x27;
    public static final byte MCP2515_REG_EFLG = (byte)0x2d;
}
