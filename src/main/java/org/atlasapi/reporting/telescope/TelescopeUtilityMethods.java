package org.atlasapi.reporting.telescope;

import com.google.common.collect.ImmutableList;
import com.metabroadcast.columbus.telescope.api.Alias;
import com.metabroadcast.common.stream.MoreCollectors;

import java.util.Set;

public class TelescopeUtilityMethods {
    public static ImmutableList<Alias> getAliases(Set<org.atlasapi.media.entity.Alias> aliases) {
        return aliases.stream()
                .map(alias -> Alias.create(alias.getNamespace(), alias.getValue()))
                .collect(MoreCollectors.toImmutableList());
    }
}
