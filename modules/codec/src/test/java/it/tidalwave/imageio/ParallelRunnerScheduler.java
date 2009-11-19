/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * ===============================================================
 *
 * Copyright (C) 2003-2009 by Tidalwave s.a.s. (http://www.tidalwave.it)
 * http://jrawio.tidalwave.it
 *
 ***********************************************************************************************************************
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
 ***********************************************************************************************************************
 *
 * $Id$
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.junit.runners.model.RunnerScheduler;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ParallelRunnerScheduler implements RunnerScheduler
  {
    private static int n = 0;
    private final ScheduledExecutorService executorService;

    public ParallelRunnerScheduler (final int poolSize)
      {
        executorService = Executors.newScheduledThreadPool(poolSize, new ThreadFactory()
          {
            // Each thread goes in its ThreadGroup because of MultiThreadHandler dispatches log handlers in function
            // of the ThreadGroup.
            public Thread newThread (final Runnable runnable)
              {
                final ThreadGroup threadGroup = new ThreadGroup(String.format("TG #%d", n++));
                return new Thread(threadGroup, runnable);
              }
          });
      }

    public void schedule (final Runnable childStatement)
      {
        executorService.submit(childStatement);
      }

    public void finished()
      {
        try
          {
//          System.err.println("shutting down and awaiting termination...");
            executorService.shutdown();
            executorService.awaitTermination(9999999, TimeUnit.SECONDS); // Note that MINUTES doesn't compile with Java 5
//              System.err.println(">>>> done");
          }
        catch (InterruptedException e)
          {
            e.printStackTrace();
          }
      }
  }
