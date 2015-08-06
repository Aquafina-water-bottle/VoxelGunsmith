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
package com.voxelplugineering.voxelsniper;

/**
 * The main class for utility operations.
 */
public final class GunsmithMain
{

    private GunsmithMain()
    {
    }

    /**
     * The main method.
     *
     * @param args The program arguments.
     */
    public static void main(String[] args)
    {
        if (args.length == 1 && "--testinit".equalsIgnoreCase(args[0]))
        {
            System.out.println("Starting init -> stop cycle test.");
            Gunsmith.getServiceManager().start();
            System.out.println();
            Gunsmith.getServiceManager().shutdown();
        } else
        {
            System.out.println("Usage: java -jar Gunsmith.jar <command> [args]\n"
                    + "\t--testinit\t\t: Performs a full initialization to shutdown cycle.\n");
        }
    }
}
