#!/bin/bash
echo "==========================================="
echo "====== Running 'ProjectToAar' Script ======"
echo "==========================================="

while getopts p:n:f:m: flag; do
  case "${flag}" in
  p) project_dir=${OPTARG} ;;
  n) project_name=${OPTARG} ;;
  f) project_flavour=${OPTARG} ;;
  m) project_milestones=${OPTARG} ;;
  esac
done

echo "====== Script Config ======"
echo "project_dir=$project_dir"
echo "project_name=$project_name"
echo "project_flavour=$project_flavour"
echo "project_milestones=$project_milestones"

#implementation 'ru.aar_generator:$project_name_$project_milestones_$project_flavour.engine_support:0.0.1'
#implementation project(':engine_support')

#x22 - "
#x27 - '

implementation="implementation"
api="api"
testImplementation="testImplementation"

aar_name="\x27ru.aar_generator:$project_name\_$project_milestones\_$project_flavour."
aar_version=":0.0.1\x27"

#project(":
project_1="project(\x27:"
#project( \x27:
project_2="project( \x27:"
#project(\x22:
project_3="project(\x22:"
#project(path: \x27:
project_4="project(path: \x27:"

#----------------- Implementation -----------------
#for implementation project(':project')
old_impl_project_str="$implementation $project_1"
#for implementation project( ':project')
old_impl_project_str_space="$implementation $project_2"
#for implementation project(":project")
old_impl_project_str_double="$implementation $project_3"
#for implementation project(path: ':project')
old_impl_project_str_path="$implementation $project_4"

#----------------- Api ----------------------------
#for api project(':project')
old_api_project_str="$api $project_1"
#for api project( ':project')
old_api_project_str_space="$api $project_2"
#for api project(":project")
old_api_project_str_double="$api $project_3"
#for api project(path: ':project')
old_api_project_str_path="$api $project_4"

#----------------- TestImplementation -------------
#for testImplementation project(':project')
old_testImplementation_project_str="$testImplementation $project_1"
#for testImplementation project( ':project')
old_testImplementation_project_str_space="$testImplementation $project_2"
#for testImplementation project(":project")
old_testImplementation_project_str_double="$testImplementation $project_3"
#for testImplementation project(path: ':project')
old_testImplementation_project_str_path="$testImplementation $project_4"

replace_implementation() {
  val_ext_gradle_file=$1
  sed -i -e "s/$old_impl_project_str/$implementation $aar_name/g" -e "/$implementation \x27/s/\x27)/$aar_version/g" $val_ext_gradle_file        #implementation project(':project')
  sed -i -e "s/$old_impl_project_str_space/$implementation $aar_name/g" -e "/$implementation \x27/s/\x27)/$aar_version/g" $val_ext_gradle_file  #implementation project( ':project')
  sed -i -e "s/$old_impl_project_str_double/$implementation $aar_name/g" -e "/$implementation \x27/s/\x22)/$aar_version/g" $val_ext_gradle_file #implementation project(":project")
  sed -i -e "s/$old_impl_project_str_path/$implementation $aar_name/g" -e "/$implementation \x27/s/\x27)/$aar_version/g" $val_ext_gradle_file   #implementation project(path: ':project')
}

replace_api() {
  val_ext_gradle_file=$1
  sed -i -e "s/$old_api_project_str/$api $aar_name/g" -e "/$api \x27/s/\x27)/$aar_version/g" $val_ext_gradle_file        #api project(':project')
  sed -i -e "s/$old_api_project_str_space/$api $aar_name/g" -e "/$api \x27/s/\x27)/$aar_version/g" $val_ext_gradle_file  #api project( ':project')
  sed -i -e "s/$old_api_project_str_double/$api $aar_name/g" -e "/$api \x27/s/\x22)/$aar_version/g" $val_ext_gradle_file #api project(":project")
  sed -i -e "s/$old_api_project_str_path/$api $aar_name/g" -e "/$api \x27/s/\x27)/$aar_version/g" $val_ext_gradle_file   #api project(path: ':project')
}

replace_test_implementation() {
  val_ext_gradle_file=$1
  sed -i -e "s/$old_testImplementation_project_str/$testImplementation $aar_name/g" -e "/$testImplementation \x27/s/\x27)/$aar_version/g" $val_ext_gradle_file        #testImplementation project(':project')
  sed -i -e "s/$old_testImplementation_project_str_space/$testImplementation $aar_name/g" -e "/$testImplementation \x27/s/\x27)/$aar_version/g" $val_ext_gradle_file  #testImplementation project( ':project')
  sed -i -e "s/$old_testImplementation_project_str_double/$testImplementation $aar_name/g" -e "/$testImplementation \x27/s/\x22)/$aar_version/g" $val_ext_gradle_file #testImplementation project(":project")
  sed -i -e "s/$old_testImplementation_project_str_path/$testImplementation $aar_name/g" -e "/$testImplementation \x27/s/\x27)/$aar_version/g" $val_ext_gradle_file   #testImplementation project(path: ':project')
}

for gradle_file_dir in $(find $project_dir -name 'build.gradle'); do
  printf "Конвертируем: $gradle_file_dir "
  replace_implementation "$gradle_file_dir"
  replace_api "$gradle_file_dir"
  replace_test_implementation "$gradle_file_dir"
  echo "УСПЕШНО!"
done
