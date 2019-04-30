/* 
*    Ref-Finder
*    Copyright (C) <2015>  <PLSE_UCLA>
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package tyRuBa.engine;

import java.util.HashMap;
import java.util.Map;

import serp.util.SoftValueMap;
import tyRuBa.engine.compilation.SemiDetCompiled;

public class SemiDetCachedRuleBase extends SemiDetCompiled {

	SemiDetCompiled compiledContents;

	/** Cache of already performed (simple)queries. */
	private Map cache = null;

	public SemiDetCachedRuleBase(SemiDetCompiled compiledRuleBase) {
		super(compiledRuleBase.getMode());
		compiledContents = compiledRuleBase;
		initCache();
	}

	private void initCache() {
		cache = RuleBase.softCache ? (Map) new SoftValueMap() : new HashMap();
	}

	/** Unification, check cache first */
	public Frame runSemiDet(Object input, RBContext context) {
		final RBTuple other = (RBTuple)input;
		FormKey k = new FormKey(other);
		CacheEntry entry = (CacheEntry) cache.get(k);
		Frame cachedResult;
		if (entry == null
			|| (cachedResult = entry.getCachedResult()) == null) {
			/* Not found in the cache */
			if (!RuleBase.silent)
				if (entry == null)
					System.err.print(".");
				else
					System.err.print("@");
			Frame result = compiledContents.runSemiDet(input, context);
			entry = new CacheEntry(k, result);
			cache.put(k, entry);
			return result;
		} else { /* Found in the Cache */
			if (!RuleBase.silent)
				System.err.print("H");
			final Frame call = new Frame();
			if (other.sameForm(entry.key.theKey, call, new Frame())) {
				return call.callResult(cachedResult);
			} else {
				throw new Error("Should never happen");
			}
		}
	}

	private class CacheEntry {
		FormKey key;
		Frame result;
		CacheEntry(FormKey k, Frame r) {
			result = r;
			key = k;
		}
		Frame getCachedResult() {
			return result;
		}
	}
	
	public String toString() {
		return "SEMIDET CACHED RULEBASE(...)";
	}

}
