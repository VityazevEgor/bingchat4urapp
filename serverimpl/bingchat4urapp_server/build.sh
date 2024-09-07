#!/bin/bash

mvn clean package -DskipTests

if [ $? -eq 0 ]; then
  echo "Команда выполнена успешно."
else
  echo "Произошла ошибка при выполнении команды."
fi
