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

import javax.annotation.Nonnull;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class MyParameterized extends Suite
  {
    private class TestClassRunnerForParameters extends BlockJUnit4ClassRunner
      {
        private final int fParameterSetNumber;

        private final List<Object[]> fParameterList;

        TestClassRunnerForParameters (final Class<?> type, final List<Object[]> parameterList, final int i)
          throws InitializationError
          {
            super(type);
            fParameterList= parameterList;
            fParameterSetNumber= i;
          }

        @Override
        public Object createTest()
          throws Exception
          {
            return getTestClass().getOnlyConstructor().newInstance(computeParams());
          }

        private Object[] computeParams()
          throws Exception
          {
            try
              {
                return fParameterList.get(fParameterSetNumber);
              }
            catch (ClassCastException e)
              {
                throw new Exception(String.format("%s.%s() must return a Collection of arrays.",
                                    getTestClass().getName(), getParametersMethod(getTestClass()).getName()));
              }
          }

        @Override
        protected String getName()
          {
            return String.format("[%s] (%s, %s)", Arrays.toString(fParameterList.get(fParameterSetNumber)), System.getProperty("java.version"), getTestMode());
          }

        @Override
        protected String testName (final FrameworkMethod method)
          {
            return String.format("%s[%s] (%s, %s)", method.getName(), Arrays.toString(fParameterList.get(fParameterSetNumber)), System.getProperty("java.version"), getTestMode());
          }

        @Nonnull
        private String getTestMode()
          {
            final String stcp = System.getProperty("surefire.test.class.path");
            return ((stcp == null) || !stcp.contains("cobertura")) ? "normal" : "cobertura";
          }

        @Override
        protected void validateConstructor (final List<Throwable> errors)
          {
            validateOnlyOneConstructor(errors);
          }

        @Override
        protected Statement classBlock (final RunNotifier notifier)
          {
            return childrenInvoker(notifier);
          }

        @Override
        public void run (final RunNotifier notifier)
          {
            try
              {
                System.err.printf("Running %s...\n", getName());
                MultiThreadHandler.setLogName(getName());
                super.run(notifier);
              }
            finally
              {
                MultiThreadHandler.resetLogName();
              }
          }
      }

    private final ArrayList<Runner> runners = new ArrayList<Runner>();

    /**
     * Only called reflectively. Do not use programmatically.
     */
    public MyParameterized (final Class<?> klass)
      throws Throwable
      {
        super(klass, Collections.<Runner>emptyList());
        final List<Object[]> parametersList = getParametersList(getTestClass());

        for (int i= 0; i < parametersList.size(); i++)
          {
            runners.add(new TestClassRunnerForParameters(getTestClass().getJavaClass(), parametersList, i));
          }

        final int executors = Integer.getInteger("testExecutors", 1);
        System.err.printf("Running tests with %d parallel executors\n", executors);

        if (executors > 1)
          {
            setScheduler(new ParallelRunnerScheduler(executors));
          }
      }

    @Override
    protected List<Runner> getChildren()
      {
        return runners;
      }

    @SuppressWarnings("unchecked")
    private List<Object[]> getParametersList (final TestClass klass)
      throws Throwable
      {
        return (List<Object[]>)getParametersMethod(klass).invokeExplosively(null);
      }

    private FrameworkMethod getParametersMethod (final TestClass testClass)
      throws Exception
      {
        final List<FrameworkMethod> methods = testClass.getAnnotatedMethods(Parameters.class);

        for (final FrameworkMethod each : methods)
          {
            final int modifiers = each.getMethod().getModifiers();

            if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))
              {
                return each;
              }
          }

        throw new Exception("No public static parameters method on class " + testClass.getName());
      }
  }
