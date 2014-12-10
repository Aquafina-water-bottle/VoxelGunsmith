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
package com.voxelplugineering.voxelsniper.common;

import com.google.common.base.Optional;
import com.voxelplugineering.voxelsniper.common.factory.WeakWrapper;

/**
 * A representation of a group of voxels within a world.
 * 
 * @param <T> The chunk type
 */
public abstract class CommonChunk<T> extends WeakWrapper<T>
{

    /**
     * The world containing this chunk.
     */
    private CommonWorld<?> world;

    /**
     * Creates a new chunk.
     * 
     * @param value the specific implementation's chunk
     * @param world the containing world
     */
    public CommonChunk(T value, CommonWorld<?> world)
    {
        super(value);
        this.world = world;
    }

    /**
     * Returns the world that this voxel group resides within.
     * 
     * @return the associated world.
     */
    public CommonWorld<?> getWorld()
    {
        return this.world;
    }

    /**
     * Returns the {@link CommonBlock} at the given position relative to the origin of this voxel chunk.
     * 
     * @param x x-axis offset from origin
     * @param y y-axis offset from origin
     * @param z z-axis offset from origin
     * @return the {@link CommonBlock} at the relative position
     */
    public abstract Optional<CommonBlock> getRelativeBlockAt(int x, int y, int z);

}
