Integrantes: Jesus Pinzon y David Velasquez

Desarrollo del laboratorio 1.

Se instalan las dependencias de java y mvn desde los sitios oficiales, añadiendolos a las variables de entorno para poder utilizarlas.

En primer lugar añadimos esta parte en el pom para ejecutar facilmente el proyecto con mvn:

<build>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>exec-maven-plugin</artifactId>
            <version>3.1.0</version>
            <configuration>
                <mainClass>edu.eci.arsw.blacklistvalidator.Main</mainClass>
            </configuration>
        </plugin>
    </plugins>
</build>

Asi con el comando mvn clean compile exec:java se ejecuta el limpiado, compilado y ejecucion de Main.


Punto 1.

Vamos a implementar las clases the threads y probar las diferencias. Se realiza entonces el codigo de clase que va a extender Thread, esta nos muestra en consola la ejecucion de un hilo en especifico, la clase CountThread que define hilos en un rango inclusivo.

Luego de esto se implementa la clase principal que va a crear los hilos y probar las diferencias entre ejecutarlos con start() y con run(). Pusimos ciertos logs para ver que tanto se demora en milisegundos y se imprime bien las ejecuciones, dandonos buenos insights de lo que estamos haciendo.

Luego de ello debemos correr mvn compile para compilar el projecto. 

<img src="assets/images/image-0.png" alt="Build Proyect" width="70%">

Y dado eso podemos correrlo directamente con java, por ejemplo con este comando:

"java -cp target/classes edu.eci.arsw.threads.CountThreadsMain"

<img src="assets/images/image-1.png" alt="Threads Execution with start()" width="70%">

Lo que podemos ver es que corriendo con start los hilos se ejecutan de manera concurrente.

<img src="assets/images/image-2.png" alt="Threads Execution with run()" width="40%">

Y vemos que con run() los hilos se ejecutan en el principal, casi como si estuvieramos corriendo codigo normal.

Entonces lo que podemos concluir de este primer ejercicio es que start crea un nuevo hilo de ejecucion, y según entendemos este automáticamente llama al metodo run(), que se puede ver en la librería:

<img src="assets/images/image-3.png" alt="Runnable Interface" width="70%">

Eso significa que run no crea un nuevo hilo sino que ejecuta en el hilo en el que esta, como llamar un metodo de forma normal. Vemos como Thread.join() hace que se espere a los otros hilos para que el programa no termine antes que los demas. Esto nos muestra que tenemos una concurrencia o paralelismo real cuando utilizamos start() y hacemos que los hilos corran al tiempo, que sea paralelismo o concurrencia depende de que tantos nucleos tenemos.


Punto 2 

Al haber ejecutado main con mvn clean compile exec:java vemos que tenemos 80000 listas negras

<img src="assets/images/image-4.png" alt="BlackListSearch Execution" width="70%">

En base a esto implementamos la paralelización del algoritmo de búsqueda de blacklists, con ello vamosa a dividir el trabajo de revisar las 80,000 listas negras entre múltiples hilos para aprovechar el paralelismo. Empezamos creando la clase BlackListSearchThread

Primero creamos una clase que extiende Thread y lo que hace es buscar en un segmento específico de las listas negras.

Luego modificamos la clase de HostBlackListsValidator

Agregamos un nuevo método checkHost(String ipaddress, int N) que implementa la búsqueda paralela de la siguiente forma:


- Se calcula el tamaño del segmento: segmentSize = totalServers / N
- Manejamos el resto: remainder = totalServers % N
- Distribuimos los servidores que quedan entre los primeros hilos


Para poder sincronizar bien hacemos lo siguiente:
- Utilizamos thread.join() para esperar que todos los hilos terminen
- Recopilamos los resultados de cada hilo después de que terminan
- Mantenemos el LOG original que muestra las listas revisadas vs total

Luego de ejecutar la nueva clase que prueba el paralelismo podemos ver algunos insights que son los siguientes:

java -cp target/classes edu.eci.arsw.blacklistvalidator.ParallelMain

Test 1 - IP IP no tan dispersa 200.24.34.55
- Con 4 hilos fueron mas o menos 27 segundos
- Encontradas en: [23, 50, 200, 500, 1000]
- Resultado: NO CONFIABLE (5 ocurrencias)

Test 2 - IP mas dispersa (202.24.34.55):
- Con 4 hilos aprox 25 segundos  
- Encontradas en: [29, 10034, 20200, 31000, 70500]
- Resultado: NO CONFIABLE (5 ocurrencias)
- Pudimos ver que los hilos ven diferentes ocurrencias dependiendo de su segmento

Test 3 - IP que no esta en listas (212.24.24.55):
- Con 4 hilos fueron como 25 segundos
- Encontradas en: []
- Resultado: CONFIABLE (0 ocurrencias)

Si tomamos el peor caso que es la mas dispersa (202.24.34.55), podemos ver algunas cosas respecto al rendimiento:

1 hilo: 115,219 ms (~115 segundos)
2 hilos: 51,285 ms (~51 segundos) - Mejora del 55%
4 hilos: 25,432 ms (~25 segundos) - Mejora del 78% 
8 hilos: 13,460 ms (~13 segundos) - Mejora del 88%

Por ejemplo aqui estan los logs de la busqueda de 8 hilos

<img src="assets/images/image-5.png" alt="8 Thread Search Logs" width="70%">

Conclusiones:

1. La paralelizacion funciona ya que se logra una reduccion en el tiempo de ejecución
2. Al duplicar hilos aproximadamente se reduce el tiempo a la mitad.
3. Los resultados se agregan bien al final osea que se sincronizaron bien

Lo que hicimos nos muestra que el problema de este punto es vergonzosamente paralelo ya que no hay dependencias entre los segmentos y cada hilo puede trabajar bien en su segmento asignado. Los logs nos muestran una mejora considerable en el rendimiento al aprovechar los varios nucleos del procesador.

2.1 Avance: Vemos que el tiempo de ejecucion no cambia mucho sin importar el caso de uso, lo que nos dice que estamos revisando todos los servidores sin importar que, suponemos que debemos utilizar una manera en la que si un hilo "termina" de alguna forma avisemos a los demas que deben terminar, haciendo que los casos por ejemplo donde no es tan disperso, se ejecuten mucho mas rapido.
