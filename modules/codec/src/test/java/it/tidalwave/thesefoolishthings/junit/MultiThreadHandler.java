/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003-2011 by Tidalwave s.a.s.
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
package it.tidalwave.thesefoolishthings.junit;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.io.IOException;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class MultiThreadHandler extends Handler
  {
    private static final Handler VOID_HANDLER = new Handler()
      {
        @Override
        public void publish (final LogRecord logRecord)
          {
          }

        @Override
        public void flush()
          {
          }

        @Override
        public void close()
          {
          }
      };
    
    private static final Map<ThreadGroup, String> LOG_NAME_MAP = new HashMap<ThreadGroup, String>();
    
    private final Map<String, Handler> handlerMap = new HashMap<String, Handler>();

    protected String directory = "target/logs";

    public static void setLogName (final @Nonnull String name)
      {
//          System.err.printf(">>>> %s bound to %s\n", Thread.currentThread().getThreadGroup(), name);
        LOG_NAME_MAP.put(Thread.currentThread().getThreadGroup(), name);
      }

    public static void resetLogName()
      {
        LOG_NAME_MAP.remove(Thread.currentThread().getThreadGroup());
      }

    public void setDirectory (final String directory)
      {
        this.directory = directory;
      }

    public String getDirectory()
      {
        return directory;
      }

    @Override
    public void publish (final @Nonnull LogRecord logRecord)
      {
        getHandler().publish(logRecord);
      }

    @Override
    public void flush()
      {
        getHandler().flush();
      }

    @Override
    public void close() 
      throws SecurityException
      {
        getHandler().close();
      }

    @Nonnull
    private synchronized Handler getHandler()
      {
        final String id = LOG_NAME_MAP.get(Thread.currentThread().getThreadGroup());
//          System.err.printf(">>>> %s retrieved %s\n", Thread.currentThread().getThreadGroup(), id);

        if (id == null)
          {
            return VOID_HANDLER;
          }

        Handler handler = handlerMap.get(id);

        if (handler == null)
          {
            try
              {
                final String fileName = id.replace(':', '_').replace('/', '_').replace('[', '_').replace(']', '_').replace(' ', '_');
                handler = new FileHandler(String.format("%s/log-%s.log", directory, fileName));
                handler.setFormatter(getFormatter());
                handlerMap.put(id, handler);
              }
            catch (IOException e)
              {
                e.printStackTrace();
              }
            catch (SecurityException e)
              {
                e.printStackTrace();
              }
          }

        return (handler != null) ? handler : VOID_HANDLER;
      }
  }
