/*
 * Copyright 2013-2015 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package consulo.lua.bundle;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.bundle.PredefinedBundlesProvider;
import com.intellij.openapi.projectRoots.impl.SdkImpl;
import com.intellij.util.Consumer;

/**
 * @author VISTALL
 * @since 10.03.2015
 */
public class LuaPredefinedBundlesProvider extends PredefinedBundlesProvider
{
	@Override
	public void createBundles(@NotNull Consumer<SdkImpl> consumer)
	{
		Map<String, LuaSdkType> map = new HashMap<String, LuaSdkType>();
		map.put("Kahlua", KahluaSdkType.getInstance());
		map.put("LuaJ", LuaJSdkType.getInstance());

		for(Map.Entry<String, LuaSdkType> entry : map.entrySet())
		{
			LuaSdkType luaSdkType = entry.getValue();
			SdkImpl sdkWithName = createSdkWithName(luaSdkType, entry.getKey());

			sdkWithName.setHomePath(luaSdkType.getStdLibraryFile().getPath());

			luaSdkType.setupSdkPaths(sdkWithName);

			consumer.consume(sdkWithName);
		}
	}
}
