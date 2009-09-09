#!/bin/sh
java org.apache.xalan.xslt.Process -IN $1 -XSL $2 -OUT $1.html

