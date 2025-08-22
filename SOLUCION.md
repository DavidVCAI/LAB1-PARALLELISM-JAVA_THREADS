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

![alt text](image.png)

Y dado eso podemos correrlo directamente con java, por ejemplo con este comando:

"java -cp target/classes edu.eci.arsw.threads.CountThreadsMain"


![alt text](image-1.png)

Lo que podemos ver es que corriendo con start los hilos se ejecutan de manera concurrente.

![alt text](image-2.png)

Y vemos que con run() los hilos se ejecutan en el principal, casi como si estuvieramos corriendo codigo normal.

Entonces lo que podemos concluir de este primer ejercicio es que start crea un nuevo hilo de ejecucion, y segun entendemos este automaticamente llama al metodo run(), que se puede ver en la libreria:

![alt text](image-3.png)

Eso significa que run no crea un nuevo hilo sino que ejecuta en el hilo en el que esta, como llamar un metodo de forma normal. Vemos como Thread.join() hace que se espere a los otros hilos para que el programa no termine antes que los demas. Esto nos muestra que tenemos una concurrencia o paralelismo real cuando utilizamos start() y hacemos que los hilos corran al tiempo, que sea paralelismo o concurrencia depende de que tantos nucleos tenemos.