package org.system.db.serviceclient;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class EnumSerializer
{
  public static <E extends Enum<E>> String serialize(Enum<E> paramEnum)
  {
    if (paramEnum == null) {
      return null;
    }
    return paramEnum.name();
  }
  
  public static <E extends Enum<E>> E deSerialize(Class<E> paramClass, String paramString)
  {
    if (paramString == null) {
      return null;
    }
    try
    {
      Enum localEnum = Enum.valueOf(paramClass, paramString);
      return (E) localEnum;
    }
    catch (IllegalArgumentException localIllegalArgumentException) {}
    return null;
  }
  
  public static <E extends Enum<E>> String[] serialize(Enum<E>[] paramArrayOfEnum)
  {
    if (paramArrayOfEnum == null) {
      return null;
    }
    int i = paramArrayOfEnum.length;
    String[] arrayOfString = new String[i];
    for (int j = 0; j < i; j++) {
      arrayOfString[j] = serialize(paramArrayOfEnum[j]);
    }
    return arrayOfString;
  }
  
  public static <E extends Enum<E>> E[] deSerialize(Class<E> paramClass, String[] paramArrayOfString)
  {
    if (paramArrayOfString == null) {
      return null;
    }
    int i = paramArrayOfString.length;
    Enum[] arrayOfEnum = (Enum[])createEnumArray(paramClass, i);
    for (int j = 0; j < i; j++) {
      arrayOfEnum[j] = deSerialize(paramClass, paramArrayOfString[j]);
    }
    return (E[]) arrayOfEnum;
  }
  
  private static <E> E[] createEnumArray(Class<E> paramClass, int paramInt)
  {
    return (E[])Array.newInstance(paramClass, paramInt);
  }
  
  public static <E extends Enum<E>> Set<String> serialize(Set<E> paramSet)
  {
    if (paramSet == null) {
      return null;
    }
    HashSet localHashSet = new HashSet();
    Iterator localIterator = paramSet.iterator();
    while (localIterator.hasNext())
    {
      Enum localEnum = (Enum)localIterator.next();
      localHashSet.add(serialize(localEnum));
    }
    return localHashSet;
  }
  
  public static <E extends Enum<E>> Set<E> deSerialize(Class<E> paramClass, Set<String> paramSet)
  {
    if (paramSet == null) {
      return null;
    }
    HashSet localHashSet = new HashSet();
    Iterator localIterator = paramSet.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localHashSet.add(deSerialize(paramClass, str));
    }
    return localHashSet;
  }
}