#!/bin/sh

grep $1 image.properties | grep EXIF | grep MakerNote | grep "$2\." | grep value | sed 's/.*value//' | sort | uniq

