/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ----------------------------------------------------------
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * Project home page: http://jrawio.dev.java.net
 * 
 *******************************************************************************
 *
 * MIT License notice
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal 
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 *******************************************************************************
 * 
 * $Id: LogFormatter.java,v 1.1 2006/02/08 20:30:23 fabriziogiudici Exp $
 * 
 ******************************************************************************/
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
 * @version CVS $Id: LogFormatter.java,v 1.1 2006/02/08 20:30:23 fabriziogiudici Exp $
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
