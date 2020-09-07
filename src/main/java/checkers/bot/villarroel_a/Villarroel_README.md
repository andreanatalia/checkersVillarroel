**Lógica del Bot**

La heurística del programa se basa principalmente en el numero de fichas de cada jugador; tanto en el número de fichas simples como en el número de fichas que se convierten en Reinas.

Además se tomó en cuenta el número de fichas en el tablero que se pueden mover o capturar, así como el número de fichas "bloqueadas", de ambos jugadores.

Para obtener el movimiento más óptimo, se tomó en cuenta, una profundidad inicial de 6, en el cual, el programa, pasará a crear los respectivos sucesores para cada tablero, para así encontrar la utilidad indicada, dependiendo el estado en el que se encuentre.
Por ejemplo, si se encuentra en el estado, en el que se ha calculado la utilidad para cada uno de los sucesores del tablero actual, y además termina en el mismo jugador que con el que inició la partida, entoces, el programa pasará a maximizar la función, es decir, que buscará la utilidad más alta de la lista de sucesores al tablero actual, y lo guardará como posible camino del nodo,
caso contrario, si el jugador es diferente al "current_player", la función pasará a buscar al que tenga la utilidad menor de todos los sucesores.
  
