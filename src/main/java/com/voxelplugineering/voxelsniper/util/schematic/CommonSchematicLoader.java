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
package com.voxelplugineering.voxelsniper.util.schematic;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.voxelplugineering.voxelsniper.Gunsmith;
import com.voxelplugineering.voxelsniper.api.entity.MessageReceiver;
import com.voxelplugineering.voxelsniper.api.service.persistence.DataContainer;
import com.voxelplugineering.voxelsniper.api.service.persistence.DataSource;
import com.voxelplugineering.voxelsniper.api.shape.MaterialShape;
import com.voxelplugineering.voxelsniper.api.util.schematic.SchematicLoader;
import com.voxelplugineering.voxelsniper.api.util.text.TextFormat;
import com.voxelplugineering.voxelsniper.api.world.material.Material;
import com.voxelplugineering.voxelsniper.service.persistence.MemoryContainer;
import com.voxelplugineering.voxelsniper.shape.NamedWorldSection;
import com.voxelplugineering.voxelsniper.shape.csg.CuboidShape;
import com.voxelplugineering.voxelsniper.util.math.Vector3i;
import com.voxelplugineering.voxelsniper.util.nbt.CompoundTag;
import com.voxelplugineering.voxelsniper.util.nbt.IntTag;
import com.voxelplugineering.voxelsniper.util.nbt.ShortTag;
import com.voxelplugineering.voxelsniper.util.nbt.StringTag;
import com.voxelplugineering.voxelsniper.util.nbt.Tag;

/**
 * A {@link SchematicLoader} for NBT stored schematics
 */
public class CommonSchematicLoader implements SchematicLoader
{

    /*
     * TODO unit tests for this
     */

    /**
     * Creates a new {@link CommonSchematicLoader}.
     */
    public CommonSchematicLoader()
    {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MaterialShape load(DataSource data) throws IOException
    {
        DataContainer schematicTag = data.read();
        if (!schematicTag.contains("Blocks"))
        {
            throw new UnsupportedOperationException("Schematic file is missing a \"Blocks\" tag");
        }
        if (!schematicTag.contains("MaterialDictionary"))
        {
            return new LegacyConverter(schematicTag).convert();
        }
        // extract dimensions
        int width = schematicTag.readInt("Width").get();
        int length = schematicTag.readInt("Length").get();
        int height = schematicTag.readInt("Height").get();
        String materials = schematicTag.readString("Materials").get();
        if (!materials.equals("Alpha"))
        {
            throw new UnsupportedOperationException("Schematic file is not an Alpha schematic");
        }
        // extract block id and data values, addId is used if the schematic
        // contains block ids above 255
        byte[] blockId = schematicTag.readByteArray("Blocks").get();
        byte[] addId = new byte[0];
        short[] blocks = new short[blockId.length];
        if (schematicTag.contains("AddBlocks"))
        {
            addId = schematicTag.readByteArray("AddBlocks").get();
        }
        // combine blockId and addId into a single short array blocks
        for (int index = 0; index < blockId.length; index++)
        {
            if ((index >> 1) >= addId.length)
            {
                blocks[index] = (short) (blockId[index] & 0xFF);
            } else
            {
                if ((index & 1) == 0)
                {
                    blocks[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (blockId[index] & 0xFF));
                } else
                {
                    blocks[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (blockId[index] & 0xFF));
                }
            }
        }
        // Need to pull out tile entities from this list of compound tags
        @SuppressWarnings("unchecked")
        List<Tag> tileEntities = schematicTag.readList("TileEntities").get();
        // this is the map we will populate with all extracted time entities
        Map<Vector3i, CompoundTag> tileEntitiesMap = Maps.newHashMap();
        for (Tag tag : tileEntities)
        {
            if (!(tag instanceof CompoundTag))
            {
                continue;
            }
            CompoundTag tileEntity = (CompoundTag) tag;
            int x = 0;
            int y = 0;
            int z = 0;

            if (tileEntity.contains("x"))
            {
                x = tileEntity.getChildTag("x", IntTag.class).get().getValue();
            }
            if (tileEntity.contains("y"))
            {
                y = tileEntity.getChildTag("y", IntTag.class).get().getValue();
            }
            if (tileEntity.contains("z"))
            {
                z = tileEntity.getChildTag("z", IntTag.class).get().getValue();
            }

            Vector3i vec = new Vector3i(x, y, z);
            tileEntitiesMap.put(vec, tileEntity);
        }

        @SuppressWarnings("unchecked")
        List<Tag> entities = schematicTag.readList("Entities").get();

        // this is the map we will populate with all extracted time entities
        Map<Vector3i, CompoundTag> entitiesMap = new HashMap<Vector3i, CompoundTag>();
        for (Tag tag : entities)
        {
            if (!(tag instanceof CompoundTag))
            {
                continue;
            }
            CompoundTag entity = (CompoundTag) tag;
            int x = 0;
            int y = 0;
            int z = 0;

            if (entity.contains("x"))
            {
                x = entity.getChildTag("x", IntTag.class).get().getValue();
            }
            if (entity.contains("y"))
            {
                y = entity.getChildTag("y", IntTag.class).get().getValue();
            }
            if (entity.contains("z"))
            {
                z = entity.getChildTag("z", IntTag.class).get().getValue();
            }
            Vector3i vec = new Vector3i(x, y, z);
            entitiesMap.put(vec, entity);
        }

        // Load material dictionary
        @SuppressWarnings("unchecked")
        List<Tag> dict = schematicTag.readList("MaterialDictionary").get();
        Map<Short, Material> materialDict = Maps.newHashMap();
        for (Tag tag : dict)
        {
            if (!(tag instanceof CompoundTag))
            {
                continue;
            }
            CompoundTag material = (CompoundTag) tag;
            short key = material.getChildTag("Key", ShortTag.class).get().getValue();
            String mat = material.getChildTag("Name", StringTag.class).get().getValue();
            materialDict.put(key, Gunsmith.getMaterialRegistry().getMaterial(mat).or(Gunsmith.getMaterialRegistry().getAirMaterial()));
        }
        // create region and return
        NamedWorldSection region;
        if (schematicTag.contains("WEOffsetX") && schematicTag.contains("WEOffsetY") && schematicTag.contains("WEOffsetZ"))
        {
            int offsetX = schematicTag.readInt("WEOffsetX").get();
            int offsetY = schematicTag.readInt("WEOffsetY").get();
            int offsetZ = schematicTag.readInt("WEOffsetZ").get();
            region = new NamedWorldSection(new CuboidShape(width, height, length, new Vector3i(offsetX, offsetY, offsetZ)), materialDict);
        } else
        {
            region = new NamedWorldSection(new CuboidShape(width, height, length, new Vector3i(0, 0, 0)), materialDict);
        }
        String name = null;
        if (schematicTag.contains("name"))
        {
            name = schematicTag.readString("name").get();
        } else
        {
            Optional<String> sourcename = data.getName();
            if (sourcename.isPresent())
            {
                name = sourcename.get().replace(".schematic", "");
            }
        }

        // region.setBlocks(blockData, blocks);
        region.setTileEntityMap(tileEntitiesMap);
        region.setEntitiesMap(entitiesMap);
        if (name != null)
        {
            region.setName(name);
        } else
        {
            region.setName("Unknown_" + System.currentTimeMillis());
        }
        return region;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(DataSource data, MaterialShape shape, MessageReceiver owner) throws IOException
    {
        int width = shape.getWidth();
        int height = shape.getHeight();
        int length = shape.getLength();

        // length, width and height cannot be larger than the size of an
        // unsigned short
        if (width > 65535 || height > 65535 || length > 65535)
        {
            if (owner == null)
            {
                Gunsmith.getLogger().warn(
                        "Failed to save schematic to " + data.getName().or("an unknown source") + ": Dimensions exceeded max size supported.");
            } else
            {
                owner.sendMessage(TextFormat.RED + "Could not save schematic, dimensions exceeded maximum supported size!");
            }
            return;
        }

        DataContainer schematic = new MemoryContainer("");
        // Store basic schematic data
        schematic.writeInt("Width", width);
        schematic.writeInt("Height", height);
        schematic.writeInt("Length", length);
        schematic.writeString("Materials", "Alpha");

        // Extract tile entities and load into the main schematic map

        List<CompoundTag> tiles = Lists.newArrayList();
        List<CompoundTag> entities = Lists.newArrayList();

        if (shape instanceof NamedWorldSection)
        {
            NamedWorldSection complex = (NamedWorldSection) shape;
            if (complex.hasTileEntities())
            {
                for (Vector3i v : complex.getTileEntities().keySet())
                {
                    CompoundTag tile = complex.getTileEntities().get(v);
                    tiles.add(tile);
                }
            }
            if (complex.hasEntities())
            {
                for (Vector3i v : complex.getEntities().keySet())
                {
                    CompoundTag entity = complex.getEntities().get(v);
                    entities.add(entity);
                }
            }
        }

        // save the region reference point in the WorldEdit style for
        // compatibility
        Vector3i origin = shape.getOrigin();
        schematic.writeInt("WEOffsetX", origin.getX());
        schematic.writeInt("WEOffsetY", origin.getY());
        schematic.writeInt("WEOffsetZ", origin.getZ());

        // store the lower byte of the block Ids
        schematic.writeByteArray("Blocks", shape.getLowerMaterialData());
        if (shape.hasExtraData())
        {
            schematic.writeByteArray("AddBlocks", shape.getUpperMaterialData());
        }
        if (shape instanceof NamedWorldSection)
        {
            NamedWorldSection complex = (NamedWorldSection) shape;
            if (complex.hasTileEntities())
            {
                schematic.writeList("TileEntities", tiles);
            }
            if (complex.hasEntities())
            {
                schematic.writeList("Entities", entities);
            }
        }

        Map<Short, Material> materialDict = shape.getMaterialsDictionary();
        List<CompoundTag> materials = Lists.newArrayList();
        for (Map.Entry<Short, Material> entry : materialDict.entrySet())
        {
            Map<String, Tag> material = Maps.newHashMap();
            material.put("Key", new ShortTag("Key", entry.getKey()));
            material.put("Name", new StringTag("Name", entry.getValue().getName()));
            CompoundTag tag = new CompoundTag("Material", material);
            materials.add(tag);
        }
        schematic.writeList("MaterialDictionary", materials);

        data.write(schematic);
        /*
         * CompoundTag schematicTag = new CompoundTag("Schematic", schematic);
         * NBTOutputStream stream = new NBTOutputStream(new GZIPOutputStream(new
         * FileOutputStream(file))); stream.writeTag(schematicTag);
         * stream.close();
         */
    }

    /**
     * A converter for converting MCEdit schematics to the new format.
     */
    public static class LegacyConverter implements SchematicLoader.Converter
    {

        /**
         * Creates a new {@link LegacyConverter}.
         * 
         * @param data The data to convert
         */
        public LegacyConverter(DataContainer data)
        {
            // TODO LegacyConverter
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
