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
package com.voxelplugineering.voxelsniper.api.platform;

import com.voxelplugineering.voxelsniper.api.service.persistence.DataSource;
import com.voxelplugineering.voxelsniper.api.service.persistence.DataSourceProvider;

/**
 * The specific implementation core class, provides direct platform utilities
 * and versioning for Gunsmith.
 */
public interface PlatformProxy
{

    /**
     * Gets the name of the platform, examples may include "Bukkit", "Spigot",
     * "Sponge", etc.
     * 
     * @return The name of the platform
     */
    String getPlatformName();

    /**
     * Gets the version of the platform.
     * 
     * @return The version of the platform
     */
    String getVersion();

    /**
     * Gets the full version string of the platform.
     * 
     * @return The full version string of the platform
     */
    String getFullVersion();

    /**
     * Gets the data source provider for obtaining data sources for serialized
     * brushes.
     * 
     * @return The data source provider
     */
    DataSourceProvider getBrushDataSource();

    /**
     * Gets the Configuration file for Metrics to be used.
     * 
     * @return The file containing the configuration for Metrics
     */
    DataSource getMetricsFile();

    /**
     * Gets the number of players online.
     * 
     * @return The number of players online
     */
    int getNumberOfPlayersOnline();

}
