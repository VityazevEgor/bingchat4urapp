#!/bin/bash

# Выполнить команду mvn clean compile assembly:single
mvn clean compile assembly:single

# Проверить успешность предыдущей команды
if [ $? -ne 0 ]; then
  echo "Ошибка при выполнении mvn clean compile assembly:single"
  exit 1
fi

# Перейти в папку target
cd target || { echo "Не удалось перейти в папку target"; exit 1; }

# Найти .jar файл
jar_file=$(find . -name "*.jar" -print -quit)

if [ -z "$jar_file" ]; then
  echo "Не найден .jar файл в папке target"
  exit 1
fi

# Установить .jar файл в локальный репозиторий с помощью команды mvn install
mvn install:install-file -Dfile="$jar_file" \
  -DgroupId=com.bingchat4urapp \
  -DartifactId=bingchat4urapp \
  -Dversion=1.7 \
  -Dpackaging=jar \
  -Dname=bingchat4urapp

# Проверить успешность установки
if [ $? -eq 0 ]; then
  echo "Успешно установлен $jar_file в локальный репозиторий"
else
  echo "Ошибка при установке .jar файла в локальный репозиторий"
  exit 1
fi
