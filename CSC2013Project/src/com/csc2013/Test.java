package com.csc2013;

import java.util.ArrayList;
import java.util.List;

public class Test
{
	private static <T> List<T> deepListCopy(List<T> src)
	{
		try
		{
			@SuppressWarnings("unchecked")
			List<T> dest = src.getClass().newInstance();
			for(T element : src)
			{
				if(element instanceof List)
				{
					List<?> elementCast = (List<?>)element;
					@SuppressWarnings("unchecked")
					T copy = (T)deepListCopy(elementCast);
					dest.add(copy);
				}
				else
				{
					dest.add(element);
				}
			}
			return dest;
		}
		catch(InstantiationException | IllegalAccessException e)
		{
			throw new RuntimeException("Cannot copy list " + src);
		}
	}
	
	public static void main(String[] args)
	{
		ArrayList<ArrayList<Double>> list = new ArrayList<>();
		for(int i = 0; i < 16; ++i)
		{
			ArrayList<Double> subList = new ArrayList<>();
			for(int j = 0; j < 16; ++j)
			{
				subList.add(Math.random());
			}
			list.add(subList);
		}
		
		final int trials = 90000;
		long start = System.currentTimeMillis();
		
		for(int i = 0; i < trials; ++i)
		{
			deepListCopy(list);
		}
		
		long stop = System.currentTimeMillis();
		
		System.out.println("time: " + (stop - start) + " ms");
		System.out.println(1000 * trials / (stop - start) + " trials/second");
	}
}
