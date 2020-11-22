#!/bin/bash

while getopts p: flag
do
    case "${flag}" in
        p) project_dir=${OPTARG};;
    esac
done

echo "====== Script Config ======";
echo "project_dir=$project_dir";

#implementation 'ru.aar_generator:engine_support:0.0.1'
#implementation project(':engine_support')

old_project_str="implementation project(\x27:"
aar_name="implementation \x27ru.aar_generator:"
aar_version=":0.0.1\x27"

for gradle_file_dir in `find $project_dir -name 'build.gradle'`
  do 
	echo $val_gradle_file
	sed -i -e "s/$old_project_str/$aar_name/g" -e "/implementation/s/\x27)/$aar_version/g" $gradle_file_dir
done
