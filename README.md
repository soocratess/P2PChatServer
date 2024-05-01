
# Sistema de Mensajería Instantánea Java RMI

Este repositorio contiene el servidor para un sistema de mensajería instantánea distribuido implementado utilizando Java RMI. El servidor gestiona las conexiones de múltiples clientes y permite la comunicación directa entre ellos, sin necesidad de pasar los mensajes a través del servidor.

## Características

- **Conexión de múltiples clientes**: El servidor puede manejar múltiples clientes conectados simultáneamente.
- **Notificaciones de conexión y desconexión**: Cada vez que un cliente se conecta o desconecta, el servidor notifica a todos los clientes activos.
- **Comunicación directa entre clientes**: Los mensajes se envían directamente de cliente a cliente.
- **Gestión opcional de grupos de amistad**: Los clientes pueden formar grupos de amigos y gestionar solicitudes de amistad.
- **Registro y autenticación de usuarios**: Los usuarios pueden registrarse e iniciar sesión utilizando un nombre de usuario y contraseña.

## Estructura del proyecto

El proyecto está dividido en varias partes principales:

- `Server.java`: Clase principal del servidor que acepta y maneja conexiones de clientes.
- `Client.java`: Clase cliente que se conecta al servidor y realiza comunicaciones directas con otros clientes.

## Configuración y ejecución

### Requisitos previos

Es necesario tener instalado Java y configurado el entorno para usar RMI.

### Configuración del servidor

1. Clonar el repositorio:

`git clone https://github.com/soocratess/P2PChatServer.git`


1. Compilar los archivos `.java`:

`javac Server.java Client.java`


1. Ejecutar el servidor:

`java Server`


### Conexión de clientes

Para conectar un cliente al servidor, ejecutar:

`java Client`


## Contribuir

Para contribuir a este proyecto, por favor, envíe un pull request o abra un issue para discutir los cambios propuestos.
