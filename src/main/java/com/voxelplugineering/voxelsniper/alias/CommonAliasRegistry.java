/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 The Voxel Plugineering Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.voxelplugineering.voxelsniper.alias;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.voxelplugineering.voxelsniper.Gunsmith;
import com.voxelplugineering.voxelsniper.api.alias.AliasRegistry;
import com.voxelplugineering.voxelsniper.api.service.persistence.DataContainer;
import com.voxelplugineering.voxelsniper.service.persistence.MemoryContainer;
import com.voxelplugineering.voxelsniper.util.StringUtilities;

/**
 * A registry for aliases.
 * <p>
 * TODO: clean up {@link #expand(String)}
 * </p>
 */
public class CommonAliasRegistry implements AliasRegistry
{

    private Map<String, String> aliases;
    private AliasRegistry parent;
    private boolean caseSensitive = true;
    private final String registryName;

    /**
     * Creates a new {@link AliasRegistry} with no parent.
     * 
     * @param name The registry name
     */
    public CommonAliasRegistry(String name)
    {
        this(name, null);
    }

    /**
     * Creates a new {@link AliasRegistry} with the given registry as its
     * parent.
     * 
     * @param name The registry name
     * @param parent the parent registry
     */
    public CommonAliasRegistry(String name, AliasRegistry parent)
    {
        this.aliases = Maps.newTreeMap(new Comparator<String>()
        {

            @Override
            public int compare(String o1, String o2)
            {
                int l = o1.length() - o2.length();
                if (l == 0)
                {
                    return o1.compareTo(o2);
                }
                return l;
            }
        });
        this.parent = parent;
        this.caseSensitive = Gunsmith.getConfiguration().get("caseSensitiveAliases", Boolean.class).or(true);
        this.registryName = name;
    }

    /**
     * Sets whether the keys of this registry should be case sensitive.
     * 
     * @param cs Case sensitivity
     */
    public void setCaseSensitive(boolean cs)
    {
        this.caseSensitive = cs;
    }

    /**
     * {@inheritDoc}
     */
    public AliasRegistry getParent()
    {
        return this.parent;
    }

    /**
     * {@inheritDoc}
     */
    public void register(String alias, String value)
    {
        checkNotNull(alias, "Alias cannot be null.");
        checkNotNull(value, "Value cannot be null.");
        checkArgument(!alias.isEmpty(), "Alias cannot be empty.");
        checkArgument(!value.isEmpty(), "Value cannot be empty.");

        if (!this.caseSensitive)
        {
            alias = alias.toLowerCase();
        }
        this.aliases.put(alias, value);
    }

    /**
     * {@inheritDoc}
     */
    public void clear()
    {
        this.aliases.clear();
    }

    /**
     * {@inheritDoc}
     */
    public Optional<String> getAlias(String alias)
    {
        checkNotNull(alias, "Alias cannot be null.");
        checkArgument(!alias.isEmpty(), "Alias cannot be empty.");
        if (!this.caseSensitive)
        {
            alias = alias.toLowerCase();
        }
        if (!this.aliases.containsKey(alias))
        {
            if (this.parent != null)
            {
                return this.parent.getAlias(alias);
            }
            return Optional.absent();
        } else
        {
            return Optional.of(this.aliases.get(alias));
        }
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getKeys(boolean deep)
    {
        Set<String> keys = new HashSet<String>();
        keys.addAll(this.aliases.keySet());
        if (this.parent != null && deep)
        {
            keys.addAll(this.parent.getKeys(true));
        }
        return keys;
    }

    /**
     * {@inheritDoc}
     */
    public String expand(String string)
    {
        checkNotNull(string, "Alias cannot be null.");
        if (!validate(string))
        {
            return "";
        }
        Pattern pattern = Pattern.compile("([\\S&&[^\\{]]+)[\\s]*(?:((?:\\{[^\\}]*\\}[\\s]*)+))?");
        String finalBrush = "";
        Matcher match = pattern.matcher(prep(string));
        while (match.find())
        {
            finalBrush += match.group(1) + " " + (match.group(2) == null ? "" : normalize(match.group(2)) + " ");
        }
        return expand_(finalBrush);
    }

    private static boolean validate(String fullBrush)
    {
        int co = 0;
        for (char c : fullBrush.toCharArray())
        {
            if (c == '{')
            {
                if (co > 0)
                {
                    return false;
                }
                co++;
            }
            if (c == '}')
            {
                if (co < 0)
                {
                    return false;
                }
                co--;
            }
        }
        return co == 0;
    }

    private static String normalize(String s)
    {
        Pattern p = Pattern.compile("(\\{[^\\}]*\\})[\\s]*");
        Matcher match = p.matcher(s);
        String f = "";
        while (match.find())
        {
            String m = match.group(1);
            m = m.trim().replace(" ", ",");
            m = m.replace("{,", "{");
            m = m.replace(",}", "}");
            while (m.contains(",,"))
            {
                m = m.replace(",,", ",");
            }
            f += m + " ";
        }
        f = f.replaceAll("\\}[\\s]*\\{", ",");
        f = f.trim();
        return f;
    }

    private String expand_(String string)
    {
        String[] split = string.split(" ");
        int i = 0;
        List<String> alreadyUsedAliases = new ArrayList<String>();
        while (i < split.length)
        {
            boolean found = false;
            outer: for (int j = i; j < split.length; j++)
            {
                String section = StringUtilities.getSection(split, i, j);

                if (!this.caseSensitive)
                {
                    section = section.toLowerCase();
                }
                if (section.contains("{") || section.contains("}"))
                {
                    found = false;
                    break outer;
                }
                for (String alias : this.getKeys(true))
                {
                    if (alias.equals(section) && !alreadyUsedAliases.contains(alias))
                    {
                        alreadyUsedAliases.add(alias);
                        found = true;
                        split = StringUtilities.replaceSection(split, this.getAlias(alias).get().split(" "), i, j);
                        break outer;
                    }
                }
            }
            if (!found)
            {
                i++;
                alreadyUsedAliases.clear();
            }
        }
        return StringUtilities.getSection(split, 0, split.length - 1);
    }

    private static String prep(String s)
    {
        s = s.trim();
        while (s.startsWith("{"))
        {
            int index = s.indexOf("}");
            if (index == -1)
            {
                s = s.substring(1);
            }
            s = s.substring(index + 1).trim();
        }
        return s;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Entry<String, String>> getEntries()
    {
        return Collections.unmodifiableSet(this.aliases.entrySet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fromContainer(DataContainer container)
    {
        for (String key : container.keySet())
        {
            Optional<String> alias = container.readString(key);
            if (alias.isPresent())
            {
                this.register(key, alias.get());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataContainer toContainer()
    {
        MemoryContainer container = new MemoryContainer(this.registryName);
        for (String key : this.aliases.keySet())
        {
            container.writeString(key, this.aliases.get(key));
        }
        return container;
    }

}
