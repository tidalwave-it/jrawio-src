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
 * $Id: TagRegistry.java,v 1.3 2006/02/13 22:41:14 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.raw;

import java.util.HashMap;
import java.util.Map;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: TagRegistry.java,v 1.3 2006/02/13 22:41:14 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class TagRegistry
  {
    private static Map registryMapByName = new HashMap();

    private Map nameMapByCode = new HashMap();

    private Map codeMapByName = new HashMap();

    private String name;

    /*******************************************************************************
     * 
     * Creates a new instance.
     * 
     * @param name  the registry name
     * 
     *******************************************************************************/
    private TagRegistry (String name)
      {
        this.name = name;
      }

    /*******************************************************************************
     * 
     * Returns the registry name.
     * 
     * @return  the registry name
     * 
     *******************************************************************************/
    public String getName ()
      {
        return name;
      }

    /*******************************************************************************
     * 
     * Retrieves or creates a registry with the given name. If the registry does not
     * exist, a new instance is created.
     * 
     * @param   name  the name
     * @return        the registry
     * 
     *******************************************************************************/
    public synchronized static TagRegistry getRegistry (String name)
      {
        TagRegistry registry = (TagRegistry)registryMapByName.get(name);

        if (registry == null)
          {
            registry = new TagRegistry(name);
            registryMapByName.put(name, registry);
          }

        return registry;
      }

    /*******************************************************************************
     *
     * Registers a new tag.
     * 
     * @param  code the tag code
     * @param  name the tag name
     * @return      the tag name
     * 
     ******************************************************************************/
    public String register (int code,
                            String name)
      {
        Integer iCode = new Integer(code);
        nameMapByCode.put(iCode, name);
        codeMapByName.put(name, iCode);

        return name;
      }

    /*******************************************************************************
     *
     * Retrieves a tag name from its code.
     * 
     * @param  code  the code
     * @return       the name
     * 
     ******************************************************************************/
    public String getTagName (int code)
      {
        return (String)nameMapByCode.get(new Integer(code));
      }

    /*******************************************************************************
     *
     * Retrieves a tag code from its name.
     * 
     * @param  name  the name
     * @return       the code
     * 
     ******************************************************************************/
    /* package */int getTagCode (String name)
      {
        Number code = (Number)codeMapByName.get(name);
        return (code == null) ? -1 : code.intValue();
      }

    /*******************************************************************************
     *
     ******************************************************************************/
    /* package */Object getKey (Object key)
      {
        if (key instanceof String)
          return key;

        Object r = nameMapByCode.get(key);

        return (r != null) ? r : "#" + key;
      }

    /*******************************************************************************
     *
     ******************************************************************************/
    public Object getKey (int key)
      {
        return getKey(new Integer(key));
      }
  }
