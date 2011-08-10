#!/bin/sh

JEANNIE_HOME=/home/alvi/projects/jeannie/playground
LIB_DIR=${JEANNIE_HOME}/lib
MODULE_DIR=`dirname $0`/cartridge

LIBS=\
${LIB_DIR}/ant-1.8.2.jar:\
${LIB_DIR}/ant-launcher-1.8.2.jar:\
${LIB_DIR}/antlr-2.7.7.jar:\
${LIB_DIR}/antlr-runtime-3.3.jar:\
${LIB_DIR}/commons-logging-1.1.1.jar:\
${LIB_DIR}/dom4j-1.6.jar:\
${LIB_DIR}/google-collections-1.0.jar:\
${LIB_DIR}/groovy-all-1.8.0.jar:\
${LIB_DIR}/gson-1.4.jar:\
${LIB_DIR}/gson-1.7.1.jar:\
${LIB_DIR}/javassist-3.8.0.GA.jar:\
${LIB_DIR}/logback-classic-0.9.29.jar:\
${LIB_DIR}/logback-core-0.9.29.jar:\
${LIB_DIR}/opencsv-2.1.jar:\
${LIB_DIR}/qdox-1.12.jar:\
${LIB_DIR}/reflections-0.9.5-RC2.jar:\
${LIB_DIR}/servlet-api-2.5.jar:\
${LIB_DIR}/slf4j-api-1.6.1.jar:\
${LIB_DIR}/snakeyaml-1.8.jar:\
${LIB_DIR}/ST4-4.0.4.jar:\
${LIB_DIR}/stringtemplate-3.2.1.jar:\
${LIB_DIR}/xml-apis-1.0.b2.jar:\
${LIB_DIR}/jeannie-generator-0.1.4-SNAPSHOT.jar

if [ -z "$1" -o -z "$2" ] ; then
echo usage "jeannie.sh <inputdir> <outputdir>"
cat ${MODULE_DIR}/README;
exit 1
fi

java -DglobalDebug=true -Xmx128M -cp ${LIBS} com.sb.jeannie.Main $1 $2 $3

