# Código de ejemplo Pablo Largo Rubio

Un fichero es código en el lenguaje de programación funcional Racket (derivado de Lisp) en el que está programado un juego en el que se genera una mazmorra aleatoria con paredes (#) y se debe encontrar el camino óptimo. Para ello se utiliza el algoritmo A* y hay tests desarrollados para cada una de las funciones.

La otra carpeta es un proyecto más grande desarrollado en Java (orientado a objetos) en el que se puede ver la utilización de herencias (entre las distintas actividades, es decir, Activity como clase padre), polimorfismo, uso de threads e interfaces de usuario con JFrames. Este proyecto trata de la simulación de un parque acuático en el que se gestionan diversas atracciones, cada una con límites de edad y aforo, lo cual controlarán los supervisores (que actúan cómo monitores). Cada supervisor y cada visitante del parque corresponde a un thread por lo que se usan técnicas de sincronización para que no haya irregularidades. También hay implementado un sistema de cliente-servidor interno en el que el servidor inicializa el parque acuático y diversos clientes pueden conectarse a ese parque acuático para realizar gestiones como buscar la ubicación de una persona determinada, saber cuántas personas hay en una atracción en un determinado momento, etc. Por cada cliente (ClientModule) que se ejecuta, se iniciará una conexión entre este y un ServerThread nuevo que se generará para poder realizar las gestiones correspondientes (por lo que se pueden tener todos los clientes que se quiera). Hay también un diagrama de clases del proyecto por si es de ayuda para entender el código. Este proyecto corresponde a una práctica de la universidad la cual modifiqué para incluir el sistema de cliente-servidor.
