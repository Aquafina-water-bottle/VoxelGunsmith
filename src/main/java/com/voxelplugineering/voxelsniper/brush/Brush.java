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
package com.voxelplugineering.voxelsniper.brush;

import com.voxelplugineering.voxelsniper.entity.Player;
import com.voxelplugineering.voxelsniper.util.Context;

/**
 * Represents a brush which may be run, only one brush instance should exist and be shared among all
 * users. A users individual runtime settings are stored within the {@link BrushVars}.
 */
public abstract class Brush
{

    /**
     * Executes this brush.
     * 
     * @param player The player executing the brush
     * @param args The player's brush variables
     * @return The execution result
     */
    public abstract ExecutionResult run(Player player, BrushVars args);

    /**
     * Initializes this brush from the given context.
     * 
     * @param context The context
     */
    public void init(Context context)
    {

    }

}
