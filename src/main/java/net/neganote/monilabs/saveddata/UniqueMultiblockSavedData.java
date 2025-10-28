package net.neganote.monilabs.saveddata;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

// Copied from CosmicCore with some minor changes (thank you Caitlynn!)
public class UniqueMultiblockSavedData extends SavedData {

    private static final String DATA_NAME = "monilabs_unique_multiblock_data";
    private static final String UNIQUE_MULTI_MAPPING = "uniqueMultiMapping";
    private static final String UNIQUE_MULTI_UUID = "uniqueMultiUuid";
    private static final String UNIQUE_MULTI_DATA = "uniqueMultiData";

    public static final HashMap<UUID, UniqueMultiblockData> UniqueMultiblockMapping = new HashMap<>(20, 0.9f);

    public static UniqueMultiblockSavedData getOrCreate(ServerLevel serverLevel) {
        return serverLevel.getServer().overworld().getDataStorage().computeIfAbsent(
                UniqueMultiblockSavedData::new,
                UniqueMultiblockSavedData::new, DATA_NAME);
    }

    private UniqueMultiblockSavedData() {}

    private UniqueMultiblockSavedData(CompoundTag nbt) {
        var list = nbt.getList(UNIQUE_MULTI_MAPPING, CompoundTag.TAG_COMPOUND);
        for (Tag tag : list) {
            if (tag instanceof CompoundTag compoundTag) {
                var uuid = UUID.fromString(compoundTag.getString(UNIQUE_MULTI_UUID));
                var data = compoundTag.getList(UNIQUE_MULTI_DATA, Tag.TAG_COMPOUND);
                UniqueMultiblockMapping.put(uuid, UniqueMultiblockData.fromTag(data));
            }
        }
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag) {
        var uniqueMultiDataList = new ListTag();
        for (var entry : UniqueMultiblockMapping.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }
            var tag = new CompoundTag();
            tag.putString(UNIQUE_MULTI_UUID, entry.getKey().toString());
            tag.put(UNIQUE_MULTI_DATA, entry.getValue().toTag());
            uniqueMultiDataList.add(tag);
        }
        compoundTag.put(UNIQUE_MULTI_MAPPING, uniqueMultiDataList);
        return compoundTag;
    }

    public boolean hasData(UUID owner, String multiblockType) {
        var data = UniqueMultiblockMapping.computeIfAbsent(owner, uuid -> new UniqueMultiblockData());
        return data.hasData(multiblockType);
    }

    public boolean isUnique(UUID owner, String multiblockType, BlockPos pos) {
        var data = UniqueMultiblockMapping.computeIfAbsent(owner, uuid -> new UniqueMultiblockData());
        return data.isUnique(multiblockType, pos);
    }

    public void addMultiblock(UUID owner, String multiblockType, BlockPos pos) {
        var data = UniqueMultiblockMapping.computeIfAbsent(owner, uuid -> new UniqueMultiblockData());
        data.addMultiblock(multiblockType, pos);
        setDirty();
    }

    public void removeMultiblock(UUID owner, String multiblockType, BlockPos pos) {
        var data = UniqueMultiblockMapping.computeIfAbsent(owner, uuid -> new UniqueMultiblockData());
        data.removeMultiblock(multiblockType, pos);
        setDirty();
    }
}
