/*
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.voxelplugineering.voxelsniper.service.persistence.DataContainer;
import com.voxelplugineering.voxelsniper.service.persistence.DataSerializable;
import com.voxelplugineering.voxelsniper.service.persistence.MemoryContainer;
import com.voxelplugineering.voxelsniper.util.DataTranslator;

/**
 * Standard brush variable keys.
 */
public class BrushVars
{

    private final Map<String, Object> global;
    private final Map<String, Object> runtime;
    private final Map<String, Map<String, Object>> brushes;
    private BrushContext context;

    public BrushVars() {
        this.global = Maps.newHashMap();
        this.runtime = Maps.newHashMap();
        this.brushes = Maps.newHashMap();
        this.context = BrushContext.GLOBAL;
    }

    public BrushContext getCurrentContext() {
        return this.context;
    }

    public void setContext(BrushContext context) {
        this.context = context;
    }

    public void clearRuntime() {
        this.runtime.clear();
    }

    public void clear() {
        this.brushes.clear();
        this.global.clear();
        this.runtime.clear();
    }

    public <T> Optional<T> get(String path, Class<T> type) {
        System.out.println("getting " + path);
        System.out.println("\tcontext " + this.context);
        Map<String, Object> data = null;
        if (this.context instanceof BrushContext.Brush) {
            String name = ((BrushContext.Brush) this.context).getBrush().getName();
            System.out.println("\tUsing brush context" + name);
            if (this.brushes.containsKey(name) && this.brushes.get(name).containsKey(path)) {
                System.out.println("\tfound in brush");
                data = this.brushes.get(name);
            }
        }
        if (data == null) {
            if (this.runtime.containsKey(path)) {
                data = this.runtime;
                System.out.println("\tUsing runtime context");
            } else if (this.global.containsKey(path)) {
                data = this.global;
                System.out.println("\tUsing global context");
            } else {
                System.out.println("\tNo value found");
                return Optional.absent();
            }
        }
        Object o = data.get(path);
        System.out.println("\t" + o);
        if(o != null) {
            try
            {
                System.out.println("\tcast " + type.cast(o));
                return Optional.of(type.cast(o));
            } catch (Exception e)
            {
                System.out.println("\ttrans " + DataTranslator.attempt(o, o.getClass(), type));
                return DataTranslator.attempt(o, o.getClass(), type);
            }
        }
        System.out.println("\tabs");
        return Optional.absent();
    }

    public boolean has(String path) {
        System.out.println("checking " + path);
        System.out.println("\tcontext " + this.context);
        if (this.context instanceof BrushContext.Brush) {
            String name = ((BrushContext.Brush) this.context).getBrush().getName();
            if (this.brushes.containsKey(name) && this.brushes.get(name).containsKey(path)) {
                System.out.println("\tFound in brush context");
                return true;
            }
        }
        if (this.runtime.containsKey(path)) {
            System.out.println("\tFound in runtime context");
            return true;
        }
        if (this.global.containsKey(path)) {
            System.out.println("\tFound in global context");
            return true;
        }
        return false;
    }

    public void set(BrushContext context, String path, Object value) {
        System.out.println("setting " + path);
        System.out.println("\tcontext " + context);
        if (context instanceof BrushContext.Brush) {
            String name = ((BrushContext.Brush) context).getBrush().getName();
            if (this.brushes.containsKey(name)) {
                System.out.println("\tset into brush " + name);
                this.brushes.get(name).put(path, value);
                return;
            }
            Map<String, Object> data = Maps.newHashMap();
            data.put(path, value);
            System.out.println("\tset into brush " + name);
            this.brushes.put(name, data);
            return;
        }
        if (context == BrushContext.RUNTIME) {
            this.runtime.put(path, value);
            System.out.println("\tset into runtime");
            return;
        }
        if (context == BrushContext.GLOBAL) {
            System.out.println("\tset into global");
            this.global.put(path, value);
            return;
        }
        throw new UnsupportedOperationException("Unknown context " + context.toString());
    }

    public boolean remove(BrushContext context, String path) {
        if (context instanceof BrushContext.Brush) {
            String name = ((BrushContext.Brush) this.context).getBrush().getName();
            if (this.brushes.containsKey(name)) {
                return this.brushes.get(name).remove(path) != null;
            }
            return false;
        }
        if (context == BrushContext.RUNTIME) {
            return this.runtime.remove(path) != null;
        }
        if (context == BrushContext.GLOBAL) {
            return this.global.remove(path) != null;
        }
        throw new UnsupportedOperationException("Unknown context " + context.toString());
    }

}
