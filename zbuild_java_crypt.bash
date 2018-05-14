#!/bin/bash

# @filename           :  zbuild_java.sh
# @author             :  Copyright (C) Church.Zhong
# @date               :  Tue Jul 25 09:19:48 CST 2017
# @function           :  yuicompressor-2.4.8.jar.
# @see                :  https://github.com/yui/yuicompressor
# @require            :  Java version >= 1.5


#<!-- The order is important here. Rhino MUST be unjarred first!
#     (some of our own classes will override the Rhino classes) -->

#set -x

EX_OK=0;

# work well on CentOS release 6.4(10.68.3.31)
MYNAME=`whoami`;
echo ${MYNAME}

# work well on CentOS release 6.4(10.68.3.31)
WORK_DIR=`pwd`;
echo ${WORK_DIR}

# I Love this dir.
OS_DATE=`date --date= +%Y%m%d`;

echo $(which java);
#/usr/bin/java


# Define some constants
TARGET=audiocodesCryptTool-1.0.0_${MYNAME}_${OS_DATE}.jar;
PROJECT_PATH=${WORK_DIR};
JAR_PATH=$PROJECT_PATH/lib;
BUILD_PATH=$PROJECT_PATH/build;
SRC_PATH=$PROJECT_PATH/src;
MANIFEST_MF=$BUILD_PATH/MANIFEST.MF;


function make_manifest_mf()
{
	echo -e "Manifest-Version: 1.0\r" > $MANIFEST_MF;
	echo -e "Ant-Version: Apache Ant 1.7.1\r" >> $MANIFEST_MF;
	echo -e "Created-By: ${MYNAME},${OS_DATE}\r" >> $MANIFEST_MF;
	echo -e "Main-Class: CryptMain\r" >> $MANIFEST_MF;
	#echo -e "Class-Path: lib/jargs-1.0.jar lib/rhino-1.7R2.jar\r" >> $MANIFEST_MF;
}

################################## shell start #####################################################

# First, remove the build directory if it exists and then create new build directory
rm -rf $BUILD_PATH;
mkdir -p $BUILD_PATH;
mkdir -p $BUILD_PATH/classes;
mkdir -p $BUILD_PATH/jar;
mkdir -p $BUILD_PATH/jar/META-INF;

# Second, remove the sources.list file if it exists and then create the sources.list file of the project
rm -f $BUILD_PATH/sources.list;
find ${SRC_PATH} -name '*.java' > $BUILD_PATH/sources.list;
mkdir -p ${BUILD_PATH}/jar/gnu/getopt
find ${SRC_PATH}/gnu -type f -name '*.properties' | xargs cp -t ${BUILD_PATH}/jar/gnu/getopt

# unjar others libary/jar
#cd $BUILD_PATH/jar;
#jar xf $JAR_PATH/jargs-1.0.jar;
#jar xf $JAR_PATH/rhino-1.7R2.jar;
cd $PROJECT_PATH;

# Compile the project
#javac -d $BUILD_PATH/jar -classpath $JAR_PATH/jargs-1.0.jar:$JAR_PATH/rhino-1.7R2.jar @$BUILD_PATH/sources.list;
javac -d $BUILD_PATH/jar @$BUILD_PATH/sources.list;

# Create MANIFEST.MF
make_manifest_mf;

# Pack all class files into a single jar file, jar is so foolish![verbose;extract;create;manifest]
jar -cvfm $BUILD_PATH/$TARGET $MANIFEST_MF -C $BUILD_PATH/jar .;

exit $EX_OK;

################################## shell end #######################################################
