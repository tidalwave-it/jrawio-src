![Maven Central](https://img.shields.io/maven-central/v/org.rawdarkroom/jrawio.svg)
[![Build Status](https://img.shields.io/jenkins/s/http/services.tidalwave.it/ci/jrawio_Build_from_Scratch.svg)](http://services.tidalwave.it/ci/view/jrawio)
[![Test Status](https://img.shields.io/jenkins/t/http/services.tidalwave.it/ci/jrawio.svg)](http://services.tidalwave.it/ci/view/jrawio)
[![Coverage](https://img.shields.io/jenkins/c/http/services.tidalwave.it/ci/jrawio.svg)](http://services.tidalwave.it/ci/view/jrawio)

jrawio
================================

jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files


Bootstrapping
-------------

In order to build the project, run from the command line:

```mvn -DskipTests```

The project can be opened and built by a recent version of the NetBeans, Eclipse or Idea IDEs.


Documentation
-------------

More information can be found on the [homepage](http://jrawio.rawdarkroom.org) of the project.


Where can I get the latest release?
-----------------------------------
You can download source and binaries from the [download page](https://bitbucket.org/tidalwave/jrawio-src/src).

Alternatively you can pull it from the central Maven repositories:

```xml
<dependency>
    <groupId>org.rawdarkroom<groupId>
    <artifactId>jrawio</artifactId>
    <version>-- version --</version>
</dependency>
```


Contributing
------------

We accept pull requests via Bitbucket or GitHub.

There are some guidelines which will make applying pull requests easier for us:

* No tabs! Please use spaces for indentation.
* Respect the code style.
* Create minimal diffs - disable on save actions like reformat source code or organize imports. If you feel the source
  ode should be reformatted create a separate PR for this change.
* Provide TestNG tests for your changes and make sure your changes don't break any existing tests by running
```mvn clean test```.

If you plan to contribute on a regular basis, please consider filing a contributor license agreement. Contact us for
 more information


License
-------
Code is released under the [Apache Licence v2](https://www.apache.org/licenses/LICENSE-2.0.txt).


Additional Resources
--------------------

* [Tidalwave Homepage](http://tidalwave.it)
* [Project Issue Tracker (Jira)](http://services.tidalwave.it/jira/browse/JRW)
* [Project Continuous Integration (Jenkins)](http://services.tidalwave.it/ci/view/jrawio)
