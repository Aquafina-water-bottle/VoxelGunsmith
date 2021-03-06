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
package com.voxelplugineering.voxelsniper.world.queue;

import com.voxelplugineering.voxelsniper.entity.EntityType;
import com.voxelplugineering.voxelsniper.entity.Player;
import com.voxelplugineering.voxelsniper.util.math.Vector3d;
import com.voxelplugineering.voxelsniper.world.World;

import java.util.Random;

public class EntityChangeQueue extends ChangeQueue {

    private static final Random rand = new Random();

    private final Vector3d position;
    private final EntityType entityType;
    private final int count;
    private final double size;
    private boolean finished = false;

    public EntityChangeQueue(ChangeQueueOwner sniper, World world, EntityType entityType, Vector3d position, int count, double size) {
        super(sniper, world);
        this.position = position;
        this.entityType = entityType;
        this.count = count;
        this.size = size;
    }

    @Override
    public boolean isFinished() {
        return this.finished;
    }

    @Override
    public void flush() {
        this.owner.addPending(this);
    }

    @Override
    public int perform(int next) {
        for (int i = 0; i < this.count; i++) {
            double randomX = rand.nextDouble() * this.size;
            double randomZ = rand.nextDouble() * this.size;
            double plusMinus = rand.nextDouble();
            double plusMinusZ = rand.nextDouble();
            this.world.spawnEntity(this.entityType, this.position
                    .add(plusMinus > 0.5 ? randomX : - randomX, 0, plusMinusZ > 0.5 ? randomZ : -randomZ), (Player) this.owner);
        }
        this.finished = true;
        return 10;
    }

    @Override
    public void reset() {
        this.finished = false;

    }
}
