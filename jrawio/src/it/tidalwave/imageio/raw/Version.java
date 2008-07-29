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
 * $Id: Version.java 40 2008-07-29 20:31:12Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.raw;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: Version.java 40 2008-07-29 20:31:12Z fabriziogiudici $
 *
 ******************************************************************************/
public final class Version
  {
    public final static String BUILD = "1.5SNAPSHOT";

    /******************************************************************************
     *
     * Used by ant to get the current tag and use it in the build process.
     *
     ******************************************************************************/
    public static void main (String[] args)
      {
        System.out.println("tag: " + BUILD);
      }
  }
