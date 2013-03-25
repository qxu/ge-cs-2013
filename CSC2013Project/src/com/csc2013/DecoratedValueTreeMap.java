package com.csc2013;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DecoratedValueTreeMap<K, V> extends TreeMap<K, V>
{
	private static final long serialVersionUID = -4086155296587102842L;
	
	private final Map<K, V> map;
	
	public DecoratedValueTreeMap()
	{
		super(new ValueComparator<>());
		this.map = new HashMap<>();
		@SuppressWarnings("unchecked")
		ValueComparator<K, V> cmp = (ValueComparator<K, V>)this.comparator();
		cmp.map = this.map;
		cmp.valuecmp = null;
	}
	
	public DecoratedValueTreeMap(Comparator<V> comparator)
	{
		super(new ValueComparator<>());
		this.map = new HashMap<>();
		@SuppressWarnings("unchecked")
		ValueComparator<K, V> cmp = (ValueComparator<K, V>)this.comparator();
		cmp.map = this.map;
		cmp.valuecmp = comparator;
	}
	
	@Override
	public V put(K key, V value)
	{
		map.put(key, value);
		return super.put(key, value);
	}
	
	private static class ValueComparator<K, V> implements Comparator<K>
	{
		Map<K, V> map;
		Comparator<V> valuecmp;
		
		@Override
		public int compare(K k1, K k2)
		{
			Map<K, V> m = this.map;
			V v1 = m.get(k1);
			V v2 = m.get(k2);
			return compareValues(v1, v2);
		}
		
		@SuppressWarnings("unchecked")
		private int compareValues(V v1, V v2)
		{
			return this.valuecmp == null ? ((Comparable<? super V>)v1)
					.compareTo(v2)
					: this.valuecmp.compare(v1, v2);
		}
	}
}
