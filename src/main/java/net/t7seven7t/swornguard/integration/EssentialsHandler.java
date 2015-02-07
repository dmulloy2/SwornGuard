/**
 * (c) 2015 dmulloy2
 */
package net.t7seven7t.swornguard.integration;

import java.util.List;
import java.util.UUID;

import net.dmulloy2.integration.DependencyProvider;
import net.t7seven7t.swornguard.SwornGuard;

import com.earth2me.essentials.Essentials;

/**
 * @author dmulloy2
 */

public class EssentialsHandler extends DependencyProvider<Essentials> {

	public EssentialsHandler(SwornGuard handler) {
		super(handler, "SwornGuard");
	}

	public List<String> getHistory(UUID uniqueId) {
		if (! isEnabled()) {
			return null;
		}

		return getDependency().getUserMap().getUserHistory(uniqueId);
	}
}