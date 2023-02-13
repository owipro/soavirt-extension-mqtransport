package com.parasoft.soavirt.transport.mq;

public class BytesToHex {
    
   //Functions for converting MessageID to readable
   public static final String HEX_CHARS = "0123456789ABCDEF";

   public static String bytesToHex(byte[] data)
   {
      StringBuffer buf = new StringBuffer();
      for (int i = 0; i < data.length; i++)
         buf.append(byteToHex(data[i]));
   
      return buf.toString();
   }
   
   public static String byteToHex(byte data)
   {
      int hi = (data & 0xF0) >> 4;
      int lo = (data & 0x0F);
      return "" + HEX_CHARS.charAt(hi) + HEX_CHARS.charAt(lo);
   }
}
