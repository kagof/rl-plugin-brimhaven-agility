package com.kagof.runelite.plugins.brimhavenagility.model;

import java.util.HashMap;

/**
 * A {@link HashMap} that always provides a default value if one isn't found in the map.
 */
public class MapWithDefault<T, V> extends HashMap<T, V>
{
	private final V def;

	public MapWithDefault(final V def)
	{
		this.def = def;
	}

	@Override
	public V get(final Object key)
	{
		return super.getOrDefault(key, def);
	}
}
