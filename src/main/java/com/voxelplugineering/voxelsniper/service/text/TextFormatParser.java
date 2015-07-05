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
package com.voxelplugineering.voxelsniper.service.text;

import com.voxelplugineering.voxelsniper.service.AbstractService;
import com.voxelplugineering.voxelsniper.service.Service;
import com.voxelplugineering.voxelsniper.util.Context;

/**
 * A proxy for platform specific format codes.
 */
public interface TextFormatParser extends Service
{

    /**
     * Gets the platform specific format code for the given {@link TextFormat}.
     * 
     * @param format The format code to convert
     * @return The platform format code
     */
    String getFormat(TextFormat format);

    /**
     * A trivial {@link TextFormatParser} which simply returns the name of the {@link TextFormat}.
     */
    public static class TrivialTextFormatParser extends AbstractService implements TextFormatParser
    {

        /**
         * Creates a new {@link TrivialTextFormatParser}.
         * 
         * @param context The context
         */
        public TrivialTextFormatParser(Context context)
        {
            super(context);
        }

        @Override
        public String getFormat(TextFormat format)
        {
            return "";
        }

        @Override
        protected void _init()
        {
        }

        @Override
        protected void _shutdown()
        {
        }

    }

}
