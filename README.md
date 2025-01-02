# LLMapi4free
LLMapi4free - это сервис, предоставляющий унифицированный доступ к различным LLM провайдерам через API. Сервис работает путем автоматизации действий в браузере, что позволяет получить бесплатный доступ к различным языковым моделям в виде API для ваших приложений.
[Video demonstration](https://youtu.be/lHJaL333qAE)

## How to run
1. Download OpenJDK version 17 or higher from the official Oracle website.
2. Download xdotool (sudo apt install xdotool)
3. Download chrome browser
4. Download the .jar file from the Releases section.
5. Run .jar file using following command: `java -jar bingchat4urapp_server-0.0.1-SNAPSHOT.jar` 
6. The application will be launched on port 8080.

## Proxy Support
If you want to use a proxy, you need to pass the proxy address as a parameter to the .jar file. Please note that only SOCKS5 proxies are supported. For example, you can pass the proxy to the application through the console like this:

```bash
java -jar bingchat4urapp_server-0.0.1-SNAPSHOT.jar --proxy 127.0.0.1:8521
```

# Как использовать?

## Принцип работы

Сервер работает по асинхронному принципу с использованием задач:

1. Клиент создаёт задачу (авторизация, отправка промпта и т.д.) через соответствующий endpoint
2. Сервер возвращает ID созданной задачи
3. Клиент периодически опрашивает endpoint `/get/{id}` для получения статуса задачи, пока она не будет завершена (`isFinished = true`)

## Base URL
```
/api
```

## Endpoints

### Аутентификация
#### Создание задачи аутентификации
```http
POST /auth
```

**Request Body:**
```json
{
    "login": "string",
    "password": "string",
    "provider": "enum(LLMproviders)"
}
```

**Validation Rules:**
- `login`: 6-50 символов
- `password`: минимум 8 символов, максимум 50
- `provider`: одно из значений enum LLMproviders

**Response:**
- `201 Created`: ID созданной задачи (целое число)
```json
42
```
- `400 Bad Request`: ошибка валидации
- `500 Internal Server Error`: внутренняя ошибка сервера

### Управление чатом
#### Создание нового чата
```http
POST /createchat
```

**Response:**
- `201 Created`: ID созданной задачи чата
```json
43
```
- `500 Internal Server Error`: внутренняя ошибка при создании задачи

#### Отправка промпта
```http
POST /sendpromt
```

**Request Body:**
```json
{
    "promt": "string",
    "timeOutForAnswer": "integer"
}
```

**Validation Rules:**
- `promt`: 4-4000 символов
- `timeOutForAnswer`: 30-300 секунд

**Response:**
- `201 Created`: ID созданной задачи
```json
44
```
- `400 Bad Request`: ошибка валидации
- `500 Internal Server Error`: внутренняя ошибка при создании задачи

### Управление провайдерами
#### Получение информации о доступных провайдерах
```http
GET /getProvidersInfo
```

**Response:**
- `200 OK`: список доступных LLM провайдеров
```json
[
    {
        "provider": "Copilot",
        "chat": {
            "Copilot": "copilot"
        },
        "gotError": false,
        "authDone": true,
        "authRequired": true,
        "lastAnswer": "Последний ответ от модели"
    },
    {
        "provider": "OpenAI",
        "chat": {
            "OpenAI": "openAI"
        },
        "gotError": false,
        "authDone": false,
        "authRequired": true,
        "lastAnswer": ""
    }
]
```

#### Установка предпочтительного провайдера
```http
POST /setPreferedProvider
```

**Request Body:**
```json
{
    "provider": "enum(LLMproviders)"
}
```

**Response:**
- `200 OK`: "Done!"
- `400 Bad Request`: ошибка валидации
- `500 Internal Server Error`: "Failed to set provider. Check server console for details"

#### Получение текущего рабочего LLM
```http
GET /getWorkingLLM
```

**Response:**
- `200 OK`: информация о текущем рабочем LLM провайдере, который используется программой для обработки промтов
```json
{
    "provider": "Copilot",
    "chat": {
        "type": "Copilot" // игнорируйте это поле
    },
    "gotError": false,
    "authDone": true,
    "authRequired": true,
    "lastAnswer": "Последний ответ от модели"
}
```

### Управление задачами
#### Получение статуса задачи
```http
GET /get/{id}
```

**Parameters:**
- `id`: ID задачи (integer)

**Response:**
- `200 OK`: информация о задаче
```json
{
    "id": 42,
    "type": 1,
    "data": {
        "param1": "value1",
        "param2": "value2"
    },
    "isFinished": true,
    "gotError": false,
    "result": "Текстовый результат выполнения задачи",
    "htmlResult": "<div>HTML результат выполнения задачи</div>",
    "imageResult": "имя_файла_изображения.png"
}
```
- `404 Not Found`: задача не найдена

### Системные endpoints
#### Завершение работы сервера
```http
GET /exit
```

**Response:**
- `200 OK`: "Server will be down in few seconds"

## Типы задач
- `type = 0`: Системная задача завершения работы
- `type = 1`: Задача аутентификации
- `type = 2`: Задача отправки промпта
- `type = 3`: Задача создания чата

## Формат ответов от LLM
При получении ответа от языковой модели, сервер всегда возвращает три поля:
- `result`: чистый текстовый ответ
- `htmlResult`: ответ в формате HTML с форматированием
- `imageResult`: имя файла изображения, если модель сгенерировала изображение

В случае возникновения ошибки:
- `gotError`: становится `true`
- `result`: содержит описание ошибки
- `isFinished`: становится `true`, указывая на завершение задачи с ошибкой

## Коды ответов

- `200 OK`: Успешное выполнение запроса
- `201 Created`: Успешное создание ресурса
- `400 Bad Request`: Ошибка в запросе (невалидные данные)
- `404 Not Found`: Запрашиваемый ресурс не найден
- `500 Internal Server Error`: Внутренняя ошибка сервера

## Примечания

- Все запросы, требующие тела, должны иметь Content-Type: application/json
- Таймаут ответа для промптов ограничен диапазоном 30-300 секунд
- При ошибках валидации сервер вернёт список конкретных ошибок валидации
- ID задач являются целыми числами и уникальны в рамках системы
- Все задачи выполняются асинхронно, клиент должен периодически проверять статус задачи
- Поля result, htmlResult и imageResult присутствуют в ответе всегда, даже если они пустые