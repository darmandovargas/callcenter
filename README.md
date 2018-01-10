# Callcenter (Chanif of Responsability Pattern and Concurrent Threads)
This is the backend test for almundo.com Interview:

Existe un call center donde hay 3 tipos de empleados: operador,
supervisor y director. El proceso de la atención de una llamada
telefónica en primera instancia debe ser atendida por un operador, si
no hay ninguno libre debe ser atendida por un supervisor, y de no
haber tampoco supervisores libres debe ser atendida por un director.

## Requerimientos
Diseñar el modelado de clases y diagramas UML necesarios
para documentar y comunicar el diseño.
Debe existir una clase Dispatcher encargada de manejar las
llamadas, y debe contener el método dispatchCall para que las
asigne a los empleados disponibles.
La clase Dispatcher debe tener la capacidad de poder procesar
10 llamadas al mismo tiempo (de modo concurrente).
Cada llamada puede durar un tiempo aleatorio entre 5 y 10
segundos.
Debe tener un test unitario donde lleguen 10 llamadas.

### Diagrama de Clases
![alt text](https://raw.githubusercontent.com/darmandovargas/callcenter/master/Diagrama%20de%20Clases%20Callcenter%20almundo.com%20.png)

### Diagrama de Casos de Uso
![alt text](https://raw.githubusercontent.com/darmandovargas/callcenter/master/Diagrama%20de%20Casos%20de%20Uso%20Callcenter%20almundo.com%20.png)

### Unit Test
El test unitario está dentro del test de la aplicación, allí se crea una instancia de Dispatcher y se indica cuantas llamadas concurrentes deberá procesar. Cuando se corre el test no se termina el hilo, asumo que por un tema del JUnit, por lo que el test es solo desmostración de TDD y como sería su funcionamiento para el test exitoso de la clase Dispatcher, si se desea ver el funcionamiento empleto se deberá ejecutar como una aplicación java el App principal de la aplicación.

### Breve Descripción de lo que se hizo
Se hizo una revisión exaustiva del probléma clásico de Java, el cual es bastante similar al del ejercicio, con lo que se determinó que la importancia del test estaba en la evaluación del patrón arquitectónico Chain of Resonsability, por medio del cual hay una clase abastracta llamada Employee la cual tiene métodos abstráctos y métodos definidos, como el de setSuccessor el cual asigna quien sigue ascendentemente en la cadena de mando, de ésta clase abstracta extienden las diferentes entidades de la cadena de mando que son Operador, Supervisor y Director, por lo que su construcción debe ser mutuamente dependientes para que se pueda escalar la llamada según criterios de disponibilidad de los operadores así:

```ruby
Operador operador = new Operador("Empleado # "+x, "Operador", 1);
Supervisor supervisor = new Supervisor("Empleado # "+x, "Supervisor", 2);
Director director = new Director("Empleado # "+x, "Director", 3);
// Creación de sucesores de la cadena de mando, el cual se escalará recursivamente con base en la disponibilidad de cada tipo de empleado.
operador.setSuccessor(supervisor);
supervisor.setSuccessor(director);
```

Con ésto relacionamos las entidades con la siguiente línea ascendente en la cadena de mando.

También se determinó que era importante denotar el conocimiento de hilos concurrentes y el manejo de variables estáticas desde diferentes hilos, por lo cual para los hilos se utilizó el servicio de creación de hilos de java llamado ExecutorService, por medio del cual se ejecuta el método definido de la clase abstracta Employee que procesa la llamada (processCallRequest), el cual está presente en todos sus hijos (Operador, Supervisor, Director) y se utiliza de manera recursiva a medida que se escalan según disponibilidad de cada tipo de empleado, para lo cual se utilizan contadores de cada tipo de empleado según su disponibilidad, y para evitar problemas de integridad en el incremento o decremento de éstas variables según se deba aplicar (bien sea porque el empleado se ocupa o se desocupa de la llamada), se utilizaron variables tipo AtomicInteger con el fin de coordinar el acceso de los hilos a éstas variables y evitar problemas en la integridad del valor debido a la concurrencia de los hilos, así:

```ruby
public static AtomicInteger operadoresDisponibles = new AtomicInteger(6);
public static AtomicInteger supervisoresDisponibles = new AtomicInteger(3);
public static AtomicInteger directoresDisponibles = new AtomicInteger(1);
```

Espero haya logrado expresar claramente los conceptos de Chain of Responsability y de hilos concurrentes que comparten recursos comunes.

A continuación un ejemplo de la respuesta de éste test, cabe recordar que varía según las características de las máquinas:

```ruby
Client 1 inicia Llamada...
Client 0 inicia Llamada...
Client 3 inicia Llamada...
Client 2 inicia Llamada...
El empleado 'OPERADOR' llamado Empleado # 3 ha tomado la llamada con el cliente: Client 3. operadoresAvailable:3, supervisoresAvailable:3, directoresAvailable: 1
El empleado 'OPERADOR' llamado Empleado # 1 ha tomado la llamada con el cliente: Client 1. operadoresAvailable:4, supervisoresAvailable:3, directoresAvailable: 1
Client 5 inicia Llamada...
Client 4 inicia Llamada...
El empleado 'OPERADOR' llamado Empleado # 0 ha tomado la llamada con el cliente: Client 0. operadoresAvailable:4, supervisoresAvailable:3, directoresAvailable: 1
El empleado 'OPERADOR' llamado Empleado # 4 ha tomado la llamada con el cliente: Client 4. operadoresAvailable:0, supervisoresAvailable:3, directoresAvailable: 1
El empleado 'OPERADOR' llamado Empleado # 5 ha tomado la llamada con el cliente: Client 5. operadoresAvailable:1, supervisoresAvailable:3, directoresAvailable: 1
El empleado 'OPERADOR' llamado Empleado # 2 ha tomado la llamada con el cliente: Client 2. operadoresAvailable:2, supervisoresAvailable:3, directoresAvailable: 1
Client 6 inicia Llamada...
Client 7 inicia Llamada...
El empleado 'SUPERVISOR' llamado Empleado # 6 ha tomado la llamada con el cliente: Client 6. operadoresAvailable:0, supervisoresAvailable:2, directoresAvailable: 1
El empleado 'SUPERVISOR' llamado Empleado # 7 ha tomado la llamada con el cliente: Client 7. operadoresAvailable:0, supervisoresAvailable:1, directoresAvailable: 1
Client 8 inicia Llamada...
El empleado 'SUPERVISOR' llamado Empleado # 8 ha tomado la llamada con el cliente: Client 8. operadoresAvailable:0, supervisoresAvailable:0, directoresAvailable: 1
Client 9 inicia Llamada...
El empleado 'DIRECTOR' llamado Empleado # 9 ha tomado la llamada con el cliente: Client 9. operadoresAvailable:0, supervisoresAvailable:0, directoresAvailable: 0
Client 10 inicia Llamada...
RESPUESTA Client 10 POR FAVOR ESPERE EN LA LÍNEA PARA SER ATENDIDO, NUESTROS AGENTES ESTÁN OCUPADOS, REMITIREMOS SU LLAMADA AL PRIMER AGENTE DISPONIBLE...
Client 11 inicia Llamada...
RESPUESTA Client 11 POR FAVOR ESPERE EN LA LÍNEA PARA SER ATENDIDO, NUESTROS AGENTES ESTÁN OCUPADOS, REMITIREMOS SU LLAMADA AL PRIMER AGENTE DISPONIBLE...

```

Feliz día !!
