# Requests - A REST API for Minecraft Servers

Requests is a PaperMC plugin that provides a RESTful API for Minecraft servers. With Requests, server admins can easily query and interact with their servers using simple REST semantics.

## Features

- RESTful API for Minecraft servers
- Query server information such as player count and server status
- Send commands to the server
- Authenticate requests using API keys
- Easy to use and integrate

## Installation

1. Download the latest version of the plugin from the [releases page](https://github.com/theMackabu/requests/releases) or from [spigotmc.org](https://no-url-yet.com).
2. Place the downloaded JAR file into the `plugins` folder of your server.
3. Restart the server.

## Usage

Requests exposes a RESTful API that can be accessed using HTTP requests. The base URL for the API is `http://localhost:5000` and can be configured in `config.toml`.

```toml
[plugin]
salt = "change this"
prefix = "<light_purple>[requests]<reset> "

[database]
tokens = "tokens.db"
players = "players.db"

[api]
port = 5000
vault = false
luckperms = false
```

### Authentication

Requests uses API keys to authenticate requests. To generate an master API key, use the following command in the server console:

```bash
/api generate-key <name>
```

The `<name>` parameter is optional and can be used to give the key a custom name. The generated API key will be displayed in the console.

To generate an player API key, use the following command in game:

```
/api token new
```

To authenticate a request, include the API key in the `Authorization` header of the HTTP request:

```bash
Authorization: Bearer <api_key>
```

### API Endpoints

Requests provides the following API endpoints:

#### GET `/server`

Returns the status of the Minecraft server.

#### GET `/players`

Returns a list of online players on the Minecraft server.

#### GET `/player/<uniqueId>`

Returns the information of a player.

#### POST `/command`

Sends a command to the Minecraft server. This route can be used by admin token only, and is disabled by default.

### Example Requests

#### Get server status

```bash
GET /server HTTP/.1
Authorization: Bearer <api_key>
User-Agent: Example Agent/0.0.1
```

#### Get player profile

```bash
GET /player/<uniqueId> HTTP/1.1
Authorization: Bearer <api_key>
User-Agent: Example Agent/0.0.1
```

#### Send command

```bash
POST /command HTTP/1.1
Authorization: Bearer <api_key>
Content-Type: application/json; charset=utf-8
User-Agent: Example Agent/0.0.1
Content-Length: 31

{"command":"say Hello, World!"}
```

## Build

To build Requests from source, follow these steps:

1. Clone the repository: `git clone https://github.com/theMackabu/requests.git`
2. Navigate to the cloned repository: `cd requests`
3. Build the plugin using Gradle: `./gradlew shadowJar`
4. The built JAR file will be located at `build/libs/requests-<version>-all.jar`.

## License

Requests is licensed under the [GPL-3 License](https://github.com/themackabu/requests/blob/main/LICENSE.md).
