#!/bin/bash

mvn clean package javadoc:jar -DskipTests
if [ $? -ne 0 ]; then
    echo "Ошибка при выполнении mvn clean compile package javadoc:jar"
    exit 1
fi

cd target || { echo "Не удалось перейти в папку target"; exit 1; }

# Находим основной fat JAR файл
jar_file=$(find . -name "llmapi4free_lib-1.1-SNAPSHOT.jar" -print -quit)
if [ -z "$jar_file" ]; then
    echo "Не найден основной .jar файл в папке target"
    exit 1
fi

# Устанавливаем основной fat JAR
mvn install:install-file -Dfile="$jar_file" \
    -DgroupId=com.vityazev_egor \
    -DartifactId=llmapi4free \
    -Dversion=1.1 \
    -Dpackaging=jar \
    -Dname=llmapi4free

if [ $? -ne 0 ]; then
    echo "Ошибка при установке основного .jar файла в локальный репозиторий"
    exit 1
fi

# Находим JAR файл с JavaDoc
javadoc_jar_file=$(find . -name "llmapi4free_lib-1.1-SNAPSHOT-javadoc.jar" -print -quit)
if [ -z "$javadoc_jar_file" ]; then
    echo "Не найден файл с JavaDoc в папке target"
    exit 1
fi

# Устанавливаем JAR с JavaDoc
mvn install:install-file -Dfile="$javadoc_jar_file" \
    -DgroupId=com.vityazev_egor \
    -DartifactId=llmapi4free \
    -Dversion=1.1 \
    -Dpackaging=jar \
    -Dclassifier=javadoc

if [ $? -eq 0 ]; then
    echo "Успешно установлен $jar_file и $javadoc_jar_file в локальный репозиторий"
else
    echo "Ошибка при установке файла с JavaDoc в локальный репозиторий"
    exit 1
fi