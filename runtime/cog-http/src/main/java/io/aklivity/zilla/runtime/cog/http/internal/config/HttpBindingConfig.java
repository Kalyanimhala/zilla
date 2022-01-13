/*
 * Copyright 2021-2022 Aklivity Inc.
 *
 * Aklivity licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.aklivity.zilla.runtime.cog.http.internal.config;

import static java.util.EnumSet.allOf;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

import io.aklivity.zilla.runtime.engine.config.BindingConfig;
import io.aklivity.zilla.runtime.engine.config.RoleConfig;

public final class HttpBindingConfig
{
    public static final SortedSet<HttpVersion> DEFAULT_VERSIONS = new TreeSet<>(allOf(HttpVersion.class));

    public final long id;
    public final long vaultId;
    public final String entry;
    public final HttpOptionsConfig options;
    public final RoleConfig kind;
    public final List<HttpRouteConfig> routes;
    public final HttpRouteConfig exit;

    public HttpBindingConfig(
        BindingConfig binding)
    {
        this.id = binding.id;
        this.vaultId = binding.vault != null ? binding.vault.id : 0L;
        this.entry = binding.entry;
        this.kind = binding.kind;
        this.options = HttpOptionsConfig.class.cast(binding.options);
        this.routes = binding.routes.stream().map(HttpRouteConfig::new).collect(toList());
        this.exit = binding.exit != null ? new HttpRouteConfig(binding.exit) : null;
    }

    public HttpRouteConfig resolve(
        long authorization,
        Function<String, String> headerByName)
    {
        return routes.stream()
            .filter(r -> r.when.isEmpty() || r.when.stream().anyMatch(m -> m.matches(headerByName)))
            .findFirst()
            .orElse(exit);
    }

    public SortedSet<HttpVersion>  versions()
    {
        return options != null && options.versions != null ? options.versions : DEFAULT_VERSIONS;
    }
}