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
 * $Id: Version.java,v 1.1 2006/02/17 15:31:58 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: Version.java,v 1.1 2006/02/17 15:31:58 fabriziogiudici Exp $
 *
 ******************************************************************************/
public final class Version
  {
    public final static DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");
    
    public final static String REQUIRED_JRAWIO_VERSION = "1.0.RC5";

    /******************************************************************************
     *
     * Used by ant to get the current tag and use it in the build process.
     *
     ******************************************************************************/
    public static void main (String[] args)
      {
        System.out.println("tag: " + dateFormat.format(new Date()) + "-" + REQUIRED_JRAWIO_VERSION);
      }
  }
