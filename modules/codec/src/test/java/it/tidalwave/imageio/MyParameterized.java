/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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

/**
 *
 */
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
