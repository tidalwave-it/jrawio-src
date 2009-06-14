/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2008 by Fabrizio Giudici
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
 * $Id: ThumbnailHelper.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.PrintWriter;
import java.io.StringWriter;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: BlueMarineLogFormatter.java 5283 2008-04-24 19:26:14Z fabriziogiudici $
 *
 ******************************************************************************/
public class SingleLineLogFormatter extends Formatter
 {
   //%d{} [%-16t] %-5p %-21c{1} - %m%n
   private static final String PADDER = "                                                                          ";
   
   private Date now = new Date();
   
   private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

   // Line separator string.  This is the value of the line.separator
   // property at the moment that the SimpleFormatter was created.
   private String lineSeparator = "\n";
//       (String)java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));

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
   public synchronized String format (final LogRecord record)
     {
       final StringBuffer buffer1 = new StringBuffer();
       // Minimize memory allocations here.
       now.setTime(record.getMillis());
       buffer1.append(dateFormat.format(now));
       buffer1.append(" ");
       buffer1.append("[");
       buffer1.append(padded(Thread.currentThread().getName(), 18));
       buffer1.append("] ");
       buffer1.append(padded(record.getLevel().getName(), 7));
       buffer1.append(" ");
       final String loggerName = record.getLoggerName();
       buffer1.append(padded((loggerName != null) ? loggerName : "", 32));
       buffer1.append(" - ");

       final String prefix = buffer1.toString();

       final StringBuffer buffer2 = new StringBuffer();
       String message = formatMessage(record);
       buffer2.append(message);
       buffer2.append(lineSeparator);

       if (record.getThrown() != null)
         {
           try
             {
               StringWriter sw = new StringWriter();
               PrintWriter pw = new PrintWriter(sw);
               record.getThrown().printStackTrace(pw);
               pw.close();
               buffer2.append(sw.toString());
             }
           catch (Exception e)
             {
             }
         }

       final StringBuffer buffer3 = new StringBuffer();

       for (final String part : buffer2.toString().split("\n"))
         {
           buffer3.append(prefix);
           buffer3.append(part);
           buffer3.append("\n");
         }

       return buffer3.toString();
     }
 }
