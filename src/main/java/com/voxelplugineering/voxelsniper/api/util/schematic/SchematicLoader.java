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
package com.voxelplugineering.voxelsniper.api.util.schematic;

import java.io.File;
import java.io.IOException;

import com.voxelplugineering.voxelsniper.api.entity.MessageReceiver;
import com.voxelplugineering.voxelsniper.api.shape.MaterialShape;

/**
 * A schematic loader is an interface for loading or saving regions of the world
 * to a persistent storage. TODO: better persistence abstraction TODO: need a
 * more complete representation for a region of a world (w/ entities and tile
 * entities mostly)
 */
public interface SchematicLoader
{

    /*
     * TODO Schematic loaders for alternate data
     * 
     * Schematic loaders for alternate data sources such as a database perhaps.
     */

    /**
     * Loads a world region from the given file. TODO: need a more abstract data
     * source.
     * 
     * @param file The file to load from
     * @return The world region
     * @throws IOException If there is an error loading
     */
    MaterialShape load(File file) throws IOException;

    /**
     * Saves a world region to file.
     * 
     * @param file The file to save to
     * @param shape The region to save
     * @param owner The message receiver to post results to
     * @throws IOException If there is an error saving
     */
    void save(File file, MaterialShape shape, MessageReceiver owner) throws IOException;

    /**
     * A schematic format converter.
     */
    public static interface Converter
    {

        /**
         * Performs the conversion and returns the converted
         * {@link MaterialShape}.
         * 
         * @return The shape
         */
        MaterialShape convert();

    }

    /**
     * A converter for converting .vstencil files to MCEdit schematics.
     */
    public static class StencilConverter implements Converter
    {

        /**
         * Creates a new {@link StencilConverter} for the given stencil file.
         * 
         * @param stencil The stencil file
         */
        public StencilConverter(File stencil)
        {
            // TODO StencilConverter
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public MaterialShape convert()
        {
            return null;
        }

    }

}
