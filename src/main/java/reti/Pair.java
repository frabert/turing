package reti;

/**
 * Struttura ausiliaria per mantenere una coppia di valori
 * @param <T1> Tipo del primo valore
 * @param <T2> Tipo del secondo valore
 */
public class Pair<T1, T2> {
  public Pair(T1 a, T2 b) {
    first = a;
    second = b;
  }

  public T1 first;
  public T2 second;
}