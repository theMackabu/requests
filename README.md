# Requests - A REST API for Minecraft Servers

Requests is a PaperMC plugin that provides a RESTful API for Minecraft servers. With Requests, server admins can easily query and interact with their servers using simple REST semantics.

## Features

- RESTful API for Minecraft servers
- Query server information such as player count and server status
- Send commands to the server
- Authenticate requests using API keys
- Easy to use and integrate

## Requirements

- Java 21 or higher
- Minecraft 1.21.x
- PaperMC or compatible server

## Installation

1. Download the latest version of the plugin from:
   - [releases page](https://github.com/theMackabu/requests/releases)
   - [spigotmc.org](https://www.spigotmc.org/resources/requests.129472/)
   - [builtbybit.com](https://builtbybit.com/resources/requests.28600/)
   - [papermc.io](https://hangar.papermc.io/theMackabu/Requests)

2. Place the downloaded JAR file into the `plugins` folder of your server.
3. Restart the server.

## Usage

Requests exposes a RESTful API that can be accessed using HTTP requests. The base URL for the API is `http://localhost:5000` and can be configured in `config.toml`.

```toml
[plugin]
salt = "change this"  # Used for token generation - change for security
prefix = "<light_purple>[requests]<reset> "

[database]
tokens = "tokens.db"
players = "players.db"

[api]
port = 5000
vault = false       # Enable Vault integration for economy data
luckperms = false   # Enable LuckPerms integration for permission data

[api.danger]
remote_commands = false
```

### Authentication

Requests uses API keys to authenticate requests. There are two types of tokens:

- **Master tokens**: Full access to all endpoints including admin operations (e.g., `/command`)
- **Player tokens**: Limited access to player-specific endpoints

To generate a master API key, use the following command in the server console:

```bash
/api generate-master [force]
```

The `<name>` parameter is optional and can be used to give the key a custom name. The generated API key will be displayed in the console.

To generate a player API key, use the following command in game:

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

#### GET `/players/<uniqueId>`

Returns the information of a player.

#### GET `/worlds`

Returns a list of worlds on the Minecraft server.

#### POST `/command`

Sends a command to the Minecraft server. This route can be used by master tokens only, and is disabled by default.

### Example Requests

#### Get server status

```bash
GET /server HTTP/1.1
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
Authorization: Bearer <master_key>
Content-Type: text/plain; charset=utf-8
User-Agent: Example Agent/0.0.1
Content-Length: 17

say Hello, World!
```

## Build

To build Requests from source, follow these steps:

1. Clone the repository: `git clone https://github.com/theMackabu/requests.git`
2. Navigate to the cloned repository: `cd requests`
3. Build the plugin using [Maid](https://github.com/exact-labs/maid): `maid build`
4. The built JAR file will be located at `build/libs/requests-<version>-all.jar`.

## License

Requests is licensed under the [GPL-3 License](https://github.com/themackabu/requests/blob/master/LICENSE.md).
