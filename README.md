# JuegoDadosProcesos
### La empresa de videojuegos StarDream Enterprise quiere crear un videojuego multijugador online, para ello nos contrata para crear un servidor al que los diferentes jugadores se conectarán para conocer cuáles son los jugadores conectados. 

**Tabla de contenido:**

1. Varios jugadores se pueden conectar a la vez, por lo que el servidor deberá dar respuesta a todos los jugadores simultáneamente.
2. Cada vez que un jugador se conecte, el jugador deberá enviar si está empezando un nuevo juego o finalizando uno existente
3. Si el jugador empieza un nuevo juego deberá enviar el tipo de partida al servidor y el nombre del jugador (nickname max 10 caracteres).
4. El servidor deberá mantener en memoria todas las partidas en curso y para cada partida tener una lista de jugadores, sabiendo para cada jugador el host, el puerto y el nickname de cada jugador.
5. Cuando el número de jugadores sea igual al especificado para el tipo de juego el servidor deberá enviar el nickname, host y puerto de todos los jugadores a cada uno de los jugadores conectados a la partida, y estos deben de mostrar por consola un listado con los jugadores conectados en su partida. Además informará a cada uno de los jugadores si es o no el anfitrión de la partida.
6. Una vez finalizado el juego, el jugador anfitrión deberá enviar el id del juego que desea finalizar, y el servidor deberá de eliminar la partida de la colección de partidas en curso.
Para simular el comportamiento anterior se nos pide crear 5 partidas del tipo de juego dados. El tipo de juegos dado, consiste en que dos jugadores tiran un dado y el que saque mayor puntuación gana. Por tanto la simulación consta de 2 jugadores por cada partida (10 usuarios simultáneos):
1. Al crearse los jugadores deberán tener un nickname.
2. Una vez la partida empiece, uno de los jugadores hará de anfitrión, el cual será comunicado por el propio servidor al iniciar la partida y el otro conectará con dicho anfitrión (invitado)
3. Cuando empieza la partida el invitado empezará tirando el dado (número aleatorio entre 1 y 6), y enviarán el valor obtenido al anfitrión, el anfitrión tirará su dado y si el valor obtenido es mayor ganará, si es menor perderá y si es igual habrá que tirar de nuevo los dados.
4. Una vez el anfitrión conozca el resultado, se lo comunicará al invitado, enviando V si ha vencido el anfitrión, D si ha perdido el anfitrión y E si hay empate.
5. En caso de empate se volverá a repetir el procedimiento, en caso de victoria o derrota se informará al servidor, que quitará la partida de la memoria y ambos jugadores finalizarán su ejecución.
Práctica entre 3 compañeros.
