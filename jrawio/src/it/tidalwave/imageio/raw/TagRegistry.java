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
 * $Id: TagRegistry.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.raw;

import java.util.HashMap;
import java.util.Map;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: TagRegistry.java 57 2008-08-21 20:00:46Z fabriziogiudici $
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
