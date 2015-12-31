/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003 - 2016 by Tidalwave s.a.s.
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * WWW: http://jrawio.rawdarkroom.org
 * SCM: https://kenai.com/hg/jrawio~src
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.raw;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import it.tidalwave.imageio.util.Logger;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import it.tidalwave.imageio.io.RAWImageInputStream;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public abstract class Directory implements Serializable
  {
    private final static String CLASS = Directory.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    private final static long serialVersionUID = 7068468438676854749L;

    protected long start;
    
    protected long end;
    
    /** The map of getTags. */
    private final Map<Object, AbstractTag> tagMapByKey = new HashMap<Object, AbstractTag>();

    /** The list of tag keys, in the same order as they were added. */
    private final List<Object> keyList = new ArrayList<Object>();
    
    protected Directory nextDirectory; // FIXME it's protected - better to make private
    
    /** The list of sub-directories. */
    private final Collection<Directory> directoryList = new ArrayList<Directory>();

    /** The map of named directories. */
    private Map<String, Directory> directoryMapByName = new HashMap<String, Directory>();

    /** The name of the registry this tag belongs to. */
    private String registryName;

    /** The registry the contained getTags belongs to. This is transient so that this class can
     * be serialized without carrying along the whole registry. Upon deserialization
     * the link to the registry must be restored by using the registryName.
     */
    protected transient TagRegistry tagRegistry;

    /*******************************************************************************************************************
     *
     * This class models an enumerated value for a tag.
     *
     ******************************************************************************************************************/
    public static class Enumeration implements Serializable
      {
        private final static long serialVersionUID = 4029468438676854749L;

        /** The tag value. */
        private int intValue;
        
        private String stringValue;

        /** The tag name. */
        private String description;

        protected Enumeration (int value, String description)
          {
            this.intValue = value;
            this.description = description;
          }

        protected Enumeration (int[] value, String description)
          {
            this(value[0], description);
          }

        protected Enumeration (String value, String description)
          {
            this.stringValue = value;
            this.description = description;
          }

        protected Enumeration (String[] value, String description)
          {
            this(value[0], description);
          }

        public int intValue ()
          {
            return intValue;
          }

        @Override
        public String toString ()
          {
            if (stringValue != null)
              {
                return description.equals("reserved") ? (description + "#" + stringValue) : description;
              }
            
            else
              {
                return description.equals("reserved") ? (description + "#" + intValue) : description;
              }
          }

        @Override
        public int hashCode ()
          {
            if (stringValue != null)
              {
                return stringValue.hashCode();
              }
            
            else
              {
                return intValue;
              }
          }

        @Override
        public boolean equals (Object object)
          {
            if (this.getClass() != object.getClass())
              {
                return false;
              }

            Enumeration e = (Enumeration)object;

            if (stringValue != null)
              {
                return this.stringValue.equals(e.stringValue);
              }
            
            else
              {
                return this.intValue == e.intValue;
              }
          }
        
        protected static boolean equals (int i1, int i2)
          {
            return i1 == i2;  
          }
        
        protected static boolean equals (int i1[], int i2)
          {
            return i1[0] == i2;  
          }
        
        protected static boolean equals (String s1, String s2)
          {
            if (s1 == s2)
              {
                return true;
              }

            if (s1 == null)
              {
                return false;
              }

            return trim(s1).equals(trim(s2));   
          }
        
        private static String trim (String s)
          {
            return (s == null) ? null : s.trim(); // TODO: also remove leading zeros
          }
      }

    /*******************************************************************************************************************
     * 
     * For de-serialization and for supporting CGLIB only. CGLIB is needed by
     * blueMarine. For this reason it must be public too.
     * 
     ******************************************************************************************************************/
    public Directory()
      {
      }

    /*******************************************************************************************************************
     * 
     * Creates a new <code>Directory</code> whose getTags belong to the given registry.
     * 
     * @param tagRegistry  the registry
     * 
     ******************************************************************************************************************/
    protected Directory (@Nonnull final TagRegistry tagRegistry)
      {
        this.tagRegistry = tagRegistry;
        registryName = tagRegistry.getName();
      }

    /*******************************************************************************************************************
     * 
     * Returns the registry the contained getTags belong to.
     * 
     * @return  the registry
     * 
     ******************************************************************************************************************/
    @Nonnull
    public TagRegistry getRegistry()
      {
        return tagRegistry;
      }
    
    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    @Nonnegative
    public long getStart()
      {
        return start;
      }

    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    @Nonnegative
    public long getEnd()
      {
        return end;
      }

    /*******************************************************************************************************************
     * 
     * @param iis
     * @param offset
     * @throws IOException
     * 
     ******************************************************************************************************************/
    public abstract long load (@Nonnull final RAWImageInputStream iis, 
                               long offset) 
      throws IOException;

    /*******************************************************************************************************************
     * 
     * @param iis
     * @param offset
     * @throws IOException
     *
     ******************************************************************************************************************/
    public void loadAll (@Nonnull final RAWImageInputStream iis, 
                         @Nonnull final long offset)       
      throws IOException
      {
        load(iis, offset);   
      }
     
    /*******************************************************************************************************************
     *
     * Adds a tag to this <code>Directory</code>.
     * 
     * @param  tag  the tag to add
     * 
     ******************************************************************************************************************/
    public void addTag (@Nonnull final AbstractTag tag)
      {
        if (tag != null)
          {
            final int key = tag.getCode();
            tagMapByKey.put(tagRegistry.getKey(key), tag);
            keyList.add(tagRegistry.getKey(key));
          }
      }

    /*******************************************************************************************************************
     *
     * Retrieves a contained tag given its key.
     * 
     * @param  key  the tag key
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public AbstractTag getTag (@Nonnull final Object key)
      {
        return tagMapByKey.get(tagRegistry.getKey(key));
      }

    /*******************************************************************************************************************
     *
     * Checks if this <code>Directory</code> contains a given tag.
     * 
     * @param  key  the tag key
     * @return      true if this <code>Directory</code> contains the tag
     * 
     ******************************************************************************************************************/
    public boolean containsTag (@Nonnull final Object key)
      {
        return (tagRegistry != null) && tagMapByKey.containsKey(tagRegistry.getKey(key));
      }

    /*******************************************************************************************************************
     *
     * Removes a given tag from this </code>Directory</code>
     * 
     * @param  key  the tag key
     * @return      the remove tag
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public AbstractTag removeTag (@Nonnull final Object key)
      {
        keyList.remove(tagRegistry.getKey(key));
        return tagMapByKey.remove(tagRegistry.getKey(key));
      }

    /*******************************************************************************************************************
     *
     * Removes all tags from this /code>Directory</code>.
     *
     ******************************************************************************************************************/
    public void removeAllTags()
      {
        keyList.clear();
        tagMapByKey.clear();
      }

    /*******************************************************************************************************************
     *
     * Returns the name of the given tag. FIXME: is meaningful here?
     * 
     * @param  key  the tag key
     * @return      the tag name
     * 
     ******************************************************************************************************************/
    @Nonnull
    public String getTagName (@Nonnull final Object key)
      {
        return tagRegistry.getTagName(((Number)key).intValue());
      }

    /*******************************************************************************************************************
     *
     * FIXME: This method is only provided for supporting 
     * it.tidalwave.image.DirectorySupport, but should be replaced with a more
     * decoupled mechanism. This method returns a plain Number when an enumeration
     * could be expected.
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public Object getObject (@Nonnull final Object key)
      {
        final AbstractTag tag = getTag(key);
        return (tag != null) ? tag.getValue() : null;
      }

    /*******************************************************************************************************************
     *
     * Returns the tag values as bytes.
     * 
     * @param  key  the tag key
     * @return      the bytes or null if the tag is not present
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public byte[] getBytes (@Nonnull final Object key)
      {
        final AbstractTag tag = getTag(key);
        return (tag != null) ? tag.getByteValues() : null;
      }

    /*******************************************************************************************************************
     *
     * Returns the tag value as a byte.
     * 
     * @param  key  the tag key
     * @return      the byte
     * @throws      NoSuchElementException  if the tag is not present
     * 
     ******************************************************************************************************************/
    public int getByte (@Nonnull final Object key)
      {
        if (!containsTag(key))
          {
            throw new NoSuchElementException("No tags with key = " + key);
          }

        return getBytes(key)[0];
      }

    /*******************************************************************************************************************
     *
     * Returns the tag values as rationals.
     * 
     * @param  key  the tag key
     * @return      the rationals or null if the tag is not present
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public TagRational[] getRationals (@Nonnull final Object key)
      {
        AbstractTag tag = getTag(key);
        return (tag != null) ? tag.getRationalValues() : null;
      }

    /*******************************************************************************************************************
     *
     * Returns the tag value as a rational.
     * 
     * @param  key  the tag key
     * @return      the rational
     * @throws      NoSuchElementException  if the tag is not present
     * 
     ******************************************************************************************************************/
    @Nonnull
    public TagRational getRational (@Nonnull final Object key)
      {
        if (!containsTag(key))
          {
            throw new NoSuchElementException("No tags with key = " + key);
          }

        return getRationals(key)[0];
      }

    /*******************************************************************************************************************
     *
     * Returns the tag values as doubles.
     * 
     * @param  key  the tag key
     * @return      the doubles or null if the tag is not present
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public double[] getDoubles (@Nonnull final Object key)
      {
        final AbstractTag tag = getTag(key);
        return (tag != null) ? asDoubles(tag.getRationalValues()) : null;
      }

    /*******************************************************************************************************************
     *
     * Returns the tag value as a double.
     * 
     * @param  key  the tag key
     * @return      the double
     * @throws      NoSuchElementException  if the tag is not present
     * 
     ******************************************************************************************************************/
    public double getDouble (@Nonnull final Object key)
      {
        if (!containsTag(key))
          {
            throw new NoSuchElementException("No tags with key = " + key);
          }

        return getRationals(key)[0].doubleValue();
      }

    /*******************************************************************************************************************
     *
     * Returns the tag values as floats.
     * 
     * @param  key  the tag key
     * @return      the floats or null if the tag is not present
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public float[] getFloats (@Nonnull final Object key)
      {
        final AbstractTag tag = getTag(key);
        return (tag != null) ? tag.getFloatValues() : null;
      }

    /*******************************************************************************************************************
     *
     * Returns the tag value as a float.
     * 
     * @param  key  the tag key
     * @return      the float
     * @throws      NoSuchElementException  if the tag is not present
     * 
     ******************************************************************************************************************/
    public float getFloat (@Nonnull final Object key)
      {
        if (!containsTag(key))
          {
            throw new NoSuchElementException("No tags with key = " + key);
          }

        return getRationals(key)[0].floatValue();
      }

    /*******************************************************************************************************************
     *
     * Returns the tag values as integers.
     * 
     * @param  key  the tag key
     * @return      the integers or null if the tag is not present
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public int[] getIntegers (@Nonnull final Object key)
      {
        final AbstractTag tag = getTag(key);

        if (tag == null)
          {
            return null;
          }

        int[] intValues = tag.getIntValues();

        if (intValues != null)
          {
            return intValues;
          }

        final byte[] byteValues = tag.getByteValues();

        if (byteValues != null)
          {
            intValues = new int[byteValues.length];

            for (int i = 0; i < byteValues.length; i++)
              {
                intValues[i] = byteValues[i] & 0xff;
              }

            return intValues;
          }

        return null;
      }

    /*******************************************************************************************************************
     *
     * Returns the tag value as an integer.
     * <br>
     * This method returns 0 if there exists a tag with the given key which contains
     * an empty array of integers. Such a tag is BayerGreenSplit in DNG files.
     * It should be clarified if this is compliant with specs (in which case this
     * behaviour should be extended to other multiple-value getXXX() methods, or if
     * it is a bug of Adobe Converter.
     * 
     * @param  key  the tag key
     * @return      the integer
     * @throws      NoSuchElementException  if the tag is not present
     * 
     ******************************************************************************************************************/
    public int getInteger (@Nonnull final Object key) 
      throws NoSuchElementException
      {
        if (!containsTag(key))
          {
            throw new NoSuchElementException("No tags with key = " + key);
          }

        int[] i = getIntegers(key);

        if (i.length == 0) // FIXME: happens with a BayerGreenSplit field in DNG files
          {
            return 0;
          }

        return i[0];
      }

    /*******************************************************************************************************************
     *
     * Returns the tag value as a string.
     * 
     * @param  key  the tag key
     * @return      the string or null if the tag has not been found
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public String getString (@Nonnull final Object key)
      {
        final AbstractTag tag = getTag(key);
        return (tag != null) ? tag.getASCIIValue() : null;
      }

    /*******************************************************************************************************************
     *
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public Directory getNextDirectory()
      {
        return nextDirectory;
      }
      
    /*******************************************************************************************************************
     *
     * Adds a sub-directory.
     * 
     * @param   subDirectory  the sub-directory
     * 
     ******************************************************************************************************************/
    public void addDirectory (@Nonnull final Directory subDirectory)
      {
        directoryList.add(subDirectory);
      }

    /*******************************************************************************************************************
     *
     * Returns an iterator over sub-directories. Note that named directories are 
     * not returned.
     * 
     * @return  the yterator
     * 
     ******************************************************************************************************************/
    @Nonnull
    public Collection<Directory> getSubDirectories()
      {
        return Collections.unmodifiableCollection(directoryList);
      }

    /*******************************************************************************************************************
     *
     * Adds a named sub-directory.
     * 
     * @param  name       the directory name
     * @param  subDirectory  the sub-directory
     * 
     ******************************************************************************************************************/
    public void addNamedDirectory (@Nonnull final String name,
                                   @Nonnull final Directory subDirectory)
      {
        directoryMapByName.put(name, subDirectory);
      }

    /*******************************************************************************************************************
     *
     * Returns a named sub-directory
     * 
     * @param   name  the sub-directory name
     * @return        the sub-directory
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public Directory getNamedDirectory (@Nonnull final String name)
      {
        return directoryMapByName.get(name);
      }

    /*******************************************************************************************************************
     * 
     * Returns the names of named sub directories
     * 
     * @return  the subdirectory names
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public String[] getSubDirectoryNames()
      {
        return directoryMapByName.keySet().toArray(new String[0]);
      }

    /*******************************************************************************************************************
     *
     * Returns the contained getTags in the same order as they were added.
     * 
     * @return  the contained getTags
     * 
     ******************************************************************************************************************/
    @Nonnull
    public Collection<AbstractTag> getTags()
      {
        final List<AbstractTag> result = new ArrayList<AbstractTag>();
        
        for (final Object key : keyList)
          {
            result.add(tagMapByKey.get(key));
          }

        return result;
      }

    /*******************************************************************************************************************
     *
     * Convenience method that converts an array of rationals into floats.
     * 
     * @param   rationals
     * @return  the floats
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public static float[] asFloats (@CheckForNull final TagRational[] rationals)
      {
        if (rationals == null)
          {
            return null;
          }

        float[] floats = new float[rationals.length];

        for (int i = 0; i < rationals.length; i++)
          {
            floats[i] = rationals[i].floatValue();
          }

        return floats;
      }

    /*******************************************************************************************************************
     *
     * Convenience method that converts an array of rationals into doubles.
     * 
     * @param   rationals
     * @return  the doubles
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public static double[] asDoubles (@CheckForNull final TagRational[] rationals)
      {
        if (rationals == null)
          {
            return null;
          }

        double[] doubles = new double[rationals.length];

        for (int i = 0; i < rationals.length; i++)
          {
            doubles[i] = rationals[i].doubleValue();
          }

        return doubles;
      }

    /*******************************************************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************************************************/
    @Override
    @Nonnull
    public String toString()
      {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(String.format("%d - %d (0x%x - 0x%x)", start, end, start, end));

        for (final Object key : keyList)
          {
            buffer.append("\n>>>>>>>> ");
            final Object value = tagMapByKey.get(key);
            buffer.append(value);
          }

        buffer.append("\n");
        return buffer.toString();
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected String toString (@Nonnull final byte[] array)
      {
        if (array.length > 64)
          {
            return "" + array.length + " bytes";
          }

        final StringBuilder buffer = new StringBuilder("");

        for (int i = 0; i < array.length; i++)
          {
            if (i > 0)
              {
                buffer.append(",");
              }

            buffer.append(Integer.toHexString(array[i] & 0xff));
          }

        return buffer.toString();
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected String toString (@Nonnull final int[] array)
      {
        if (array.length > 64)
          {
            return "" + array.length + " integers";
          }

        final StringBuilder buffer = new StringBuilder("");

        for (int i = 0; i < array.length; i++)
          {
            if (i > 0)
              {
                buffer.append(",");
              }

            buffer.append(Integer.toString(array[i]));
          }

        return buffer.toString();
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected String toString (@Nonnull final double[] array)
      {
        if (array.length > 64)
          {
            return "" + array.length + " doubles";
          }

        final StringBuilder buffer = new StringBuilder("");

        for (int i = 0; i < array.length; i++)
          {
            if (i > 0)
              {
                buffer.append(",");
              }

            buffer.append(Double.toString(array[i]));
          }

        return buffer.toString();
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected String toString (@Nonnull final TagRational[] array)
      {
        final StringBuilder buffer = new StringBuilder("");

        for (int i = 0; i < array.length; i++)
          {
            if (i > 0)
              {
                buffer.append(",");
              }

            buffer.append(array[i].toString());
          }

        return buffer.toString();
      }

    /*******************************************************************************************************************
     * 
     * Customized deserialization code. This method restores the link to the registry
     * this tag belongs to.
     * 
     * @param  is
     * @throws IOException
     * @throws ClassNotFoundException
     * 
     ******************************************************************************************************************/
    private void readObject (@Nonnull final ObjectInputStream is) 
      throws IOException, ClassNotFoundException
      {
        is.defaultReadObject();
        tagRegistry = TagRegistry.getRegistry(registryName);
      }
  }
