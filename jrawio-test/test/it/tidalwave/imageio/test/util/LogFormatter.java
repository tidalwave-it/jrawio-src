/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * Project home page: http://jrawio.tidalwave.it
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 *
 *******************************************************************************
 *
 * $Id: LogFormatter.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.test.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: LogFormatter.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
public class LogFormatter extends Formatter
 {
   //%d{} [%-16t] %-5p %-21c{1} - %m%n
   private static final String PADDER = "                                                                          ";
   private Date now = new Date();
   private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

   // Line separator string.  This is the value of the line.separator
   // property at the moment that the SimpleFormatter was created.
   private String lineSeparator =
       (String)java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));

   private static String padded (
       String s,
       int    width)
     {
       int i = s.length() - Math.abs(width);

       if (i < 0)
         {
           i = 0;
         }

       return (width < 0) ? (s + PADDER).substring(0, -width) : (s + PADDER).substring(i, i + width);
     }

   /**
    * Format the given LogRecord.
    * @param record the log record to be formatted.
    * @return a formatted log record
    */
   public synchronized String format (LogRecord record)
     {
       StringBuffer sb = new StringBuffer();

       // Minimize memory allocations here.
       now.setTime(record.getMillis());
       sb.append(dateFormat.format(now));
       sb.append(" ");
       sb.append("[");
       sb.append(padded(Thread.currentThread().getName(), 16));
       sb.append("] ");
       sb.append(padded(record.getLevel().getName(), 7));
       sb.append(" ");

       String s = record.getSourceClassName();

       if (s == null)
         {
           s = record.getLoggerName();
           sb.append(padded(s, 32));
         }

       else
         {
           int j = s.lastIndexOf('.');

           if (j > 0)
             {
               s = s.substring(j + 1);
             }

           j = s.lastIndexOf('$');

           if (j > 0)
             {
               s = s.substring(0, j);
             }

           sb.append(padded(s, -26));
         }

       sb.append(" - ");

       String message = formatMessage(record);
       sb.append(message);
       sb.append(lineSeparator);

       if (record.getThrown() != null)
         {
           try
             {
               StringWriter sw = new StringWriter();
               PrintWriter pw = new PrintWriter(sw);
               record.getThrown().printStackTrace(pw);
               pw.close();
               sb.append(sw.toString());
             }
           catch (Exception e)
             {
             }
         }

       return sb.toString();
     }
 }
