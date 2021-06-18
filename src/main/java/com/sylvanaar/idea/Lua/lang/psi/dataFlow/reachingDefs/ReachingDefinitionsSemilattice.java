/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.sylvanaar.idea.Lua.lang.psi.dataFlow.reachingDefs;

import com.sylvanaar.idea.Lua.lang.psi.dataFlow.Semilattice;
import consulo.util.collection.primitive.ints.*;

import java.util.ArrayList;
import java.util.Set;

/**
 * @author ven
 */
public class ReachingDefinitionsSemilattice implements Semilattice<IntObjectMap<IntSet>>
{
	public IntObjectMap<IntSet> join(ArrayList<IntObjectMap<IntSet>> ins)
	{
		if(ins.isEmpty())
		{
			return IntMaps.newIntObjectHashMap();
		}

		IntObjectMap<IntSet> result = IntMaps.newIntObjectHashMap();
		for(IntObjectMap<IntSet> map : ins)
		{
			merge(result, map);
		}

		return result;
	}

	private void merge(final IntObjectMap<IntSet> result, IntObjectMap<IntSet> map2)
	{
		map2.forEach(new IntObjConsumer<IntSet>()
		{
			public void accept(int num, IntSet defs)
			{
				IntSet defs2 = result.get(num);
				if(defs2 == null)
				{
					defs2 = IntSets.newHashSet(defs.toArray());
					result.put(num, defs2);
				}
				else
				{
					defs2.addAll(defs.toArray());
				}
			}
		});
	}

	public boolean eq(final IntObjectMap<IntSet> m1, final IntObjectMap<IntSet> m2)
	{
		if(m1.size() != m2.size())
		{
			return false;
		}

		for(IntObjectMap.IntObjectEntry<IntSet> entry : m1.entrySet())
		{
			int num = entry.getKey();
			IntSet defs1 = entry.getValue();

			final IntSet defs2 = m2.get(num);
			if(defs2 == null || !defs2.equals(defs1))
			{
				return false;
			}
		}
		return true;
	}
}
